package cn.crabapples.wechatmessage.service;

import cn.crabapples.wechatmessage.messages.EncryptMessage;
import cn.crabapples.wechatmessage.messages.SignMessage;
import cn.crabapples.wechatmessage.messages.Message;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    boolean checkServer(SignMessage message);

    EncryptMessage xml2EncBean(String xmlString);

    String decodeXml(EncryptMessage message);

    String encodeXml(String xmlString);

    Message xml2Bean(String xmlString);

    String Bean2Xml(Message message);

}
