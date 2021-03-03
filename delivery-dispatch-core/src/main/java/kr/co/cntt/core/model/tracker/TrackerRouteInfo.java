/**
 * 2021-03-04
 * 라이더(트래커)의 최적의 경로 정보 제공을 위한 내용
 * */
package kr.co.cntt.core.model.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackerRouteInfo extends Common implements Dto {
    private Integer routeRank;          // 경로 순서
    private Integer routeType;          // 경로에 대한 종류 0 = 픽업장소 , 1 = 목적지

    private String name;                // 경로 요청자의 이름

    private String routeId;             // 경로에 대한 ID
    private String routeName;           // 경로에 대한 이름 (픽업장소 = 매장명, 목적지 = X)

    private String address;             // 경로에 대한 주소
    private String latitude;            // 경로에 대한 위치 (위도)
    private String longitude;           // 경로에 대한 위차 (경도)

    private String distance;            // 이전 경로로부터 현재 경로간의 거리 단위 : M

    private String reservationDatetime; // 고객이 요청한 도착 예정 시간

    // 브랜드 관리
    private String brandCode;           // 매장에 대한 브랜드코드
    private String brandName;           // 매장에 대한 브랜드명칭
}
