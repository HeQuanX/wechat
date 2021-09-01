package cn.crabapples.wechatmessage.messages.pull;

import cn.crabapples.wechatmessage.messages.BaseMessage;
import com.alibaba.fastjson.JSONObject;

public class EventMessage extends BaseMessage {
    private String event;
    private String eventKey;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
