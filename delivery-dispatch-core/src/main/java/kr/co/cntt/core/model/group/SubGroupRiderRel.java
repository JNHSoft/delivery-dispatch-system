package kr.co.cntt.core.model.group;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubGroupRiderRel extends SubGroup implements Dto {


    private static final long serialVersionUID = 8678353531739536561L;

    private String subGroupId;
    private String riderId;

}
