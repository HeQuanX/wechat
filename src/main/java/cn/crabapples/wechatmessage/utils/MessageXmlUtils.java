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
 * TODO Message2XmlUtils
 *
 * @author Mr.He
 * 2019/7/3 23:24
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name 29404
 */
public class MessageXmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(MessageXmlUtils.class);

    /**
     * 字符串首字母转大写
     */
    public static String firstChar2UpCase(CharSequence charts) {
        logger.debug("将[{}]的首字母转换为大写", charts);
        char[] nameChars = charts.toString().toCharArray();
        nameChars[0] -= 32;
        return String.valueOf(nameChars);
    }

    /**
     * 反射获取对象及其所继承的所有的父类的属性
     *
     * @param obj 需要获取属性的对象
     * @return 获取到的属性
     */
    public static Field[] getAllFields(Object obj) {
        logger.debug("获取[{}]及其所继承的所有的父类的属性", obj);
        Class<?> clazz = obj instanceof Class ? (Class<?>) obj : obj.getClass();
        Class<?> superClass = clazz.getSuperclass();
        Field[] superFields = superClass == null ? null : getAllFields(superClass);
        Field[] fields = clazz.getDeclaredFields();
        if (null == superFields) {
            return fields;
        }
        ArrayList<Field> list = new ArrayList<>(Arrays.asList(superFields));
        list.addAll(Arrays.asList(fields));
        Field[] allFields = new Field[superFields.length + fields.length];
        return list.toArray(allFields);
    }

    /**
     * 将传入的xml文档根据 MsgType 转换为对应的message对象
     *
     * @param document xml文档
     * @return message对象
     */
    public static Message beanSetField(Document document) {
        logger.info("开始将Xml转换为Message对象");
        Element root = document.getRootElement();
        String msgType = root.getChild("MsgType").getValue();
        Message message = MessageTypes.getInstanceByType(msgType);
        logger.info("消息类型:[{}],对象签名:[{}]", msgType, message.getClass());
        Field[] allFields = getAllFields(message);
        try {
            for (Field field : allFields) {
                String key = firstChar2UpCase(field.getName());
                String value = root.getChild(key).getValue();
                logger.debug("当前属性key:[{}],当前属性value:[{}]", key, value);
                field.setAccessible(true);
                field.set(message, value);
            }
        } catch (IllegalAccessException e) {
            logger.error("Xml转换为Message对象时出现异常:\n[{}]", e.getMessage(), e);
        }
        logger.info("Xml转换为Message对象完成");
        return message;
    }

    /**
     * 将传入的Message对象转换成xml文档
     *
     * @param message message对象
     * @param <T>     继承自Message
     * @return xml文档
     */
    public static <T extends Message> Document xmlSetField(T message) {
        logger.info("开始将Message转换为Xml对象");
        Field[] allFields = getAllFields(message);
        Document document = new Document();
        Element root = new Element("xml");
        logger.debug("创建Xml根节点完成");
        document.addContent(root);
        try {
            for (Field field : allFields) {
                field.setAccessible(true);
                String key = field.getName();
                Object value = field.get(message);
                logger.info("当前xml节点名称:[{}],当前xml节点值:[{}]", key, value);
                Element child = new Element(firstChar2UpCase(key));
                child.addContent(new CDATA(value.toString()));
                root.addContent(child);
            }
        } catch (IllegalAccessException e) {
            logger.error("Message转换为Xml时出现异常:\n[{}]", e.getMessage(), e);
        }
        logger.info("Message转换为Xml完成");
        return document;
    }

    public static String delXmlVersionInfo(String source) {
        int start = source.contains("<xml>") ? source.indexOf("<xml>") : 0;
        int end = source.lastIndexOf("</xml>") + 6;
        System.out.println(start + "-" + end + "-" + source.length());
        return source.substring(start, end);
    }

    public static void main(String[] args) {
        String str1 = "<xml>\n" +
                "  <ToUserName><![CDATA[oxF9Y6CD0X_jjAO7X8o-mfrcTI4I]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[gh_58a7a8173b10]]></FromUserName>\n" +
                "  <CreateTime><![CDATA[1630467490]]></CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[哈哈哈哈]]></Content>\n" +
                "</xml>";
        String str2 = "c57d162519324302  \u0001\u0010<xml><ToUserName><![CDATA[gh_58a7a8173b10]]></ToUserName>\n" +
                "<FromUserName><![CDATA[oxF9Y6CD0X_jjAO7X8o-mfrcTI4I]]></FromUserName>\n" +
                "<CreateTime>1630477138</CreateTime>\n" +
                "<MsgType><![CDATA[text]]></MsgType>\n" +
                "<Content><![CDATA[1]]></Content>\n" +
                "<MsgId>23342817066145230</MsgId>\n" +
                "</xml>wx5e4bd217fdb0c9d4";
        System.err.println(delXmlRootNode(str1));
        System.err.println(delXmlRootNode(str2));
    }

    public static String delXmlRootNode(String source) {
        String xml = delXmlVersionInfo(source);
        int start = xml.contains("<xml>") ? xml.indexOf("<xml>") + 5 : 0;
        int end = xml.lastIndexOf("</xml>") != -1 ? xml.lastIndexOf("</xml>") : xml.length();
        System.out.println(start + "-" + end + "-" + source.length());
        return xml.substring(start, end);
    }


}
