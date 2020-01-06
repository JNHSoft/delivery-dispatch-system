package kr.co.cntt.core.model.rider;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class RiderAssistant {
    private int ast_admin_id;
    private int ast_group_id;
    private int ast_subgroup_id;
    private int ast_store_id;
    private int ast_flag;
    private int ast_sort;

    private  Rider selectSubgroupRiderRelsResult;
}
