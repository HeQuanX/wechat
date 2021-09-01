package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class MusicMessage extends BaseMessage {
    private String picUrl;
    private Music music;

    static class Music {
        private String title;
        private String description;
        private String musicUrl;
        private String hQMusicUrl;
        private String thumbMediaId;

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

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public String gethQMusicUrl() {
            return hQMusicUrl;
        }

        public void sethQMusicUrl(String hQMusicUrl) {
            this.hQMusicUrl = hQMusicUrl;
        }

        public String getThumbMediaId() {
            return thumbMediaId;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
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

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
