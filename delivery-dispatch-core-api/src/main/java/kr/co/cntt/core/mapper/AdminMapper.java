package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.login.User;
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
     * @return Admin Info 조회 결과값
     */
    public List<Admin> selectAdminInfo(Admin admin);

    /**
     * <p> chatUser 등록
     *
     * @param user
     * @return
     */
    public int insertChatUser(User user);

    /**
     * <p> chatRoom 등록
     *
     * @param user
     * @return
     */
    public int insertChatRoom(User user);

    /**
     * <p> chatUserChatRoomRel 등록
     *
     * @param user
     * @return
     */
    public int insertChatUserChatRoomRel(User user);

    /**
     * <p> 기사 목록 조회
     *
     * @param admin
     * @return
     */
    public List<Rider> selectRiders(Admin admin);

    /**
     * <p> 기사 등록
     *
     * @param rider
     * @return
     */
    public int insertRider(Rider rider);

    /**
     * <p> 상점 목록 조회
     *
     * @param user
     * @return
     */
    public List<Store> selectStores(User user);

    /**
     * <p> 상점 등록
     *
     * @param store
     * @return
     */
    public int insertStore(Store store);

    /**
     * <p> 상점 기사 전체 소속 목록
     *
     * @param user
     * @return
     */
    public List<StoreRiderRel> selectStoreRiderRel(User user);

}
