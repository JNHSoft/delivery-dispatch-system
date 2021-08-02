package kr.co.cntt.core.model.rider;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiderSharedInfo extends Common implements Dto {
    private int seq;
    private String riderId;
    private String reqUser;
    private Integer sharedType;
    private Integer beforeStatus;
    private Integer afterStatus;
}
