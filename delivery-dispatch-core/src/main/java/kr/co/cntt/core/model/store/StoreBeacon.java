package kr.co.cntt.core.model.store;

/**
 * 2022-01-23
 * 스토어가 가지고 있는 Beacon에 대한 정보
 * */

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreBeacon extends User implements Dto {

    //private String uuid;                // Beacon의 UUID
    private Integer major;              // Beacon의 major
    private Integer minor;              // Beacon의 minor
    //private Integer rssi;              // Beacon의 Px Power, 시그널 강도
}
