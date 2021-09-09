package cn.crabapples.wechatmessage.service;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import cn.crabapples.wechatmessage.messages.SignMessage;
import cn.crabapples.wechatmessage.messages.Message;
import cn.crabapples.wechatmessage.messages.push.TextMessage;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    boolean checkServer(SignMessage message);

    default Message demo(BaseMessage source) {
        TextMessage target = new TextMessage();
        target.setFromUserName("gh_58a7a8173b10");
//        target.setToUserName(source.getFromUserName());
        target.setToUserName("oxF9Y6CD0X_jjAO7X8o-mfrcTI4I");
        target.setMsgType("text");
        target.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000));
        target.setContent("哈哈哈哈");
        return target;
    }

    String getResultMessage(String xmlData);
}
