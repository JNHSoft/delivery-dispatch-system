package kr.co.cntt.core.model.admin;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin extends User implements Dto {
    private static final long serialVersionUID = 7451424064388009284L;

    private String chatUserId;
    private String state;
}
