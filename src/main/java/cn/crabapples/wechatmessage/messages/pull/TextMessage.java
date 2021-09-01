package cn.crabapples.wechatmessage.messages.pull;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class TextMessage extends BaseMessage {
    private String content;
    private String msgId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
