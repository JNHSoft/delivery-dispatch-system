package kr.co.cntt.core.model.thirdParty;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThirdParty extends Common implements Dto {

    private static final long serialVersionUID = 5980328590169744479L;
    private String name;
    private String adminId;
    private String level;
}
