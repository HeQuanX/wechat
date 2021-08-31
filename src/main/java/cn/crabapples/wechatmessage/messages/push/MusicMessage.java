package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.Message;

public class MusicMessage extends Message {
    private String picUrl;
    private Music music;

    static class Music {
        private String title;
        private String description;
        private String musicUrl;
        private String hQMusicUrl;
        private String thumbMediaId;
    }
}
