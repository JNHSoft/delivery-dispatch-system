package kr.co.cntt.core.model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("store")
public class Store extends User implements Dto {
    private static final long serialVersionUID = 6587372870369862831L;

    private String expirationDate;
    private String adminId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    private String code;
    private String phone;
    private String storeName;
    private String chStoreName;
    private String storePhone;
    private String address;
    private String detailAddress;
    private String chAddress;
    private String chDetailAddress;
    private String latitude;
    private String longitude;
    private String storeStatus;
    private String radius;
    private String storeDistanceSort;
    private String assignmentStatus;
    private String assignmentLimit;
    private String thirdParty;

    private Group group;
    private SubGroup subGroup;
    private SubGroupStoreRel subGroupStoreRel;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String isAdmin;

}

