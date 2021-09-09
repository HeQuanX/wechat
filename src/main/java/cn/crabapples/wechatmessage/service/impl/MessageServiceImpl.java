package cn.crabapples.wechatmessage.service.impl;

import cn.crabapples.wechatmessage.ApplicationException;
import cn.crabapples.wechatmessage.mapper.MessageMapper;
import cn.crabapples.wechatmessage.messages.BaseMessage;
import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.SignMessage;
import cn.crabapples.wechatmessage.service.MessageService;
import cn.crabapples.wechatmessage.utils.AesUtils;
import cn.crabapples.wechatmessage.utils.MessageXmlUtils;
import cn.crabapples.wechatmessage.utils.SignUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.tomcat.util.buf.HexUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Value("${token}")
    private String token;
    @Value("${aesKey}")
    private String aesKey;
    private final boolean IS_ENCRYPT = false;
    private final SqlSession sqlSession;

    public MessageServiceImpl(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    /**
     * 保存收到的消息到数据库
     *
     * @param requestMessage 收到的消息
     */
    private void saveMessage(Message requestMessage) {
        MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
        cn.crabapples.wechatmessage.entity.Message message = new cn.crabapples.wechatmessage.entity.Message();
        BeanUtils.copyProperties(requestMessage, message);
        mapper.saveMessage(message);
    }

    /**
     * 检测请求的签名是否正确
     *
     * @param message 签名信息
     */
    public boolean checkServer(SignMessage message) {
        logger.info("开始检测消息签名是否为微信服务器,签名信息:\n[{}]", message);
        try {
            Long timestamp = message.getTimestamp();
            String nonce = message.getNonce();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            String content = SignUtils.sort(token, timestamp.toString(), nonce);
            byte[] cipherBytes = messageDigest.digest(content.getBytes());
            String result = HexUtils.toHexString(cipherBytes);
            return result.equals(message.getSignature());
        } catch (NoSuchAlgorithmException e) {
            logger.error("检测消息签名是否为微信服务器时出现异常:\n[{}]", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取返回给微信服务器的数据
     *
     * @param requestEncryptXml 微信发送的请求数据
     * @return 返回给微信服务器的数据
     */
    public String getResultMessage(String requestEncryptXml) {
        // 将[收到的][xmlString]转换成[EncryptMessage]对象
        EncryptMessage requestEncryptMessage = xml2EncMessage(requestEncryptXml);
        // 对[收到的][EncryptMessage]对象解密
        String requestXml = decodeXml(requestEncryptMessage);
        // 将[收到的][xmlString]转换成message对象(未加密)
        Message requestMessage = xml2Bean(requestXml);
        saveMessage(requestMessage);
        // todo 生成测试回复
        Message responseMessage = demo((BaseMessage) requestMessage);
        // 将[回复的]message对象转换为[xmlString]
        String responseXmlString = bean2Xml(responseMessage);
        if (IS_ENCRYPT) {
            /*
             * todo 返回数据加密后无法解析
             * 可能是因为加密填充方式的原因，暂时没有解决方案
             */
            responseXmlString = MessageXmlUtils.delXmlRootNode(responseXmlString);
            // 对[回复的][xmlString]加密
            String encryptString = encodeXml(responseXmlString);
            encryptString = new String(Base64.getDecoder().decode(encryptString.getBytes(StandardCharsets.UTF_8)));
            // 将[回复的][encryptString]转换成[EncryptMessage]对象
            EncryptMessage pushEncMessage = string2ResultEncBean((BaseMessage) responseMessage, encryptString);
            logger.info("测试解密->\n[{}]", decodeXml(pushEncMessage));
            String resultXml = bean2Xml(pushEncMessage);
            logger.info("返回结果->加密前:\n[{}]\n加密后:[{}]", responseXmlString, resultXml);
            return resultXml;
        }
        return responseXmlString;
    }


    /**
     * 将加密的XML转换为Bean
     *
     * @param xmlString 加密的XMl
     */
    private EncryptMessage xml2EncMessage(String xmlString) {
        logger.info("开始将加密的Xml转换为message,xml:\n[{}]", xmlString);
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            Document document = saxBuilder.build(inputStream);
            Element root = document.getRootElement();
            String toUserName = root.getChild("ToUserName").getValue();
            String encrypt = root.getChild("Encrypt").getValue();
            EncryptMessage encryptMessage = new EncryptMessage();
            encryptMessage.setToUserName(toUserName);
            encryptMessage.setEncrypt(encrypt);
            return encryptMessage;
        } catch (Exception e) {
            logger.error("将加密的XML转换为Bean时出现异常:\n[{}]", e.getMessage(), e);
            throw new ApplicationException("xml2EncBean exception ");
        }
    }

    /**
     * 将加密的字符串转换为Bean
     *
     * @param encString 加密的String
     */
    private EncryptMessage string2ResultEncBean(BaseMessage source, String encString) {
        logger.info("开始将加密的字符串转换为Bean,消息对象:\n[{}],加密数据:\n[{}]", source, encString);
        try {
            EncryptMessage encryptMessage = new EncryptMessage();
            encryptMessage.setToUserName(source.getToUserName());
            encryptMessage.setEncrypt(encString);
            return encryptMessage;
        } catch (Exception e) {
            logger.error("将加密的字符串转换为Bean时出现异常:\n[{}]", e.getMessage(), e);
            throw new ApplicationException("encString2EncBean exception ");
        }
    }

    /**
     * 解密xml
     *
     * @param message 存储了加密信息的Bean
     */
    private String decodeXml(EncryptMessage message) {
        logger.info("解密EncryptMessage对象,对象信息:\n[{}]", message);
        try {
            String encrypt = message.getEncrypt();
            final String string = AesUtils.doFinal(aesKey, encrypt, Cipher.DECRYPT_MODE);
            logger.info("解密EncryptMessage对象,结果:\n[{}]", string);
            return string;
        } catch (Exception e) {
            logger.error("将EncryptMessage对象解密为xml时出现异常:\n[{}]", e.getMessage(), e);
            throw new ApplicationException("decodeXml exception", e);
        }
    }

    /**
     * 加密xml
     *
     * @param xmlString 需要加密的xml
     */
    private String encodeXml(String xmlString) {
        logger.info("开始对xml进行加密,原数据:\n[{}]", xmlString);
        try {
            return AesUtils.doFinal(aesKey, xmlString, Cipher.ENCRYPT_MODE);
        } catch (Exception e) {
            logger.error("对xml进行加密时出现异常:\n[{}]", e.getMessage(), e);
            throw new ApplicationException("encodeXml exception");
        }
    }

    /**
     * 解析xml为对应的message对象
     *
     * @param xmlString 需要解析的xml
     */
    private Message xml2Bean(String xmlString) {
        logger.info("开始解析xml为对应的message对象,xml:\n[{}]", xmlString);
        SAXBuilder saxBuilder = new SAXBuilder();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))) {
            Document document = saxBuilder.build(inputStream);
            return MessageXmlUtils.beanSetField(document);
        } catch (Exception e) {
            logger.error("解析xml为对应的message对象时出现异常:\n[{}]", e.getMessage(), e);
            throw new ApplicationException("Xml2Bean exception");
        }
    }

    /**
     * 转换message对象为xml
     *
     * @param message 需要封装的对象
     * @See Message
     */
    private String bean2Xml(Message message) {
        logger.info("开始转换message对象为xml,对象信息:\n[{}]", message);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = MessageXmlUtils.xmlSetField(message);
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            XMLOutputter xmlOutputter = new XMLOutputter(format);
            xmlOutputter.output(document, outputStream);
            String xmlString = outputStream.toString();
            logger.info("转换message对象为xml完成,结果->:\n[{}]", xmlString);
            return xmlString;
        } catch (Exception e) {
            logger.error("转换message对象为xml时出现异常:\n[{}]", e.getMessage(), e);
            throw new ApplicationException("Bean2Xml exception");
        }
    }
}
