package cn.crabapples.wechatmessage.messages.push;

import cn.crabapples.wechatmessage.messages.Message;

public class ImageMessage extends Message {
    private String picUrl;
    private Image image;

    static class Image {
        private String mediaId;
    }
}
