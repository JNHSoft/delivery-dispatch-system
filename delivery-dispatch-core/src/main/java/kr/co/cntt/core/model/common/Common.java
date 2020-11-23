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
}
