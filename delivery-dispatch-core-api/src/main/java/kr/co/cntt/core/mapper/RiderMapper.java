package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderRouteInfo;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.sms.SmsApplyInfo;
import kr.co.cntt.core.model.store.Store;

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
     * <p> 라이더 세션 관련 변경
     *
     * @param session
     * @return
     */
    public int updateRiderSession(RiderSession session);

    /**
     * <p> Rider 정보 조회
     *
     * @return Rider Info 조회 결과값
     */
    public Rider getRiderInfo(Common common);

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
     * @param map(storeId, orderId)
     * @return
     */
    public List<Rider> selectForAssignRiders(Map map);

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
    public Integer updateRiderWorkingAuto(HashMap map);

    public List<Map<String, String>> selectRiderToken(Order order);

    public List<Map<String, String>> selectRiderTokenByOrderId(Order order);

    public List<Reason> selectRejectReason(Common common);

    public List<Map> selectRiderTokenByChatUserId(Chat chat);

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
     * Rider 비밀번호 초기화 (매장 요청 시)
     *
     * @param rider
     * @return
     */
    public int resetRiderPasswordforStore(Rider rider);

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

    /**
     * 20.08.07
     * 라이더 승인 관련
     * */
    public List<RiderApprovalInfo> selectApprovalRiderList(Common common);

    /**
     * 라이더 승인 정보
     * */
    public RiderApprovalInfo selectApprovalRiderInfo(Common common);

    /**
     * 라이더 승인 정보 변경
     * */
    public int updateApprovalRiderInfo(RiderApprovalInfo riderInfo);

    /**
     * 라이더 요청 등록 페이지에서 필요로 하는 기본 정보
     * */
    public List<Store> selectAllStore();

    /**
     * 라이더 가입 요청
     * */
    public int insertApprovalInfo(RiderApprovalInfo riderInfo);

    /**
     * 라이더 암호 가져오기
     * */
    public String selectApprovalRiderPw(String id);

    /**
     * 라이더 정보 삭제 ( 라이더 및 서브 그룹 )
     * */
    public int deleteRiderInfo(Rider rider);

    /**
     * 유효기간 만료 계정 체크
     * */
    int updateOverExpDate();

    /**
     * 유효기간 만료 계정 Token 삭제
     * */
    int deleteOverExpDateToken();

    /**
     * Approval Rider Row data 삭제
     * */
    int deleteApprovalRiderRowData(RiderApprovalInfo riderInfo);

    /**
     * 라이더 인증번호 저장
     * */
    int insertRiderApplySMS(SmsApplyInfo smsApplyInfo);

    /**
     * 라이더 정보 업데이트
     * */
    int updateRiderApplySMS(SmsApplyInfo smsApplyInfo);

    /**
     * 라이더 인증번호 체크
     * */
    SmsApplyInfo selectRiderApplySMS(SmsApplyInfo smsApplyInfo);

    /**
     * 라이더 세션 변경 (os 정보 포함)
     * */
    int updatePushToken(Rider rider);
    
    
    /**
     * 2021-03-05 라이더 경로와 관련한 데이터를 가져오는 쿼리
     * */

    /// 라이더가 가지고 있는 주문에 대한 리스트
    List<Order> getOrderForRider(Rider rider);

    /// 주문에 대한 매장정보를 가져온다.
    List<Store> getStoreInfoAtOrder(String[] arrStoreID);

    /**
     * 가입 승인 요청 시, 기존에 등록된 LOGIN ID가 있는지 확인
     * */
    List<Rider> selectRegistRiderInfoList(User user);
}
