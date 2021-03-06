package kr.co.cntt.core.model.group;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubGroup extends Group implements Dto {

    private static final long serialVersionUID = 1867412011428091859L;

    private String groupId;

}
