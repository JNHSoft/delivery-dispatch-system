package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.store.StoreRiderRel;

import java.util.List;

public interface AdminService {

    /**
     * <p> selectLoginAdmin
     *
     * @return
     */
    public String selectLoginAdmin(Admin admin);

    /**
     * <p> selectAdminTokenCheck
     *
     * @return
     */
    public int selectAdminTokenCheck(Admin admin);

    /**
     * <p> insertAdminSession
     *
     * @return
     */
    public int insertAdminSession(Admin admin);

    /**
     * <p> getAdminInfo
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Admin> getAdminInfo(Common common) throws AppTrException;

    /**
     * <p> getRiders
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Rider> getRiders(Common common) throws AppTrException;

    /**
     * <p> postRider
     *
     * @param rider
     * @return
     */
    public int postRider(Rider rider);

    /**
     * Delete Rider
     * @param rider
     * @return
     */
    public int deleteRider(Rider rider);


    /**
     * <p> getStores
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Store> getStores(Common common) throws AppTrException;

    /**
     * <p> postStore
     *
     * @param store
     * @return
     */
    public int postStore(Store store);

    /**
     *  Delete Store
     * @param store
     * @return
     */
    public int deleteStore(Store store);


    /**
     * <p> getStoreRiderRel
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<StoreRiderRel> getStoreRiderRel(Common common) throws AppTrException;

    /**
     * <p> putStoreRiderRel
     *
     * @param storeRiderRel
     * @return
     */
    public int putStoreRiderRel(StoreRiderRel storeRiderRel);

}
