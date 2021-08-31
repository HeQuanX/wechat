package cn.crabapples.wechatmessage.utils;

import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.MessageTypes;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO AES加密字符串
 *
 * @author Mr.He
 * 2019/7/3 23:24
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name 29404
 */
public class BeanXmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(BeanXmlUtils.class);
    private static final int BLOCK_SIZE = 32;
    private static final Charset CHARSET = StandardCharsets.UTF_8;



    static String firstChar2UpCase(CharSequence charts) {
        char[] nameChars = charts.toString().toCharArray();
        nameChars[0] -= 32;
        return String.valueOf(nameChars);
    }

    public static Message beanSetField(Document document) {
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

    public static <T extends Message> Document xmlSetField(T message) {
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
