package kr.co.cntt.core.service.api;


import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.rider.Rider;

import java.util.List;
import java.util.Map;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.store.Store;

public interface RiderService {
    /**
     * <p> selectLoginRider
     *
     * @return
     */
    public Map selectLoginRider(Rider rider);

    /**
     * <p> selectRiderTokenCheck
     *
     * @return
     */
    public int selectRiderTokenCheck(Rider rider);

    /**
     * <p> selectRiderTokenCheck
     *
     * @return
     */
    public User selectRiderTokenLoginCheck(Rider rider);

    /**
     * <p> insertRiderSession
     *
     * @return
     */
    public int insertRiderSession(Rider rider);

    /**
     * <p> updateRiderSession
     *
     * @param token
     * @return
     */
    public int updateRiderSession(String token);

    /**
     * <p> selectRiderInfo
     *
     * @return
     */
    public Rider getRiderInfo(Rider rider) throws AppTrException;

    /**
     * <p> selectStoreRiders
     *
     * @return
     */
    public List<Rider> getStoreRiders(User user) throws AppTrException;

    /**
     * rider 정보 수정
     *
     * @param rider
     * @return
     */
    public int updateRiderInfo(Rider rider) throws AppTrException;

    /**
     * rider 출/퇴근
     * @param rider
     * @return
     */
    public int updateWorkingRider(Rider rider) throws AppTrException ;

    /**
     * Rider 위치 정보 전송
     * @param rider
     * @return
     * @throws AppTrException
     */
    public int updateRiderLocation(Rider rider) throws AppTrException ;

    /**
     * Rider 위치 정보 조회
     * @param rider
     * @return
     */
    public Rider getRiderLocation(Rider rider) throws AppTrException ;

    /**
     * Rider 들 정보 조회
     * @param rider
     * @return
     */
    public Map getRidersLocation(Rider rider) throws AppTrException ;



    /**
     * <p> getSubgroupRiderRels
     *
     * @param common
     * @return
     */
    public List<Rider> getSubgroupRiderRels(Common common) throws AppTrException;

    /**
     *  <p> getRiderAssignmentStatus
     *
     * @param rider
     * @return
     */
    public Map getRiderAssignmentStatus(Rider rider);

    /**
     *  <p> putRiderReturnTime
     *
     * @param rider
     * @return
     */
    public int putRiderReturnTime(Rider rider);

    /**
     *  <p> autoRiderWorking
     *
     * @param
     * @return
     */
    public void autoRiderWorking() throws AppTrException;

    /**
     * <p> getRejectReasonList
     *
     * @param common
     * @return
     * @throws AppTrException
     */
    public List<Reason> getRejectReasonList(Common common) throws AppTrException;

    /**
     * <p> getMobileVersion
     *
     * @param device
     * @return
     */
    public String getMobileVersion(String device);

    /**
     * 라이더 가입 승인 전 정상 등록된 모든 스토어 정보 가져오기
     * */
    public List<Store> selectAllStore();

    public Map postRiderApproval(RiderApprovalInfo approvalInfo) throws AppTrException;

    public Map getCheckRiderApproval(RiderApprovalInfo approvalInfo) throws AppTrException;
}
