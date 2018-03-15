package kr.co.cntt.core.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Chat extends Common implements Dto {

    private static final long serialVersionUID = -9135643164318045316L;

    private String chatUserId;
    private String chatRoomId;
    private String message;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;
    private String lastMessage;

}
