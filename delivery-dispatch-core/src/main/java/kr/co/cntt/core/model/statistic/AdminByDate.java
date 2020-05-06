package kr.co.cntt.core.model.statistic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminByDate extends ByDate {
    private String groupID;
    private String groupName;
    private String subGroupID;
    private String subGroupName;
}
