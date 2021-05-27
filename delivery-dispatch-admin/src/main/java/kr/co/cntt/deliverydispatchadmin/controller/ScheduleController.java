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
