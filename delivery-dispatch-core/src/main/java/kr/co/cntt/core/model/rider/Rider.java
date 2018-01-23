package kr.co.cntt.core.model.rider;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("rider")
public class Rider extends User implements Dto {
    private static final long serialVersionUID = 6384739497543017734L;

    private String createdDatetime;
    private String modifiedDatetime;
    private String lastAccess;
    private String chatUserId;
    private String adminId;
    private String branchId;
    private String phone;
    private String position;
    private String gender;
    private String employmentType;
    private String address;
    private String working;
    private String status;
    private String delay;
    private String latitude;
    private String longitude;
    private String locationUpdated;
    private String insurancePhone;
    private String vehicleNumber;
    private String emergencyPhone;
    private String entireOrderList;
    private String showCall;
    private String assignmentLimit;
    private String distance;
    private String hasRight;
    private String comment;
    private String deleted;
}

