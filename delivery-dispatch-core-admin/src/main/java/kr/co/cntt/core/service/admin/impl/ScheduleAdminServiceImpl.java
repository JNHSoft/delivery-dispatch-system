package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.service.admin.ScheduleAdminService;
import kr.co.cntt.core.service.admin.StatisticsAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("scheduleAdminService")
public class ScheduleAdminServiceImpl implements ScheduleAdminService {

    /**
     * 객체 주입
     */
    RiderMapper riderMapper;
    StatisticsAdminService statisticsAdminService;


    @Autowired
    public ScheduleAdminServiceImpl(RiderMapper riderMapper, StatisticsAdminService statisticsAdminService){
        this.riderMapper = riderMapper;
        this.statisticsAdminService = statisticsAdminService;
    }

    @Override
    public int resetRiderSharedStatusForStore(){
        return riderMapper.updateResetSharedRiderForStore();
    }
}
