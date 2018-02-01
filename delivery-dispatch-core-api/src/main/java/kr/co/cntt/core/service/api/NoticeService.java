package kr.co.cntt.core.service.api;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.notice.Notice;

public interface NoticeService {
    /**
     * 공지사항 등록
     * @param notice
     * @author Nick
     * @return
     */
    public int postNotice(Notice notice);

    /**
     * 공지사항 수정
     * @param notice
     * @return
     */
    public int updateNotice(Notice notice);

    /**
     * 공지사항 삭제
     * @param notice
     * @return
     */
    public int deleteNotice(Notice notice) throws AppTrException;

    /**
     * 공지사항 상세 보기 진행중
     * @param notice
     * @return
     */
    public Notice detailNotice(Notice notice) throws AppTrException;
}
