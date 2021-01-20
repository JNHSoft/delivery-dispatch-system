package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.service.admin.ScheduleAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service("scheduleAdminService")
public class ScheduleAdminServiceImpl implements ScheduleAdminService {

    @Value("${mail.id}")
    private String user;

    @Value("${mail.pwd}")
    private String pwd;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.auth}")
    private String auth;

    @Value("${mail.enable}")
    private String enable;

    private String kAdmin = "";
    private String pAdmin = "";

    @Override
    public boolean sendStatisticsByMail() {
        // PizzaHut 통계 엑셀 가져오기
        
        
        return false;
    }

    @Override
    public boolean sendStatisticsByOrderByMail() {
        return false;
    }

    @Override
    public boolean sendStatisticsByDateByMail() {
        return false;
    }

    @Override
    public boolean sendStatisticsByIntervalByMail() {
        return false;
    }

    @Override
    public boolean sendStatisticsByMailForKFC() {
        return false;
    }

    @Override
    public boolean sendStatisticsByOrderByMailForKFC() {
        return false;
    }

    @Override
    public boolean sendStatisticsByDateByMailForKFC() {
        return false;
    }

    @Override
    public boolean sendStatisticsByIntervalByMailForKFC() {
        return false;
    }
}
