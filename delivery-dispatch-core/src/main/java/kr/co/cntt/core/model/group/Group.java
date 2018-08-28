package kr.co.cntt.core.model.group;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Group extends Common implements Dto {

    private static final long serialVersionUID = -3244230391088948168L;

    private String name;
    private String adminId;

    private String subGroupCount;
    private String subGroupStoreCount;

}
