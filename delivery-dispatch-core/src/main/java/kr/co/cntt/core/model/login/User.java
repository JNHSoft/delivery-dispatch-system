package kr.co.cntt.core.model.login;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Dto {
    private static final long serialVersionUID = 6248879064571878900L;

    private String id;
    private String chatUserId;
    private String chatRoomId;
    private String loginId;
    private String loginPw;
    private String name;
    private String accessToken;
    private String token;

}