package kr.co.cntt.api.exporter;

public interface Api {
    // APP -> CNT APP API
    /** base uri */
    String Path = "/API";

    /** 토큰발행 */
    String GET_TOKEN = "/getToken.do";

    // ############################################################################################ //

    /** [Admin] 정보 조회 */
    String ADMIN_INFO = "/getAdminInfo.json";

    /** [Admin] 기사 목록 조회 */
    String ADMIN_RIDERS_LIST = "/getRiders.json";

    /** [Admin] 기사 등록 */
    String ADMIN_RIDER_POST = "/postRider.json";

    /** [Admin] 상점 목록 조회 */
    String ADMIN_STORES_LIST = "/getStores.json";

    /** [Admin] 상점 등록 */
    String ADMIN_STORE_POST = "/postStore.json";

    /** [Admin] 상점 기사 전체 소속 목록 */
    String ADMIN_STORE_RIDER_REL = "/getStoreRiderRel.json";

    // ############################################################################################ //

    /** Rider 정보 조회 */
    String RIDER_INFO = "/getRiderInfo.json";

    /** 해당 스토어 Rider 목록*/
    String STORE_RIDERS= "/getStoreRiders.json";

    /** Rider 정보 수정  */
    String RIDER_UPDATE_INFO = "/updateRiderInfo.json";

    // ############################################################################################ //

    /** Store 정보 조회 */
    String STORE_INFO = "/getStoreInfo.json";

    /** Store 정보 수정  */
    String STORE_UPDATE_INFO = "/updateStoreInfo.json";

    // ############################################################################################ //

    /** Order 등록 */
    String ORDER_POST = "/postOrder.json";

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
