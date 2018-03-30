package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.StoreService;
import kr.co.cntt.core.util.Misc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("storeService")
public class StoreServiceImpl extends ServiceSupport implements StoreService {

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
     * Order DAO
     */
    private OrderMapper orderMapper;

    /**
     * @param storeMapper USER D A O
     * @author Nick
     */
    @Autowired
    public StoreServiceImpl(StoreMapper storeMapper, OrderMapper orderMapper) {
        this.storeMapper = storeMapper;
        this.orderMapper = orderMapper;
    }

    // login_id 체크하는 함수
    @Override
    public Map selectLoginStore(Store store) {
        return storeMapper.selectLoginStore(store);
    }

    // login_id 에 맞는 token 값 체크 하는 함수
    @Override
    public int selectStoreTokenCheck(Store store) {
        return storeMapper.selectStoreTokenCheck(store);
    }

    // login_id 에 맞는 token 값 체크 하는 함수
    @Override
    public User selectStoreTokenLoginCheck(Store store) {
        return storeMapper.selectStoreTokenLoginCheck(store);
    }

    // token 값 insert 해주는 함수
    @Override
    public int insertStoreSession(Store store) {
        return storeMapper.insertStoreSession(store);
    }

    @Override
    public int updateStoreSession(String token) {
        return storeMapper.updateStoreSession(token);
    }

    // store 정보 조회
    @Secured({"ROLE_ADMIN", "ROLE_STORE" , "ROLE_RIDER"})
    @Override
    public Store getStoreInfo(Store store) throws AppTrException {
        // 권한
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // store 가 조회
        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            // token 값 선언
            store.setAccessToken(store.getToken());
            store.setId("");
            store.setIsAdmin("");
        }
        // 권한이 없는 다른 user 가 조회
        else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            store.setAccessToken(null);
            store.setId("");
            store.setIsAdmin("");
        }

        // log 확인
        log.info(">>> token: " + store.getAccessToken());
        log.info(">>> token: " + store.getToken());

        // 리스트
        Store S_Store = storeMapper.selectStoreInfo(store);

        if (S_Store == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00007), ErrorCodeEnum.E00007.name());
        }

        return S_Store;
    }

    // store 정보 수정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
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

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            store.setId(null);
            store.setCode(null);
            store.setSubGroupStoreRel(null);
        }

        int result = storeMapper.updateStoreInfo(store);

        if (result != 0) {
            redisService.setPublisher("store_info_updated", "id:"+store.getId()+", admin_id:"+C_Store.getAdminId());
        }

        return result;
    }

    // 배정 모드 설정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putStoreAssignmentStatus(Store store){
        int result = storeMapper.updateStoreAssignmentStatus(store);

        Store C_Store = storeMapper.selectStoreInfo(store);

        if (result != 0) {
            redisService.setPublisher("store_info_updated", "id:"+store.getId()+", admin_id:"+C_Store.getAdminId());
        }

        return result;
    }

    //배정 서드파티 설정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putStoreThirdParty(Store store){
        String[] tempStr = (store.getThirdParty()).split("(?<=\\G.{" + 1 + "})");
        store.setThirdParty(StringUtils.arrayToDelimitedString(tempStr, "|"));

        int result = storeMapper.updateStoreThirdParty(store);

        Store C_Store = storeMapper.selectStoreInfo(store);

        if (result != 0) {
            redisService.setPublisher("store_info_updated", "id:"+store.getId()+", admin_id:"+C_Store.getAdminId());
        }

        return result;
    }

    //배정 서드파티 목록
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty){
        return storeMapper.selectThirdParty(thirdParty);
    }

    //알림음 설정
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public int putStoreAlarm(Store store){
        String[] tempStr = (store.getAlarm()).split("(?<=\\G.{" + 1 + "})");
        store.setAlarm(StringUtils.arrayToDelimitedString(tempStr, "|"));

        int result = storeMapper.updateStoreAlarm(store);

        Store C_Store = storeMapper.selectStoreInfo(store);

        if (result != 0) {
            redisService.setPublisher("store_info_updated", "id:"+store.getId()+", admin_id:"+C_Store.getAdminId());
        }

        return result;
    }

    //알림음 목록
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public List<Alarm> getAlarm(Store store){ return storeMapper.selectAlarm(store); }


    // 통계 목록(list)
    @Secured("ROLE_STORE")
    @Override
    public List<Order> getStoreStatistics(Order order) throws AppTrException {

        List<Order> S_Statistics = storeMapper.selectStoreStatistics(order);

        if (S_Statistics.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00033), ErrorCodeEnum.E00033.name());
        }

        return S_Statistics;

    }

    // 통계 조회
    @Secured("ROLE_STORE")
    @Override
    public Order getStoreStatisticsInfo(Order order) throws AppTrException {

        Order S_Order = storeMapper.selectStoreStatisticsInfo(order);

        if (S_Order == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00034), ErrorCodeEnum.E00034.name());
        }


        Misc misc = new Misc();

        if (S_Order.getLatitude() != null && S_Order.getLongitude() != null) {


            Order orderInfo = orderMapper.selectOrderLocation(S_Order.getId());

            try {
                S_Order.setDistance(Double.toString(misc.getHaversine(orderInfo.getLatitude(), orderInfo.getLongitude(), orderInfo.getLatitude(), orderInfo.getLongitude()) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return S_Order;
    }


}
