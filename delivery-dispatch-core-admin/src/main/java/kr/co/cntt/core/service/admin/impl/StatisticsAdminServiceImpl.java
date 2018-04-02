package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
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

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;



    @Autowired
    public StatisticsAdminServiceImpl(AdminMapper adminMapper , StoreMapper storeMapper, RiderMapper riderMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
    }

    // 통계 리스트
    @Override
    public List<Order> selectAdminStatistics(Order order) {
        return adminMapper.selectAdminStatistics(order);
    }
    // 통계 상세 보기
    @Override
    public Order selectAdminStatisticsInfo(Order order){return adminMapper.selectAdminStatisticsInfo(order);}

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
        return adminMapper.selectAdminStatisticsExcel(order);
    }

}
