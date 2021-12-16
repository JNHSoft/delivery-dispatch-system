package kr.co.cntt.core.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 통계 및 DashBoard 등에서 사용되는 검색 조건 모음집
 * */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class SearchInfo extends Common implements Dto {
    private String groupId;             // RC ID
    private String subgroupId;          // AC Name
    private String storeId;             // STORE ID
    @JsonProperty(defaultValue = "sDate")
    private String sDate;               // Search Start Date
    @JsonProperty(defaultValue = "eDate")
    private String eDate;               // Search End Date
    private String orderStatus;         // 주문 상태를 가지고 있는 경우도 있어서

    // 라이더 쉐어의 검색 조건으로 인한 필드 추가
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> riderIds;      // 라이더 쉐어 히스토리를 구하기 위한 조건 API 전용

    // 대시보드에서 특정 조건은 브랜드별로 다르게 검출하기 위함
    private String brandCode;

    public SearchInfo deapCopy(){
        SearchInfo search = new SearchInfo();

        search.setGroupId(this.getGroupId());
        search.setSubgroupId(this.getSubgroupId());
        search.setStoreId(this.getStoreId());
        search.setSDate(this.getSDate());
        search.setEDate(this.getEDate());
        search.setPeakType(this.getPeakType());
        search.setToken(this.getToken());
        search.setRole(this.getRole());
        search.setDays(this.getDays());
        search.setOrderStatus(this.getOrderStatus());

        search.setBrandCode(this.getBrandCode());

        return search;
    }
}
