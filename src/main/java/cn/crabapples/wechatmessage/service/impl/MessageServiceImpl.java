package cn.crabapples.wechatmessage.service.impl;

import cn.crabapples.wechatmessage.ApplicationException;
import cn.crabapples.wechatmessage.messages.BaseMessage;
import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.SignMessage;
import cn.crabapples.wechatmessage.service.MessageService;
import cn.crabapples.wechatmessage.utils.AesUtils;
import cn.crabapples.wechatmessage.utils.MessageXmlUtils;
import cn.crabapples.wechatmessage.utils.SignUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Value("${token}")
    private String token;
    @Value("${aesKey}")
    private String aesKey;

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
     * 将加密的XML转换为Bean
     *
     * @param xmlString 加密的XMl
     */
    public EncryptMessage xml2EncBean(String xmlString) {
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
    public EncryptMessage string2ResultEncBean(BaseMessage source, String encString) {
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
    public String decodeXml(EncryptMessage message) {
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
    public String encodeXml(String xmlString) {
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
    public Message xml2Bean(String xmlString) {
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
    public String bean2Xml(Message message) {
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
