package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.store.StoreRiderRel;

import java.util.List;

public interface RiderService {
    /**
     * <p> selectLoginRider
     *
     * @return
     */
    public String selectLoginRider(Rider rider);

    /**
     * <p> selectRiderTokenCheck
     *
     * @return
     */
    public int selectRiderTokenCheck(Rider rider);

    /**
     * <p> insertRiderSession
     *
     * @return
     */
    public int insertRiderSession(Rider rider);

    /**
     * <p> selectRiderInfo
     *
     * @return
     */
    public List<Rider> getRiderInfo(Rider rider) throws AppTrException;

    /**
     * <p> selectStoreRiders
     *
     * @return
     */
    public List<Rider> getStoreRiders(User user) throws AppTrException;

    /**
     * rider 정보 수정
     *
     * @param rider
     * @return
     */
    public int updateRiderInfo(Rider rider) throws AppTrException;

    /**
     * rider 출/퇴근
     * @param rider
     * @return
     */
    public int updateWorkingRider(Rider rider) throws AppTrException ;

}
