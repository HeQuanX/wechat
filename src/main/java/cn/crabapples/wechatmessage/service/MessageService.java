package cn.crabapples.wechatmessage.service;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.MessageTypes;
import cn.crabapples.wechatmessage.messages.Messages;
import cn.crabapples.wechatmessage.utils.AesUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
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

    public Object xml2Bean(String xmlString) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            Document document = saxBuilder.build(inputStream);
            Element root = document.getRootElement();
            String MsgType = root.getChild("MsgType").getValue();
            Messages messages = MessageTypes.getInstanceByType(MsgType);
            final Field[] fields = messages.getClass().getDeclaredFields();
            for (Field field : fields) {
                System.out.println(field.getName());
            }
            messages = setParentField(messages, root);
            System.out.println(messages);
//            String encrypt = root.getChild("Encrypt").getValue();
//            EncryptMessage encryptMessage = new EncryptMessage();
//            encryptMessage.setToUserName(toUserName);
//            encryptMessage.setEncrypt(encrypt);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T extends Messages> Messages setParentField(T message, Element root) {
        Field[] parentFields = message.getClass().getSuperclass().getDeclaredFields();
        Field[] fields = message.getClass().getDeclaredFields();
        ArrayList<Field> list = new ArrayList<>(Arrays.asList(parentFields));
        list.addAll(Arrays.asList(fields));
        Field[] allFields = new Field[parentFields.length + fields.length];
        allFields = list.toArray(allFields);
        try {
            for (Field field : allFields) {
                String name = field.getName();
                char[] nameChars = name.toCharArray();
                nameChars[0] -= 32;
                String key = String.valueOf(nameChars);
                String value = root.getChild(key).getValue();
                field.setAccessible(true);
                field.set(message, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return message;
    }

}
