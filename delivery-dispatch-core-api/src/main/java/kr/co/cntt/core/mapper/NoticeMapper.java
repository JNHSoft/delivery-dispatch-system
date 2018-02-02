package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.notice.Notice;

import java.util.List;

/**
 * <per>
 * kr.co.cntt.core.mapper
 *    └─ NoticeMapper.java
 * </per>
 *  공지 사항 관련
 * @author Nick
 * @since  2018-01-30
 *
 */
@DeliveryDispatchMapper
public interface NoticeMapper {
    /**
     * 공지사항 등록
     * @param notice
     * @return
     */
    public int insertNotice(Notice notice);

    /**
     * AdminID 확인
     * @param notice
     * @return
     */
    public Notice selectAdminId(Notice notice);

    /**
     * StoreID 확인
     * @param notice
     * @return
     */
    public Notice selectStoreAdminId(Notice notice);

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
    public int deleteNotice(Notice notice);

    /**
     * 공지사항 상세보기 세분화 rider , admin ,store
     * @param notice
     * @return
     */
    // store Admin Detail
    public List<Notice> getAdminStoreDetailNoticeList(Notice notice);
    // store Detail
    public List<Notice> getStoreDetailNoticeList(Notice notice);

    // rider Admin Detail
    public List<Notice> getRiderAdminDetailNoticeList(Notice notice);
    // rider Store Detail
    public List<Notice> getRiderStoreDetailNoticeList(Notice notice);

    // Admin admin
    public List<Notice> getAdminDetailNoticeList(Notice notice);
    // Admin Store store
    public List<Notice> getStoreAdminDetailNoticeList(Notice notice);


    /**
     * 공지사항 리스트 세분화 Rider, Admin , Store
     * @param notice
     * @return
     */
    // store Admin
    public List<Notice> getStoreAdminNoticeList(Notice notice);
    // store
    public List<Notice> getStoreNoticeList(Notice notice);

    // rider 공지사항 Admin
    public List<Notice> getRiderAdminNoticeList(Notice notice);

    // rider 공지사항 Store
    public List<Notice> getRiderStoreNoticeList(Notice notice);

    // admin 공지사항 Admin
    public List<Notice> getAdminNoticeList(Notice notice);

}
