package kr.co.cntt.core.model.notice;


import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notice extends User implements Dto {

    private static final long serialVersionUID = -4336674568434203200L;

    private String createdDatetime;
    private String modifiedDatetime;
    private String id;

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

}
