package kr.co.cntt.core.model.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.payment.Payment;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.store.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Order extends Common implements Dto {
    private static final long serialVersionUID = -3663510383729036464L;

    private String regOrderId;
    private String webOrderId;
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
    private String returnDatetime;
    private String deviceOs;
    private String distance;
    private String assignedFirst;
    private String assignXy;
    private String pickupXy;
    private String completeXy;
    private String reservationStatus;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private char[] statusArray;
    private String count;

    private OrderCheckAssignment orderCheckAssignment;
    private Rider rider;
    private Store store;

    private Group group;
    private SubGroup subGroup;
    private SubGroupStoreRel subGroupStoreRel;


    private Payment payment;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String startDate;
    private String endDate;


}
