package kr.co.cntt.core.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Common implements Dto {

    private static final long serialVersionUID = -9062580392729458975L;

    private String createdDatetime;
    private String modifiedDatetime;
    private String deleted;
    private String id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String token;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String role;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String currentDatetime;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String days;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String lang;
    // 20.10.07 app 사용 구분
    private String appType;
    // 사용자가 사용하는 OS 정보
    private String platform;
    // 20.12.24 데이터 조회 시, 날짜 형태 구분
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean chkTime;
    // 20.12.24 데이터 조회 시, 피크 타임 구분
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean chkPeakTime;
    // 21.01.06 피크 타임 조회 종류 (0=전체, 1=점심, 2=저녁
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String peakType;

    // 20.12.30 마지막 주문으로 부터 경과된 시간
    private String orderDiff;
}
