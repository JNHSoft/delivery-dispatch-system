package kr.co.cntt.core.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ChartInfo extends Common implements Dto {
    // 차트가 그려져야되는 종류 0=막대 그래프 # 1=꺽쇠 그래프
    private int chartType;
    // X좌표의 최소값 (가로)
    private String minX;
    // X좌표의 최대값 (가로)
    private String maxX;
    // X좌표에 표시될 문자열 개수
    private float intervalX;
    // Y좌표의 최소 값 (세로)
    private float minY;
    // Y좌표의 최대 값 (세로)
    private float maxY;
    // Y좌표에 표시될 문자열 개수
    private float intervalY;

    // 상세 페이지의 내용을 출력한다.
    private List<DashboardInfo> detail;
}
