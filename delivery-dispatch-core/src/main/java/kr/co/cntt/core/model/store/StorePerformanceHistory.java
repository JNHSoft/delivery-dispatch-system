package kr.co.cntt.core.model.store;


import kr.co.cntt.core.concurrent.task.ILog;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>kr.co.cntt.core.model.store
 * <p>StorePerformanceHistory.java
 * <p>상점 수행 이력 model
 */
@Getter
@Setter
public class StorePerformanceHistory implements ILog {

    private static final long serialVersionUID = -2138768558020014535L;

    private int storePerformanceHistorySeq;
    private Integer storeSeq;
    private String storeId;
    private String ip;
    private String requestUri;
    private String performanceDatetime;
    private String performanceTask;
}
