package kr.co.cntt.core.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class DashboardInfo extends Common implements Dto {
    private String dashBoardType;               // 대시보드에 대한 내용 정의
    private Float mainValue;                    // 대시보드에 표기 되는 메인 데이터
    private String unit;                        // 메인 Value에 대한 단위 표시
    private Long avgValue;                      // 평균이 되는 시간
    private Float variation;                    // 직전 대비 증감률
}
