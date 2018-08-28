package kr.co.cntt.core.model.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCheckAssignment extends Common implements Dto {

    private static final long serialVersionUID = 3196247714328684624L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String adminId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String storeId;
    private String riderId;
    private String orderId;
    private String confirmedDatetime;
    private String deniedDatetime;
    private String status;
    private String reason;

}
