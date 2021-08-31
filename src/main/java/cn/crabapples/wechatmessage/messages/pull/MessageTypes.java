package cn.crabapples.wechatmessage.messages.pull;

import cn.crabapples.wechatmessage.messages.Message;

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
                throw new RuntimeException("get instance fail");
            }
        }
        throw new RuntimeException("not found type:" + type);
    }
}
