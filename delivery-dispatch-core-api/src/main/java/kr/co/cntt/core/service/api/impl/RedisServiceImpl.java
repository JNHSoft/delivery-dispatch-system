package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.model.redis.Message;
import kr.co.cntt.core.redis.service.Publisher;
import kr.co.cntt.core.service.api.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService{

    /**
     * Redis Publisher
     */
    @Autowired
    private Publisher publisher;

    @Override
    public void setPublisher(String type, String msg) {
        Message message = new Message();
        message.setType("pubsub:web-notification");
        if (msg != "") {
            message.setContent("push_data:{type:"+type+", "+msg+"}");
        } else {
            message.setContent("push_data:{type:"+type+"}");
        }


        publisher.publish(message);
    }
}
