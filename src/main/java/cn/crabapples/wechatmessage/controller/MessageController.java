package cn.crabapples.wechatmessage.controller;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.Message;
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
    public Object server(BaseMessage base, @Nullable @RequestBody String xmlData) {
        boolean isWx = messageService.checkServer(base);
        if (isWx) {
            if (null != xmlData) {
                EncryptMessage encryptMessage = messageService.xml2EncBean(xmlData);
                String xml = messageService.decodeXml(encryptMessage);
                Message message = messageService.xml2Bean(xml);
                String xmlString = messageService.Bean2Xml(message);
                String result = messageService.encodeXml(xmlString);
                System.err.println(result);
                return null;
//        decode(encryptMessage, message);
            } else {
                return base.getEchostr();
            }
        }
        return "false";

    }

}
