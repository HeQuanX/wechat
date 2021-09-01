package cn.crabapples.wechatmessage.messages.pull;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class ImageMessage extends BaseMessage {
    private String picUrl;
    private String mediaId;
    private String msgId;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
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
