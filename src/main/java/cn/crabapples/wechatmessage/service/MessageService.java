package cn.crabapples.wechatmessage.service;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.pull.MessageTypes;
import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.push.TextMessage;
import cn.crabapples.wechatmessage.utils.AesUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class MessageService {
    @Value("${token}")
    private String token;
    @Value("${aesKey}")
    private String aesKey;


    public boolean checkServer(BaseMessage message) {
        try {
            Long timestamp = message.getTimestamp();
            String nonce = message.getNonce();
            // 获取指定摘要算法的messageDigest对象
            MessageDigest messageDigest = MessageDigest.getInstance("SHA"); // 此处的sha代表sha1
            // 调用digest方法，进行加密操作
            String content = sort(token, timestamp.toString(), nonce);
            byte[] cipherBytes = messageDigest.digest(content.getBytes());
            String result = HexUtils.toHexString(cipherBytes);
            return result.equals(message.getSignature());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String sort(String... content) {
        Arrays.sort(content);
        StringBuilder text = new StringBuilder();
        for (String s : content) {
            text.append(s);
        }
        return text.toString();
    }

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
        }
        return null;
    }

    public String decodeXml(EncryptMessage message) {
        try {
            String encrypt = message.getEncrypt();
            return AesUtils.doFinal(aesKey, encrypt, Cipher.DECRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String encodeXml(String message) {
        try {
            return AesUtils.doFinal(aesKey, message, Cipher.ENCRYPT_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Message xml2Bean(String xmlString) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            Document document = saxBuilder.build(inputStream);
            return beanSetField(document);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("xml 2 bean fail");
        }
    }

    public String Bean2Xml(Message message) {
        try {
            TextMessage newMessage = new TextMessage();
            newMessage.setFromUserName(message.getToUserName());
            newMessage.setToUserName(message.getFromUserName());
            newMessage.setMsgType(message.getMsgType());
            newMessage.setCreateTime(message.getCreateTime());
            newMessage.setContent("哈哈哈哈");

            final Document document = xmlSetField(newMessage);
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            XMLOutputter xmlout = new XMLOutputter(format);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            xmlout.output(document, outputStream);
            String xmlString = outputStream.toString();
            outputStream.close();
            System.err.println(xmlString);
            return xmlString;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bean 2 xml fail");
        }
    }

    private String firstChar2UpCase(CharSequence charts) {
        char[] nameChars = charts.toString().toCharArray();
        nameChars[0] -= 32;
        return String.valueOf(nameChars);
    }

    private Message beanSetField(Document document) {
        Element root = document.getRootElement();
        String MsgType = root.getChild("MsgType").getValue();
        Message message = MessageTypes.getInstanceByType(MsgType);
        Field[] parentFields = message.getClass().getSuperclass().getDeclaredFields();
        Field[] fields = message.getClass().getDeclaredFields();
        ArrayList<Field> list = new ArrayList<>(Arrays.asList(parentFields));
        list.addAll(Arrays.asList(fields));
        Field[] allFields = new Field[parentFields.length + fields.length];
        allFields = list.toArray(allFields);
        try {
            for (Field field : allFields) {
                String key = firstChar2UpCase(field.getName());
                String value = root.getChild(key).getValue();
                field.setAccessible(true);
                field.set(message, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return message;
    }

    private <T extends Message> Document xmlSetField(T message) {
        Field[] parentFields = message.getClass().getSuperclass().getDeclaredFields();
        Field[] fields = message.getClass().getDeclaredFields();
        ArrayList<Field> list = new ArrayList<>(Arrays.asList(parentFields));
        list.addAll(Arrays.asList(fields));
        Field[] allFields = new Field[parentFields.length + fields.length];
        allFields = list.toArray(allFields);
        Document document = new Document();
        Element root = new Element("xml");
        document.addContent(root);

        try {
            for (Field field : allFields) {
                field.setAccessible(true);
                String key = field.getName();
                final Object value = field.get(message);
                Element child = new Element(firstChar2UpCase(key));
                child.addContent(new CDATA(value.toString()));
                root.addContent(child);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return document;
    }

}
