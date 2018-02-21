package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;

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
     * 기가 삭제
     * @param rider
     * @return
     */
    public int deleteRider(Rider rider);


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
     * 상점 삭제
     * @param store
     * @return
     */
    public int deleteStore(Store store);

}
