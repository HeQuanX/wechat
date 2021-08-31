package cn.crabapples.wechatmessage.utils;

import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.MessageTypes;
import cn.crabapples.wechatmessage.messages.pull.TextMessage;
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
    private static final Charset CHARSET = StandardCharsets.UTF_8;


    public static String firstChar2UpCase(CharSequence charts) {
        char[] nameChars = charts.toString().toCharArray();
        nameChars[0] -= 32;
        return String.valueOf(nameChars);
    }

    public static void main(String[] args) {
//        final Class<? super TextMessage> aClass = TextMessage.class.getSuperclass();
//        System.out.println(aClass);
//        final Class<? super TextMessage> aClass1 = aClass.getSuperclass();
//        System.out.println(aClass1);
//        final Class<? super TextMessage> aClass2 = aClass1.getSuperclass();
//        System.out.println(aClass2);
//        final Class<? super TextMessage> aClass3 = aClass2.getSuperclass();
//        System.out.println(aClass3);
        final Field[] allFields = getAllFields(new TextMessage());
        for (Field field : allFields) {
            System.out.println(field.getName());
        }
    }

    public static Field[] getAllFields(Object obj) {
        Object superClass = obj.getClass().getSuperclass();
        Field[] parentFields = superClass == null ? null : getAllFields(superClass);
        Field[] fields = obj.getClass().getDeclaredFields();
        if (null == parentFields) {
            return fields;
        }
        ArrayList<Field> list = new ArrayList<>(Arrays.asList(parentFields));
        list.addAll(Arrays.asList(fields));
        Field[] allFields = new Field[parentFields.length + fields.length];
        return list.toArray(allFields);
    }

    public static Message beanSetField(Document document) {
        Element root = document.getRootElement();
        String MsgType = root.getChild("MsgType").getValue();
        Message message = MessageTypes.getInstanceByType(MsgType);
        Field[] allFields = getAllFields(message);
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
        Field[] allFields = getAllFields(message);
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
