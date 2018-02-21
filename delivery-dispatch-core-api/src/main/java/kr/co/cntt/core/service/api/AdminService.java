package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;

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
     * <p> getGroups
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Group> getGroups(Common common) throws AppTrException;

    /**
     * <p> postGroup
     *
     * @param group
     * @return
     */
    public int postGroup(Group group);

    /**
     * <p> putGroup
     *
     * @param group
     * @return
     */
    public int putGroup(Group group);

    /**
     * <p> deleteGroup
     *
     * @param group
     * @return
     */
    public int deleteGroup(Group group);

    /**
     * <p> getSubgroups
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<SubGroup> getSubgroups(Common common) throws AppTrException;

    /**
     * <p> postSubgroup
     *
     * @param subGroup
     * @return
     */
    public int postSubgroup(SubGroup subGroup);

    /**
     * <p> putSubgroup
     *
     * @param subGroup
     * @return
     */
    public int putSubgroup(SubGroup subGroup);

    /**
     * <p> deleteSubgroup
     *
     * @param subGroup
     * @return
     */
    public int deleteSubgroup(SubGroup subGroup);

    /**
     * <p> getNoneSubgroupStoreRels
     *
     * @param subGroupStoreRel
     * @return
     * @throws AppTrException
     */
    public List<SubGroupStoreRel> getNoneSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel) throws AppTrException;

    /**
     * <p> getSubgroupStoreRels
     *
     * @param subGroupStoreRel
     * @return
     * @throws AppTrException
     */
    public List<SubGroupStoreRel> getSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel) throws AppTrException;

    /**
     * <p> postSubgroupStoreRel
     * @param store
     * @return
     */
    public int postSubgroupStoreRel(Store store);

    /**
     * <p> putSubgroupStoreRel
     *
     * @param store
     * @return
     */
    public int putSubgroupStoreRel(Store store);

    /**
     * <p> deleteSubgroupStoreRel
     *
     * @param subGroupStoreRel
     * @return
     */
    public int deleteSubgroupStoreRel(SubGroupStoreRel subGroupStoreRel);

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
     * <p> deleteRider
     *
     * @param common
     * @return
     */
    public int deleteRider(Common common);

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
     *  <p> deleteStore
     *
     * @param common
     * @return
     */
    public int deleteStore(Common common);

}
