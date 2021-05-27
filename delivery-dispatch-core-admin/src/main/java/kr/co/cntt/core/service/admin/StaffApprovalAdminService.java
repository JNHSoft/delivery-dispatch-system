package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StaffApprovalAdminService {
    /** 라이더 승인 리스트 가져오기 */
    public List<RiderApprovalInfo> getRiderApprovalList(Common common);

    /** 라이더 개별 정보 */
    public RiderApprovalInfo getRiderApprovalInfo(Common common);

    /** 라이더 정보 삭제 */
    int deleteRiderInfo(Rider rider);

    /**
     * <p> setRiderInfo
     * 라이더 승인과 관련한 정보 변경 건
     * */
    int setRiderInfo(RiderApprovalInfo riderInfo);

    /**
     * <p> update ExpiryDatetime for Rider Session
     * */
    int updateRiderSession(RiderSession session);

    String selectApprovalRiderPw(String id);

    Store selectStoreInfo(Store store);

    int insertChatUser(Rider rider);

    int insertRider(Rider rider);

    int insertSubGroupRiderRel(Rider rider);

    int insertAdminRiderSession(Rider rider);

    int updateRiderInfo(Rider rider);

    // 라이더 Approval Row Data 삭제
    int deleteApprovalRiderRowData(RiderApprovalInfo riderInfo);

    // 라이더 비밀번호 초기화
    int resetRiderPassword(Rider rider);

    // 라이더 정보 가져오기
    Rider getRiderInfo(Common common);

    // 라이더를 빌려줄 매장의 정보 가져오기
    List<Store> getSharedStoreList(Rider rider);

    /**
     * 21.05.21 라이더가 속해질 타 매장의 정보 저장
     * */
    int regSharedStoreInfo(Rider rider);

    /**
     * 21.05.21 라이더가 소속되어 있던 타 매장 정보 삭제
     * */
    int deleteSharedStoreInfo(Rider rider);
}
