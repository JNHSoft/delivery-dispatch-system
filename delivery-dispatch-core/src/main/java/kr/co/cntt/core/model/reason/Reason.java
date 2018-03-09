package kr.co.cntt.core.model.reason;


import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reason extends Common implements Dto {
    private static final long serialVersionUID = -3716426996620732995L;

    private String created_datetime;
    private String modified_datetime;
    private String reason;
}
