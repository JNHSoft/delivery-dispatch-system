package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.rider.Rider;

import java.util.List;
import java.util.Map;

public interface RiderService {
    /**
     * <p> selectLoginRider
     *
     * @return
     */
    public String selectLoginRider(Rider rider);

    /**
     * <p> selectRiderTokenCheck
     *
     * @return
     */
    public int selectRiderTokenCheck(Rider rider);

    /**
     * <p> insertRiderSession
     *
     * @return
     */
    public int insertRiderSession(Rider rider);

    /**
     * <p> selectRiderInfo
     *
     * @return
     */
    public List<Rider> getRiderInfo(Rider rider) throws AppTrException;

    /**
     * <p> selectStoreRiders
     *
     * @return
     */
    public List<Rider> getStoreRiders(User user) throws AppTrException;

    /**
     * rider 정보 수정
     *
     * @param rider
     * @return
     */
    public int updateRiderInfo(Rider rider) throws AppTrException;

    /**
     * rider 출/퇴근
     * @param rider
     * @return
     */
    public int updateWorkingRider(Rider rider) throws AppTrException ;

    /**
     * Rider 위치 정보 전송
     * @param rider
     * @return
     * @throws AppTrException
     */
    public int updateRiderLocation(Rider rider) throws AppTrException ;

    /**
     * Rider 위치 정보 조회
     * @param rider
     * @return
     */
    public List<Rider> getRiderLocation(Rider rider) throws AppTrException ;

    /**
     * Rider 들 정보 조회
     * @param rider
     * @return
     */
    public Map getRidersLocation(Rider rider) throws AppTrException ;

    /**
     * Rider pushToken 등록
     * @param rider
     * @return
     * @throws AppTrException
     */
    public int updatePushToken(Rider rider) throws AppTrException ;

     * <p> getSubgroupRiderRels
     *
     * @param common
     * @return
     */
    public List<Rider> getSubgroupRiderRels(Common common) throws AppTrException;


}
