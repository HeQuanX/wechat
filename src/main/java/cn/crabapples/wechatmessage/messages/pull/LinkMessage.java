package cn.crabapples.wechatmessage.messages.pull;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class LinkMessage extends BaseMessage {
    private String title;
    private String description;
    private String url;
    private String msgId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
