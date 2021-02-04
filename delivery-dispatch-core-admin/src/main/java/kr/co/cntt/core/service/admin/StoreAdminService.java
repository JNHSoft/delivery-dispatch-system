package kr.co.cntt.core.service.admin;


import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StoreAdminService {
    /**
     * store 리스트 조회
     * @author Nick
     * @since  2018-03-07
     *
     */
    public List<Store> selectStoreList(Store store);

    /**
     * store 정보 조회
     *
     * @param store
     * @return
     */
    public Store selectStoreInfo(Store store);

    /**
     * <p> 그룹 목록 조회
     *
     * @param Store
     * @return
     */
    public List<Group> selectGroupsList(Store Store);


    /**
     * <p> 서브그룹 목록 조회
     *
     * @param store
     * @return
     */
    public List<SubGroup> selectSubGroupsList(Store store);

    /**
     * <p> 상점 그룹 목록 조회
     *
     * @param subGroupStoreRel
     * @return
     */
    public List<SubGroupStoreRel> selectSubgroupStoreRels(SubGroupStoreRel subGroupStoreRel);


    /**
     * Store 정보 수정
     *
     * @param store
     * @return
     */
    public int updateStoreInfo(Store store);

    /**
     * <p> 서브그룹 상점 수정
     *
     * @param store
     * @return
     */
    public int updateSubGroupStoreRel(Store store);

    /**
     * <p> 배정 모드 설정
     *
     * @param store
     * @return
     */
    public int updateStoreAssignmentStatus(Store store);

    /**
     * <p> 서브그룹에 상점 등록
     *
     * @param store
     * @return
     */
    public int insertSubGroupStoreRel(Store store);

    /**
     * <p> 상점 등록
     *
     * @param store
     * @return
     */
    public int insertStore(Store store);


    /**
     * <p> chatUser 등록
     *
     * @param store
     * @return
     */
    public int insertChatUser(Store store);

    /**
     * <p> chatRoom 등록
     *
     * @param store
     * @return
     */
    public int insertChatRoom(Store store);

    /**
     * <p> chatUserChatRoomRel 등록
     *
     * @param store
     * @return
     */
    public int insertChatUserChatRoomRel(Store store);


    /**
     * <p> 상점 삭제
     *
     * @param store
     * @return
     */
    public int deleteStore(Store store);


    /**
     * insert token
     *
     * @param store
     * @return
     */
    public int insertAdminStoreSession(Store store);


    /**
     * 상점 아이디 중복 확인
     * @param store
     * @return
     */
    public int selectStoreLoginIdCheck(Store store);


    /**
     * 기사 서브그룹 수정 by store_id
     * @param store
     * @return
     */
    public int putSubGroupRiderRelByStoreId(Store store);

    /**
     * 상점 비밀번호 초기화
     * @param store
     * @return
     */
    public int resetStorePassword(Store store);

}
