package kr.co.cntt.core.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
