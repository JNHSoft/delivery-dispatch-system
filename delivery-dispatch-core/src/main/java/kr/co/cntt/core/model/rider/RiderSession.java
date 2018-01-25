package kr.co.cntt.core.model.rider;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Setter
@Getter
public class RiderSession implements Dto {

    private static final long serialVersionUID = 8414160608713552339L;

    private String createdDatetime;
    private String modifiedDatetime;
    private String accessToken;
    private String expiryDatetime;
    private String os;
    private String pushToken;
    private String device;
    private String id;
    private String rider_id;
}
