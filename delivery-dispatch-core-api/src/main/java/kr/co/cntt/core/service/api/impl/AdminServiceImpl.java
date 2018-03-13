package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.AdminService;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.util.Geocoder;
import kr.co.cntt.core.util.Misc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("adminService")
public class AdminServiceImpl extends ServiceSupport implements AdminService {

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * Order DAO
     */
    private OrderMapper orderMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * @param adminMapper USER D A O
     */
    @Autowired
    public AdminServiceImpl(AdminMapper adminMapper, StoreMapper storeMapper, OrderMapper orderMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
        this.orderMapper = orderMapper;
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
    public int postGroup(Group group) {
        int result = adminMapper.insertGroup(group);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int putGroup(Group group) {
        int result = adminMapper.updateGroup(group);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteGroup(Group group) {
        int result = adminMapper.deleteGroup(group);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

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
    public int postSubgroup(SubGroup subGroup) {
        int result = adminMapper.insertSubGroup(subGroup);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int putSubgroup(SubGroup subGroup) {
        int result = adminMapper.updateSubGroup(subGroup);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteSubgroup(SubGroup subGroup) {
        int result = adminMapper.deleteSubGroup(subGroup);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

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
    public int postSubgroupStoreRel(Store store) {
        int result = adminMapper.insertSubGroupStoreRel(store);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int putSubgroupStoreRel(Store store) {
        int result = adminMapper.updateSubGroupStoreRel(store);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteSubgroupStoreRel(SubGroupStoreRel subGroupStoreRel) {
        int result = adminMapper.deleteSubGroupStoreRel(subGroupStoreRel);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Rider> getRiders(Common common) throws AppTrException {

        List<Rider> S_Rider = adminMapper.selectRiders(common);

        if (S_Rider.size() ==0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00006), ErrorCodeEnum.E00006.name());
        }

        return S_Rider;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int postRider(Rider rider) {

        rider.setType("3");
        adminMapper.insertChatUser(rider);
//        adminMapper.insertChatRoom(rider);

        int result = 0;
        if (rider.getChatUserId() != null/* && rider.getChatRoomId() != null*/) {
//            adminMapper.insertChatUserChatRoomRel(rider);

            if (rider.getSubGroupStoreRel() != null) {
                result = adminMapper.insertRider(rider);

                if (result != 0) {
                    redisService.setPublisher("rider_info_updated", "");
                }

                return adminMapper.insertSubGroupRiderRel(rider);
            } else {
                result = adminMapper.insertRider(rider);

                if (result != 0) {
                    redisService.setPublisher("rider_info_updated", "");
                }

                return result;
            }
        } else {
            // TODO : chatUser or chatRoom deleted
            return 0;
        }
    }

    @Secured("ROLE_ADMIN")
    @Override
    public int deleteRider(Common common){
        int result = adminMapper.deleteRider(common);

        if (result != 0) {
            redisService.setPublisher("rider_info_updated", "");
        }

        return result;
    }

    @Secured("ROLE_ADMIN")
    @Override
    public List<Store> getStores(Common common) throws AppTrException {

        List<Store> S_Store = adminMapper.selectStores(common);

        if (S_Store.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00005), ErrorCodeEnum.E00005.name());
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

        store.setType("2");

        adminMapper.insertChatUser(store);
        adminMapper.insertChatRoom(store);
        if (store.getChatUserId() != null && store.getChatRoomId() != null) {
            adminMapper.insertChatUserChatRoomRel(store);

            if (store.getGroup() != null && store.getSubGroup() != null) {
                adminMapper.insertStore(store);
                adminMapper.insertSubGroupStoreRel(store);

                Misc misc = new Misc();
                Map<String, Integer> storeHaversineMap = new HashMap<>();
                List<Store> S_Store = storeMapper.selectSubGroupStores(store);

                for (Store s : S_Store) {
                    if (store.getId().equals(s.getId())) {
                        continue;
                    }

                    try {
                        storeHaversineMap.put(s.getId(), misc.getHaversine(store.getLatitude(), store.getLongitude(), s.getLatitude(), s.getLongitude()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                store.setStoreDistanceSort(StringUtils.arrayToDelimitedString(misc.sortByValue(storeHaversineMap).toArray(), "|"));

                return storeMapper.updateStoreInfo(store);
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

    //배정 모드 추가
    @Secured("ROLE_ADMIN")
    @Override
    public int putAdminAssignmentStatus(Admin admin){
        int result = adminMapper.updateAdminAssignmentStatus(admin);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    //배정 서드파티 추가
    @Secured("ROLE_ADMIN")
    @Override
    public int postThirdParty(ThirdParty thirdParty){
        int result = adminMapper.insertThirdParty(thirdParty);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    //배정 서드파티 수정
    @Secured("ROLE_ADMIN")
    @Override
    public int putThirdParty(ThirdParty thirdParty){
        int result = adminMapper.updateThirdParty(thirdParty);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    //배정 서드파티 삭제
    @Secured("ROLE_ADMIN")
    @Override
    public int deleteThirdParty(ThirdParty thirdParty){
        int result = adminMapper.deleteThirdParty(thirdParty);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    //알림음 추가
    @Secured("ROLE_ADMIN")
    @Override
    public int postAlarm(Alarm alarm){
        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmSS");
        String[] tmp = alarm.getOriFileName().split("\\.");
        alarm.setFileName(RandomStringUtils.randomAlphanumeric(16) + "_" + LocalDateTime.now().format(dateformatter) + "." + tmp[1]);

        int result = adminMapper.insertAlarm(alarm);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    //알림음 삭제
    @Secured("ROLE_ADMIN")
    @Override
    public int deleteAlarm(Alarm alarm){
        int result = adminMapper.deleteAlarm(alarm);

        if (result != 0) {
            redisService.setPublisher("config_updated", "");
        }

        return result;
    }

    // 통계 목록(list)
    @Secured("ROLE_ADMIN")
    @Override
    public List<Order> getAdminStatistics(Order order) throws AppTrException {

        List<Order> A_Statistics = adminMapper.selectAdminStatistics(order);

        if (A_Statistics.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }

        return A_Statistics;

    }

    // 통계 조회
    @Secured("ROLE_ADMIN")
    @Override
    public Order getAdminStatisticsInfo(Order order) throws AppTrException {

        Order A_Order = adminMapper.selectAdminStatisticsInfo(order);

        if (A_Order == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.A0011), ErrorCodeEnum.A0011.name());
        }


        Misc misc = new Misc();

        if (A_Order.getLatitude() != null && A_Order.getLongitude() != null) {


            Order orderInfo = orderMapper.selectOrderLocation(A_Order.getId());

            try {
                A_Order.setDistance(Double.toString(misc.getHaversine(orderInfo.getLatitude(), orderInfo.getLongitude(), orderInfo.getLatitude(), orderInfo.getLongitude()) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return A_Order;
    }


}
