package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.*;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.util.Misc;
import kr.co.deliverydispatch.service.StoreSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("storeSettingServiceImpl")
public class StoreSettingServiceImpl extends ServiceSupport implements StoreSettingService {
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    /**
     * Notice DAO
     */
    private NoticeMapper noticeMapper;
    /**
     * @param storeMapper STORE D A O
     * @param riderMapper Rider D A O
     * @param noticeMapper Notice D A O
     */
    @Autowired
    public StoreSettingServiceImpl(StoreMapper storeMapper, RiderMapper riderMapper, NoticeMapper noticeMapper) {
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
        this.noticeMapper = noticeMapper;
    }

    @Override
    public Store getStoreInfo(Store store) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            store.setAccessToken(store.getToken());
            store.setRole("ROLE_STORE");
        }

        Store S_Store = storeMapper.selectStoreInfo(store);
        if (S_Store == null) {
            return null;
        }
        return S_Store;
    }

    @Override
    public int updateStoreInfo(Store store) {
        store.setAccessToken(store.getToken());

        Misc misc = new Misc();
        Map<String, Integer> storeHaversineMap = new HashMap<>();
        Store C_Store = storeMapper.selectStoreInfo(store);
        List<Store> S_Store = storeMapper.selectSubGroupStores(store);

//        for (Store cs : C_Store) {
        for (Store s : S_Store) {
            if (C_Store.getId().equals(s.getId())) {
                continue;
            }

            try {
                storeHaversineMap.put(s.getId(), misc.getHaversine(C_Store.getLatitude(), C_Store.getLongitude(), s.getLatitude(), s.getLongitude()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        }

        if (storeHaversineMap.isEmpty()) {
            store.setStoreDistanceSort("-1");
        } else {
            store.setStoreDistanceSort(StringUtils.arrayToDelimitedString(misc.sortByValue(storeHaversineMap).toArray(), "|"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            store.setId(null);
            store.setCode(null);
            store.setSubGroupStoreRel(null);
        }

        int result = storeMapper.updateStoreInfo(store);

        if (result != 0) {
//            redisService.setPublisher("store_info_updated", "id:"+store.getId()+", admin_id:"+C_Store.getAdminId());
        }

        return result;
    }

    @Override
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty){
        return storeMapper.selectThirdParty(thirdParty);
    }

    @Override
    public int putStoreThirdParty(Store store){

//        String[] tempStr = (store.getThirdParty()).split("(?<=\\G.{" + 1 + "})");
//        store.setThirdParty(StringUtils.arrayToDelimitedString(tempStr, "|"));

        int result = storeMapper.updateStoreThirdParty(store);

        Store C_Store = storeMapper.selectStoreInfo(store);

        if (result != 0) {
//            redisService.setPublisher("store_info_updated", "id:"+store.getId()+", admin_id:"+C_Store.getAdminId());
        }

        return result;
    }

    @Override
    public List<Rider> getMyStoreRiderRels(Common common) {
        return riderMapper.selectMyStoreRiderRels(common);
    }

    @Override
    public Rider getRiderInfo(Rider rider) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_RIDER.*")) {
            rider.setAccessToken(rider.getToken());
            rider.setId("");
            rider.setIsAdmin("");
        } else if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            rider.setAccessToken(rider.getToken());
//            rider.setId("");
            rider.setIsAdmin("");
        }

        Rider S_Rider = riderMapper.getRiderInfo(rider);

        if (S_Rider == null) {
            return null;
        }

        return S_Rider;
    }

    @Override
    public int updateRiderInfo(Rider rider){

        Rider S_Rider = riderMapper.getRiderInfo(rider);
        if(!S_Rider.getPhone().equals(rider.getPhone())){
            rider.setChangePhone("1");
        }

        int nRet = riderMapper.updateRiderInfoStore(rider);



        if (nRet != 0) {
            if (S_Rider.getSubGroupStoreRel() != null) {
                redisService.setPublisher("rider_updated", "id:" + S_Rider.getId() + ", admin_id:" + S_Rider.getAdminId() + ", store_id:" + S_Rider.getSubGroupStoreRel().getStoreId());
            } else {
                redisService.setPublisher("rider_updated", "id:" + S_Rider.getId() + ", admin_id:" + S_Rider.getAdminId());
            }
        }

        return nRet;
    }

    @Override
    public List<Alarm> getAlarm(Store store){ return storeMapper.selectAlarm(store); }

    @Override
    public int putStoreAlarm(Store store){
/*        String[] tempStr = (store.getAlarm()).split("(?<=\\G.{" + 1 + "})");
        store.setAlarm(StringUtils.arrayToDelimitedString(tempStr, "|"));*/

        int result = storeMapper.updateStoreAlarm(store);

        Store C_Store = storeMapper.selectStoreInfo(store);

        if (result != 0) {
            redisService.setPublisher("store_info_updated", "id:"+store.getId()+", admin_id:"+C_Store.getAdminId());
        }

        return result;
    }

    @Override
    public int putNoticeConfirm(Notice notice) {
        int ret = 0;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().matches(".*ROLE_STORE.*")) {
            notice.setRole("ROLE_STORE");
            List<Notice> C_Notice = noticeMapper.selectNoticeConfirm(notice);

            if (C_Notice.size() == 0) {
                ret = noticeMapper.insertNoticeConfirm(notice);
            } else {
                ret = noticeMapper.updateNoticeConfirm(notice);
            }
        }

        return ret;
    }
}
