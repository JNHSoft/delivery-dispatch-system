package kr.co.cntt.core.model.rider;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiderActiveInfo  implements Dto {
    private Integer orderCount;         // 라이더가 처리한 전체 주문 수
    private String avgD7Times;         // 라이더의 D7 평균
    private String avgD7;               // 라이더의 D7 비율
    private Integer avgD30Times;        // 라이더의 D30 평균
    private String avgD30;              // 라이더의 D30 비율
    private Double tpoh;                // 라이더가 시간 당 처리한 주문 수(완료)
}
