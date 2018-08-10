package kr.co.cntt.core.model.redis;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Message {

    private Content content;
    private String type;

}