package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.cntt.core.model.rider.RiderApprovalInfo;
import kr.co.cntt.core.model.rider.RiderSession;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.RiderService;
import kr.co.cntt.core.util.ShaEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service("riderService")
public class RiderServiceImpl extends ServiceSupport implements RiderService {

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    /**
     * @param riderMapper USER D A O
     */
    @Autowired
    public RiderServiceImpl(RiderMapper riderMapper) {
        this.riderMapper = riderMapper;
    }

    @Override
    public Map selectLoginRider(Rider rider) {
        return riderMapper.selectLoginRider(rider);
    }

    @Override
    public int selectRiderTokenCheck(Rider rider) {
        return riderMapper.selectRiderTokenCheck(rider);
    }

    @Override
    public User selectRiderTokenLoginCheck(Rider rider) {
        return riderMapper.selectRiderTokenLoginCheck(rider);
    }

    @Override
    public int insertRiderSession(Rider rider) {
        return riderMapper.insertRiderSession(rider);
    }

    @Override
    public int updateRiderSession(RiderSession session) {
        return riderMapper.updateRiderSession(session);
    }

    // rider 정보 조회
    @Secured({"ROLE_ADMIN", "ROLE_STORE" , "ROLE_RIDER"})
    @Override
    public Rider getRiderInfo(Rider rider) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
            rider.setId("");
            rider.setIsAdmin("");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            rider.setAccessToken(null);
            rider.setId("");
            rider.setIsAdmin("");
        }

        Rider S_Rider = riderMapper.getRiderInfo(rider);

        if (S_Rider == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00008), ErrorCodeEnum.E00008.name());
        }

        return S_Rider;
    }

    @Override
    public List<Rider> getStoreRiders(User user) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            user.setAccessToken(user.getToken());
            user.setId("");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]") || authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            user.setAccessToken(null);
            user.setId("");
        }

        List<Rider> S_Rider = riderMapper.getStoreRiders(user);

        if (S_Rider.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00037), ErrorCodeEnum.E00037.name());
        }

        return S_Rider;
    }
    // rider 정보 수정
    @Secured({"ROLE_ADMIN", "ROLE_STORE" , "ROLE_RIDER"})
    @Override
    public int updateRiderInfo(Rider rider) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
            rider.setId(null);
            rider.setIsAdmin(null);
            rider.setCode(null);
            rider.setSubGroupRiderRel(null);
            // 현재 비밀번호 받아서 비밀번호 변경 적용 Nick
            ShaEncoder sha = new ShaEncoder(512);
            rider.setCurrentPw(sha.encode(rider.getCurrentPw()));
            Rider tempRider = riderMapper.getRiderInfo(rider);
            if(tempRider.getLoginPw().equals(rider.getCurrentPw())){
                rider.setLoginPw(sha.encode(rider.getNewPw()));
            } else{
                return 0;
            }

        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            rider.setCode(null);
        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            rider.setAccessToken(null);
            rider.setId(null);
            rider.setIsAdmin(null);
            rider.setCode(null);
            rider.setSubGroupRiderRel(null);
        }

        int nRet = riderMapper.updateRiderInfo(rider);

        Rider S_Rider = riderMapper.getRiderInfo(rider);

        if (nRet != 0) {
            if (S_Rider.getSubGroupStoreRel() != null) {
                redisService.setPublisher(Content.builder().type("rider_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).subGroupId(S_Rider.getSubGroupStoreRel().getSubGroupId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("rider_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).build());
            }
        }

        return nRet;
    }

    // rider 출/퇴근
    @Override
    public int updateWorkingRider(Rider rider) throws AppTrException {

        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            // rider 가 아닌 user 가 넘어왔을때 token 값 null 로 셋팅
            rider.setAccessToken(null);
        }

        int nRet = riderMapper.updateWorkingRider(rider);

        Rider S_Rider = riderMapper.getRiderInfo(rider);

        if (nRet != 0) {
            if (S_Rider.getSubGroupStoreRel() != null) {
                redisService.setPublisher(Content.builder().type("rider_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).subGroupId(S_Rider.getSubGroupStoreRel().getSubGroupId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("rider_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).build());
            }
        }

        return nRet;

    }

    // rider 위치 정보 전송
    @Override
    public int updateRiderLocation(Rider rider) throws AppTrException {
        // Role 확인 token 으로 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            rider.setAccessToken(null);
        }

        int nRet = riderMapper.updateRiderLocation(rider);

        Rider S_Rider = riderMapper.getRiderInfo(rider);


        if (nRet != 0) {
            if (S_Rider.getSubGroupStoreRel() != null) {
                redisService.setPublisher(Content.builder().type("rider_location_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).subGroupId(S_Rider.getSubGroupStoreRel().getSubGroupId()).latitude(S_Rider.getLatitude()).longitude(S_Rider.getLongitude()).build());
            } else {
                redisService.setPublisher(Content.builder().type("rider_location_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).latitude(S_Rider.getLatitude()).longitude(S_Rider.getLongitude()).build());
            }
        }

        return nRet;
    }

    // rider 가 자기 위치 정보 조회
    @Override
    public Rider getRiderLocation(Rider rider) throws AppTrException {
        // Role 확인 token 으로 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
            rider.setId("");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            rider.setAccessToken(null);
            rider.setId("");
        }

        Rider S_Rider = riderMapper.getRiderLocation(rider);

        if (S_Rider == null) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00008), ErrorCodeEnum.E00008.name());
        }
        return S_Rider;
    }

    // store , admin rider 들 위치 정보 조회
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public Map getRidersLocation(Rider rider) throws AppTrException {
        // list 선언
        List<Rider> A_Rider = new ArrayList<>();
        List<Rider> S_Rider = new ArrayList<>();

        // Role 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            // token 값 선언
            rider.setAccessToken(rider.getToken());
            // Store
            S_Rider = riderMapper.getStoreRidersLocation(rider);
        } else if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            rider.setAccessToken(rider.getToken());

            A_Rider = riderMapper.getAdminRidersLocation(rider);
        }
        // map 으로 넘겨준다
        Map<String, List<Rider>> map = new HashMap<>();
        map.put("adminRider", A_Rider);
        map.put("storeRider", S_Rider);
        return map;
    }



    @Secured("ROLE_STORE")
    @Override
    public List<Rider> getSubgroupRiderRels(Common common) throws AppTrException {
        List<Rider> S_Rider = riderMapper.selectSubgroupRiderRels(common);

        if (S_Rider.size() == 0) {
            throw new AppTrException(getMessage(ErrorCodeEnum.E00038), ErrorCodeEnum.E00038.name());
        }

        return S_Rider;
    }

    //배정 모드 조회
    @Override
    public Map getRiderAssignmentStatus(Rider rider){
        // Role 확인 token 으로 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            rider.setAccessToken(null);
        }

        String nRet = riderMapper.selectRiderAssignmentStatus(rider);
        Map<String, String> map = new HashMap<>();
        map.put("assignmentStatus", nRet);
        return map;
    }

    //라이더 재배치
    @Secured("ROLE_STORE")
    @Override
    public int putRiderReturnTime(Rider rider){
        int nRet = riderMapper.updateRiderReturnTime(rider);

        Rider S_Rider = riderMapper.getRiderInfo(rider);

        if (nRet != 0) {
            if (S_Rider.getSubGroupStoreRel() != null) {
                redisService.setPublisher(Content.builder().type("rider_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).subGroupId(S_Rider.getSubGroupStoreRel().getSubGroupId()).build());
            } else {
                redisService.setPublisher(Content.builder().type("rider_updated").id(S_Rider.getId()).adminId(S_Rider.getAdminId()).storeId(S_Rider.getStore().getId()).build());

            }
        }

        return nRet;
    }

    //기사 휴식 시간
    @Override
    public void autoRiderWorking() throws AppTrException {
        List<Rider> riderList = riderMapper.selectRiderRestHours();
        if(riderList != null){
            for(Rider rider : riderList){
                HashMap map = new HashMap();
                map.put("idNum", rider.getId());
                if(rider.getRestHours() !=null) {
                    int restHoursLength = (rider.getRestHours()).length();
                    int num = (restHoursLength + 1) / 3;
                    for (int i = 1; i <= num; i++) {
                        if(map.containsKey("num")){
                            map.remove("num");
                        }
                        map.put("num", i);
                        int temp = riderMapper.updateRiderWorkingAuto(map);
                        if (temp != 1) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_STORE" , "ROLE_RIDER"})
    @Override
    public List<Reason> getRejectReasonList(Common common) throws AppTrException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            common.setRole("ROLE_STORE");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            common.setRole("ROLE_RIDER");
        } else if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            common.setRole("ROLE_ADMIN");
        }

        List<Reason> reasonList = riderMapper.selectRejectReason(common);

        return reasonList;
    }

    @Override
    public String getMobileVersion(String device) {
        return riderMapper.selectMobileVersion(device);
    }

    /** 라이더 등록 페이지에서 필요로 하는 기본 정보 */
    public List<Store> selectAllStore(){
        return riderMapper.selectAllStore();
    }

    /** 라이더 등록 요청 */
    @Override
    public Map postRiderApproval(RiderApprovalInfo approvalInfo) throws AppTrException{
        Map<String, Object> returnMap = new HashMap<>();

        // 기본 정보 확인
        if (StringUtils.isEmpty(approvalInfo.getAdminId()) || StringUtils.isEmpty(approvalInfo.getLoginId()) || StringUtils.isEmpty(approvalInfo.getLoginPw()) ||
                StringUtils.isEmpty(approvalInfo.getName()) || StringUtils.isEmpty(approvalInfo.getPhone()) || StringUtils.isEmpty(approvalInfo.getStore().getId()) ||
                StringUtils.isEmpty(approvalInfo.getLatitude()) || StringUtils.isEmpty(approvalInfo.getLongitude())){
            throw new AppTrException("정보가 누락되었습니다.", "303");
        }

        approvalInfo.setRole("ROLE_RIDER");

        List<RiderApprovalInfo> searchRiders = riderMapper.selectApprovalRiderList(approvalInfo);

        // 라이더 ID 중복 확인
        for (RiderApprovalInfo rider:searchRiders
             ) {
            // 등록이 수락된 경우 유효기간을 체크합니다.
            switch (rider.getApprovalStatus()){
                case "1":
                    if (rider.getSession() != null){
                        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date expDate = dateFormat.parse(dateFormat.format(timeFormat.parse(rider.getSession().getExpiryDatetime())));
                            Date nowDate = dateFormat.parse(dateFormat.format(new Date()));

                            if (expDate.getTime() > nowDate.getTime()){
                                throw new AppTrException("등록 실패! 사용 중인 계정입니다.", "402");
                            }
                        } catch(AppTrException e){
                            throw e;
                        } catch(Exception e){
                            e.printStackTrace();
                            throw new AppTrException("등록 실패! API 제공 업체에 문의하십시오.", "505");
                        }
                    }else if (rider.getSession() == null){
                        throw new AppTrException("등록 실패! 사용 중인 계정입니다.", "402");
                    }

                    break;
                case "0":
                    if (rider.getSession() != null){
                        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date expDate = dateFormat.parse(dateFormat.format(timeFormat.parse(rider.getSession().getExpiryDatetime())));
                            Date nowDate = dateFormat.parse(dateFormat.format(new Date()));

                            if (expDate.getTime() > nowDate.getTime()){
                                throw new AppTrException("등록 실패! 등록 요청이 진행 중입니다.", "403");
                            }
                        } catch(AppTrException e){
                            throw e;
                        } catch(Exception e){
                            e.printStackTrace();
                            throw new AppTrException("등록 실패! API 제공 업체에 문의하십시오.", "505");
                        }
                    }else if (rider.getSession() == null){
                        throw new AppTrException("등록 실패! 등록 요청이 진행 중입니다.", "403");
                    }
                    break;
                default:
            }
        }


        // 정보 등록
        riderMapper.insertApprovalInfo(approvalInfo);

        returnMap.put("status", "success");
        returnMap.put("id", approvalInfo.getId());


        return returnMap;
    }
    
    /** 라이더 승인 조회 체크 */
    @Override
    public Map getCheckRiderApproval(RiderApprovalInfo approvalInfo) throws AppTrException{
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("status", "OK");

        // 정보 확인
        if (StringUtils.isEmpty(approvalInfo.getLoginId()) || StringUtils.isEmpty(approvalInfo.getLoginPw())){
            throw new AppTrException("필수 정보가 입력되지 않았습니다.", "404");
        }

        if (approvalInfo.getLevel().equals("3")){
            approvalInfo.setRole("ROLE_RIDER");
        }

        // RiderApprovalInfo 값 가져오기
        RiderApprovalInfo riderApprovalInfo = riderMapper.selectApprovalRiderInfo(approvalInfo);

        if (riderApprovalInfo == null){
            throw new AppTrException("등록된 계정이 없습니다.", "405");
        }

        // 유효 기간 체크
        if (riderApprovalInfo.getSession() != null && !StringUtils.isEmpty(riderApprovalInfo.getSession().getExpiryDatetime())){
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date expDate = dateFormat.parse(dateFormat.format(timeFormat.parse(riderApprovalInfo.getSession().getExpiryDatetime())));
                Date nowDate = dateFormat.parse(dateFormat.format(new Date()));

                if (expDate.getTime() < nowDate.getTime()){
                    throw new AppTrException("사용 기간이 만료되었습니다.", "411");
                }
            } catch(AppTrException e){
                throw e;
            } catch(Exception e){
                e.printStackTrace();
                throw new AppTrException("API 제공 업체에 문의하십시오.", "505");
            }
        }

        switch (riderApprovalInfo.getApprovalStatus()){
            case "0":
                // 관리자가 확인 진행 중
                throw new AppTrException("등록 요청을 확인하는 중입니다.", "413");
            case "1":
                // 승인이 되었으며, 정상적인 계정인지 확인
                if (StringUtils.isEmpty(riderApprovalInfo.getRiderId())){
                    throw new AppTrException("담당자에게 확인하시길 바랍니다.", "505");
                }

                // 라이더 정보를 확인 후 값을 전달한다.
                Map riderMap = riderMapper.selectLoginRider(approvalInfo);

                approvalInfo.setToken(riderMap.get("accessToken").toString());

                Rider riderInfo = riderMapper.getRiderInfo(approvalInfo);

                if (riderInfo == null || !riderInfo.getId().equals(riderApprovalInfo.getRiderId())){
                    throw new AppTrException("요청한 사원과 등록된 사원이 일치하지 않습니다.", "1234");
                }
                returnMap.put("riderId", riderApprovalInfo.getRiderId());

                break;
            case "2":
            case "3":
                // 승인 거절
                throw new AppTrException("등록 요청이 거절되었습니다.", "412");
            default:
                throw new AppTrException("등록 상태를 확인할 수 없습니다.", "412");
        }

        return returnMap;
    }
}

