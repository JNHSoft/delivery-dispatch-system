package kr.co.cntt.api.scheduling;

import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.service.api.OrderService;
import kr.co.cntt.core.service.api.RiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Schedule {
    private RiderMapper riderMapper;
    private OrderService orderService;
    private RiderService riderService;

    @Autowired
    public Schedule(RiderMapper riderMapper, OrderService orderService, RiderService riderService) {
        this.riderMapper = riderMapper;
        this.orderService = orderService;
        this.riderService = riderService;
    }

    //라이더 재배치 스케줄링
    @Scheduled(fixedDelayString = "60000")
    public void ReturnReset() {
        riderMapper.resetRiderReturnTime();
        log.info("라이더 재배치 스케줄링 - 1분");
    }

    // 주문 자동 배정 스케줄링
//    @Scheduled(cron = "0 */5 * * * *")
    @Scheduled(fixedDelayString = "60000")
    public void autoAssignOrder() throws AppTrException {
        log.info("주문 자동 배정 스케줄링 - 1분마다다");
        orderService.autoAssignOrder();
        orderService.reservationOrders();
    }

    // 기사 휴식 시간 스케줄링
    @Scheduled(fixedDelayString = "60000")
    public void AutoRestTime() throws AppTrException {
        riderService.autoRiderWorking();
        log.info("기사 휴식 시간 스케줄링 - 1분");
    }

    // 라이더 유효기간 Over Check
    @Scheduled(cron = "0 0 1 * * ?")
    public void overExpDate() throws AppTrException{
        riderService.updateOverExpDate();
        log.info("유효기간 만료 체크 스케쥴링 - 매일 A.M 1");
    }
}
