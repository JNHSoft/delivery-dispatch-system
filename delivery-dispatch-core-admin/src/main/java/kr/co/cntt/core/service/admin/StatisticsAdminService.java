package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.statistic.AdminByDate;
import kr.co.cntt.core.model.statistic.ByDate;
import kr.co.cntt.core.model.statistic.Interval;
import kr.co.cntt.core.model.statistic.IntervalAtTWKFC;
import kr.co.cntt.core.model.store.Store;

import java.util.List;
import java.util.Map;

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
     * @param searchInfo
     * @return
     */
    public List<Order> selectAdminStatisticsExcel(SearchInfo searchInfo);


    /**
     * 2020.04.24 Store 통계 페이지 추가
     * */

    /**
     * 주문별 통게페이지
     * @param searchInfo
     * @return
     * */
    List<Order> selectStoreStatisticsByOrderForAdmin(SearchInfo searchInfo);

    /**
     * 매장 기간별 통계 페이지
     * */
    List<AdminByDate> selectStoreStatisticsByDateForAdmin(SearchInfo searchInfo);

    /**
     * 매장 누적 통계 페이지
     * */
    Interval selectAdminStatisticsByInterval(Order order);

    /**
     * 매장 누적 통계 페이지 - 30분 미만 목록
     * */
    List<Map> selectAdminStatisticsMin30BelowByDate(Order order);

    /**
     * KFC
     * */

    /**
     * 매장 기간별 통계 페이지 KFC
     * */
    List<AdminByDate> selectStoreStatisticsByDateForAdminAtTWKFC(SearchInfo searchInfo);

    /**
     * 매장 누적 통계 페이지 KFC
     * */
    IntervalAtTWKFC selectAdminStatisticsByIntervalAtTWKFC(Order order);

    /**
     * 매장 누적 통계 페이지 - 30분 미만 목록 KFC
     * */
    List<Map> selectAdminStatisticsMin30BelowByDateAtTWKFC(Order order);

}
