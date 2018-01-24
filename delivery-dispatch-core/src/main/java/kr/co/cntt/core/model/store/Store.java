package kr.co.cntt.core.model.store;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("store")
public class Store extends User implements Dto {
    private static final long serialVersionUID = 6587372870369862831L;

    private String createdDatetime;
	private String modifiedDatetime;
	private String expirationDate;
	private String lastAccess;
	private String chatUserId;
	private String adminId;
	private String branchId;

	private String code;
	private String phone;
	private String storeName;
	private String chStoreName;
	private String storePhone;
	private String birth;
	private String email;
	private String addr;
	private String detailAddr;
	private String chAddr;
	private String chDetailAddr;
	private String latitude;
    private String longitude;
    private String comment;
	private String storeStatus;
    private String showNumOfRiders;
	private String storeCookingTime;
    private String menuFeeStatus;
    private String customerPhoneStatus;
    private String defaultFee;
	private String radius;
    private String storeDistanceSort;
	private String assignmentStatus;
    private String deleted;
}

