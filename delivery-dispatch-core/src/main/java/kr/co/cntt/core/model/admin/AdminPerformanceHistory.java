package kr.co.cntt.core.model.admin;

import kr.co.cntt.core.concurrent.task.ILog;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>kr.co.cntt.core.model.admin
 * <p>AdminPerformanceHistory.java
 * <p>관리자 수행 이력 model
 */
@Getter
@Setter
public class AdminPerformanceHistory implements ILog {
    private static final long serialVersionUID = 3101714190367728568L;

    private int adminPerformanceHistorySeq;
    private Integer adminSeq;
    private String adminId;
    private String ip;
    private String requestUri;
    private String performanceDatetime;
    private String performanceTask;
}

