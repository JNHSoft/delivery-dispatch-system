package kr.co.cntt.core.model.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedRiderInfo {
    // PK Value
    private int seq;

    // Target Info
    private int adminid;
    private Integer groupid;
    private Integer subgroupid;
    private Integer storeid;

    // Shared Target Into
    private Integer shared_adminid;
    private Integer shared_groupid;
    private Integer shared_subgroupid;
    private Integer shared_storeid;


    private Integer use_flag;
    private Integer index;

    private String created;
    private String modified;
    private String deleted;
}
