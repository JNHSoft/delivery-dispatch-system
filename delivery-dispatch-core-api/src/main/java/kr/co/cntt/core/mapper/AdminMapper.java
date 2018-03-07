package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;

import java.util.List;

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
    public Admin loginAdminInfo(Admin admin);


    /**
     * <p> selectLoginAdmin
     *
     * @return loginId String
     */
    public String selectLoginAdmin(Admin admin);

    /**
     * <p> selectAdminTokenCheck
     *
     * @return Count Int
     */
    public int selectAdminTokenCheck(Admin admin);

    /**
     * <p> Admin Session Insert
     *
     * @return Insert 결과값
     */
    public int insertAdminSession(Admin admin);

    /**
     * <p> Admin 정보 조회
     *
     * @param common
     * @return Admin Info 조회 결과값
     */
    public List<Admin> selectAdminInfo(Common common);

    /**
     * <p> 그룹 목록 조회
     *
     * @param common
     * @return
     */
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
    public List<SubGroupStoreRel> selectNoneSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel);

    /**
     * <p> 상점 그룹 목록 조회
     *
     * @param subGroupStoreRel
     * @return
     */
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
    public List<Rider> selectRiders(Common common);

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
    public List<Order> selectAdminStatistics(Order order);

    /**
     * 통계 조회
     * @param order
     * @return
     */
    public Order selectAdminStatisticsInfo(Order order);


    /**
     * <p> ADMIN 페이지 상점 목록 조회
     *
     * @param store
     * @return
     */
    public List<Store> selectStoreList(Store store);
}
