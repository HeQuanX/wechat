package cn.crabapples.wechatmessage.messages;

public enum MessageTypes {
    TEXT("text", TextMessages.class),
    IMAGE("image", ImageMessages.class),
    VOICE("voice", VoiceMessages.class),
    VIDEO("video", VideoMessages.class),
    SHORT_VIDEO("shortvideo", ShortVideoMessages.class),
    LOCATION("location", LocationMessages.class),
    LINK("link", LinkMessages.class);
    String type;
    Class<? extends Messages> instance;

    MessageTypes(String type, Class<? extends Messages> instance) {
        this.type = type;
        this.instance = instance;
    }

    public static Messages getInstanceByType(String type) {
        for (MessageTypes messageTypes : MessageTypes.values()) {
            try {
                if (messageTypes.type.equals(type)) {
                    return (Messages) messageTypes.instance.newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException("get instance fail");
            }
        }
        throw new RuntimeException("not found type:" + type);
    }
}
