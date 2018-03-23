package kr.co.cntt.core.service.admin;


import kr.co.cntt.core.model.notice.Notice;

import java.util.List;

public interface NoticeAdminService {

    /**
     * <p> getNoticeList
     *
     * @param notice
     * @return Notice
     */
    public List<Notice> getNoticeList(Notice notice);

    /**
     * <p> getNotice
     *
     * @param notice
     * @return Notice
     */
    public Notice getNotice(Notice notice);

}
