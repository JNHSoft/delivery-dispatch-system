package kr.co.cntt.core.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class TimeSectionInfo implements Dto {
    private String adminId;
    private String adminName;
    private String groupId;
    private String groupName;
    private String subGroupId;
    private String subGroupName;
    private String storeId;
    private String storeName;
    private Integer orderCount;
    private Integer D30;            /// <=30
    private Integer D40;            /// BETWEEN 31 ~ 40
    private Integer D50;            /// BETWEEN 41 ~ 50
    private Integer D60;            /// BETWEEN 51 ~ 60
    private Integer OverTime;       /// > 60
}
