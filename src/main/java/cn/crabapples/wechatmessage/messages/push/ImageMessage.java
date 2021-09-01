package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class ImageMessage extends BaseMessage {
    private String picUrl;
    private Image image;

    static class Image {
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
