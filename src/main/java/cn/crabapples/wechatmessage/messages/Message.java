package cn.crabapples.wechatmessage.messages;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private String toUserName;

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
