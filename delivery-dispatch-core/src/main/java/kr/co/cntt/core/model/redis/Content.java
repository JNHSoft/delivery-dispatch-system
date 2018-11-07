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

    // 라이더 로케이션 정보 업데이트를 위해 추가
    private String latitude;
    private String longitude;
}