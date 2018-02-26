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
    private String storeId;
    private String riderId;
    private String combinedOrderId;
    private String status;
    private String address;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String areaAddress;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String districtAddress;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String streetAddress;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String estateAddress;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    private OrderCheckAssignment orderCheckAssignment;
    private Rider rider;

}
