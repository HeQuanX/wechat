package cn.crabapples.wechatmessage.dto;

public enum MessageTypes {
    TEXT("text", TextMessages.class),
    IMAGE("image", TextMessages.class),
    VOICE("voice", TextMessages.class),
    VIDEO("video", TextMessages.class),
    SHORT_VIDEO("shortvideo", TextMessages.class),
    LOCATION("location", TextMessages.class),
    LINK("link", TextMessages.class);
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
