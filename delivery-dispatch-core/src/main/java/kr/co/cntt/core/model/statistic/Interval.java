package kr.co.cntt.core.model.statistic;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Interval implements Dto {

    private static final long serialVersionUID = 678886833051774229L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Map> intervalMinute;

    private List<Object[]> intervalMinuteCounts;

}
