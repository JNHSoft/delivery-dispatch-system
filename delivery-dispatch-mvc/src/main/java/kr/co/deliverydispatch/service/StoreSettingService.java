package kr.co.deliverydispatch.service;

import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;

import java.util.List;

public interface StoreSettingService {
    /**
     * <p> getStoreInfo
     *
     * @param store
     * @return
     */
    public Store getStoreInfo(Store store);

    /**
     * Store 정보 수정
     *
     * @param store
     * @return
     */
    public int updateStoreInfo(Store store);

    /**
     * ThirdParty List
     *
     * @param thirdParty
     * @return
     */
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty);

    /**
     *  <p> putThirdParty
     *
     * @param store
     * @return
     */
    public int putStoreThirdParty(Store store);

    /**
     *  <p> getMyStoreRiderRels
     *
     * @param common
     * @return List<Rider>
     */
    public List<Rider> getMyStoreRiderRels(Common common);

    /**
     *  <p> getRiderInfo
     *
     * @param rider
     * @return Rider
     */
    public Rider getRiderInfo(Rider rider);

    /**
     *  <p> updateRiderInfo
     *
     * @param rider
     * @return
     */
    public int updateRiderInfo(Rider rider);

    /**
     *  <p> getAlarm
     *
     * @param store
     * @return List<Alarm>
     */
    public List<Alarm> getAlarm(Store store);

    /**
     *  <p> putStoreAlarm
     *
     * @param store
     * @return int
     */
    public int putStoreAlarm(Store store);

    /**
     * <p> 상점 - 공지사항 확인
     *
     * @param notice
     * @return int
     */
    public int putNoticeConfirm(Notice notice);

    /**
     * <p> getAdminInfo
     *
     * @param store
     * @return Admin
     */
    public Admin getAdminInfo(Store store);


}
