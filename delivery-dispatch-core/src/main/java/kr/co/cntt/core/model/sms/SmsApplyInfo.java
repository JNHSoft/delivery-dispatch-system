package kr.co.cntt.core.model.sms;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsApplyInfo extends User implements Dto {
    private String phone;
    private String applyNo;
    private String applyStatus;
    private String applyDatetime;
}
