package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.store.StoreRiderRel;

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

    /**
     * <p> 상점 기사 전체 소속 목록
     *
     * @param common
     * @return
     */
    public List<StoreRiderRel> selectStoreRiderRel(Common common);

    /**
     * <p> 상점 기사 소속 변경
     *
     * @param storeRiderRel
     * @return
     */
    public int updateStoreRiderRel(StoreRiderRel storeRiderRel);

    /**
     * <p> 상점 기사 소속 등록
     *
     * @param storeRiderRel
     * @return
     */
    public int insertStoreRiderRel(StoreRiderRel storeRiderRel);

}
