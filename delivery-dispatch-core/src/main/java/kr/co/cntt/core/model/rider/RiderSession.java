package kr.co.cntt.core.model.rider;

import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RiderSession extends Common implements Dto {

    private static final long serialVersionUID = 8414160608713552339L;

    private String expiryDatetime;
    private String os;
    private String pushToken;
    private String device;
    private String rider_id;

}
