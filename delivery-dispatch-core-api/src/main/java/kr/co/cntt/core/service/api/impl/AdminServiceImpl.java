package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.login.User;
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

import java.util.ArrayList;
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
    public List<Admin> getAdminInfo(Admin admin) throws AppTrException {
        admin.setAccessToken(admin.getToken());

        List<Admin> S_Admin = adminMapper.selectAdminInfo(admin);

        if (S_Admin.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return S_Admin;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Rider> getRiders(Admin admin) throws AppTrException {
        admin.setAccessToken(admin.getToken());

        List<Rider> S_Rider = adminMapper.selectRiders(admin);

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
            return adminMapper.insertRider(rider);
        } else {
            // TODO : chatUser or chatRoom deleted
            return 0;
        }
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Store> getStores(User user) throws AppTrException {
        List<Store> S_Store = adminMapper.selectStores(user);

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
            return adminMapper.insertStore(store);
        } else {
            // TODO : chatUser or chatRoom deleted
            return 0;
        }
    }

}
