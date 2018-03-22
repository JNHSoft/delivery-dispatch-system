package kr.co.cntt.api.service;

import kr.co.cntt.api.config.IServiceRouter;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.alarm.Alarm;
import kr.co.cntt.core.model.chat.Chat;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.payment.Payment;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.model.tracker.Tracker;
import lombok.Getter;

@Getter
public enum ApiServiceRouter implements IServiceRouter {
	
	/** [Admin] 정보조회 */
	ADMIN_INFO("adminService", "getAdminInfo", Common.class),

	/** [Admin] 그룹 목록 조회 */
	ADMIN_GROUP_LIST("adminService", "getGroups", Common.class),

	/** [Admin] 그룹 등록 */
	ADMIN_GROUP_POST("adminService", "postGroup", Group.class),

	/** [Admin] 그룹 수정 */
	ADMIN_GROUP_PUT("adminService", "putGroup", Group.class),

	/** [Admin] 그룹 삭제 */
	ADMIN_GROUP_DELETE("adminService", "deleteGroup", Group.class),

	/** [Admin] 서브그룹 목록 조회 */
	ADMIN_SUBGROUP_LIST("adminService", "getSubgroups", Common.class),

	/** [Admin] 서브그룹 등록 */
	ADMIN_SUBGROUP_POST("adminService", "postSubgroup", SubGroup.class),

	/** [Admin] 서브그룹 수정 */
	ADMIN_SUBGROUP_PUT("adminService", "putSubgroup", SubGroup.class),

	/** [Admin] 서브그룹 삭제 */
	ADMIN_SUBGROUP_DELETE("adminService", "deleteSubgroup", SubGroup.class),

    /** [Admin] 서브그룹 미지정 목록 조회 */
    ADMIN_NONE_SUBGROUP_STORE_REL_LIST("adminService", "getNoneSubgroupStoreRels", SubGroupStoreRel.class),

	/** [Admin] 상점 그룹 목록 조회 */
	ADMIN_SUBGROUP_STORE_REL_LIST("adminService", "getSubgroupStoreRels", SubGroupStoreRel.class),

	/** [Admin] 상점 그룹 설정 */
	ADMIN_SUBGROUP_STORE_REL_POST("adminService", "postSubgroupStoreRel", Store.class),

	/** [Admin] 상점 그룹 수정 */
	ADMIN_SUBGROUP_STORE_REL_PUT("adminService", "putSubgroupStoreRel", Store.class),

	/** [Admin] 상점 그룹 삭제 */
	ADMIN_SUBGROUP_STORE_REL_DELETE("adminService", "deleteSubgroupStoreRel", SubGroupStoreRel.class),

	/** [Admin] 기사 목록 조회 */
	ADMIN_RIDERS_LIST("adminService", "getRiders", Common.class),

	/** [Admin] 기사 등록 */
	ADMIN_RIDER_POST("adminService", "postRider", Rider.class),

	/** [Admin] 기사 삭제 */
	ADMIN_RIDER_DELETE("adminService", "deleteRider", Common.class),

	/** [Admin] 상점 목록 조회 */
	ADMIN_STORES_LIST("adminService", "getStores", Common.class),

	/** [Admin] 상점 등록 */
	ADMIN_STORE_POST("adminService", "postStore", Store.class),

	/** [Admin] 상점 삭제 */
	ADMIN_STORE_DELETE("adminService", "deleteStore", Common.class),

	/** [Admin] 배정모드 추가**/
	ADMIN_ASSIGNMENT_STATUS_PUT("adminService", "putAdminAssignmentStatus", Admin.class),

	/** [Admin] 배정서드파티 추가**/
	ADMIN_THIRD_PARTY_POST("adminService", "postThirdParty", ThirdParty.class),

	/** [Admin] 배정서드파티 수정**/
	ADMIN_THIRD_PARTY_PUT("adminService", "putThirdParty", ThirdParty.class),

	/** [Admin] 배정서드파티 삭제**/
	ADMIN_THIRD_PARTY_DELETE("adminService", "deleteThirdParty", ThirdParty.class),

	/** [Admin] 알림음 추가**/
	ADMIN_ALARM_POST("adminService", "postAlarm", Alarm.class),

	/** [Admin] 알림음 삭제**/
	ADMIN_ALARM_DELETE("adminService", "deleteAlarm", Alarm.class),

	/** [Admin] 통계 목록**/
	ADMIN_STATISTICS("adminService", "getAdminStatistics", Order.class),

