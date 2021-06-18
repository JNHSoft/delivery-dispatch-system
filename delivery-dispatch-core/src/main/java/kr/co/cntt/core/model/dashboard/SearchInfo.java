package kr.co.cntt.core.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 대시보드에서 사용되는 검색 조건에 대한 Object
 * */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class SearchInfo extends Common implements Dto {
    private String groupId;             // RC ID
    private String subgroupId;          // AC ID
    private String storeId;             // STORE ID
    private String sDate;               // Search Start Date
    private String eDate;               // Search End Date

    public SearchInfo deapCopy(){
        SearchInfo search = new SearchInfo();

        search.setGroupId(this.getGroupId());
        search.setSubgroupId(this.getSubgroupId());
        search.setStoreId(this.getStoreId());
        search.setSDate(this.getSDate());
        search.setEDate(this.getEDate());
        search.setPeakType(this.getPeakType());
        search.setToken(this.getToken());

        return search;
    }
}
