package kr.co.cntt.core.model.statistic;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ByDate implements Dto {

    private static final long serialVersionUID = -1934384042972192641L;

    private String storeName;
    private String dayToDay;
    private String orderPickup;             // 평균 (tc)
    private String pickupComplete;          // 평균 (tc)
    private String stayTime;                // 평균 (tc)
    private String orderComplete;           // 평균 (tc)
    private String completeReturn;          // 평균 (tc)
    private String pickupReturn;            // 평균 (tc)
    private String orderReturn;             // 평균 (tc)
    private String minD7Below;              // 달성률 (tc) -> KFC  전용 d7_success 개수 / tc 개수
    private String min30Below;              // 달성률 (tc)
    private String min30To40;               // 달성률 (tc)
    private String min40To50;               // 달성률 (tc)
    private String min50To60;               // 달성률 (tc)
    private String min60To90;               // 달성률 (tc)
    private String min90Under;              // 달성률 (tc)
    private String errtc;                   // 개수 / Average는 errTC가 있는 매장 개수 만큼 나누기
    private String thirdtc;                 // 개수 / Average는 개수가 있는 매장 수만큼 나누기
    private String tc;                      // 개수 / Average는 개수가 있는 매장 수만큼 나누기
    private String totalSales;              // 금액 / Average는 객단가로 표현하자
    private String tplh;                    // 개수 나누기 시간 실제 값은 tc/tplh를 통해 tplh를 만든다
    private String spmh;                    // 금액 나누기 시간 실제 값은 totalSales / spmh를 통해 spmh를 만든다
    private String hours;                   // 시간에 대한 숫자 (초 / 3600)
    private String totalPickupReturn;       // 전체 소요 시간
    private String avgDistance;             // 평균

    private String d7Success;               // 개수
}
