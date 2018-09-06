package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.ChatMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.deliverydispatch.service.StoreStatementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public List<Order> getStoreStatisticsByOrder(Order order) {
        List<Order> S_Statistics = storeMapper.selectStoreStatisticsByOrder(order);
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
        return S_Order;
    }
    //통계 조회 엑셀
    @Override
    public List<Order> getStoreStatisticsExcel(Order order) {
        List<Order> statisticsList = storeMapper.selectStoreStatisticsExcel(order);
        return statisticsList;
    }

    @Override
    public Interval getStoreStatisticsByInterval(Order order) {

        Interval interval = new Interval();
        interval.setIntervalMinute(storeMapper.selectStoreStatisticsByInterval(order));

        List<Object> list = new ArrayList<>();

        int intervalSize = interval.getIntervalMinute().size();
        long intervalCount = 0;

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> i < 10).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 10 && i < 11)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 10 && i < 11)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 11 && i < 12)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 12 && i < 13)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 13 && i < 14)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 14 && i < 15)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 15 && i < 16)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 16 && i < 17)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 17 && i < 18)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 18 && i < 19)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 19 && i < 20)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 20 && i < 21)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 21 && i < 22)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 22 && i < 23)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 23 && i < 24)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 24 && i < 25)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 25 && i < 26)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 26 && i < 27)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 27 && i < 28)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 28 && i < 29)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 29 && i < 30)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 30 && i < 31)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 31 && i < 32)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 32 && i < 33)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 33 && i < 34)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 34 && i < 35)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 35 && i < 36)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 36 && i < 37)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 37 && i < 38)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 38 && i < 39)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 39 && i < 40)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 40 && i < 41)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 41 && i < 42)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 42 && i < 43)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 43 && i < 44)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 44 && i < 45)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 45 && i < 46)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 46 && i < 47)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 47 && i < 48)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 48 && i < 49)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 49 && i < 50)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 50 && i < 51)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 51 && i < 52)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 52 && i < 53)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 53 && i < 54)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 54 && i < 55)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 55 && i < 56)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 56 && i < 57)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 57 && i < 58)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 58 && i < 59)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 59 && i < 60)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 60 && i < 61)).count();
        addInterval(list, intervalSize, intervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> i >= 61).count();
        addInterval(list, intervalSize, intervalCount);

        interval.setIntervalMinuteCounts(list);

        return interval;
    }

    public List<Object> addInterval(List list, int intervalSize, long intervalCount) {

        Object[] array = new Object[3];

        array[0] = intervalCount;
        if (intervalCount > 0) {
            array[1] = Double.parseDouble(String.format("%.2f", intervalCount / (double) 100 * 100));
        } else {
            array[1] = 0;
        }
        if (intervalCount > 0 && intervalSize > 0) {
            array[2] = Double.parseDouble(String.format("%.2f", intervalCount / (double) intervalSize * 100));
        } else {
            array[2] = 0;
        }
        list.add(array);

        return list;
    }
}
