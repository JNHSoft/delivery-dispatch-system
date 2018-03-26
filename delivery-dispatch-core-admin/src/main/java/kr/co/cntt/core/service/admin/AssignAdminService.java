package kr.co.cntt.core.service.admin;


import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.thirdParty.ThirdParty;

import java.util.List;

public interface AssignAdminService {

    /**
     * <p> putAdminAssignmentStatus
     *
     * @param admin
     * @return int
     */
    public int putAdminAssignmentStatus(Admin admin);

    /**
     * <p> postThirdParty
     *
     * @param thirdParty
     * @return int
     */
    public int postThirdParty(ThirdParty thirdParty);

    /**
     * <p> postThirdParty
     *
     * @param thirdParty
     * @return int
     */
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty);

    /**
     * <p> getAssignedAdvance
     *
     * @param reason
     * @return int
     */
    public List<Reason> getAssignedAdvance(Reason reason);

    /**
     * <p> getassignedReject
     *
     * @param reason
     * @return int
     */
    public List<Reason> getassignedReject(Reason reason);

    /**
     * <p> postAssignedAdvance
     *
     * @param reason
     * @return int
     */
    public int postAssignedAdvance(Reason reason);

    /**
     * <p> postAssignedReject
     *
     * @param reason
     * @return int
     */
    public int postAssignedReject(Reason reason);

}
