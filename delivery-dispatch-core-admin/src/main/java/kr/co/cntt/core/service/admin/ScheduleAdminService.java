package kr.co.cntt.core.service.admin;

public interface ScheduleAdminService {
    // 관리자 1페이지 통계 PizzaHut
    boolean sendStatisticsByMail();

    boolean sendStatisticsByMailForKFC();
}
