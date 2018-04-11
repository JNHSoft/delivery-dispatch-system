package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StatisticsAdminService {

    /**
     * 통계 목록
     * @param order
     * @return
     */
    public List<Order> selectAdminStatistics(Order order);

    /**
     * 통계 조회
     * @param order
     * @return
     */
    public Order selectAdminStatisticsInfo(Order order);


    /**
     * <p> 그룹 목록
     *
     * @param order
     * @return Group
     */
    public List<Group> getGroupList(Order order);


    /**
     * <p> 서브 그룹 목록
     *
     * @param order
     * @return SubGroup
     */
    public List<SubGroup> getSubGroupList(Order order);


    /**
     * <p> 상점 그룹 목록 조회
     *
     * @param subGroupStoreRel
     * @return
     */
    public List<SubGroupStoreRel> selectSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel);


    /**
     * 통계 목록 Excel
     * @param order
     * @return
     */
    public List<Order> selectAdminStatisticsExcel(Order order);
}
