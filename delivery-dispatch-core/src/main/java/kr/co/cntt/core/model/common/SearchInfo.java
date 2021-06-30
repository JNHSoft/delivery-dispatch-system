package kr.co.cntt.core.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private String sDate;               // Search Start Date
    private String eDate;               // Search End Date
    private String orderStatus;         // 주문 상태를 가지고 있는 경우도 있어서

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

        return search;
    }
}
