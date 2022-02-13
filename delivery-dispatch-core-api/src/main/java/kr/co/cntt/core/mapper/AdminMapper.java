package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.common.SearchInfo;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.statistic.AdminByDate;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.model.login.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> AdminMapper.java </p>
 * <p> Admin 관련 </p>
 *
 * @author Aiden
 * @see DeliveryDispatchMapper
 */
@DeliveryDispatchMapper
public interface AdminMapper {
    /**
     * <p> loginAdminInfo
     *
     * @return loginId String
     */
    @Transactional(readOnly=true)
    public Admin loginAdminInfo(Admin admin);


    /**
     * <p> selectLoginAdmin
     *
     * @return loginId String
     */
    @Transactional(readOnly=true)
    public Map selectLoginAdmin(Admin admin);

    /**
     * <p> selectAdminTokenCheck
     *
     * @return Count Int
     */
    @Transactional(readOnly=true)
    public int selectAdminTokenCheck(Admin admin);

    /**
     * <p> selectAdminTokenCheck
     *
     * @return Count Int
     */
    @Transactional(readOnly=true)
    public User selectAdminTokenLoginCheck(Admin admin);

    /**
     * <p> Admin Session Insert
     *
     * @return Insert 결과값
     */
    public int insertAdminSession(Admin admin);

    /**
     * <p> Admin 토큰 만료일, last_access update
     *
     * @param token
     * @return
     */
    public int updateAdminSession(String token);

    /**
     * <p> Admin 정보 조회
     *
     * @param common
     * @return Admin Info 조회 결과값
     */
    @Transactional(readOnly=true)
    public List<Admin> selectAdminInfo(Common common);

    /**
     * <p> Admin 정보 수정
     *
     * @param admin
     * @return
     */
    public int updateAdminInfo(Admin admin);

    /**
     * <p> 그룹 목록 조회
     *
     * @param common
     * @return
     */
    @Transactional(readOnly=true)
    public List<Group> selectGroups(Common common);

    /**
     * <p> 그룹 등록
     *
     * @param group
     * @return
     */
    public int insertGroup(Group group);

    /**
     * <p> 그룹 수정
     *
     * @param group
     * @return
     */
    public int updateGroup(Group group);

    /**
     * <p> 그룹 삭제
     *
     * @param group
     * @return
     */
    public int deleteGroup(Group group);

    /**
     * <p> 서브그룹 목록 조회
     *
     * @param common
     * @return
     */
    @Transactional(readOnly=true)
    public List<SubGroup> selectSubGroups(Common common);

    /**
     * <p> 서브그룹 등록
     *
     * @param subGroup
     * @return
     */
    public int insertSubGroup(SubGroup subGroup);

    /**
     * <p> 서브그룹 수정
     *
     * @param subGroup
     * @return
     */
    public int updateSubGroup(SubGroup subGroup);

    /**
     * <p> 서브그룹 삭제
     *
     * @param subGroup
     * @return
     */
    public int deleteSubGroup(SubGroup subGroup);

