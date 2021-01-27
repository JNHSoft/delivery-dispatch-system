package kr.co.cntt.core.model.group;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SubGroup extends Group implements Dto {

    private static final long serialVersionUID = 1867412011428091859L;

    private String groupId;
    // 21-01-21 그룹화 시 생성되는 이름
    private String groupingName;
}
