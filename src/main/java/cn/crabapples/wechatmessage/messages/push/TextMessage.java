package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class TextMessage extends BaseMessage {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