    /**
     * <p> 상점 미지정 그룹 목록 조회
     *
     * @param subGroupStoreRel
     * @return
     */
    @Transactional(readOnly=true)
    public List<SubGroupStoreRel> selectNoneSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel);

    /**
     * <p> 상점 그룹 목록 조회
     *
     * @param subGroupStoreRel
     * @return
     */
    @Transactional(readOnly=true)
    public List<SubGroupStoreRel> selectSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel);

    /**
     * <p> 서브그룹에 상점 등록
     *
     * @param store
     * @return
     */
    public int insertSubGroupStoreRel(Store store);

    /**
     * <p> 서브그룹 상점 수정
     *
     * @param store
     * @return
     */
    public int updateSubGroupStoreRel(Store store);


    /**
     * <p> 상점 서브그룹만  수정    Nick 추가
     *
     * @param store
     * @return
     */
    public int updateStoreSubGroup(Store store);


    /**
     * <p> 기사 상점만  수정    Nick 추가
     *
     * @param rider
     * @return
     */
    public int updateRiderStore(Rider rider);



    /**
     * <p> 상점 서브그룹 삭제
     *
     * @param subGroupStoreRel
     * @return
     */
    public int deleteSubGroupStoreRel(SubGroupStoreRel subGroupStoreRel);

    /**
     * <p> 상점 소속 서브그룹에 기사 등록
     *
     * @param rider
     * @return
     */
    public int insertSubGroupRiderRel(Rider rider);

    /**
     * <p> chatUser 등록
     *
     * @param common
     * @return
     */
    public int insertChatUser(Common common);

    /**
     * <p> chatRoom 등록
     *
     * @param common
     * @return
     */
    public int insertChatRoom(Common common);

    /**
     * <p> chatUserChatRoomRel 등록
     *
     * @param common
     * @return
     */
    public int insertChatUserChatRoomRel(Common common);

    /**
     * <p> 기사 목록 조회
     *
     * @param common
     * @return
     */
    @Transactional(readOnly=true)
    public List<Rider> selectRiders(Common common);

    /**
     * <p> 기사 목록 조회
     *
     * @param common
     * @return
     */
    @Transactional(readOnly=true)
    public int selectRiderCountForStore(Common common);

    /**
     * <p> 기사 등록
     *
     * @param rider
     * @return
     */
    public int insertRider(Rider rider);

    /**
     * <p> 기사 삭제
     *
     * @param common
     * @return
     */
    public int deleteRider(Common common);


    /**
     * <p> 상점 목록 조회
     *
     * @param common
     * @return
     */
    @Transactional(readOnly=true)
    public List<Store> selectStores(Common common);


    /**
     * <p> 상점 등록
     *
     * @param store
     * @return
     */
    public int insertStore(Store store);

    /**
     * <p> 상점 삭제
     *
     * @param common
     * @return
     */
    public int deleteStore(Common common);

    /**
     * <p> 배정 모드 추가
     *
     * @param admin
     * @return
     */
    public int updateAdminAssignmentStatus(Admin admin);

    /**
     * <p> 배정 서드파티 추가
     *
     * @param thirdParty
     * @return
     */
    public int insertThirdParty(ThirdParty thirdParty);

    /**
     * <p> 배정 서드파티 수정
     *
     * @param thirdParty
     * @return
     */
    public int updateThirdParty(ThirdParty thirdParty);

    /**
     * <p> 배정 서드파티 삭제
     *
     * @param thirdParty
     * @return
     */
    public int deleteThirdParty(ThirdParty thirdParty);

    // 21.12.07 관리자에서 서드파티를 삭제 시, 설정된 스토어의 정보를 가져와 관련 서드파티만 삭제할 수 있도록 적용한다.
    List<Store> selectThirdPartyStoreList(ThirdParty thirdParty);

    // 21.12.07 관리자에서 서드파티를 삭제 시, 설정된 스토어의 정보를 가져와 관련 서드파티만 삭제할 수 있도록 적용한다.
    int updateThirdPartyStoreInfo(Store store);

    /**
     * <p> 알림음 추가
     *
     * @param alarm
     * @return
     */
    public int insertAlarm(Alarm alarm);

    /**
     * <p> 알림음 삭제
     *
     * @param alarm
     * @return
     */
    public int deleteAlarm(Alarm alarm);


    /**
     * 통계 목록
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    public List<Order> selectAdminStatistics(Order order);

    /**
     * 통계 목록 Excel admin 에서 쓰기위해서 mapper 만추가했음 api는 등록하지않음
     * @param searchInfo
     * @return
     */
    @Transactional(readOnly=true)
    public List<Order> selectAdminStatisticsExcel(SearchInfo searchInfo);


    /**
     * 통계 조회
     * @param order
     * @return
     */
    @Transactional(readOnly=true)
    public Order selectAdminStatisticsInfo(Order order);


    /**
     * <p> ADMIN 페이지 상점 목록 조회
     *
     * @param store
     * @return
     */
    @Transactional(readOnly=true)
    public List<Store> selectStoreList(Store store);

    /**
     * <p> 배정 거절 사유 추가
     *
     * @param reason
     * @return
     */
    public int insertRejectReason(Reason reason);

    /**
     * <p> 배정 거절 사유 수정
     *
     * @param reason
     * @return
     */
    public int updateRejectReason(Reason reason);

    /**
     * <p> 배정 거절 사유 삭제
     *
     * @param reason
     * @return
     */
    public int deleteRejectReason(Reason reason);

    /**
     * <p> order 우선 배정 사유 추가
     *
     * @param reason
     * @return
     */
    public int insertOrderFirstAssignmentReason(Reason reason);

    /**
     * <p> order 우선 배정 사유 수정
     *
     * @param reason
     * @return
     */
    public int updateOrderFirstAssignmentReason(Reason reason);

    /**
     * <p> order 우선 배정 사유 삭제
     *
     * @param reason
     * @return
     */
    public int deleteOrderFirstAssignmentReason(Reason reason);


    /**
     * 상점 아이디 중복 확인
     * @param store
     * @return
     */
    public int selectStoreLoginIdCheck(Store store);

    /**
     * 기사 아이디 중복 확인
     * @param rider
     * @return
     */
    public int selectRiderLoginIdCheck(Rider rider);

    /**
     * 기사 서브그룹 수정 by store_id
     * @param store
     * @return
     */
    public int updateSubGroupRiderRelByStoreId(Store store);

    /**
     * <p> 라이더 서브 그룹 수정(상점 서브그룹만 수정 시)
     *
     * @param store
     * @return
     */
    public int updateRiderSubGroup(Store store);

    /**
     * <p> 라이더 Token 삭제
     *
     * @param rider
     * @return
     */
    public int deleteRiderToken(Rider rider);

    /**
     * 2020.04.24 통계 페이지 추가
     * */
    @Transactional(readOnly=true)
    List<Order> selectStoreStatisticsByOrderForAdmin(SearchInfo searchInfo);

    /**
     * 2020.04.28 일자별 통계 페이지 추가
     * */
    @Transactional(readOnly=true)
    List<AdminByDate> selectStoreStatisticsByDateForAdmin(SearchInfo searchInfo);
    @Transactional(readOnly=true)
    List<AdminByDate> selectStoreStatisticsByDateForAdminAtTWKFC(SearchInfo searchInfo);

    /**
     * 누적 통계 페이지 추가 1
     * */
    @Transactional(readOnly=true)
    List<Integer> selectStatisticsByInterval(Order order);
    @Transactional(readOnly=true)
    List<Map> selectStatisticsByIntervalAtTWKFC(Order order);

    /**
     * 누적 통계 페이지 추가 1
     * */
    @Transactional(readOnly=true)
    List<Map> selectStatisticsMin30BelowByDate(Order order);
    @Transactional(readOnly=true)
    List<Map> selectStatisticsMin30BelowByDateAtTWKFC(Order order);

    /**
     * 20.12.30
     * 누적 시간 오버된 매장 개수 구하기
     * */
    @Transactional(readOnly=true)
    Map selectOverTimeByStore(Store store);
    
    /**
     * 21.01.21
     * 서브 그룹 그룹핑 처리
     * */
    @Transactional(readOnly=true)
    List<SubGroup> selectSubGroupGrouping(Common common);
    @Transactional(readOnly=true)
    List<SubGroupStoreRel> selectSubgrouGroupingStoreRels(SubGroupStoreRel subGroupStoreRel);

    /**
     * 메일을 받을 계정
     * */
    @Transactional(readOnly=true)
    List<String> selectReceivedMailAccount(String adminID);

    /**
     * 22.02.11 Beacon 공통 정보 가져오기
     * */
    Admin selectBeaconCommInfo(Admin adminInfo);

    /**
     * 22.02.11 Beacon 정보 변경
     * */
    int updateBeaconInfo(Admin adminInfo);

    /**
     * 21.07.24 스토어 정보 가져오기
     * */
//    List<Map<String, Object>> selectRegNewStoreList();
}
