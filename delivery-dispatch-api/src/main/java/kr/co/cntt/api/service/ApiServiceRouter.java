package kr.co.cntt.api.service;

import kr.co.cntt.api.config.IServiceRouter;

import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.R_Rider;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import lombok.Getter;

@Getter
public enum ApiServiceRouter implements IServiceRouter {
	
	/** Admin 정보조회 */
	ADMIN_INFO("adminService", "getAdminInfo", Admin.class),

	/** Rider 정보조회 */
	RIDER_INFO("riderService", "getRiderInfo", Rider.class),

	/** Store 정보조회 */
	STORE_INFO("storeService", "getStoreInfo", Store.class),

	/** Order 등록 */
	ORDER_POST("orderService", "postOrder", Order.class),

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