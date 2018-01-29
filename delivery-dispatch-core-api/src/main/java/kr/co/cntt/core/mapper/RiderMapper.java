package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.StoreRiderRel;

import java.util.List;

/**
 * <p> kr.co.cntt.core.mapper </p>
 * <p> RiderMapper.java </p>
 * <p> Rider 관련 </p>
 * @see DeliveryDispatchMapper
 * @author Merlin
 */
@DeliveryDispatchMapper
public interface RiderMapper {

    /**
     * <p> selectLoginRider
     *
     * @return loginId String
     */
    public String selectLoginRider(Rider rider);

    /**
     * <p> selectRiderTokenCheck
     *
     * @return Count Int
     */
    public int selectRiderTokenCheck(Rider rider);

    /**
     * <p> Rider Session Insert
     *
     * @return Insert 결과값
     */
    public int insertRiderSession(Rider rider);


    /**
     * <p> Rider 정보 조회
     *
     * @return Rider Info 조회 결과값
     */
    public List<Rider> getRiderInfo(Rider rider);

    /**
     * <p> 해당 스토어 Rider 목록
     *
     * @return 해당 스토어 Rider 목록 조회 결과값
     */
    public List<Rider> getStoreRiders(StoreRiderRel storeRiderRel);
}
