package kr.co.cntt.core.model.rider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.cntt.core.model.Dto;
import kr.co.cntt.core.model.group.Group;
import kr.co.cntt.core.model.group.SubGroup;
import kr.co.cntt.core.model.group.SubGroupRiderRel;
import kr.co.cntt.core.model.group.SubGroupStoreRel;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.order.OrderCheckAssignment;
import kr.co.cntt.core.model.store.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Rider extends User implements Dto {
    private static final long serialVersionUID = 2593751310056353317L;

    private String adminId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    private String code;
    private String phone;
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
    private String workingHours;
    private String restHours;
    private String teenager;
    private String returnTime;
    private String orderCount;
    private String workCount;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String assignCount;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String minAssignedDatetime;
    private String minPickedUpDatetime;
    private String minOrderStatus;

    private List<RiderSession> riderSession;

    private Group group;
    private SubGroup subGroup;
    private SubGroupRiderRel subGroupRiderRel;
    private SubGroupStoreRel subGroupStoreRel;
    private Store RiderStore;
    private Store OrderStore;
    private Order Order;
    private Store store;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String isAdmin;
    private String changePhone;
    private String orderStandbyDatetime;
    private String orderStandbyStatus;
    private int distance;

    private OrderCheckAssignment orderCheckAssignment;
}
