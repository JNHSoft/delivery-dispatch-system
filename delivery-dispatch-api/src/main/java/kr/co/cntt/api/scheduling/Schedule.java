package kr.co.cntt.api.scheduling;

import kr.co.cntt.core.mapper.RiderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Schedule {
    private RiderMapper riderMapper;

    @Autowired
    public Schedule(RiderMapper riderMapper) {
        this.riderMapper = riderMapper;
    }

    //라이더 재배치 스케줄링
    @Scheduled(fixedDelayString = "60000")
    public void ReturnReset() throws InterruptedException{
        riderMapper.resetRiderReturnTime();
        System.out.println("라이더 재배치 스케줄링 - 1분");
    }
}
