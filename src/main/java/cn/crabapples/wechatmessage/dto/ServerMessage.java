package cn.crabapples.wechatmessage.dto;

public class ServerMessage {
    @Override
    public String toString() {
        return "ServerMessage{" +
                "signature='" + signature + '\'' +
                ", echostr='" + echostr + '\'' +
                ", timestamp=" + timestamp +
                ", nonce='" + nonce + '\'' +
                '}';
    }

    private String signature;
    private String echostr;
    private Long timestamp;
    private String nonce;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEchostr() {
        return echostr;
    }

    public void setEchostr(String echostr) {
        this.echostr = echostr;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
