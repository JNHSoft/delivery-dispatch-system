package kr.co.cntt.core.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.rider.Rider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order extends Common implements Dto {
    private static final long serialVersionUID = -3663510383729036464L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String adminId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String storeId;
    private String riderId;
    private String combinedOrderId;
    private String status;
    private String address;
    private String areaAddress;
    private String districtAddress;
    private String streetAddress;
    private String estateAddress;
    private String buildingAddress;
    private String detailAddress;
    private String latitude;
    private String longitude;
    private String cookingTime;
    private String menuName;
    private String menuPrice;
    private String deliveryPrice;
    private String totalPrice;
    private String paid;
    private String message;
    private String phone;
    private String reservationDatetime;
    private String assignedDatetime;
    private String pickedUpDatetime;
    private String completedDatetime;
    private String deviceOs;
    private String distance;

    private Rider rider;

}
