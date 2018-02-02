package kr.co.cntt.core.model.notice;


import kr.co.cntt.core.model.Dto;
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
    private String writerId;
    private String writerName;
    private String writerType;
    private String adminId;
    private String title;
    private String content;
    private String toRider;
    private String toStore;
    private String deleted;
}
