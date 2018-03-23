package kr.co.cntt.core.model.payment;


import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment extends Common implements Dto {

    private static final long serialVersionUID = 8384493233610556562L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String adminId;
    private String storeId;
    private String orderId;
    private String type;
    private String status;
    private String cardNumber;
    private String approvalNumber;
    private String approvalDate;
    private String amount;
    private String acquirerCode;
    private String acquirerName;
    private String payType;

}
