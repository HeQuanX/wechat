package cn.crabapples.wechatmessage.messages.push;


import cn.crabapples.wechatmessage.messages.BaseMessage;

public class VideoMessage extends BaseMessage {
    private String picUrl;
    private Video video;

    static class Video {
        private String mediaId;
        private String title;
        private String description;

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

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
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
