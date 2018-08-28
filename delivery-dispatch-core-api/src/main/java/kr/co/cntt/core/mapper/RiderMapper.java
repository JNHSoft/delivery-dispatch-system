package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.login.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map selectLoginRider(Rider rider);

    /**
     * <p> selectRiderTokenCheck
     *
     * @return Count Int
     */
    public int selectRiderTokenCheck(Rider rider);

    /**
     * <p> selectRiderTokenLoginCheck
     *
     * @return Count Int
     */
    public User selectRiderTokenLoginCheck(Rider rider);

    /**
     * <p> Rider Session Insert
     *
     * @return Insert 결과값
     */
    public int insertRiderSession(Rider rider);


    /**
     * insert Admin token
     *
     * @param rider
     * @return
     */
    public int insertAdminRiderSession(Rider rider);




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
     * Rider 정보 업데이트 -상점
     * @param rider
     * @return
     */
    public int updateRiderInfoStore(Rider rider);

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

    public SubGroupRiderRel selectMySubgroupRiderRels(Rider rider);

    /**
     *  <p> 라이더현황
     *
     * @param
     * @return
     */
    public List<Rider> selectRiderNow(Common common);
    public List<Rider> selectRiderFooter(Common common);
    public List<Rider> selectMyStoreRiderRels(Common common);

    /**
     * <p> 모바일 버전 조회
     *
     * @param device
     * @return
     */
    public String selectMobileVersion(String device);

    /**
     * Rider 비밀번호 초기화
     *
     * @param rider
     * @return
     */
    public int resetRiderPassword(Rider rider);

    /**
     * Rider 주문대기푸시시간설정
     *
     * @param common
     * @return
     */
    public int updateRiderOrderStandbyDateTime(Common common);

    /**
     * Rider 주문대기상태변경
     *
     * @param common
     * @return
     */
    public int updateRiderOrderStandbyStatus(Common common);

}
