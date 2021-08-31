package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.Message;

import java.util.List;

public class NewsMessage extends Message {
    private String picUrl;
    private String articleCount;
    private List<Articles> articles;

    static class Articles {
        private String title;
        private String description;
        private String picUrl;
        private String url;
    }
}
