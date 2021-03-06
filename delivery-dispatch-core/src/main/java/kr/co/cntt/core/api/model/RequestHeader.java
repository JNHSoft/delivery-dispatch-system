package kr.co.cntt.core.api.model;

import lombok.Getter;
import lombok.Setter;

/**
 * App Api전용 request header
 *
 * @author su
 */
@Getter
@Setter
public class RequestHeader {
    private String date;
    private String token;
    private String level;
    //private String serviceKey;
}
