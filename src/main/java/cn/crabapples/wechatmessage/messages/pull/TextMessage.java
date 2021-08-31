package cn.crabapples.wechatmessage.messages.pull;

import cn.crabapples.wechatmessage.messages.Message;

public class TextMessage extends Message {
    private String content;
    private String msgId;
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