	/** [Admin] 통계 조회**/
	ADMIN_STATISTICS_INFO("adminService", "getAdminStatisticsInfo", Order.class),

	/** [Admin] 배정 거절 사유 추가 **/
	ADMIN_REJECT_REASON_POST("adminService", "postRejectReason", Reason.class),

	/** [Admin] 배정 거절 사유 수정 **/
    ADMIN_REJECT_REASON_PUT("adminService", "putRejectReason", Reason.class),

	/** [Admin] 배정 거절 사유 삭제 **/
	ADMIN_REJECT_REASON_DELETE("adminService", "deleteRejectReason", Reason.class),

	/** [Admin] order 우선 배정 사유 추가 **/
	ORDER_FIRST_ASSIGNMENT_REASON_POST("adminService", "postOrderFirstAssignmentReason", Reason.class),

	/** [Admin] order 우선 배정 사유 수정 **/
	ORDER_FIRST_ASSIGNMENT_REASON_PUT("adminService", "putOrderFirstAssignmentReason", Reason.class),

	/** [Admin] order 우선 배정 사유 삭제 **/
	ORDER_FIRST_ASSIGNMENT_REASON_DELETE("adminService", "deleteOrderFirstAssignmentReason", Reason.class),

	// ############################################################################################ //

	/** Rider 정보조회 */
	RIDER_INFO("riderService", "getRiderInfo", Rider.class),

	/** Rider 정보수정 */
	RIDER_UPDATE_INFO("riderService", "updateRiderInfo", Rider.class),

	/** 해당 스토어 Rider 목록 */
	STORE_RIDERS("riderService", "getStoreRiders", User.class),

	/** Rider 출/퇴근 */
	RIDER_UPDATE_WORKING("riderService", "updateWorkingRider", Rider.class),

	/** Rider 위치 전송 */
	RIDER_UPDATE_RIDER_LOCATION("riderService", "updateRiderLocation", Rider.class),

	/** Rider 위치 조회 */
	RIDER_LOCATION_INFO("riderService", "getRiderLocation", Rider.class),

	/** Rider 들 위치 조회 */
	RIDERS_LOCATION_INFO("riderService", "getRidersLocation", Rider.class),

	/** 해당 그룹 소속 기사 목록 조회 */
	SUBGROUP_RIDER_REL_LIST("riderService", "getSubgroupRiderRels", Common.class),

	/** Rider 배정 모드 조회*/
	RIDER_ASSIGNMENT_STATUS_GET("riderService", "getRiderAssignmentStatus", Rider.class),

	/** Rider 재배치 */
	RIDER_RETURN_TIME_PUT("riderService","putRiderReturnTime", Rider.class),

	/** Rider 배정 거절 사유 목록 조회 */
	REJECT_REASON_LIST("riderService", "getRejectReasonList", Common.class),

	// ############################################################################################ //

	/** Store 정보조회 */
	STORE_INFO("storeService", "getStoreInfo", Store.class),

	/** Store 정보수정 */
	STORE_UPDATE_INFO("storeService", "updateStoreInfo", Store.class),

	/** Store 배정모드 추가**/
	STORE_ASSIGNMENT_STATUS_PUT("storeService", "putStoreAssignmentStatus", Store.class),

	/** Store상점 배정서드파티 설정**/
	STORE_THIRD_PARTY_PUT("storeService", "putStoreThirdParty", Store.class),

	/** 배정서드파티 목록**/
	THIRD_PARTY_GET("storeService", "getThirdParty", ThirdParty.class),

	/** Store 알림음 설정**/
	STORE_ALARM_PUT("storeService", "putStoreAlarm", Store.class),

	/** 알림음 목록**/
	ALARM_GET("storeService", "getAlarm", Store.class),

	/** Store 통계 목록**/
	STORE_STATISTICS("storeService", "getStoreStatistics", Order.class),

	/** Store 통계 조회**/
	STORE_STATISTICS_INFO("storeService", "getStoreStatisticsInfo", Order.class),

	// ############################################################################################ //

	/** Order 등록 */
	ORDER_POST("orderService", "postOrder", Order.class),

	/** Order 목록 조회 */
	ORDERS_LIST("orderService", "getOrders", Order.class),

	/** Order 정보 조회 */
	ORDER_INFO("orderService", "getOrderInfo", Order.class),

	/** Order 정보 수정 */
	ORDER_UPDATE_INFO("orderService", "putOrderInfo", Order.class),

	/** Order 강제 배정 */
	ORDER_ASSIGNED("orderService", "putOrderAssigned", Order.class),

