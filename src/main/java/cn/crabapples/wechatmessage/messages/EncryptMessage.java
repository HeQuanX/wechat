package cn.crabapples.wechatmessage.messages;

public class EncryptMessage extends Message{
    private String toUserName;
    private String encrypt;

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }
}
