package kr.co.cntt.api.scheduling;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.service.api.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Schedule {
    private RiderMapper riderMapper;
    private OrderService orderService;

    @Autowired
    public Schedule(RiderMapper riderMapper, OrderService orderService) {
        this.riderMapper = riderMapper;
        this.orderService = orderService;
    }

    //라이더 재배치 스케줄링
    @Scheduled(fixedDelayString = "60000")
    public void ReturnReset() throws InterruptedException {
        riderMapper.resetRiderReturnTime();
        log.info("라이더 재배치 스케줄링 - 1분");
    }

    // 주문 자동 배정 스케줄링
    @Scheduled(cron = "0 */5 * * * *")
    public void autoAssignOrder() throws InterruptedException, AppTrException {
        log.info("주문 자동 배정 스케줄링 - 5분마다다");
        orderService.autoAssignOrder();
    }

}
