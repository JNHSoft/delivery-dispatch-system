/**
 * FCM 구조체
 * https://firebase.google.com/docs/cloud-messaging/http-server-ref?hl=ko#notification-payload-support
 * */
package kr.co.cntt.core.model.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmBody implements Dto {
    private String to;                          // 단일 대상인 경우
    private List<String> registration_ids;      // 여러 대상인 경우
    private String condition;                   // 발송 대상 조건
    private String collapse_key;                //
    private String priority;                    // 메시지 우선 순위 Android = normal or high / iOS = 5 or 10
    private Boolean content_available;          // true : APN 서버로 전송 iOS
    private Boolean mutable_content;            //
    private Integer time_to_live;               // 메세지 주기
    private String restricted_package_name;     // 안드로이드 전용
    private Boolean dry_run = false;            // 테스트

    private Object data;                                            // 선택 데이터 옵션
    private FcmNotification notification = new FcmNotification();   // Notices 내용

    // 다른 버전용
    private Object android;                     // 안드로이드 객체에서 사용될 것 미 사용
    private Object webpush;                     // webpush로 사용될 객체 미 사용
    private Object apns;                        // iOS 에서 사용될 객체 미 사용
    private Object fcm_options;                 // FCM 옵션 미 사용

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
