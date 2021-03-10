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
    public Store getStoreInfo(Store store);

    /**
     * <p> getRiderNow
     *
     * @param common
     * @return List<Rider>
     */
    public List<Rider> getRiderNow(Common common);

    /**
     * <p> getRiderFooter
     *
     * @param common
     * @return List<Rider>
     */
    public List<Rider> getRiderFooter(Common common);


    /**
     * <p> putRiderReturnTime
     *
     * @param rider
     * @return int
     */
    public int putRiderReturnTime(Rider rider);

    /**
     * <p> getChat
     *
     * @param chat
     * @return List<Chat>
     */
    public List<Chat> getChat(Chat chat);

    /**
     * <p> postChat
     *
     * @param chat
     * @return int
     */
    public int postChat(Chat chat);

    /**
     * <p> getRiderApprovalList
     *
     * */
    public List<RiderApprovalInfo> getRiderApprovalList(Common common);

    /**
     * <p> getRegistRiderInfoList </p>
     *
     * */
    List<Rider> getRegistRiderInfoList(User user);

    /**
     * <p> getRiderApprovalInfo
     * */
    public RiderApprovalInfo getRiderApprovalInfo(Common common);

    /**
     * <p> setRiderInfo
     * 라이더 승인과 관련한 정보 변경 건
     * */
    public int setRiderInfo(RiderApprovalInfo riderInfo);

    /**
     * <p> update ExpiryDatetime for Rider Session
     * */
    public int updateRiderSession(RiderSession session);

    /**
     * deleteApprovalRiderRowData
     * */
    int deleteApprovalRiderRowData(RiderApprovalInfo riderInfo);

    /**
     * 라이더 패스워드 초기화
     * */
    int resetRiderPassword(Rider rider);
}
