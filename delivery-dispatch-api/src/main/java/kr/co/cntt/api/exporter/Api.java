package kr.co.cntt.api.exporter;

public interface Api {
    // APP -> CNT APP API
    /** base uri */
    String Path = "/API";

    /** 토큰발행 */
    String GET_TOKEN = "/getToken.do";

    /** 토큰만료 */
//    String PUT_TOKEN = "/putToken.do";

    /** APP Version Check */
    String VERSION_CHECK = "/versionCheck.do";

    /** pushToken 등록   */
    String SET_PUSH_TOKEN = "/updatePushToken.json";

    //////// 2020.08.27 라이더 승인 요청 프로세 관련 항목 ///////////////////////
    /** 가입 페이지 기본 정보 */
    String SIGN_UP_DEFAULT_INFO = "/getSignUpDefaultInfo.do";

    /** 라이더 승인 요청 등록 */
    String REG_RIDER_APPROVAL = "/postRiderApproval.json";

    /** 라이더 승인 진행 현황 조회 */
    String APPROVAL_CHECK = "/getCheckRiderApproval.json";

    /** 라이더 인증번호 문자 발송 */
    String SEND_APPLY_SMS = "/sendApplySMS.json";

    /** 라이더 인증번호 확인 */
    String CHECK_APPLY_SMS = "/checkApplySMS.json";

    //////// 2020.08.27 라이더 승인 요청 프로세 관련 항목 ///////////////////////

    // ############################################################################################ //

    /** [Admin] 정보 조회 */
    String ADMIN_INFO = "/getAdminInfo.json";

    /** [Admin] 정보 수정 */
    String ADMIN_INFO_PUT = "/putAdminInfo.json";

    /** [Admin] 그룹 목록 조회 */
    String ADMIN_GROUP_LIST = "/getGroups.json";

    /** [Admin] 그룹 등록 */
    String ADMIN_GROUP_POST = "/postGroup.json";

    /** [Admin] 그룹 수정 */
    String ADMIN_GROUP_PUT = "/putGroup.json";

    /** [Admin] 그룹 삭제 */
    String ADMIN_GROUP_DELETE = "/deleteGroup.json";

    /** [Admin] 서브그룹 목록 조회 */
    String ADMIN_SUBGROUP_LIST = "/getSubgroups.json";

    /** [Admin] 서브그룹 등록 */
    String ADMIN_SUBGROUP_POST = "/postSubgroup.json";

    /** [Admin] 서브그룹 수정 */
    String ADMIN_SUBGROUP_PUT = "/putSubgroup.json";

    /** [Admin] 서브그룹 삭제 */
    String ADMIN_SUBGROUP_DELETE = "/deleteSubgroup.json";

    /** [Admin] 상점 미지정 그룹 목록 조회 */
    String ADMIN_NONE_SUBGROUP_STORE_REL_LIST = "/getNoneSubgroupStoreRels.json";

    /** [Admin] 상점 그룹 목록 조회 */
    String ADMIN_SUBGROUP_STORE_REL_LIST = "/getSubgroupStoreRels.json";

    /** [Admin] 상점 그룹 설정 */
    String ADMIN_SUBGROUP_STORE_REL_POST = "/postSubgroupStoreRel.json";

    /** [Admin] 상점 그룹 수정 */
    String ADMIN_SUBGROUP_STORE_REL_PUT = "/putSubgroupStoreRel.json";


    /** [Admin] 상점 서브 그룹 수정  Nick 추가 */
    String ADMIN_STORE_SUBGROUP_PUT = "/putStoreSubgroup.json";

    /** [Admin] 기사 서브 그룹 수정  Nick 추가 */
    String ADMIN_RIDER_STORE_PUT = "/putRiderStore.json";

    /** [Admin] 상점 아이디 중복 확인  추가 */
    String ADMIN_STORE_LOGINID_CHECK = "/selectStoreLoginIdCheck.json";

    /** [Admin] 기사 아이디 중복 확인  추가 */
    String ADMIN_RIDER_LOGINID_CHECK = "/selectRiderLoginIdCheck.json";


    /** [Admin] 상점 그룹 삭제 */
    String ADMIN_SUBGROUP_STORE_REL_DELETE = "/deleteSubgroupStoreRel.json";

    /** [Admin] 기사 목록 조회 */
    String ADMIN_RIDERS_LIST = "/getRiders.json";

    /** [Admin] 기사 등록 */
    String ADMIN_RIDER_POST = "/postRider.json";

