package kr.co.cntt.core.model.notice;


import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Notice extends User implements Dto {

    private static final long serialVersionUID = -4336674568434203200L;

    private String writerName;

    private String adminId;
    private String title;
    private String content;

    private String toGroupId;
    private String toSubGroupId;
    private String toStoreId;

    // 파일 업로드
    private String oriFileName;
    private String fileName;
    private String fileSize;

    private String deleted;

    private String confirmedDatetime;
    private String confirmedCount;
    private String toGroupCount;
    private String toSubgroupCount;
    private String toStoreCount;
    private String toAllCount;

    private String groupName;
    private String subgroupName;
    private String storeName;

}
