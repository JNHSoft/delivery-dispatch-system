package kr.co.cntt.core.model.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmNotification extends FcmCommon implements Dto {
    // iOS 용
    private String subtitle;        // 알림 부제목

    // android 용
    private String android_channel_id;  //
    private String icon;                // 알림 아이콘
    private String tag;                 //
    private String color;               // 알림 아이콘 색상
}