    /** [Admin] 기사 삭제 */
    String ADMIN_RIDER_DELETE = "/deleteRider.json";

    /** [Admin] 상점 목록 조회 */
    String ADMIN_STORES_LIST = "/getStores.json";

    /** [Admin] 상점 등록 */
    String ADMIN_STORE_POST = "/postStore.json";

    /** [Admin] 상점 삭제 */
    String ADMIN_STORE_DELETE = "/deleteStore.json";

    /** [Admin] 배정모드 추가**/
    String ADMIN_ASSIGNMENT_STATUS_PUT = "/putAdminAssignmentStatus.json";

    /** [Admin] 배정서드파티 추가**/
    String ADMIN_THIRD_PARTY_POST = "/postThirdParty.json";

    /** [Admin] 배정서드파티 수정**/
    String ADMIN_THIRD_PARTY_PUT = "/putThirdParty.json";

    /** [Admin] 배정서드파티 삭제**/
    String ADMIN_THIRD_PARTY_DELETE = "/deleteThirdParty.json";

    /** [Admin] 알림음 추가**/
    String ADMIN_ALARM_POST = "/postAlarm.json";

    /** [Admin] 알림음 삭제**/
    String ADMIN_ALARM_DELETE = "/deleteAlarm.json";

    /** [Admin] 통계 목록**/
    String ADMIN_STATISTICS = "/getAdminStatistics.json";

    /** [Admin] 통계 조회**/
    String ADMIN_STATISTICS_INFO = "/getAdminStatisticsInfo.json";

    /** [Admin] 배정 거절 사유 추가 */
    String ADMIN_REJECT_REASON_POST = "/postRejectReason.json";

    /** [Admin] 배정 거절 사유 수정 */
    String ADMIN_REJECT_REASON_PUT = "/putRejectReason.json";

    /** [Admin] 배정 거절 사유 삭제 */
    String ADMIN_REJECT_REASON_DELETE = "/deleteRejectReason.json";

    /** [Admin] order 우선 배정 사유 추가 */
    String ORDER_FIRST_ASSIGNMENT_REASON_POST = "/postOrderFirstAssignmentReason.json";

    /** [Admin] order 우선 배정 사유 수정 */
    String ORDER_FIRST_ASSIGNMENT_REASON_PUT = "/putOrderFirstAssignmentReason.json";

    /** [Admin] order 우선 배정 사유 삭제 */
    String ORDER_FIRST_ASSIGNMENT_REASON_DELETE = "/deleteOrderFirstAssignmentReason.json";

    // ############################################################################################ //

    /** Rider 정보 조회 */
    String RIDER_INFO = "/getRiderInfo.json";

    /** 해당 스토어 Rider 목록*/
    String STORE_RIDERS= "/getStoreRiders.json";

    /** Rider 정보 수정  */
    String RIDER_UPDATE_INFO = "/updateRiderInfo.json";

    /** Rider 출/퇴근   */
    String RIDER_UPDATE_WORKING = "/updateWorkingRider.json";

    /** Rider 위치 전송   */
    String RIDER_UPDATE_LOCATION = "/updateRiderLocation.json";

    /** Rider 자기 위치 정보 조회   */
    String RIDER_LOCATION_INFO = "/getRiderLocation.json";

    /** Rider 들 위치 정보 조회   */
    String RIDERS_LOCATION_INFO = "/getRidersLocation.json";

    /** 해당 그룹 소속 기사 목록 조회 */
    String SUBGROUP_RIDER_REL_LIST = "/getSubgroupRiderRels.json";

    /** Rider 배정 모드 조회**/
    String RIDER_ASSIGNMENT_STATUS_GET = "/getRiderAssignmentStatus.json";

    /** Rider 재배치**/
    String RIDER_RETURN_TIME_PUT = "/putRiderReturnTime.json";

    /** 배정 거절 사유 목록 조회 **/
    String REJECT_REASON_LIST = "/getRejectReasonList.json";

    /** Rider 경로정보 가져오기 */
    String RIDER_TOTAL_ROUTE_INFO = "/getTotalRouteInfos.json";
    /** Rider 경로정보 가져오기 */
    String RIDER_TOTAL_ROUTE_INFO2 = "/getTotalRouteInfos2.json";

    String RIDER_ACTIVE_INFO = "/getRiderActiveInfo.json";

    // ############################################################################################ //

    /** Store 정보 조회 */
    String STORE_INFO = "/getStoreInfo.json";

    /** Store 정보 수정  */
    String STORE_UPDATE_INFO = "/updateStoreInfo.json";

