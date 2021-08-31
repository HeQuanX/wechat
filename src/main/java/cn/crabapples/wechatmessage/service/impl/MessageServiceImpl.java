package cn.crabapples.wechatmessage.service.impl;

import cn.crabapples.wechatmessage.ApplicationException;
import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.SignMessage;
import cn.crabapples.wechatmessage.messages.push.TextMessage;
import cn.crabapples.wechatmessage.service.MessageService;
import cn.crabapples.wechatmessage.utils.AesUtils;
import cn.crabapples.wechatmessage.utils.BeanXmlUtils;
import cn.crabapples.wechatmessage.utils.SignUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
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
        try {
            Long timestamp = message.getTimestamp();
            String nonce = message.getNonce();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA"); // 此处的sha代表sha1
            String content = SignUtils.sort(token, timestamp.toString(), nonce);
            byte[] cipherBytes = messageDigest.digest(content.getBytes());
            String result = HexUtils.toHexString(cipherBytes);
            return result.equals(message.getSignature());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将加密的XML转换为Bean
     *
     * @param xmlString 加密的XMl
     */
    public EncryptMessage xml2EncBean(String xmlString) {
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
            e.printStackTrace();
            throw new ApplicationException("xml2EncBean exception ");
        }
    }

    /**
     * 解密xml
     *
     * @param message 存储了加密信息的Bean
     */
    public String decodeXml(EncryptMessage message) {
        try {
            String encrypt = message.getEncrypt();
            return AesUtils.doFinal(aesKey, encrypt, Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("decodeXml exception");
        }
    }

    /**
     * 加密xml
     *
     * @param xmlString 需要加密的xml
     */
    public String encodeXml(String xmlString) {
        try {
            return AesUtils.doFinal(aesKey, xmlString, Cipher.ENCRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("encodeXml exception");
        }
    }


    /**
     * 解析xml为对应的message对象
     *
     * @param xmlString 需要解析的xml
     */
    public Message xml2Bean(String xmlString) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            Document document = saxBuilder.build(inputStream);
            return BeanXmlUtils.beanSetField(document);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Xml2Bean exception");
        }
    }

    /**
     * 封装message对象为xml
     *
     * @param message 需要封装的对象
     * @See Message
     */
    public String Bean2Xml(Message message) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            TextMessage newMessage = new TextMessage();
//            newMessage.setFromUserName(message.getToUserName());
//            newMessage.setToUserName("oxF9Y6CD0X_jjAO7X8o-mfrcTI4I");
//            newMessage.setMsgType("text");
//            newMessage.setCreateTime("1630397150");
//            newMessage.setContent("哈哈哈哈");
            Document document = BeanXmlUtils.xmlSetField(message);
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            XMLOutputter xmlout = new XMLOutputter(format);
            xmlout.output(document, outputStream);
            return outputStream.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Bean2Xml exception");
        }
    }

}
