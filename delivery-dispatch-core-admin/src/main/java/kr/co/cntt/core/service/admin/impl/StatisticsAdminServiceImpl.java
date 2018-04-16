package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import kr.co.cntt.core.util.Misc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        Misc misc = new Misc();

        if (A_Order.getLatitude() != null && A_Order.getLongitude() != null) {

            Store storeLocation = storeMapper.selectStoreLocation(A_Order.getStoreId());

            try {
                A_Order.setDistance(Double.toString(misc.getHaversine(storeLocation.getLatitude(), storeLocation.getLongitude(), A_Order.getLatitude(), A_Order.getLongitude()) / (double) 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        Misc misc = new Misc();
        for (Order statistics:statisticsList){
            if (statistics.getLatitude() != null && statistics.getLongitude() != null){
                Store storeLocation = storeMapper.selectStoreLocation(statistics.getStoreId());
                try {
                    statistics.setDistance(Double.toString(misc.getHaversine(storeLocation.getLatitude(), storeLocation.getLongitude(), statistics.getLatitude(), statistics.getLongitude()) / (double) 1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return statisticsList;
    }

}
