package kr.co.cntt.api.exporter;

public interface Api {
    // APP -> CNT APP API
    /** base uri */
    String Path = "/API";

    /** 토큰발행 */
    String GET_TOKEN = "/getToken.do";

    // ############################################################################################ //

    /** [Admin] 정보 조회 */
    String ADMIN_INFO = "/admin/getAdminInfo.json";

    /** [Admin] 기사 목록 조회 */
    String ADMIN_RIDERS_LIST = "/admin/getRiders.json";

    /** [Admin] 상점 목록 조회 */
    String ADMIN_STORES_LIST = "/admin/getStores.json";

    // ############################################################################################ //

    /** Rider 정보 조회 */
    String RIDER_INFO = "/getRiderInfo.json";

    /** 해당 스토어 Rider 목록*/

    String STORE_RIDERS= "/getStoreRiders.json";

    // ############################################################################################ //

    /** Store 정보 조회 */
    String STORE_INFO = "/getStoreInfo.json";

    // ############################################################################################ //

    /** Order 등록 */
    String ORDER_POST = "/postOrder.json";

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
