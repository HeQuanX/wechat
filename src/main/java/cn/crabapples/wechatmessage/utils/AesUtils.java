package cn.crabapples.wechatmessage.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * TODO AES加密字符串
 *
 * @author Mr.He
 * 2019/7/3 23:24
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name 29404
 */
public class AesUtils {
    private static final Logger logger = LoggerFactory.getLogger(AesUtils.class);

    public static String doFinal(String aesKeyString, String source, int type) throws Exception {
        byte[] aesKey = Base64.getDecoder().decode(aesKeyString + "=");
        IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        if (type == Cipher.ENCRYPT_MODE) {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] dataByte = cipher.doFinal(source.getBytes());
            byte[] encodeByte = Base64.getEncoder().encode(dataByte);
            String data = parseByte2HexStr(encodeByte);
            logger.info("加密之后的数据:[{}]", data);
            return data;
        } else if (type == Cipher.DECRYPT_MODE) {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] decodeByte = Base64.getDecoder().decode(source);
            byte[] dataByte = cipher.doFinal(decodeByte);
            String data = new String(dataByte);
            data = data.substring(data.indexOf("<xml>"), data.lastIndexOf(">") + 1);
            logger.info("解密之后的数据:[{}]", data);
            return data;
        } else {
            throw new RuntimeException("please input type");
        }
    }


    /**
     * 用于将密钥种子转换为KEY
     *
     * @param seed 密钥种子
     * @return 密钥
     * @throws Exception 生成密钥可能出现的异常
     */
    private static Key createKey(String seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        /*
         * Linux下默认的算法是“NativePRNG”
         * windows下默认是“SHA1PRNG”（sun提供的算法）
         */
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed.getBytes());
        keyGenerator.init(128, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        logger.debug("生成的key为:[{}],种子为:[{}]", secretKey, seed);
        return secretKey;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param source 二进制数组
     * @return 16进制字符串
     */
    public static String parseByte2HexStr(byte[] source) {
        StringBuilder sb = new StringBuilder();
        for (byte b : source) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr 16进制字符串
     * @return 二进制数组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
