package cn.crabapples.wechatmessage.controller;

import cn.crabapples.wechatmessage.messages.SignMessage;
import cn.crabapples.wechatmessage.service.MessageService;
import cn.crabapples.wechatmessage.service.impl.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("/server")
    public Object server(SignMessage sign, @Nullable @RequestBody String xmlData) {
        logger.info("收到请求->\n签名信息->\n[{}],\n数据信息:\n[{}]", sign, xmlData);
        boolean isWx = messageService.checkServer(sign);
        logger.info("检测消息签名是否为微信服务器:[{}]", isWx);
        return isWx ? null != xmlData ? messageService.getResultMessage(xmlData) : sign.getEchostr() : false;
    }
}
