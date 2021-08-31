package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.Message;

public class VideoMessage extends Message {
    private String picUrl;
    private Video video;

    static class Video {
        private String mediaId;
        private String title;
        private String description;
    }
}
