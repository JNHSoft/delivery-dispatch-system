package kr.co.cntt.core.model.rider;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiderStatsInfo extends User implements Dto {
    // 라이더 사용이 허용된 날짜
    private String acceptDatetime;

    // 라이더의 유효기간
    private String expiryDatetime;

    // 라이더가 배달한 전체 주문 건수
    private Integer totalOrderCount;

    // 라이더가 소속된 매장의 배달 건수
    private Integer myStoreOrderCount;

    // 라이더가 공유된 매장의 배달 건수
    private Integer sharedStoreOrderCount;

    // 라이더의 현재 출근 상태
    private Integer currentWorkingStatus;

    // 라이더의 현재 공유 상태
    private Integer currentsharedStatus;

    // 매장 정보
    private Map orgStoreInfo;

    // 근무시간 리스트
    private List<Map> workingTimes;

    // 공유상태 리스트
    private List<Map> changeSharedStatusInfos;

    // 공유된 매장의 주문 정보
    private List<Map> sharedStoreOrderInfos;
}
