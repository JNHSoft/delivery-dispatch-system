package kr.co.cntt.api.service;

import kr.co.cntt.api.config.IServiceRouter;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.store.StoreRiderRel;
import lombok.Getter;

@Getter
public enum ApiServiceRouter implements IServiceRouter {
	
	/** [Admin] 정보조회 */
	ADMIN_INFO("adminService", "getAdminInfo", Common.class),

	/** [Admin] 기사 목록 조회 */
	ADMIN_RIDERS_LIST("adminService", "getRiders", Common.class),

	/** [Admin] 기사 등록 */
	ADMIN_RIDER_POST("adminService", "postRider", Rider.class),

	/** [Admin] 기사 삭제 */
	ADMIN_RIDER_DELETE("adminService", "deleteRider", Rider.class),

	/** [Admin] 상점 목록 조회 */
	ADMIN_STORES_LIST("adminService", "getStores", Common.class),

	/** [Admin] 상점 등록 */
	ADMIN_STORE_POST("adminService", "postStore", Store.class),

	/** [Admin] 상점 삭제 */
	ADMIN_STORE_DELETE("adminService", "deleteStore", Store.class),

	/** [Admin] 상점 기사 전체 소속 목록 */
	ADMIN_STORE_RIDER_REL("adminService", "getStoreRiderRel", Common.class),

	/** [Admin] 상점 기사 소속 등록, 변경 */
	ADMIN_RIDER_TO_STORE_PUT("adminService", "putStoreRiderRel", StoreRiderRel.class),

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



	// ############################################################################################ //

	/** Store 정보조회 */
	STORE_INFO("storeService", "getStoreInfo", Store.class),

	/** Store 정보수정 */
	STORE_UPDATE_INFO("storeService", "updateStoreInfo", Store.class),

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