package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.login.User;
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
     * @param admin
     * @return
     * @throws AppTrException
     */
    public List<Admin> getAdminInfo(Admin admin) throws AppTrException;

    /**
     * <p> getRiders
     *
     * @param admin
     * @return
     * @throws AppTrException
     */
    public List<Rider> getRiders(Admin admin) throws AppTrException;

    /**
     * <p> postRider
     *
     * @param rider
     * @return
     */
    public int postRider(Rider rider);

    /**
     * <p> getStores
     *
     * @param user
     * @return
     * @throws AppTrException
     */
    public List<Store> getStores(User user) throws AppTrException;

    /**
     * <p> postStore
     *
     * @param store
     * @return
     */
    public int postStore(Store store);


    /**
     * <p> getStoreRiderRel
     *
     * @param user
     * @return
     * @throws AppTrException
     */
    public List<StoreRiderRel> getStoreRiderRel(User user) throws AppTrException;

}
