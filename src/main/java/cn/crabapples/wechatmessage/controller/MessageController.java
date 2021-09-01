package cn.crabapples.wechatmessage.controller;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.SignMessage;
import cn.crabapples.wechatmessage.service.MessageService;
import cn.crabapples.wechatmessage.service.impl.MessageServiceImpl;
import cn.crabapples.wechatmessage.utils.AesUtils;
import cn.crabapples.wechatmessage.utils.MessageXmlUtils;
import cn.crabapples.wechatmessage.utils.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageService messageService;
    private final boolean IS_ENCRYPT = false;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("/server")
    public Object server(SignMessage sign, @Nullable @RequestBody String xmlData) {
        logger.info("收到请求->\n签名信息->\n[{}],\n数据信息:\n[{}]", sign, xmlData);
        boolean isWx = messageService.checkServer(sign);
        logger.info("检测消息签名是否为微信服务器:[{}]", isWx);
        if (isWx) {
            if (null != xmlData) {
                // 将[收到的][xmlString]转换成[EncryptMessage]对象
                EncryptMessage pullEncMessage = messageService.xml2EncBean(xmlData);
                // 对[收到的][EncryptMessage]对象解密
                String xml = messageService.decodeXml(pullEncMessage);
                // 将[收到的][xmlString]转换成message对象(未加密)
                Message message = messageService.xml2Bean(xml);
                // todo 生成测试回复
                Message target = MessageService.demo((BaseMessage) message);
                // 将[回复的]message对象转换为[xmlString]
                String resultXmlString = messageService.bean2Xml(target);
                if (IS_ENCRYPT) {
                    /*
                     * todo 返回数据加密后无法解析
                     * 可能是因为加密填充方式的原因，暂时没有解决方案
                     */
                    resultXmlString = MessageXmlUtils.delXmlRootNode(resultXmlString);
                    // 对[回复的][xmlString]加密
                    String encryptString = messageService.encodeXml(resultXmlString);
                    encryptString = new String(Base64.getDecoder().decode(encryptString.getBytes(StandardCharsets.UTF_8)));
                    // 将[回复的][encryptString]转换成[EncryptMessage]对象
                    EncryptMessage pushEncMessage = messageService.string2ResultEncBean((BaseMessage) target, encryptString);
                    logger.info("测试解密->\n[{}]", messageService.decodeXml(pushEncMessage));
                    String resultXml = messageService.bean2Xml(pushEncMessage);
                    logger.info("返回结果->加密前:\n[{}]\n加密后:[{}]", resultXmlString, resultXml);
                    return resultXml;
                }
                return resultXmlString;
            } else {
                return sign.getEchostr();
            }
        }
        return "false";

    }

}
