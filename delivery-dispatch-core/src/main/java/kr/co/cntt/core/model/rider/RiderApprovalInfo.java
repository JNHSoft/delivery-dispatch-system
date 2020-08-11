package kr.co.cntt.core.model.rider;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
//@ToString
public class RiderApprovalInfo extends Rider implements Dto {

    private String approvalStatus;
    private String riderId;

    private Rider riderDetail;
    private RiderSession session;
}
