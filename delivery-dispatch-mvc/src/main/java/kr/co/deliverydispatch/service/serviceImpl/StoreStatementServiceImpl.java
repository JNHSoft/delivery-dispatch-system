package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.ChatMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.util.Misc;
import kr.co.deliverydispatch.service.StoreStatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service("storeStatementService")
public class StoreStatementServiceImpl extends ServiceSupport implements StoreStatementService{
    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;
    /**
     * Order DAO
     */
    private OrderMapper orderMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    /**
     * Chat DAO
     */
    private ChatMapper chatMapper;

    /**
     * @param orderMapper ORDER D A O
     * @param storeMapper STORE D A O
     * @param riderMapper Rider D A O
     */
    @Autowired
    public StoreStatementServiceImpl(OrderMapper orderMapper, StoreMapper storeMapper, RiderMapper riderMapper) {
        this.orderMapper = orderMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
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


    // 통계 목록(list)
    @Override
    public List<Order> getStoreStatistics(Order order){
        List<Order> S_Statistics = storeMapper.selectStoreStatistics(order);
        if (S_Statistics.size() == 0) {
            return Collections.<Order>emptyList();
        }
        return S_Statistics;
    }
    // 통계 조회
    @Override
    public Order getStoreStatisticsInfo(Order order){
        Order S_Order = storeMapper.selectStoreStatisticsInfo(order);
        if (S_Order == null) {
            return null;
        }
        /*Misc misc = new Misc();
        if (S_Order.getLatitude() != null && S_Order.getLongitude() != null) {
            Store storeInfo = storeMapper.selectStoreLocation(S_Order.getStoreId());
            try {
                S_Order.setDistance(Double.toString(misc.getHaversine(storeInfo.getLatitude(), storeInfo.getLongitude(), S_Order.getLatitude(), S_Order.getLongitude()) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        return S_Order;
    }
    //통계 조회 엑셀
    @Override
    public List<Order> getStoreStatisticsExcel(Order order) {
        List<Order> statisticsList = storeMapper.selectStoreStatisticsExcel(order);
        /*Misc misc = new Misc();
        for (Order statistics:statisticsList) {
            if (statistics.getLatitude() != null && statistics.getLongitude() != null){
                Store storeLocation = storeMapper.selectStoreLocation(statistics.getStoreId());
                try {
                    statistics.setDistance(Double.toString(misc.getHaversine(storeLocation.getLatitude(), storeLocation.getLongitude(), statistics.getLatitude(), statistics.getLongitude()) / (double) 1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/
        return statisticsList;
    }

}
