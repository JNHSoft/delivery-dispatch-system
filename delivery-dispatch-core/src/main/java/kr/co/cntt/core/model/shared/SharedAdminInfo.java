package kr.co.cntt.core.model.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedAdminInfo {
    private int seq;

    private int adminID;
    private String adminName;

    private int allowAdminID;
    private String allowAdminName;
    private int allowFlag;
    private int allowSort;

    private Integer allowGroupID;
    private Integer allowSubGroupID;
    private Integer allowStoreID;

    private String created;
    private String modified;
    private String deleted;
}
