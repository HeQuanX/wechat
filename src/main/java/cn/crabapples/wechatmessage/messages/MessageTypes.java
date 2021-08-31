package cn.crabapples.wechatmessage.messages;

import cn.crabapples.wechatmessage.ApplicationException;
import cn.crabapples.wechatmessage.messages.pull.*;

public enum MessageTypes {
    TEXT("text", TextMessage.class),
    IMAGE("image", ImageMessage.class),
    VOICE("voice", VoiceMessage.class),
    VIDEO("video", VideoMessage.class),
    SHORT_VIDEO("shortvideo", ShortVideoMessage.class),
    LOCATION("location", LocationMessage.class),
    LINK("link", LinkMessage.class),
    EVENT("event", EventMessage.class);
    String type;
    Class<? extends Message> instance;

    MessageTypes(String type, Class<? extends Message> instance) {
        this.type = type;
        this.instance = instance;
    }

    public static Message getInstanceByType(String type) {
        for (MessageTypes messageTypes : MessageTypes.values()) {
            try {
                if (messageTypes.type.equals(type)) {
                    return messageTypes.instance.newInstance();
                }
            } catch (Exception e) {
                throw new ApplicationException("get instance fail");
            }
        }
        throw new ApplicationException("not found type:" + type);
    }
}
