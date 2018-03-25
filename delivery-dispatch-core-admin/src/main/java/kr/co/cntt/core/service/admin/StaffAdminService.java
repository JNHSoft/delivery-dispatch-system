package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;

import java.util.List;

public interface StaffAdminService {

    /**
     * <p> 기사 목록 조회
     *
     * @param rider
     * @return
     */
    public List<Rider> selectRiderList(Rider rider);


    /**
     * <p> 기사 상세 조회
     *
     * @return
     */
    public Rider getRiderInfo(Rider rider);

    /**
     * store 리스트 조회
     * @author Nick
     * @since  2018-03-07
     *
     */
    public List<Store> selectStoreList(Store store);


    /**
     * Rider 수정
     * @param rider
     * @return
     */
    public int updateRiderInfo(Rider rider);



    /**
     * <p> 기사 상점만  수정    Nick 추가
     *
     * @param rider
     * @return
     */
    public int updateRiderStore(Rider rider);


    /**
     * <p> 기사 등록
     *
     * @param rider
     * @return
     */
    public int insertRider(Rider rider);


    /**
     * <p> 상점 소속 서브그룹에 기사 등록
     *
     * @param rider
     * @return
     */
    public int insertSubGroupRiderRel(Rider rider);

    /**
     * store 정보 조회
     *
     * @param store
     * @return
     */
    public Store selectStoreInfo(Store store);


    /**
     * <p> chatUser 등록
     *
     * @param rider
     * @return
     */
    public int insertChatUser(Rider rider);

    /**
     * <p> 기사 삭제
     *
     * @param rider
     * @return
     */
    public int deleteRider(Rider rider);


}
