package cn.crabapples.wechatmessage.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SignUtils {
    private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);

    public static String sort(String... content) {
        Arrays.sort(content);
        StringBuilder text = new StringBuilder();
        for (String s : content) {
            text.append(s);
        }
        return text.toString();
    }

}
