package kr.co.cntt.api.exporter;

public interface Api {
    // APP -> CNT APP API
    /** base uri */
    String Path = "/API";

    /** 토큰발행 */
    String GET_TOKEN = "/getToken.do";

    /** APP Version Check */
    String VERSION_CHECK = "/versionCheck.do";

    /** pushToken 등록   */
    String SET_PUSH_TOKEN = "/updatePushToken.json";


    // ############################################################################################ //

    /** [Admin] 정보 조회 */
    String ADMIN_INFO = "/getAdminInfo.json";

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


    // ############################################################################################ //

    /** Store 정보 조회 */
    String STORE_INFO = "/getStoreInfo.json";

    /** Store 정보 수정  */
    String STORE_UPDATE_INFO = "/updateStoreInfo.json";

    /** [Admin] 배정모드 수정**/
    String STORE_ASSIGNMENT_STATUS_PUT = "/putStoreAssignmentStatus.json";

    // ############################################################################################ //

    /** Order 등록 */
    String ORDER_POST = "/postOrder.json";

    /** Order 목록 조회 */
    String ORDERS_LIST = "/getOrders.json";

    /** Order 정보 조회 */
    String ORDER_INFO = "/getOrderInfo.json";

    /** Order 정보 수정 */
    String ORDER_UPDATE_INFO = "/putOrderInfo.json";

    /** Order 강제 배정 */
    String ORDER_ASSIGNED = "/putOrderAssigned.json";

    /** Order 픽업 */
    String ORDER_PICKEDUP = "/putOrderPickedUp.json";

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

    /** 공지사항 상세보기 진행중*/
    String NOTICE_DETAIL = "/detailNotice.json";

    /** 공지사항 리스트 */
    String NOTICE_LIST = "/getNoticeList.json";



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
