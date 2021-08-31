package cn.crabapples.wechatmessage.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private String toUserName;

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }


}
