package kr.co.cntt.core.model.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONPropertyName;

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

    // 2022-01-17 라이더 앱에서 위치 변경을 호출할 간격 확인 (단위 : 초)
    private Integer locationRefreshTime;

    // 2022-01-25 비콘의 TxPower(RSSI) 기본 세팅 값
    private Integer rssi;

    // 2022-01-25 비콘 주기 (단위 : 초)
    private Integer beaconCycle;

    // 2022-02-11 Beacon UUID
    @JsonProperty(defaultValue = "uuid")
    private String uuid;
}