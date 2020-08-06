package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.statistic.AdminByDate;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import kr.co.cntt.core.util.Misc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("StatisticsAdminService")
public class StatisticsAdminServiceImpl implements StatisticsAdminService {

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;



    @Autowired
    public StatisticsAdminServiceImpl(AdminMapper adminMapper , StoreMapper storeMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
    }

    // 통계 리스트
    @Override
    public List<Order> selectAdminStatistics(Order order) {
        return adminMapper.selectAdminStatistics(order);
    }
    // 통계 상세 보기
    @Override
    public Order selectAdminStatisticsInfo(Order order) {
        Order A_Order = adminMapper.selectAdminStatisticsInfo(order);
        /*Misc misc = new Misc();

        if (A_Order.getLatitude() != null && A_Order.getLongitude() != null) {

            Store storeLocation = storeMapper.selectStoreLocation(A_Order.getStoreId());

            try {
                A_Order.setDistance(Double.toString(misc.getHaversine(storeLocation.getLatitude(), storeLocation.getLongitude(), A_Order.getLatitude(), A_Order.getLongitude()) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        return A_Order;
    }
    // 그룹 조회
    @Override
    public List<Group> getGroupList(Order order) {
        return adminMapper.selectGroups(order);
    }

    // 서브 그룹 조회
    @Override
    public List<SubGroup> getSubGroupList(Order order) {
        return adminMapper.selectSubGroups(order);
    }

    // 서브 그룹 조회
    @Override
    public List<SubGroupStoreRel> selectSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel) {
        return adminMapper.selectSubgroupStoreRels(subGroupStoreRel);
    }

    // 통계 리스트 Excel
    @Override
    public List<Order> selectAdminStatisticsExcel(Order order) {
        List<Order> statisticsList =  adminMapper.selectAdminStatisticsExcel(order);
        /*Misc misc = new Misc();
        for (Order statistics:statisticsList){
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

    // 주문별 통계 페이지
    public List<Order> selectStoreStatisticsByOrderForAdmin(Order order){
        List<Order> statistByOrder =  adminMapper.selectStoreStatisticsByOrderForAdmin(order);
        if (statistByOrder.size() == 0) {
            return Collections.<Order>emptyList();
        }

        return statistByOrder;
    }

    // 매장 일자별 통계 페이지
    public List<AdminByDate> selectStoreStatisticsByDateForAdmin(Order order){
        List<AdminByDate> statistByDate = adminMapper.selectStoreStatisticsByDateForAdmin(order);

        if (statistByDate.size() == 0){
            return Collections.<AdminByDate>emptyList();
        }

        return statistByDate;
    }

    // 매장 일자별 통계 페이지 TW KFC
    public List<AdminByDate> selectStoreStatisticsByDateForAdminAtTWKFC(Order order){
        List<AdminByDate> statistByDate = adminMapper.selectStoreStatisticsByDateForAdminAtTWKFC(order);

        if (statistByDate.size() == 0){
            return Collections.<AdminByDate>emptyList();
        }

        return statistByDate;
    }


    // 매장 누적 통계
    public Interval selectAdminStatisticsByInterval(Order order) {
        Interval interval = new Interval();
        interval.setIntervalMinute(adminMapper.selectStatisticsByInterval(order));

        List<Object[]> list = new ArrayList<>();

        int intervalSize = interval.getIntervalMinute().size();
        long intervalCount = 0;
        double sumIntervalCount = 0;

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> i < 10).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 10 && i < 11)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 11 && i < 12)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 12 && i < 13)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 13 && i < 14)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 14 && i < 15)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 15 && i < 16)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 16 && i < 17)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 17 && i < 18)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 18 && i < 19)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 19 && i < 20)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 20 && i < 21)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 21 && i < 22)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 22 && i < 23)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 23 && i < 24)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 24 && i < 25)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 25 && i < 26)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 26 && i < 27)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 27 && i < 28)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 28 && i < 29)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 29 && i < 30)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 30 && i < 31)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 31 && i < 32)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 32 && i < 33)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 33 && i < 34)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 34 && i < 35)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 35 && i < 36)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 36 && i < 37)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 37 && i < 38)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 38 && i < 39)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 39 && i < 40)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 40 && i < 41)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 41 && i < 42)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 42 && i < 43)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 43 && i < 44)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 44 && i < 45)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 45 && i < 46)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 46 && i < 47)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 47 && i < 48)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 48 && i < 49)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 49 && i < 50)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 50 && i < 51)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 51 && i < 52)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 52 && i < 53)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 53 && i < 54)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 54 && i < 55)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 55 && i < 56)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 56 && i < 57)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 57 && i < 58)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 58 && i < 59)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 59 && i < 60)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> (i >= 60 && i < 61)).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        intervalCount = interval.getIntervalMinute().parallelStream().filter(i -> i >= 61).count();
        sumIntervalCount = addInterval(list, intervalSize, intervalCount, sumIntervalCount);

        interval.setIntervalMinuteCounts(list);

        return interval;
    }
    // 매장 누적 통계
    public IntervalAtTWKFC selectAdminStatisticsByIntervalAtTWKFC(Order order) {
        IntervalAtTWKFC interval = new IntervalAtTWKFC();
        interval.setIntervalMinute(adminMapper.selectStatisticsByIntervalAtTWKFC(order));

        List<Object[]> list = new ArrayList<>();

        Map<String, Integer> intervalSize = new HashMap<>();
        Map<String, Long> intervalCount = new HashMap<>();
        Map<String, Double> sumIntervalCount = new HashMap<>();

//        interval.getIntervalMinute().forEach(x->{
//            System.out.println(x);
//        });

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


//        System.out.println(d30Result);
//        System.out.println(d7Result);

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

    // 매장 누적 통계 30분 미만 목록
    public List<Map> selectAdminStatisticsMin30BelowByDate(Order order){
        List<Map> min30Below_Statistics = adminMapper.selectStatisticsMin30BelowByDate(order);
        if (min30Below_Statistics.size() == 0){
            return Collections.<Map>emptyList();
        }

        return min30Below_Statistics;
    }
    // 매장 누적 통계 30분 미만 목록
    public List<Map> selectAdminStatisticsMin30BelowByDateAtTWKFC(Order order){
        List<Map> min30Below_Statistics = adminMapper.selectStatisticsMin30BelowByDateAtTWKFC(order);
        if (min30Below_Statistics.size() == 0){
            return Collections.<Map>emptyList();
        }

        return min30Below_Statistics;
    }
    //Default
    public double addInterval(List list, int intervalSize, long intervalCount, double sumIntervalCount) {

        Object[] array = new Object[3];

        array[0] = intervalCount;
        if (intervalCount > 0) {
            array[1] = Double.parseDouble(String.format("%.2f", intervalCount / (double) intervalSize * 100));
        } else {
            array[1] = 0;
        }

        sumIntervalCount = sumIntervalCount + Double.parseDouble(String.format("%.2f", intervalCount / (double) intervalSize * 100));
        if (sumIntervalCount > 0) {
            array[2] = Double.parseDouble(String.format("%.2f", sumIntervalCount));
        } else {
            array[2] = 0;
        }

        list.add(array);

        return sumIntervalCount;
    }
    // KFC
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
