package cn.crabapples.wechatmessage.controller;

import cn.crabapples.wechatmessage.dto.ServerMessage;
import cn.crabapples.wechatmessage.utils.AesUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class MessageController {
    @Value("${token}")
    private String token;

    @RequestMapping("/server")
    public Object server(HttpServletRequest request, ServerMessage message) {
        String echostr = message.getEchostr();
        Long timestamp = message.getTimestamp();
        String nonce = message.getNonce();
        String sign = Sha1Sign(sort(token, timestamp.toString(), nonce));
        xml2Bean(request);
        return sign.equals(message.getSignature()) ? echostr : "false";
    }

    private String Sha1Sign(String content) {
        try {
            // 获取指定摘要算法的messageDigest对象
            MessageDigest messageDigest = MessageDigest.getInstance("SHA"); // 此处的sha代表sha1
            // 调用digest方法，进行加密操作
            byte[] cipherBytes = messageDigest.digest(content.getBytes());
            return HexUtils.toHexString(cipherBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
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

    private void readInputStream(HttpServletRequest request) {
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            for (String line = ""; line != null; line = bufferedReader.readLine()) {
                System.err.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void xml2Bean(HttpServletRequest request) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(request.getInputStream());
            Element root = document.getRootElement();
            System.err.println(root.getChild("ToUserName").getValue());
            String encrypt = root.getChild("Encrypt").getValue();
            System.err.println(encrypt);
            String data = AesUtils.doFinal1("FspdP9MZmDHwYVgkvi1IrotwDAYYiiCUFamMiVWMgrY", encrypt, Cipher.DECRYPT_MODE);
            System.err.println(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}