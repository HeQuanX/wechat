package cn.crabapples.wechatmessage.messages;


import com.alibaba.fastjson.JSONObject;

public abstract class BaseMessage extends Message {
    private String fromUserName;
    private String createTime;
    private String msgType;


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

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
