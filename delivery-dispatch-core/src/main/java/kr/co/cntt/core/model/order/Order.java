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
import kr.co.cntt.core.model.thirdParty.ThirdParty;
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
    private String assignedType;            // 2021.12.03 배정에 대한 Type 1=Auto, 2=Manual
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
    private String riderArrivedStoreDatetime; // 2021.12.15 라이더가 매장에 도착한 시간
    private String pickedUpDatetime;
    private String arrivedDatetime;         // 2020.05.08 배달지 문앞도착
    private String completedDatetime;
    private String returnDatetime;
    private String deviceOs;
    private String distance;
    private String assignedFirst;
    private String assignXy;
    private String riderArriveStoreXy;         // 2021.12.15 라이더가 매장에 도착한 위치
    private String pickupXy;
    private String arriveXy;                   // 배달지 문앞 도착 좌표
    private String completeXy;
    private String reservationStatus;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean isCombined;


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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String isAdmin;

    // 서드파티 추가
    private ThirdParty thirdParty;

    // 20.02.03 봇 사용으로 인한 필드 추가
    private String item_XA12;           //latitude
    private String item_XA11;           //longitude

    // 설문조사 점수 가져오기
    private String deliveryPoint;       // 배달 직원 점수
    private String speedPoint;          // 배달 속도 점수
}
