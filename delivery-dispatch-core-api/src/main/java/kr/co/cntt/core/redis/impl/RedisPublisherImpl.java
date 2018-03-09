package kr.co.cntt.core.redis.impl;

import kr.co.cntt.core.model.redis.Message;
import kr.co.cntt.core.redis.service.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisPublisherImpl implements Publisher {
    @Autowired
    private RedisTemplate<String, Message> publishTemplate;


    @Override
    public void publish(Message message) {
        // TODO Auto-generated method stub
        publishTemplate.convertAndSend(message.getType(), message.getContent());
    }
}