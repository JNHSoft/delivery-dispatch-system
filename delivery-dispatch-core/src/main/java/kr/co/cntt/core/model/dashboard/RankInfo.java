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
public class RankInfo extends Common implements Dto {
    private String storeName;               // 매장 이름
    private Float value;                     // 정상 주문 건수
    private Float achievementRate;            // 달성률
}
