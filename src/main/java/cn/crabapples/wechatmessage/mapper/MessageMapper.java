package cn.crabapples.wechatmessage.mapper;

import cn.crabapples.wechatmessage.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    int saveMessage(Message message);
}
