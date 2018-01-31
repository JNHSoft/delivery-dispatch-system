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

    /** [Admin] 기사 등록 */
    String ADMIN_RIDER_POST = "/admin/postRider.json";

    /** [Admin] 상점 목록 조회 */
    String ADMIN_STORES_LIST = "/admin/getStores.json";

    /** [Admin] 상점 등록 */
    String ADMIN_STORE_POST = "/admin/postStore.json";

    // ############################################################################################ //

    /** Rider 정보 조회 */
    String RIDER_INFO = "/v1/getRiderInfo.json";

    /** 해당 스토어 Rider 목록*/
    String STORE_RIDERS= "/v1/getStoreRiders.json";

    // ############################################################################################ //

    /** Store 정보 조회 */
    String STORE_INFO = "/v1/getStoreInfo.json";

    /** Store 정보 수정  */
    String STORE_UPDATE_INFO = "/v1/updateStoreInfo.json";

    // ############################################################################################ //

    /** Order 등록 */
    String ORDER_POST = "/v1/postOrder.json";

    // ############################################################################################ //


    /** 공지사항 등록 */
    String NOTICE_POST = "/notice/postNotice.json";

    /** 공지사항 수정 */
    String NOTICE_UPDATE = "/notice/updateNotice.json";

    /** 공지사항 삭제 */

    /** 공지사항 상세보기 */

    /** 공지사항 리스트 */


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
