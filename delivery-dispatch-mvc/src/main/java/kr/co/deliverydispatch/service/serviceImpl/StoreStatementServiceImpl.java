package kr.co.deliverydispatch.service.serviceImpl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.ChatMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.ByDate;
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

import java.util.*;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.stream.Collectors;

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

    // date
    @Override
    public List<ByDate> getStoreStatisticsByDate(Order order) {
        List<ByDate> S_Statistics = storeMapper.selectStoreStatisticsByDate(order);
        if (S_Statistics.size() == 0) {
            return Collections.<ByDate>emptyList();
        }
        return S_Statistics;
    }

    //통계 조회 엑셀
    @Override
    public List<ByDate> getStoreStatisticsExcelByDate(Order order) {
        List<ByDate> statisticsDate = storeMapper.selectStoreStatisticsByDate(order);
        return statisticsDate;
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

        List<Object[]> list = new ArrayList<>();

        Map<String, Integer> intervalSize = new HashMap<>();
        Map<String, Long> intervalCount = new HashMap<>();
        Map<String, Double> sumIntervalCount = new HashMap<>();

        interval.getIntervalMinute().forEach(x->{
            System.out.println(x);
        });

        List<Integer> d30List = new ArrayList<>();
        List<Integer> d7List = new ArrayList<>();

        for (Map map:interval.getIntervalMinute()
             ) {
            if (map.get("interval_minute") != null){
                d30List.add(Integer.parseInt(map.get("interval_minute").toString()));
            }
            if (map.get("d7_minute") != null){
                d7List.add(Integer.parseInt(map.get("d7_minute").toString()));
            }
        }

        /// D30에 대한 집계 함수 실행
        Map<Integer, Long> d30Result =
                d30List.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );

        /// D07에 대한 집계 함수 실행
        Map<Integer, Long> d7Result =
                d7List.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );

        // 데이터 토탈 개수
        intervalSize.put("d30IntervalSize", d30List.size());
        intervalSize.put("d7IntervalSize", d7List.size());


        System.out.println(d30Result);
        System.out.println(d7Result);

        // 7분 미만 주문의 카운트
        intervalCount.put("d30IntervalCount", d30Result.entrySet().parallelStream().filter(x->x.getKey() < 7).mapToLong(x -> x.getValue()).sum());
        intervalCount.put("d7IntervalCount", d7Result.entrySet().parallelStream().filter(x->x.getKey() < 7).mapToLong(x -> x.getValue()).sum());
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);
        intervalCount.clear();

        // 7분 이상 10분 미만 주문의 카운트
        intervalCount.put("d30IntervalCount", d30Result.entrySet().parallelStream().filter(x->x.getKey() >= 7 && x.getKey() < 10).mapToLong(x -> x.getValue()).sum());
        intervalCount.put("d7IntervalCount", d7Result.entrySet().parallelStream().filter(x->x.getKey() >= 7 && x.getKey() < 10).mapToLong(x -> x.getValue()).sum());
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);
        intervalCount.clear();

        for (int i = 10; i < 61; i++) {
            final int z = i;
            intervalCount.put("d30IntervalCount", d30Result.entrySet().parallelStream().filter(x->x.getKey() == z).mapToLong(x -> x.getValue()).sum());
            intervalCount.put("d7IntervalCount", d7Result.entrySet().parallelStream().filter(x->x.getKey() == z).mapToLong(x -> x.getValue()).sum());
            sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);
            intervalCount.clear();
        }

        // 60분 초과 주문의 카운트
        intervalCount.put("d30IntervalCount", d30Result.entrySet().parallelStream().filter(x->x.getKey() >= 61).mapToLong(x -> x.getValue()).sum());
        intervalCount.put("d7IntervalCount", d7Result.entrySet().parallelStream().filter(x->x.getKey() >= 61).mapToLong(x -> x.getValue()).sum());
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);
        intervalCount.clear();

        interval.setIntervalMinuteCounts(list);

        return interval;
    }

    @Override
    public List<Map> getStoreStatisticsMin30BelowByDate(Order order) {
        List<Map> min30Below_Statistics = storeMapper.selectStoreStatisticsMin30BelowByDate(order);
        if (min30Below_Statistics.size() == 0) {
            return Collections.<Map>emptyList();
        }
        return min30Below_Statistics;
    }

    public Map<String, Double> addInterval(List list, Map<String, Integer> intervalSize, Map<String, Long> intervalCount, Map<String, Double> sumIntervalCount) {

        /// 0 = 분별 개수 1 = 분별 퍼센트 2 = 토탈 퍼센트
        /// 3 = D7 개수 4 = D7 퍼센트 5 = D7 토탈 퍼센트
        Object[] array = new Object[6];

        double d30Sum = 0;
        double d7Sum = 0;

        // D30 내용 추가
        array[0] = intervalCount.get("d30IntervalCount");
        if (intervalCount.get("d30IntervalCount") != null && intervalCount.get("d30IntervalCount").intValue() > 0){
            array[1] = Double.parseDouble(String.format("%.2f", intervalCount.get("d30IntervalCount").intValue() / intervalSize.get("d30IntervalSize").doubleValue() * 100));
        }else{
            array[1] = 0;
        }

        d30Sum = Double.parseDouble(String.format("%.2f", intervalCount.get("d30IntervalCount").intValue() / intervalSize.get("d30IntervalSize").doubleValue() * 100));

        if(!sumIntervalCount.containsKey("d30SumIntervalCount")){
            sumIntervalCount.put("d30SumIntervalCount", d30Sum);
        }else{
            d30Sum = sumIntervalCount.get("d30SumIntervalCount").doubleValue() + d30Sum;
            sumIntervalCount.put("d30SumIntervalCount", d30Sum);
        }

        if (d30Sum > 0){
            array[2] = d30Sum;
        }else{
            array[2] = 0;
        }

        // D7 내용 추가
        array[3] = intervalCount.get("d7IntervalCount");
        if (intervalCount.get("d7IntervalCount") != null && intervalCount.get("d7IntervalCount").intValue() > 0){
            array[4] = Double.parseDouble(String.format("%.2f", intervalCount.get("d7IntervalCount").intValue() / intervalSize.get("d7IntervalSize").doubleValue() * 100));
        }else{
            array[4] = 0;
        }

        d7Sum = Double.parseDouble(String.format("%.2f", intervalCount.get("d7IntervalCount").intValue() / intervalSize.get("d7IntervalSize").doubleValue() * 100));

        if(!sumIntervalCount.containsKey("d7SumIntervalCount")){
            sumIntervalCount.put("d7SumIntervalCount", d7Sum);
        }else{
            d7Sum = sumIntervalCount.get("d7SumIntervalCount").doubleValue() + d7Sum;
            sumIntervalCount.put("d7SumIntervalCount", d7Sum);
        }

        if (d7Sum > 0){
            array[5] = d7Sum;
        }else{
            array[5] = 0;
        }


        list.add(array);

        return sumIntervalCount;
    }
    
}
