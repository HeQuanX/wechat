package cn.crabapples.wechatmessage.messages;

import com.alibaba.fastjson.JSONObject;

public class EncryptMessage extends Message{
    private String encrypt;


    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
