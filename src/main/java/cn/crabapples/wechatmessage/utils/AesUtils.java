package cn.crabapples.wechatmessage.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;


/**
 * TODO AES工具类
 *
 * @author Mr.He
 * 2019/7/3 23:24
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name 29404
 */
public class AesUtils {
    private static final Logger logger = LoggerFactory.getLogger(AesUtils.class);
    private static final String aesKey = "FspdP9MZmDHwYVgkvi1IrotwDAYYiiCUFamMiVWMgrY";

    public static void main(String[] args) throws Exception {
        String str = "LRp1/tVnlsT7N14OY/7XPHp6xxurfawG2bdAf6JRJgkJqKlH+3oL2Q+82M0IUc06aV4LNCY4qMqjoKJkehcfol+pKFuBk1rpnaZ2msn6AJxyvPQHcjJPUW2g83E152caxEO6KUm5k0PdWjhk26ofGXEcAUbXt6D6PZAVZKsVPaa7NpIUBvHmT1TghmbIQmKvY43vYgQjyWkI0vOjLU6arvpBzphdDfvrYp8xX3LnDuEh81ezJQxe/635/piFTWQErjeNtQObKJkkpbI234S+X0UweY2VKau3Dv2u22NM7tstD5oBgIO9/wgr22TKoWll2oAQqNmExOsiS5310cL7hWBWxCV8nauPco5bllFeSIU=";
        String str2 = "<xml>123456789</xml>";
        final String str3 = doFinal(aesKey, str2, 1);
        doFinal(aesKey, str3, 2);
    }

    public static String doFinal(String aesKeyString, String source, int type) throws Exception {
        byte[] aesKey = Base64.getDecoder().decode(aesKeyString + "=");
        IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
        Security.addProvider(new BouncyCastleProvider());
        if (type == Cipher.ENCRYPT_MODE) {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] dataByte = cipher.doFinal(source.getBytes());
            byte[] encodeByte = Base64.getEncoder().encode(dataByte);
            new String(Base64.getEncoder().encode(encodeByte));
            String data = new String(Base64.getEncoder().encode(encodeByte));
            logger.info("加密之后的数据:\n[{}]", data);
            return data;
        } else if (type == Cipher.DECRYPT_MODE) {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] decodeByte = Base64.getDecoder().decode(source);
            byte[] dataByte = cipher.doFinal(decodeByte);
            String data = new String(dataByte);
            data = MessageXmlUtils.delXmlVersionInfo(data);
            logger.info("解密之后的数据:\n[{}]", data);
            return data;
        } else {
            throw new RuntimeException("please input type");
        }
    }
}
