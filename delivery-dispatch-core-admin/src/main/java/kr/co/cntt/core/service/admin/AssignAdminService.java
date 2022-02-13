package kr.co.cntt.core.service.admin;

import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.store.StoreBeacon;
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
     * <p> 배정 서드파티 수정
     *
     * @param thirdParty
     * @return
     */
    public int updateThirdParty(ThirdParty thirdParty);


    /**
     * <p> 배정 서드파티 삭제
     *
     * @param thirdParty
     * @return
     */
    public int deleteThirdParty(ThirdParty thirdParty);




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
     * order 우선 배정 사유 수정
     *
     * @param reason
     * @return
     */
    public int putAssignedAdvance(Reason reason);


    /**
     * <p> order 우선 배정 사유 삭제
     *
     * @param reason
     * @return
     */
    public int deleteAssignedAdvance(Reason reason);

    /**
     * <p> postAssignedReject
     *
     * @param reason
     * @return int
     */
    public int postAssignedReject(Reason reason);

    /**
     * <p> 배정 거절 사유 수정
     *
     * @param reason
     * @return
     */
    public int putAssignedReject(Reason reason);


    /**
     * <p> 배정 거절 사유 삭제
     *
     * @param reason
     * @return
     */
    public int deleteRejectReason(Reason reason);


    /**
     * <p>Beacon 정보 업데이트</p>
     * */
    int updateStoreBeaconInfo(StoreBeacon beacon);

    /**
     * <p> Beacon 공통 정보 가져오기 - 관리자 </p>
     * */
    Admin getBeaconCommInfo(Admin adminInfo);

    /**
     * <p>Beacon 공통 정보 업데이트 - 관리자 정보</p>
     * */
    int updateAdminBeaconInfo(Admin adminInfo);

}
