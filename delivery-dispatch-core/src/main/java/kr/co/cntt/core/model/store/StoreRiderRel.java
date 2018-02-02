package kr.co.cntt.core.model.store;

import kr.co.cntt.core.model.Common;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRiderRel extends Common implements Dto {

    private static final long serialVersionUID = -4427066318008803379L;

    private String adminId;
    private String riderId;
    private String riderName;
    private String riderPhone;
    private String storeId;
    private String storeName;

}
