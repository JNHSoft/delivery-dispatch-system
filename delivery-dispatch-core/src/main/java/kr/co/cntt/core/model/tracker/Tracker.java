package kr.co.cntt.core.model.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.cntt.core.model.Dto;
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
    private String riderLeftImg;
    private String riderRightImg;

    //제 3자배송인지 확인하기 위함
    private String thirdPartyId;

    // 설문조사를 완료 했는지 확인하기
    private String deliveryPoint;       // 배달 직원 점수
    private String speedPoint;          // 배달 속도 점수
}
