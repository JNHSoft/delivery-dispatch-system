package kr.co.cntt.core.model.order;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order implements Dto {
    private static final long serialVersionUID = -3663510383729036464L;

    private String createdDatetime;
    private String modifiedDatetime;
    private String id;
    private String adminId;
    private String storeId;
    private String riderId;
    private String address;
    private String detailAddress;
    private String latitude;
    private String longitude;
    private String status;
    private String menuName;
    private String menuPrice;
    private String cookingTime;
    private String toBePaid;
    private String paid;
    private String message;
    private String phone;
    private String reservation;
    private String reservationDatetime;
    private String assignedDatetime;
    private String pickedUpDatetime;
    private String completedDatetime;
    private String requiredGender;
    private String deviceOs;
}
