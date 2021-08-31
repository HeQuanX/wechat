package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.Message;

public class VoiceMessage extends Message {
    private String picUrl;
    private Voice voice;

    static class Voice {
        private String mediaId;
    }
}
