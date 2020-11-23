package kr.co.cntt.core.model.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmCommon implements Dto {
    private String title;           // Title
    private String body;            // 본문 내용
    private String sound = "default";           // 소리
    private String click_action;    // 클릭 이벤트

    private String body_loc_key;    // APN = loc-key
    private String body_loc_args;   // Json 형태의 배열 스트링
    private String title_loc_key;   //
    private String title_loc_args;  // Json 형태의 배열 스트링
}
