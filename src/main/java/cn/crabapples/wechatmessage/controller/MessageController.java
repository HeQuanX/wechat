package cn.crabapples.wechatmessage.controller;

import cn.crabapples.wechatmessage.dto.BaseMessage;
import cn.crabapples.wechatmessage.dto.EncryptMessage;
import cn.crabapples.wechatmessage.service.MessageService;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("/server")
    public Object server(BaseMessage message, @Nullable @RequestBody String xmlData) {
        boolean isWx = messageService.checkServer(message);
        if (isWx) {
            if (null != xmlData) {
                EncryptMessage encryptMessage = messageService.xml2EncBean(xmlData);
                String xml = messageService.decodeXml(encryptMessage);
                messageService.xml2Bean(xml);
                return null;

//        decode(encryptMessage, message);
            } else {
                return message.getEchostr();
            }
        }
        return "false";

    }

}
