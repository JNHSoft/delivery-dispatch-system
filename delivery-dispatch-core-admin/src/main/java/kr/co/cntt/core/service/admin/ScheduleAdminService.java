package kr.co.cntt.core.service.admin;

public interface ScheduleAdminService {
    // 관리자 1페이지 통계 PizzaHut
    boolean sendStatisticsByMail();
//    // 관리자 2페이지 통계 PizzaHut
//    boolean sendStatisticsByOrderByMail();
//    // 관리자 3페이지 통계 PizzaHut
//    boolean sendStatisticsByDateByMail();
//    // 관리자 4페이지 통계 PizzaHut
//    boolean sendStatisticsByIntervalByMail();

    boolean sendStatisticsByMailForKFC();
//    boolean sendStatisticsByOrderByMailForKFC();
//    boolean sendStatisticsByDateByMailForKFC();
//    boolean sendStatisticsByIntervalByMailForKFC();
}
