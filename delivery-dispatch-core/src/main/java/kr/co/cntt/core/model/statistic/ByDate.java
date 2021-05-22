package kr.co.cntt.core.model.statistic;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ByDate implements Dto {

    private static final long serialVersionUID = -1934384042972192641L;

    private String storeName;
    private String dayToDay;
    private String orderPickup;
    private String pickupComplete;
    private String stayTime;
    private String orderComplete;
    private String completeReturn;
    private String pickupReturn;
    private String orderReturn;
    private String minD7Below;
    private String min30Below;
    private String min30To40;
    private String min40To50;
    private String min50To60;
    private String min60To90;
    private String min90Under;
    private String errtc;
    private String thirdtc;
    private String tc;
    private String totalSales;
    private String tplh;
    private String spmh;
    private String totalPickupReturn;
    private String avgDistance;

    private String d7Success;
   
}
