package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.notice.Notice;

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
     * 공지사항 상세보기
     * @param notice
     * @return
     */
    public Notice detailNotice(Notice notice);
}
