package kr.co.cntt.api.exporter;

public interface Api {
    // APP -> CNT APP API
    /** base uri */
    String Path = "/API";
    /** 서비스키 등록 */
    String SET_SERVICE_KEY = "/setservicekey.do";
    /** 토큰발행 */
    String GET_TOKEN = "/gettoken.do";
    /** 회원로그인 */
    String REG_LOGIN_DATE = "/login.json";

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
