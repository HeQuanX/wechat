package cn.crabapples.wechatmessage.messages;


public abstract class BaseMessage extends Message {
    private String toUserName;
    private String fromUserName;
    private String createTime;
    private String msgType;

    @Override
    public String getToUserName() {
        return toUserName;
    }

    @Override
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
