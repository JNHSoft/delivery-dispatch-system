package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.AdminService;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.Misc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("adminService")
public class AdminServiceImpl extends ServiceSupport implements AdminService {

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * @param adminMapper USER D A O
     */
    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public String selectLoginAdmin(Admin admin) {
        return adminMapper.selectLoginAdmin(admin);
    }

    @Override
    public int selectAdminTokenCheck(Admin admin) {
        return adminMapper.selectAdminTokenCheck(admin);
    }

    @Override
    public int insertAdminSession(Admin admin) {
        return adminMapper.insertAdminSession(admin);
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Admin> getAdminInfo(Common common) throws AppTrException {

        List<Admin> S_Admin = adminMapper.selectAdminInfo(common);

        if (S_Admin.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.M0001), ErrorCodeEnum.M0001.name());
        }

        return S_Admin;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Group> getGroups(Common common) throws AppTrException {

        List<Group> S_Group = adminMapper.selectGroups(common);

        if (S_Group.size() ==0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00001), ErrorCodeEnum.E00001.name());
        }

        return S_Group;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int postGroup(Group group) { return adminMapper.insertGroup(group); }

    @Secured("ROLE_ADMIN")
    @Override
    public int putGroup(Group group) { return adminMapper.updateGroup(group); }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteGroup(Group group) { return adminMapper.deleteGroup(group); }

    @Secured("ROLE_ADMIN")
    @Override
    public List<SubGroup> getSubgroups(Common common) throws AppTrException {

        List<SubGroup> S_Group = adminMapper.selectSubGroups(common);

        if (S_Group.size() ==0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00002), ErrorCodeEnum.E00002.name());
        }

        return S_Group;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int postSubgroup(SubGroup subGroup) { return adminMapper.insertSubGroup(subGroup); }

    @Secured("ROLE_ADMIN")
    @Override
    public int putSubgroup(SubGroup subGroup) { return adminMapper.updateSubGroup(subGroup); }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteSubgroup(SubGroup subGroup) { return adminMapper.deleteSubGroup(subGroup); }

    @Secured("ROLE_ADMIN")
    @Override
    public List<SubGroupStoreRel> getNoneSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel) throws AppTrException {

        List<SubGroupStoreRel> S_Group = adminMapper.selectNoneSubgroupStoreRels(subGroupStoreRel);

        if (S_Group.size() ==0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00003), ErrorCodeEnum.E00003.name());
        }

        return S_Group;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<SubGroupStoreRel> getSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel) throws AppTrException {

        List<SubGroupStoreRel> S_Group = adminMapper.selectSubgroupStoreRels(subGroupStoreRel);

        if (S_Group.size() ==0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00004), ErrorCodeEnum.E00004.name());
        }

        return S_Group;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int postSubgroupStoreRel(Store store) { return adminMapper.insertSubGroupStoreRel(store); }

    @Secured("ROLE_ADMIN")
    @Override
    public int putSubgroupStoreRel(Store store) { return adminMapper.updateSubGroupStoreRel(store); }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteSubgroupStoreRel(SubGroupStoreRel subGroupStoreRel) { return adminMapper.deleteSubGroupStoreRel(subGroupStoreRel); }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Rider> getRiders(Common common) throws AppTrException {

        List<Rider> S_Rider = adminMapper.selectRiders(common);

        if (S_Rider.size() ==0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Rider;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int postRider(Rider rider) {

        rider.setType("3");
        adminMapper.insertChatUser(rider);
        adminMapper.insertChatRoom(rider);

        if (rider.getChatUserId() != null && rider.getChatRoomId() != null) {
            adminMapper.insertChatUserChatRoomRel(rider);

            if (rider.getSubGroupStoreRel() != null) {
                adminMapper.insertRider(rider);

                return adminMapper.insertSubGroupRiderRel(rider);
            } else {

                return adminMapper.insertRider(rider);
            }
        } else {
            // TODO : chatUser or chatRoom deleted
            return 0;
        }
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteRider(Common common){ return adminMapper.deleteRider(common); }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Store> getStores(Common common) throws AppTrException {

        List<Store> S_Store = adminMapper.selectStores(common);

        if (S_Store.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Store;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int postStore(Store store) {

        if ((store.getLatitude() == null || store.getLatitude() == "") || (store.getLongitude() == null || store.getLongitude() == "")) {
            Geocoder geocoder = new Geocoder();
            try {
                Map<String, String> geo = geocoder.getLatLng(store.getAddress());
                store.setLatitude(geo.get("lat"));
                store.setLongitude(geo.get("lng"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Misc misc = new Misc();
        Map<String, Integer> storeHaversineMap = new HashMap<>();
        List<Store> S_Store = adminMapper.selectStores(store);
        for (Store s : S_Store) {
            try {
                storeHaversineMap.put(s.getId(), misc.getHaversine(store.getLatitude(), store.getLongitude(), s.getLatitude(), s.getLongitude()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        store.setStoreDistanceSort(StringUtils.arrayToDelimitedString(misc.sortByValue(storeHaversineMap).toArray(), ","));
        store.setType("2");

        adminMapper.insertChatUser(store);
        adminMapper.insertChatRoom(store);
        if (store.getChatUserId() != null && store.getChatRoomId() != null) {
            adminMapper.insertChatUserChatRoomRel(store);

            if (store.getGroup() != null && store.getSubGroup() != null) {
                adminMapper.insertStore(store);
                return adminMapper.insertSubGroupStoreRel(store);
            } else {

                return adminMapper.insertStore(store);
            }
        } else {
            // TODO : chatUser or chatRoom deleted
            return 0;
        }
    }

    // 상점 삭제
    @Secured("ROLE_ADMIN")
    @Override
    public int deleteStore(Common common){ return adminMapper.deleteStore(common); }

}