	/** Order 픽업 */
	ORDER_PICKEDUP("orderService", "putOrderPickedUp", Order.class),

	/** Order 완료 */
	ORDER_COMPLETED("orderService", "putOrderCompleted", Order.class),

	/** Order 취소 */
	ORDER_CANCELED("orderService", "putOrderCanceled", Order.class),

	/** Order 취소 */
	ORDER_ASSIGN_CANCELED("orderService", "putOrderAssignCanceled", Order.class),

	/** Order 배정 확인 */
	ORDER_CONFIRM_ASSIGNMENT("orderService", "postOrderConfirm", Order.class),

	/** Order 배정 거부 */
	ORDER_DENY_ASSIGNMENT("orderService", "postOrderDeny", Order.class),

	/** Order 우선배정 */
	ORDER_ASSIGNED_FIRST("orderService", "putOrderAssignedFirst", Order.class),

	/** Order 우선 배정 사유 목록 조회 */
	ORDER_FIRST_ASSIGNMENT_REASON_GET("orderService", "getOrderFirstAssignmentReason", Common.class),

	// ############################################################################################ //

	/** Payment  결제 정보 조회 */
	PAYMENT_INFO("paymentService", "getPaymentInfo", Payment.class),

	/** Payment  결제 정보 등록 */
	PAYMENT_INFO_POST("paymentService", "postPaymentInfo", Payment.class),

	/** Payment  결제 정보 수정 */
	PAYMENT_INFO_UPDATE("paymentService", "updatePaymentInfo", Payment.class),

	// ############################################################################################ //

	/** Notice 등록 */
	NOTICE_POST("noticeService", "postNotice", Notice.class),

	/** Notice 수정 */
	NOTICE_UPDATE("noticeService", "updateNotice", Notice.class),

	/** Notice 삭제 */
	NOTICE_DELETE("noticeService", "deleteNotice", Notice.class),

	/** Notice 상세 보기 진행 중*/
	NOTICE_DETAIL("noticeService", "detailNotice", Notice.class),

	/** Notice 리스트 */
	NOTICE_LIST("noticeService", "getNoticeList", Notice.class),

	/** Notice 확인 */
	NOTICE_CONFIRM("noticeService", "putNoticeConfirm", Notice.class),

	/** pushToken 등록   */
	SET_PUSH_TOKEN("userService", "updatePushToken", User.class),

	// ############################################################################################ //

	/** 채팅 보내기 */
	CHAT_POST("chatService", "postChat", Chat.class),

	/** 채팅 읽기 */
	CHAT_GET("chatService", "getChat", Chat.class),

	/** 채팅방 목록 */
	CHATROOM_GET("chatService", "getChatRoom", Chat.class),

	// ############################################################################################ //

	/** 트래커 - 주문 정보 조회 */
	TRACKER_JSON_GET("trackerService", "getJsonTracker", Tracker.class),

	/** 트래커 - 주문 정보 조회 */
	TRACKER_GET("trackerService", "getTracker", Tracker.class),

	// ############################################################################################ //

//	/** 주문요청2 */
//	SET_ORDER2("TRO001SV_APP", "setorder2", R_TRO001_1.class),
//	/** 회원정보등록 */
//	REG_MEMBER("TRM002SV", "regmember", R_TRM002.class),
//	/** 회원정보수정 */
//	MOD_MEMBER("TRM002SV", "modmember", R_TRM002.class),
//	/** 회원휴먼 복구 */
//	RESTORE_MEMBER("TRM002SV", "restoremember", R_TRM002.class),
//	/** 회원휴먼 처리 */
//	DORMANCY_MEMBER("TRM002SV", "dormancymember", R_TRM002.class),
//	/** 회원탈퇴 */
//	DEL_MEMBER("TRM002SV", "delmember", R_TRM002.class),
//	/** 회원로그인 로그 */
//	REG_LOGIN_DATE("TRM002SV", "regLoginDate", R_TRM002.class),
	
	;
	private String qualifierName;
	private String method;
	private Class<?> in;
	//private Class<?> out;
	private ApiServiceRouter(String qualifierName, String method, Class<?> in) {
		this.qualifierName = qualifierName;
		this.method = method;
		this.in = in;
		//this.out = out;
	}

	public static ApiServiceRouter service(String serviceName) {
		for (ApiServiceRouter r : ApiServiceRouter.values()) {
			if (r.getMethod().equals(serviceName)) {
				return r;
			}
		}
		return null;
	}
}