package kr.co.cntt.core.mapper;

import kr.co.cntt.core.annotation.DeliveryDispatchMapper;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.*;
import kr.co.cntt.core.model.sms.SmsApplyInfo;
import kr.co.cntt.core.model.store.Store;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly=true)
    public Map selectLoginRider(Rider rider);

    /**
     * <p> selectRiderTokenCheck
     *
     * @return Count Int
     */
    @Transactional(readOnly=true)
    public int selectRiderTokenCheck(Rider rider);

    /**
     * <p> selectRiderTokenLoginCheck
     *
     * @return Count Int
     */
    @Transactional(readOnly=true)
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
    @Transactional(readOnly=true)
    public Rider getRiderInfo(Common common);

    /**
     * <p> 해당 스토어 Rider 목록
     *
     * @return 해당 스토어 Rider 목록 조회 결과값
     */
    @Transactional(readOnly=true)
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
     * 21-03-16
     * 라이더 출근 히스토리에 데이터가 있는지 확인
     * */
    @Transactional(readOnly=true)
    Map<String, Object> selectRiderWorkingHistory(Rider rider);

    /**
     * 21-03-16
     * 라이더 출근에 대한 정보 등록
     * */
    int insertRiderWorkingHistory(Rider rider);

    /**
     * 21-03-16
     * 라이더의 퇴근에 대한 정보 등록
     * */
    int updateRiderWorkingHistory(Rider rider);


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
    @Transactional(readOnly=true)
    public Rider getRiderLocation(Rider rider);

    /**
     * Rider들 위치 정보 조회 admin , store 나눔
     * @param rider
     * @return
     */
    // riders admin
    @Transactional(readOnly=true)
    public List<Rider> getAdminRidersLocation(Rider rider);

    // riders store
    @Transactional(readOnly=true)
    public List<Rider> getStoreRidersLocation(Rider rider);


    /**
     * <p> 해당 그룹 기사 목록 조회
     *
     * @param common
     * @return
     */
    @Transactional(readOnly=true)
    public List<Rider> selectSubgroupRiderRels(Common common);

    /**
     * <p> 배정 모드 조회
     *
     * @param rider
     * @return
     */
    @Transactional(readOnly=true)
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
    @Transactional(readOnly=true)
    public List<Rider> selectForAssignRiders(Map map);

    /**
     *  <p> 자동 휴식 관련 기사 목록
     *
     * @param
     * @return
     */
    @Transactional(readOnly=true)
    public List<Rider> selectRiderRestHours();

    /**
     *  <p> 자동 휴식
     *
     * @param
     * @return
     */
    public Integer updateRiderWorkingAuto(HashMap map);

    @Transactional(readOnly=true)
    public List<Map> selectRiderToken(Order order);

    @Transactional(readOnly=true)
    public List<Map> selectRiderTokenByOrderId(Order order);

    @Transactional(readOnly=true)
    public List<Reason> selectRejectReason(Common common);

    @Transactional(readOnly=true)
    public List<Map> selectRiderTokenByChatUserId(Chat chat);

    @Transactional(readOnly=true)
    public SubGroupRiderRel selectMySubgroupRiderRels(Rider rider);

    /**
     *  <p> 라이더현황
     *
     * @param
     * @return
     */
    @Transactional(readOnly=true)
    public List<Rider> selectRiderNow(Common common);
    @Transactional(readOnly=true)
    public List<Rider> selectRiderFooter(Common common);
    @Transactional(readOnly=true)
    public List<Rider> selectMyStoreRiderRels(Common common);

    /**
     * <p> 모바일 버전 조회
     *
     * @param device
     * @return
     */
    @Transactional(readOnly=true)
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
    @Transactional(readOnly=true)
    public List<RiderApprovalInfo> selectApprovalRiderList(Common common);

    /**
     * 라이더 승인 정보
     * */
    @Transactional(readOnly=true)
    public RiderApprovalInfo selectApprovalRiderInfo(Common common);

    /**
     * 라이더 승인 정보 변경
     * */
    public int updateApprovalRiderInfo(RiderApprovalInfo riderInfo);

    /**
     * 라이더 요청 등록 페이지에서 필요로 하는 기본 정보
     * */
    @Transactional(readOnly=true)
    public List<Store> selectAllStore();

    /**
     * 라이더 가입 요청
     * */
    public int insertApprovalInfo(RiderApprovalInfo riderInfo);

    /**
     * 라이더 암호 가져오기
     * */
    @Transactional(readOnly=true)
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
    @Transactional(readOnly=true)
    SmsApplyInfo selectRiderApplySMS(SmsApplyInfo smsApplyInfo);

    /**
     * 라이더 세션 변경 (os 정보 포함)
     * */
    int updatePushToken(Rider rider);


    /**
     * 2021-03-05 라이더 경로와 관련한 데이터를 가져오는 쿼리
     * */

    /// 라이더가 가지고 있는 주문에 대한 리스트
    @Transactional(readOnly=true)
    List<Order> getOrderForRider(Rider rider);

    /// 주문에 대한 매장정보를 가져온다.
    @Transactional(readOnly=true)
    List<Store> getStoreInfoAtOrder(String[] arrStoreID);

    /**
     * 가입 승인 요청 시, 기존에 등록된 LOGIN ID가 있는지 확인
     * */
    @Transactional(readOnly=true)
    List<Rider> selectRegistRiderInfoList(User user);

    /// 21-03-16
    /// 라이더의 당일 활동에 필요로 하는 정보 추출
    @Transactional(readOnly=true)
    RiderActiveInfo selectRiderActiveInfo(Rider rider);

    /**
     * 21.05.21 라이더가 속해질 타 매장 정보 저장
     * */
    int insertSharedStoreInfo(Rider rider);
    /**
     * 21.05.21 라이더가 속해진 타 매장 정보 삭제
     * */
    int deleteSharedStoreInfo(Rider rider);

    /**
     * 21.05.21 매장에 할당된 타 매장의 라이더 정보 초기화
     * */
    int updateResetSharedRiderForStore();
}
