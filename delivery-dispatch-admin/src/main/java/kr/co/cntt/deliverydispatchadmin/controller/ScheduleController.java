package kr.co.cntt.deliverydispatchadmin.controller;


import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.admin.ScheduleAdminService;
import kr.co.cntt.core.service.admin.StoreAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

@Slf4j
@Controller
public class ScheduleController {
    /**
     * 객체 주입
     */
    StoreAdminService storeAdminService;
    ScheduleAdminService scheduleAdminService;

    @Value("${mail.sender}")
    private String internalIP;

    // 자동 데이터 관리 시스템
    private static Map<String, Object> autoCheckOverTimeStore = new HashMap<>();

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    @Autowired
    public ScheduleController(StoreAdminService storeAdminService, ScheduleAdminService scheduleAdminService){
        this.storeAdminService = storeAdminService;
        this.scheduleAdminService = scheduleAdminService;
    }

    /**
     * 20.12.31 초과된 주문이 있는지 확인 후 관련 정보 전달
     * 5분 단위로 실행
     * */
    @Scheduled(fixedDelayString = "300000")
    public void overTimeStore(){
        log.info("마지막 주문으로부터 30분이 초과된 주문 확인");
        Boolean bNotice = false;
        Store store = new Store();
        store.setRole("ROLE_ADMIN_AUTO");

        Map storeCount = storeAdminService.storeOverTimeCount(store);

        try{
            // 30분의 개수를 저장 후 비교
            if (storeCount.containsKey("over30") && Integer.parseInt(storeCount.get("over30").toString()) > 0){
                if (!autoCheckOverTimeStore.containsKey("over30")){
                    autoCheckOverTimeStore.put("over30", storeCount.get("over30"));
                    bNotice = true;
                }

                // 값을 비교한다
                if (Integer.parseInt(autoCheckOverTimeStore.get("over30").toString()) < Integer.parseInt(storeCount.get("over30").toString())){
                    bNotice = true;
                }

                if (Integer.parseInt(autoCheckOverTimeStore.get("over30").toString()) != Integer.parseInt(storeCount.get("over30").toString())){
                    autoCheckOverTimeStore.put("over30", storeCount.get("over30"));
                    log.info("over30 개수 발생 # " + storeCount.get("over30"));
                }
            }

            // 60분의 개수를 저장 후 비교
            if (storeCount.containsKey("over60") && Integer.parseInt(storeCount.get("over60").toString()) > 0){
                if (!autoCheckOverTimeStore.containsKey("over60")){
                    autoCheckOverTimeStore.put("over60", storeCount.get("over60"));
                    bNotice = true;
                }
                // 값을 비교한다
                if (Integer.parseInt(autoCheckOverTimeStore.get("over60").toString()) < Integer.parseInt(storeCount.get("over60").toString())){
                    bNotice = true;
                }

                if (Integer.parseInt(autoCheckOverTimeStore.get("over60").toString()) != Integer.parseInt(storeCount.get("over60").toString())){
                    autoCheckOverTimeStore.put("over60", storeCount.get("over60"));
                    log.info("over60 개수 발생 # " + storeCount.get("over60"));
                }
            }

            // Notice 발생 시, 데이터 체크 전송
            if (bNotice){
                // 웹소켓 데이터 전송
                redisService.setPublisher(Content.builder().type("check_overTimeStore").build());
                log.info("check Over Time Store 발생!");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 21.01.20 일정 시간 단위로 이메일 발송
     * 월~금요일 9시부터 22시까지 1시간 단위로 매일 발송
     * */
    @Scheduled(cron = "0 0 9-22 * * MON-FRI")
    public void statisticsSendByMail(){
        String strInternalIP = getInternalIP();

        if (Arrays.stream(internalIP.split(",")).filter(x -> x.equals(strInternalIP)).count() > 0){
            log.info("통계 자료 메일 발송 시작 ## " + new Date());
            log.info("피자헛 통계 전송 결과 : " + scheduleAdminService.sendStatisticsByMail());
            log.info("KFC 통계 전송 결과 : " + scheduleAdminService.sendStatisticsByMailForKFC());
            log.info("통계 자료 메일 발송 완료 ## " + new Date());
        }
    }

    /**
     * 21.05.24 일정 시간이 될 시에 라이더에게 공유된 매장 초기화
     * 매일 00시 05분에 매장에 공유된 타 라이더의 초기화 진행
     * */
    @Scheduled(cron = "0 05 0 * * *")
    public void resetRiderSharedForStore(){
        String strInternalIP = getInternalIP();

        if (Arrays.stream(internalIP.split(",")).filter(x -> x.equals(strInternalIP)).count() > 0){
            log.info("라이더의 타 매장 공유 설정 초기화 시작 ## " + new Date());
            log.info("라이더의 매장 공유 초기화 결과 : " + scheduleAdminService.resetRiderSharedStatusForStore());
            log.info("라이더의 타 매장 공유 설정 초기화 완료 ## " + new Date());
        }
    }

//    @ResponseBody
//    @GetMapping("/mailPizzaHut")
//    public String testMail(){
//        String strIP = getInternalIP();
//
//        System.out.println("########" + strIP + "####");
//        for (String data: internalIP.split(",")) {
//            System.out.println("#########" + data + "#######");
//        }
//
//
//        if (Arrays.stream(internalIP.split(",")).filter(x -> x.equals(strIP)).count() > 0){
//            System.out.println("########################################## 성공");
//            scheduleAdminService.sendStatisticsByMail();
//        }else {
//            System.out.println("########################################## 실패");
//        }
//
//        return "OK";
//    }
//
//    @ResponseBody
//    @GetMapping("/mailKFC")
//    public String testMailAtKFC(){
//        scheduleAdminService.sendStatisticsByMailForKFC();
//        return "OK";
//    }

    private String getInternalIP(){
        InetAddress ip = null;
        String strIP = "";

        // 리눅스의 IP 정보를 가져온다.
        try{
            boolean isLoopBack = true;
            Enumeration<NetworkInterface> en;
            en = NetworkInterface.getNetworkInterfaces();

            // 네트워크 인터페이스 종류를 모두 추출한다.
            while (en.hasMoreElements()){
                NetworkInterface ni = en.nextElement();
                if (ni.isLoopback())
                    continue;

                Enumeration<InetAddress> inetAddress = ni.getInetAddresses();

                while (inetAddress.hasMoreElements()){
                    InetAddress ia = inetAddress.nextElement();

                    if (ia.getHostAddress() != null && ia.getHostAddress().indexOf(".") != -1){
                        strIP = ia.getHostAddress();
                        isLoopBack = false;
                        //break;
                    }
                }

                if (!isLoopBack){
                    break;
                }
            }

        } catch (Exception e){

        }

        return strIP;
    }
}
