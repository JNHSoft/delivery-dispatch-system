package kr.co.cntt.core.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class CharInfo extends DashboardInfo implements Dto {
    // 차트가 그려져야되는 종류 0=막대 그래프 # 1=꺽쇠 그래프
    private int chartType;
    // X좌표의 최소값 (가로)
    private float minX;
    // X좌표의 최대값 (가로)
    private float maxX;
    // X좌표의 간격
    private float intervalX;
    // Y좌표의 최소 값 (세로)
    private float minY;
    // Y좌표의 최대 값 (세로)
    private float maxY;
    // Y좌표의 간격
    private float intervalY;
}
