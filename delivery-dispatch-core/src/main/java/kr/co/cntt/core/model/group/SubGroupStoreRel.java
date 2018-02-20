package kr.co.cntt.core.model.group;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubGroupStoreRel extends SubGroup implements Dto {

    private static final long serialVersionUID = -6622817529449090579L;

    private String subGroupId;
    private String storeId;

    private String storeName;
    private String chStoreName;

}
