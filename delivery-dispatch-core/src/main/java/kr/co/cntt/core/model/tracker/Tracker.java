package kr.co.cntt.core.model.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.order.OrderCheckAssignment;
import kr.co.cntt.core.model.payment.Payment;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tracker implements Dto {
    private static final long serialVersionUID = 4143172714593838816L;

    private String id;
    private String createdDatetime;
    private String reservationDatetime;
    private String regOrderId;
    private String webOrderId;
    private String status;
    private String latitude;
    private String longitude;
    private String cookingTime;
    private String detailAddress;

    private String code;
    private String storeName;
    private String storePhone;
    private String storeLatitude;
    private String storeLongitude;
    private String storeDetailAddress;

    private String riderName;
    private String riderPhone;
    private String riderLatitude;
    private String riderLongitude;
    private String requestDate;

    private String distance;

    // 트래커가 배달 중인 주문에 대한 브랜드 코드 및 네임
    private String brandCode;
    private String brandName;

    //트래커가 배달 중인 스토어에 대한 이미지 경로
    private String brandImg;

}
