package kr.co.cntt.core.model.store;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRiderRel implements Dto{
    private static final long serialVersionUID = -4427066318008803379L;
    private String createdDatetime;
    private String modifiedDatetime;
    private String id;
    private String adminId;
    private String riderId;
    private String storeId;
}
