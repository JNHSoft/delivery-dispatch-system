package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;

import java.util.HashMap;
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
     * <p> Rider 토큰 만료일, last_access, working update
     *
     * @param token
     * @return
     */
    public int updateRiderSession(String token);

    /**
     * <p> Rider 정보 조회
     *
     * @return Rider Info 조회 결과값
     */
    public Rider getRiderInfo(Rider rider);

    /**
     * <p> 해당 스토어 Rider 목록
     *
     * @return 해당 스토어 Rider 목록 조회 결과값
     */
    public List<Rider> getStoreRiders(User user);


    /**
     * Rider 정보 업데이트
     * @param rider
     * @return
     */
    public int updateRiderInfo(Rider rider);

    /**
     * Rider 출/퇴근
     * @param rider
     * @return
     */
    public int updateWorkingRider(Rider rider);


    /**
     * Rider 위치 정보 전송
     * @param rider
     * @return
     */
    public int updateRiderLocation(Rider rider);

    /**
     * Rider 위치 정보 조회
     * @param rider
     * @return
     */
    public Rider getRiderLocation(Rider rider);

    /**
     * Rider들 위치 정보 조회 admin , store 나눔
     * @param rider
     * @return
     */
    // riders admin
    public List<Rider> getAdminRidersLocation(Rider rider);

    // riders store
    public List<Rider> getStoreRidersLocation(Rider rider);


    /**
     * <p> 해당 그룹 기사 목록 조회
     *
     * @param common
     * @return
     */
    public List<Rider> selectSubgroupRiderRels(Common common);

    /**
     * <p> 배정 모드 조회
     *
     * @param rider
     * @return
     */
    public String selectRiderAssignmentStatus(Rider rider);


    /**
     * <p> 라이더 재배치 스케줄링
     *
     * @param
     * @return
     */
    public void resetRiderReturnTime();

    /**
     * <p> 라이더 재배치
     *
     * @param rider
     * @return
     */
    public int updateRiderReturnTime(Rider rider);

    /**
     * <p> 자동 배정 관련 기사 목록
     *
     * @param storeId
     * @return
     */
    public List<Rider> selectForAssignRiders(String storeId);

    /**
     *  <p> 자동 휴식 관련 기사 목록
     *
     * @param
     * @return
     */
    public List<Rider> selectRiderRestHours();

    /**
     *  <p> 자동 휴식
     *
     * @param
     * @return
     */
    public int updateRiderWorkingAuto(HashMap map);

    public List<String> selectRiderToken(Order order);

    public List<String> selectRiderTokenByOrderId(Order order);

    public List<Reason> selectRejectReason(Common common);

    public List<String> selectRiderTokenByChatUserId(Chat chat);
}
