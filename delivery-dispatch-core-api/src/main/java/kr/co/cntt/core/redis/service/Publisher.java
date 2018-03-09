package kr.co.cntt.core.redis.service;

import kr.co.cntt.core.model.redis.Message;

public interface Publisher {

    public void publish(Message message);

}