    /** Store 배정모드 설정**/
    String STORE_ASSIGNMENT_STATUS_PUT = "/putStoreAssignmentStatus.json";

    /** Store 상점 배정서드파티 설정**/
    String STORE_THIRD_PARTY_PUT = "/putStoreThirdParty.json";

    /** 배정서드파티 목록**/
    String THIRD_PARTY_GET = "/getThirdParty.json";

    /** Store 알림음 설정**/
    String STORE_ALARM_PUT = "/putStoreAlarm.json";

    /** 알림음 목록**/
    String ALARM_GET = "/getAlarm.json";

    /** Store 통계 목록**/
    String STORE_STATISTICS = "/getStoreStatistics.json";

    /** Store 통계 목록**/
    String STORE_STATISTICS_INFO = "/getStoreStatisticsInfo.json";

    // ############################################################################################ //

    /** Order 등록 */
    String ORDER_POST = "/postOrder.json";

    /** Order 목록 조회 */
    String ORDERS_LIST = "/getOrders.json";
    
    /** Order History 조회 */
    String SEARCH_ORDERS_LIST = "/getOrderHistory.json";

    /** Order 정보 조회 */
    String ORDER_INFO = "/getOrderInfo.json";

    /** Order 정보 수정 */
    String ORDER_UPDATE_INFO = "/putOrderInfo.json";

    /** Order 강제 배정 */
    String ORDER_ASSIGNED = "/putOrderAssigned.json";

    /** Order 픽업 */
    String ORDER_PICKEDUP = "/putOrderPickedUp.json";

    /** Order 도착 */
    String ORDER_ARRIVED = "/putOrderArrived.json";

    /** Order 완료 */
    String ORDER_COMPLETED = "/putOrderCompleted.json";

    /** Order 취소 */
    String ORDER_CANCELED = "/putOrderCanceled.json";

    /** Order 배정 취소 */
    String ORDER_ASSIGN_CANCELED = "/putOrderAssignCanceled.json";

    /** Order 배정 확인 */
    String ORDER_CONFIRM_ASSIGNMENT = "/postOrderConfirm.json";

    /** Order 배정 거부 */
    String ORDER_DENY_ASSIGNMENT = "/postOrderDeny.json";

    /** Order 우선배정 */
    String ORDER_ASSIGNED_FIRST = "/putOrderAssignedFirst.json";

    /** 우선 배정 사유 목록 조회 **/
    String ORDER_FIRST_ASSIGNMENT_REASON_GET = "/getOrderFirstAssignmentReason.json";

    /** Order 복귀 */
    String ORDER_RETURN = "/putOrderReturn.json";

    // ############################################################################################ //

    /** Payment 카드 결제 정보 조회 */
    String PAYMENT_INFO = "/getPaymentInfo.json";

    /** Payment 카드 결제 정보 등록 */
    String PAYMENT_INFO_POST = "/postPaymentInfo.json";

    /** Payment 카드 결제 정보 수정 */
    String PAYMENT_INFO_UPDATE = "/updatePaymentInfo.json";

    // ############################################################################################ //

    /** 공지사항 등록 */
    String NOTICE_POST = "/postNotice.json";

    /** 공지사항 수정 */
    String NOTICE_UPDATE = "/updateNotice.json";

    /** 공지사항 삭제 */
    String NOTICE_DELETE = "/deleteNotice.json";

    /** 공지사항 상세보기 */
    String NOTICE_DETAIL = "/detailNotice.json";

    /** 공지사항 리스트 */
    String NOTICE_LIST = "/getNoticeList.json";

    /** 공지사항 확인 */
    String NOTICE_CONFIRM = "/putNoticeConfirm.json";

    // ############################################################################################ //

    /** 채팅 보내기 */
    String CHAT_POST = "/postChat.json";

    /** 채팅 읽기 */
    String CHAT_GET = "/getChat.json";

    /** 채팅방 목록 */
    String CHATROOM_GET = "/getChatRoom.json";

    // ############################################################################################ //

    /** 트래커 - 주문 정보 조회 */
    String TRACKER_JSON_GET = "/getJsonTracker.json";

    /** 트래커 - 주문 정보 조회 */
    String TRACKER_GET = "/getTracker.do";

    // ############################################################################################ //

    // MVC || ADMIN || Call Center -> CNT APP API SERVER
    /** base uri */
    String INSIDE_PATH = "/API";

    /**
     * 생존 확인
     * #FROM Test#
     */
    String ALIVE_TEST = "/aliveTest";

}
