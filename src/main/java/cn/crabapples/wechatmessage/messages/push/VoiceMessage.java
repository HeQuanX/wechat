package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class VoiceMessage extends BaseMessage {
    private String picUrl;
    private Voice voice;

    static class Voice {
        private String mediaId;

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }
        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
