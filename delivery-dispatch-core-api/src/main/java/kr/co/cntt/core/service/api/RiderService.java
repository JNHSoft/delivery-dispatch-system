package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.rider.Rider;
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
    public List<Rider> getStoreRiders(StoreRiderRel storeRiderRel) throws AppTrException;
}
