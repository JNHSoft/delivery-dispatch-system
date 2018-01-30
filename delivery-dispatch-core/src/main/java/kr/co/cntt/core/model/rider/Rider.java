package kr.co.cntt.core.model.rider;

import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.login.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Rider extends User implements Dto {
    private static final long serialVersionUID = 6384739497543017734L;

    private String createdDatetime;
    private String modifiedDatetime;
    private String lastAccess;
    private String adminId;
    private String type;

    private String phone;
    private String position;
    private String gender;
    private String employmentType;
    private String address;
    private String working;
    private String status;
    private String latitude;
    private String longitude;
    private String locationUpdated;
    private String vehicleNumber;
    private String emergencyPhone;
    private String assignmentLimit;
    private String comment;
    private String deleted;

    private List<RiderSession> riderSession;
}

