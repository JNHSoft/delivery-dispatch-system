package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StoreRiderService {
    /**
     * <p> getStoreInfo
     *
     * @param store
     * @return
     */
    Store getStoreInfo(Store store);

    /**
     * <p> getRiderNow
     *
     * @param common
     * @return List<Rider>
     */
    List<Rider> getRiderNow(Common common);

    /**
     * <p> getRiderFooter
     *
     * @param common
     * @return List<Rider>
     */
    List<Rider> getRiderFooter(Common common);


    /**
     * <p> putRiderReturnTime
     *
     * @param rider
     * @return int
     */
    int putRiderReturnTime(Rider rider);

    /**
     * <p> getChat
     *
     * @param chat
     * @return List<Chat>
     */
    List<Chat> getChat(Chat chat);

    /**
     * <p> postChat
     *
     * @param chat
     * @return int
     */
    int postChat(Chat chat);

    /**
     * <p> getRiderApprovalList
     *
     * */
    List<RiderApprovalInfo> getRiderApprovalList(Common common);

    /**
     * <p> getRegistRiderInfoList </p>
     *
     * */
    List<Rider> getRegistRiderInfoList(User user);

    /**
     * <p> getRiderApprovalInfo
     * */
    RiderApprovalInfo getRiderApprovalInfo(Common common);

    /**
     * <p> setRiderInfo
     * 라이더 승인과 관련한 정보 변경 건
     * */
    int setRiderInfo(RiderApprovalInfo riderInfo);

    /**
     * <p> update ExpiryDatetime for Rider Session
     * */
    int updateRiderSession(RiderSession session);

    /**
     * deleteApprovalRiderRowData
     * */
    int deleteApprovalRiderRowData(RiderApprovalInfo riderInfo);

    /**
     * 라이더 패스워드 초기화
     * */
    int resetRiderPassword(Rider rider);
    
    /**
     * 21.05.20 소속된 관리자가 관리하는 스토어 현황 가져오기
     * */
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
