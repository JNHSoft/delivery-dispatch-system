package kr.co.cntt.core.model.redis;

import lombok.*;

@Builder
@Getter
@Setter
public class Content {

    private String id;
    private String type;
    private String storeId;
    private String riderId;
    private String adminId;
    private String orderId;
    private String subGroupId;
    private String recvChatUserId;

}