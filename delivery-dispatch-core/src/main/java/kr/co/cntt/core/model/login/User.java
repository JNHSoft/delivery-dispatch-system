package kr.co.cntt.core.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends Common implements Dto {
    private static final long serialVersionUID = 6248879064571878900L;

    private String lastAccess;
    private String chatUserId;
    private String chatRoomId;
    private String loginId;
    private String loginPw;
    private String name;
    private String accessToken;

    private String pushToken;
    private String level;

    // 사용자 브랜드 관리
    private String brandCode;
    private String brandName;

    // 브랜드 이미지 경로
    private String brandImg;

}