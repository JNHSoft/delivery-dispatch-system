package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.model.rider.*;
import kr.co.cntt.core.model.sms.SmsApplyInfo;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.ServiceSupport;
import kr.co.cntt.core.service.api.RiderService;
import kr.co.cntt.core.util.Misc;
import kr.co.cntt.core.util.ShaEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
     * Store DAO
     * */
    private StoreMapper storeMapper;

    /**
     * @param riderMapper USER D A O
     */
    @Autowired
    public RiderServiceImpl(RiderMapper riderMapper, StoreMapper storeMapper) {
        this.riderMapper = riderMapper;
        this.storeMapper = storeMapper;
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
            Map<String, Object> riderHistoryInfo = riderMapper.selectRiderWorkingHistory(rider);

            // 라이더 상태에 따른, 히스토리 내용 생성
            if (rider.getWorking().equals("1")){
                // 출근 요청인 경우
                if (riderHistoryInfo == null){
                    // 출근에 대한 히스토리가 없으므로, 데이터를 입력한다.
                    riderMapper.insertRiderWorkingHistory(rider);
                    log.info("라이더의 출근 히스토리 등록 => " + rider.getToken());
                }
            }else if (rider.getWorking().equals("0")){
                // 퇴근 요청인 경우
                if (riderHistoryInfo != null){
                    // 출근에 대한 히스토리가 없으므로, 넘어간다.
                    riderMapper.updateRiderWorkingHistory(rider);
                    log.info("라이더의 퇴근 히스토리 등록 => " + rider.getToken());
                }
            }

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
                        Integer temp = riderMapper.updateRiderWorkingAuto(map);
                        if (temp != null && temp != 1) {
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
            throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
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

                            if (rider.getSession().getExpiryDatetime() == null){
                                rider.getSession().setExpiryDatetime(timeFormat.format(new Date()));
                            }

                            Date expDate = dateFormat.parse(dateFormat.format(timeFormat.parse(rider.getSession().getExpiryDatetime())));
                            Date nowDate = dateFormat.parse(dateFormat.format(new Date()));

                            if (expDate.getTime() >= nowDate.getTime()){
                                throw new AppTrException(getMessage(ErrorCodeEnum.E00041), ErrorCodeEnum.E00041.name());
                            }
                        } catch(AppTrException e){
                            throw e;
                        } catch(Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                            throw new AppTrException(getMessage(ErrorCodeEnum.E00045), ErrorCodeEnum.E00045.name());
                        }
                    }else if (rider.getSession() == null){
                        throw new AppTrException(getMessage(ErrorCodeEnum.E00041), ErrorCodeEnum.E00041.name());
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
                                throw new AppTrException(getMessage(ErrorCodeEnum.E00042), ErrorCodeEnum.E00042.name());
                            }
                        } catch(AppTrException e){
                            throw e;
                        } catch(Exception e){
//                            e.printStackTrace();
                            log.error(e.getMessage());
                            throw new AppTrException(getMessage(ErrorCodeEnum.E00045), ErrorCodeEnum.E00045.name());
                        }
                    }else if (rider.getSession() == null){
                        throw new AppTrException(getMessage(ErrorCodeEnum.E00042), ErrorCodeEnum.E00042.name());
                    }
                    break;
                case "5":       // 일시정지는 유효기간 체크하지 않음
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00054), ErrorCodeEnum.E00054.name());
                default:
            }
        }

        /**
         * 21-07-07
         * 정보 등록 시, 매장 및 관리자 ID가 정상적으로 되어 있는지 확인하는 프로세스 추가
         * */
        // 스토어에 대한 정보 가져오기
        Common searchStore = new Common();

        searchStore.setId(approvalInfo.getStore().getId());
        searchStore.setRole("ROLE_SYSTEM");

        Store store = storeMapper.selectStoreInfo(searchStore);

        // STORE ID로 조회했으므로, 무조건 맞을거라는 가정으로 작업을 진행한다.
        if (!(approvalInfo.getAdminId().equals(store.getAdminId()))){
            log.debug("라이더 가입 요청 정보 중 관리자 정보가 잘못되어 강제 변경 되었습니다. => API에서 보내진 관리자 정보 및 스토어 정보" + approvalInfo.getAdminId() + " (스토어: " + approvalInfo.getStore().getId() + ")" );
            approvalInfo.setAdminId(store.getAdminId());
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
            throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
        }

        if (approvalInfo.getLevel().equals("3")){
            approvalInfo.setRole("ROLE_RIDER");
        }

        // RiderApprovalInfo 값 가져오기
        RiderApprovalInfo riderApprovalInfo = riderMapper.selectApprovalRiderInfo(approvalInfo);

        if (riderApprovalInfo == null){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00049), ErrorCodeEnum.E00049.name());
        }

        // 유효 기간 체크
        if (riderApprovalInfo.getSession() != null && !StringUtils.isEmpty(riderApprovalInfo.getSession().getExpiryDatetime())){
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date expDate = dateFormat.parse(dateFormat.format(timeFormat.parse(riderApprovalInfo.getSession().getExpiryDatetime())));
                Date nowDate = dateFormat.parse(dateFormat.format(new Date()));

                if (expDate.getTime() < nowDate.getTime()){
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00050), ErrorCodeEnum.E00050.name());
                }
            } catch(AppTrException e){
                throw e;
            } catch(Exception e){
//                e.printStackTrace();
                log.error(e.getMessage());
                throw new AppTrException(getMessage(ErrorCodeEnum.E00053), ErrorCodeEnum.E00053.name());
            }
        }

        switch (riderApprovalInfo.getApprovalStatus()){
            case "0":
                // 관리자가 확인 진행 중
                throw new AppTrException(getMessage(ErrorCodeEnum.E00046), ErrorCodeEnum.E00046.name());
            case "1":
                // 승인이 되었으며, 정상적인 계정인지 확인
                if (StringUtils.isEmpty(riderApprovalInfo.getRiderId())){
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00051), ErrorCodeEnum.E00051.name());
                }

                // 라이더 정보를 확인 후 값을 전달한다.
                Map riderMap = riderMapper.selectLoginRider(approvalInfo);

                approvalInfo.setToken(riderMap.get("accessToken").toString());

                Rider riderInfo = riderMapper.getRiderInfo(approvalInfo);

                if (riderInfo == null || !riderInfo.getId().equals(riderApprovalInfo.getRiderId())){
                    throw new AppTrException(getMessage(ErrorCodeEnum.E00052), ErrorCodeEnum.E00052.name());
                }
                returnMap.put("riderId", riderApprovalInfo.getRiderId());

                break;
            case "5":
                throw new AppTrException(getMessage(ErrorCodeEnum.E00055), ErrorCodeEnum.E00055.name());
            case "2":
            case "3":
                // 승인 거절
                throw new AppTrException(getMessage(ErrorCodeEnum.E00047), ErrorCodeEnum.E00047.name());
            default:
                throw new AppTrException(getMessage(ErrorCodeEnum.E00048), ErrorCodeEnum.E00048.name());
        }

        return returnMap;
    }

    @Override
    public int updateOverExpDate() throws AppTrException {
        log.info("updateOverExpDate 실행");

        int iresult = riderMapper.updateOverExpDate();            // 유효기간 만료 계정 상태 변경
        log.info("updateOverExpDate 변경");

        if (iresult > 0){
            iresult = riderMapper.deleteOverExpDateToken();      // 유효기간 만료 계정의 토큰 삭제
            log.info("deleteOverExpDateToken 변경");
        }

        log.info("updateOverExpDate 실행완료 [" + iresult + "]");
        return iresult;
    }

    /**
     * 라이더 휴대폰 인증
     * */
    @Override
    public Map sendApplySMS(SmsApplyInfo smsApplyInfo) throws AppTrException{

        // 정보 확인 [휴대폰번호 / 브랜드코드 / 로그인할 ID]
        if (StringUtils.isEmpty(smsApplyInfo.getPhone()) || StringUtils.isEmpty(smsApplyInfo.getBrandCode()) || StringUtils.isEmpty(smsApplyInfo.getLoginId())){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
        }

        // DB에 등록이 되어 있는지 데이터 확인 단 65초 이하인 경우 조회되지 않는다.
        SmsApplyInfo orgSMSData = riderMapper.selectRiderApplySMS(smsApplyInfo);

        log.info("sendApplySMS Data => " + orgSMSData);

        // 등록되어 있는 경우 시간을 비교한다.
        // 1분이 초과되지 않은 경우, SMS 재전송을 할 수 없다. 오류 메세지 전달
        if (orgSMSData != null){
            throw new AppTrException(getMessage(ErrorCodeEnum.SM00001), ErrorCodeEnum.SM00001.name());
        }

        // 등록되지 않았거나 시간이 초과된 경우 신규 프로세스를 적용한다.
        Map<String, String> map = new HashMap<>();
        String strApplyNo = getRndNumber(6);        // 인증번호 추출
        smsApplyInfo.setApplyNo(strApplyNo);

        // 인증번호를 등록한다 (Insert)
        int iResult = riderMapper.insertRiderApplySMS(smsApplyInfo);

        if (iResult < 1){
            throw new AppTrException(getMessage(ErrorCodeEnum.S0002), ErrorCodeEnum.S0002.name());
        }

        // 문자 발송 API 실행
        Map<String, String> smsParaMap = new HashMap<>();
        String strUrl = "https://smsapi.mitake.com.tw/api/mtk/SmSend";

        if (smsApplyInfo.getBrandCode().equals("1")){
            smsParaMap.put("smbody", getMessage("M00001", strApplyNo));     // 문구 내용
            smsParaMap.put("username", "dmsK");                                  // 로그인 ID
//            String userid, String userpwd, String phone, String msgBody
        }else {
            smsParaMap.put("smbody", getMessage("M00000", strApplyNo));     // 문구 내용
            smsParaMap.put("username", "dmsP");                                  // 로그인 ID
        }
        smsParaMap.put("password", "97161500");                                  // 로그인 PW
        smsParaMap.put("dstaddr", smsApplyInfo.getPhone().toString());                      // 수신자 휴대폰 번호
        //smsParaMap.put("dstaddr", "0905757978");                      // 수신자 휴대폰 번호


        log.debug("smsParameter Map = " + smsParaMap);
        String smsResult = sendSMSAPI(strUrl, smsParaMap);

        log.debug("############# API 결과 ###############");
        log.debug(smsResult);
        log.debug("############# API 결과 ###############");

        String[] arrResult = smsResult.split("\n");

        log.debug("API 발송 완료 ### => " + smsApplyInfo);
        map.put("message", "OK");
        map.put("applyNo", strApplyNo);
        map.put("smsResult", smsResult);

        return map;
    }

    /**
     * 라이더 휴대폰 인증번호 체크
     * */
    @Override
    public Map checkApplySMS(SmsApplyInfo smsApplyInfo) throws AppTrException{
        // 정보 확인 [휴대폰번호 / 브랜드코드 / 로그인할 ID / 인증번호]
        if (StringUtils.isEmpty(smsApplyInfo.getPhone()) || StringUtils.isEmpty(smsApplyInfo.getBrandCode())
                || StringUtils.isEmpty(smsApplyInfo.getLoginId()) || StringUtils.isEmpty(smsApplyInfo.getApplyNo())){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
        }

        // DB에 등록이 되어 있는지 데이터 확인 단 65초 이하인 경우 조회되지 않는다.
        SmsApplyInfo orgSMSData = riderMapper.selectRiderApplySMS(smsApplyInfo);

        if (orgSMSData == null){
            throw new AppTrException(getMessage(ErrorCodeEnum.SM00002), ErrorCodeEnum.SM00002.name());
        }

        // 인증번호 체크
        if (!orgSMSData.getApplyNo().equals(smsApplyInfo.getApplyNo().toUpperCase())){
            throw new AppTrException(getMessage(ErrorCodeEnum.SM00003), ErrorCodeEnum.SM00003.name());
        }

        smsApplyInfo.setApplyStatus("1");
        riderMapper.updateRiderApplySMS(smsApplyInfo);

        log.debug("인증성공 => " + smsApplyInfo);
        Map<String, String> map = new HashMap<>();
        map.put("message", "OK");
        return map;
    }

    @Override
    public boolean updateRiderOSInfo(Rider rider) throws AppTrException {

        int iResult = riderMapper.updatePushToken(rider);

        if (iResult > 0){
            return true;
        }

        return false;
    }

    /**
     * 2021-03-05 라이더 전체 경로 가져오는 프로세스
     * */
    @Override
    public Map getTotalRouteInfos(Rider rider) throws AppTrException {
        Map<String, Object> resultMap = new HashMap<>();

        // 라이더 정보 체크
        if (StringUtils.isEmpty(rider.getToken()) || StringUtils.isEmpty(rider.getLatitude()) || StringUtils.isEmpty(rider.getLongitude())){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
        }

        Rider riderInfo = riderMapper.getRiderInfo(rider);
        List<Order> orderList = riderMapper.getOrderForRider(rider).stream().sorted(Comparator.comparing(Order::getReservationDatetime, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());

        if (orderList.size() < 1){
            log.info("1. 싸이클 정보 : " + riderInfo.getId() + " #### 라이더에게 배정된 주문이 없습니다.");
            throw new AppTrException(getMessage(ErrorCodeEnum.ERR0001), ErrorCodeEnum.ERR0001.name());
        }

        // 정보 확인을 위한 내용
        log.info("주문 정보 리스트 ##### S");
        for (Order o:orderList
             ) {
            log.info(o.getId() + " => " + o);
        }
        log.info("주문 정보 리스트 #### F");

        List<String> storeIdList = new ArrayList<>();
        List<String> usedStoreId = new ArrayList<>();
        List<String> usedOrderId = new ArrayList<>();

        Misc misc = new Misc();

        for (Order o: orderList){
            storeIdList.add(o.getStoreId());
        }

        List<Store> storeList = riderMapper.getStoreInfoAtOrder(storeIdList.toArray(new String[storeIdList.size()]));

        List<RiderRouteInfo> routeInfoList = new ArrayList<>();

        List<Order> nonAssignedOrderList = orderList.stream().filter(x -> !x.getStatus().equals("1")).collect(Collectors.toList());

        // 배정 이후의 개수가 존재한다면, 관련 스토어의 순서를 정렬한다.
        if (nonAssignedOrderList != null && nonAssignedOrderList.size() > 0){
            // 픽업 시간이 빠른 순으로 정렬 (처음 1개의 데이터만 추출
            Order firstOrder = nonAssignedOrderList.stream()
                    .sorted(Comparator.comparing(Order::getPickedUpDatetime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .findFirst().get();
            Store storeInfo = storeList.stream().filter(x -> x.getId().equals(firstOrder.getStoreId())).findFirst().get();

            log.info("2. 싸이클 정보 : " + riderInfo.getId() + " #### 픽업 이후의 상태의 주문이 존재합니다. => " + firstOrder.getId() + " ## " + firstOrder.getRegOrderId());

            // 현재 라이더 위치에서 스토어 간의 거리를 구한다
            int iDistance = 0;

            try{
                iDistance = misc.getHaversine(rider.getLatitude(), rider.getLongitude(), storeInfo.getLatitude(), storeInfo.getLongitude());
            }catch (Exception e){
                log.info("3. 싸이클 정보 : " + riderInfo.getId() + " #### 거리 도출 중 오류 발생");
                log.error(e.getMessage());
            }

            // 매장에 대한 주문 개수를 구한다.
            List<String> orderID = new ArrayList<>();

            orderList.stream().filter(x -> x.getStoreId().equals(storeInfo.getId())).forEach(x -> orderID.add(x.getRegOrderId()));

            log.info("4. 싸이클 정보 : 매장정보 [" + storeInfo.getId() + " # " + storeInfo.getStoreRealName() + "]의 주문 개수 : [" + orderList.size() + "]");

            for (String strID : orderID){
                RiderRouteInfo route = new RiderRouteInfo();
                route.setRouteRank(1);
                route.setRouteType(0);
                route.setId(riderInfo.getId());
                route.setName(riderInfo.getName());
                //route.setRouteId(storeInfo.getId());
                route.setRouteId(strID);
                route.setRouteName(storeInfo.getStoreRealName());
                route.setAddress(storeInfo.getAddress());
                route.setLatitude(storeInfo.getLatitude());
                route.setLongitude(storeInfo.getLongitude());
                route.setDistance(String.valueOf(iDistance));
                route.setBrandCode(storeInfo.getBrandCode());
                route.setBrandName(storeInfo.getBrandName());

                // 21-04-20 스토어에도 주문 상태 값을 넣어달라는 요청으로 인하여 상태값 주임
                route.setOrder(new Order());
                route.getOrder().setStatus(orderList.stream().filter(x -> x.getRegOrderId().equals(strID)).findFirst().get().getStatus());

                usedStoreId.add(storeInfo.getId());

                routeInfoList.add(route);
            }

            log.info("5. 싸이클 정보 : " + riderInfo.getId() + " #### 픽업 이후의 상태의 스토어 정보를 등록하였습니다. => [" + routeInfoList + "]");
        }

        int loopCount = storeList.size() + orderList.size();

        // 전체 순서를 정렬한다.
        for (int i = 0; i < loopCount; i++) {
            log.info("6. 싸이클 정보 : " + riderInfo.getId() + "#### routeInfo의 개수를 구합니다 => " + routeInfoList.size());
            // 만약 첫 픽업지가 정해진 경우라면, 처음 지정은 건너뛴다.
            if (routeInfoList.size() > 0 && i == 0){
                log.info("7. 싸이클 정보 : " + riderInfo.getId() + " #### 첫번째 픽업지가 설정되어 있습니다.");
                continue;
            }

            // 다음 순번에 넣을 정보를 저장해놓은다.
            Store minStore = null;
            Order minOrder = null;
            RiderRouteInfo route = new RiderRouteInfo();
            route.setRouteRank(i + 1);

            // 첫 픽업지가 정해지지 않은 경우도 있을 수 있으므로
            if (i == 0){
                int minDistance = -1;
                log.info("8. 싸이클 정보 : " + riderInfo.getId() + " #### i = 0 #### 첫번째 픽업지를 찾는 중입니다.");

                for (Store store:storeList
                ) {
                    int distance = 0;

                    try{
                        distance = misc.getHaversine(rider.getLatitude(), rider.getLongitude(), store.getLatitude(), store.getLongitude());
                    } catch (Exception e){
                        log.info("9. 싸이클 정보 : " + riderInfo.getId() + " #### i = 0 #### 거리 계산 중 오류 발생.");
                        log.error(e.getMessage());
                    }

                    if (minDistance == -1){
                        minDistance = distance;
                        minStore = store;
                    }else{
                        minDistance = Integer.min(minDistance, distance);
                        minStore = store;
                    }
                }

                if (minStore != null){
                    // 주문번호로 변경
                    // 매장에 대한 주문 개수를 구한다.
                    List<String> orderID = new ArrayList<>();
                    String minStoreID = minStore.getId();

                    orderList.stream().filter(x -> x.getStoreId().equals(minStoreID)).forEach(x -> orderID.add(x.getRegOrderId()));

                    log.info("10. 싸이클 정보 : " + riderInfo.getId() + " #### i = 0 #### 첫번째 픽업지 등록을 진행합니다. 매장에 대한 주문 개수. => " + orderID.size());

                    for (int j = 0; j < orderID.size(); j++) {
                        String strOrderID = orderID.get(j);                     // 21-04-20 요청사항에 따라, 픽업지에도 주문 상태를 넣어줘야한다.

                        if (j != orderID.size() - 1){
                            // 중복 매장 개수를 등록해야하는 경우를 위해 작업
                            RiderRouteInfo addRoute = new RiderRouteInfo();
                            addRoute.setRouteRank(route.getRouteRank());

                            addRoute.setRouteType(0);
                            addRoute.setId(riderInfo.getId());
                            addRoute.setName(riderInfo.getName());
                            //route.setRouteId(minStore.getId());
                            addRoute.setRouteId(strOrderID);
                            addRoute.setRouteName(minStore.getStoreRealName());
                            addRoute.setAddress(minStore.getAddress());
                            addRoute.setLatitude(minStore.getLatitude());
                            addRoute.setLongitude(minStore.getLongitude());
                            addRoute.setDistance(String.valueOf(minDistance));
                            addRoute.setBrandCode(minStore.getBrandCode());
                            addRoute.setBrandName(minStore.getBrandName());

                            // 21-04-20 스토어에도 주문 상태 값을 넣어달라는 요청으로 인하여 상태값 주임
                            addRoute.setOrder(new Order());
                            addRoute.getOrder().setStatus(orderList.stream().filter(x -> x.getRegOrderId().equals(strOrderID)).findFirst().get().getStatus());


                            routeInfoList.add(addRoute);

                            addRoute = null;
                        }else{
                            route.setRouteType(0);
                            route.setId(riderInfo.getId());
                            route.setName(riderInfo.getName());
                            //route.setRouteId(minStore.getId());
                            //route.setRouteId(orderID.get(j));
                            route.setRouteId(strOrderID);
                            route.setRouteName(minStore.getStoreRealName());
                            route.setAddress(minStore.getAddress());
                            route.setLatitude(minStore.getLatitude());
                            route.setLongitude(minStore.getLongitude());
                            route.setDistance(String.valueOf(minDistance));
                            route.setBrandCode(minStore.getBrandCode());
                            route.setBrandName(minStore.getBrandName());

                            // 21-04-20 스토어에도 주문 상태 값을 넣어달라는 요청으로 인하여 상태값 주임
                            route.setOrder(new Order());
                            route.getOrder().setStatus(orderList.stream().filter(x -> x.getRegOrderId().equals(strOrderID)).findFirst().get().getStatus());

                            routeInfoList.add(route);
                        }

                        usedStoreId.add(minStore.getId());
                    }

                    log.info("11. 싸이클 정보 : " + riderInfo.getId() + " #### i = 0 #### 첫번째 픽업지 등록을 진행했습니다. => " + route.getRouteId());
                }

            }
            // 처음 픽업지가 정해진 이후에 다음 목적지가 어디인지 구한다.
            else{
                // 이전 위치에 대한 정보를 가져온다.
                RiderRouteInfo beforeRoute = routeInfoList.get(i - 1);
                log.info("12. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 이전 픽업지 ID(종류) => " + beforeRoute.getRouteId() + "(" + beforeRoute.getRouteType() + ")");

                int beforeStoreDistance = -1;
                int beforeOrderDistance = -1;


                // 스토어 정보를 구한다.
                log.info("13. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 스토어 정보 구하기 시작");
                for (Store store:storeList){
                    // 사용된 Store 정보라면 넘어간다.
                    if (usedStoreId.stream().filter(x -> x.equals(store.getId())).count() > 0){
                        continue;
                    }

                    int currentStoreDistance = 0;

                    try{
                        currentStoreDistance = misc.getHaversine(beforeRoute.getLatitude(), beforeRoute.getLongitude(), store.getLatitude(), store.getLongitude());
                    }catch (Exception e){
                        log.info("14. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 스토어 거리 계산 중 오류 발생");
                        log.error(e.getMessage());
                    }

                    // 비교할 이전 정보가 없는 경우, 최초 스토어 정보를 저장하고 넘어간다.
                    if (minStore == null){
                        minStore = store;
                        beforeStoreDistance = currentStoreDistance;
                        continue;
                    }

                    // 이전 스토어와 현재 스토어 간의 정보를 비교한다.
                    if (beforeStoreDistance > currentStoreDistance){
                        minStore = store;
                        beforeStoreDistance = currentStoreDistance;
                    }
                }

                log.info("15. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 스토어 정보 구하기 종료");

                log.info("16. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 주문 정보 구하기 시작");
                // 주문 정보를 가져온다.
                for (Order order:orderList) {
                    if (usedOrderId.stream().filter(x -> x.equals(order.getId())).count() > 0){
                        log.info("17. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 사용된 주문 번호입니다.");
                        continue;
                    }

                    // 경로 상에서 스토어를 지난 후의 주문인지 확인 한다.
                    if (!(routeInfoList.stream().filter(x -> x.getRouteId().equals(order.getRegOrderId()) && x.getRouteType().equals(0)).count() > 0)){
                        log.info("18. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 주문에 대한 스토어가 안 지났습니다.");
                        continue;
                    }

                    int currentOrderDistance = 0;

                    try{
                        currentOrderDistance = misc.getHaversine(beforeRoute.getLatitude(), beforeRoute.getLongitude(), order.getLatitude(), order.getLongitude());
                    }catch (Exception e){
                        log.info("19. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 주문 거리 계산 중 오류발생");
                        log.error(e.getMessage());
                    }

                    // 비교할 이전 정보가 없는 경우, 최초 주문 정보를 저장하고 넘어간다.
                    if (minOrder == null){
                        minOrder = order;
                        beforeOrderDistance = currentOrderDistance;
                        continue;
                    }

                    // 시간 비교를 진행하여, 5분 이상 차이가 나는 경우 거리와 상관 없이 우선 순위가 되어야 한다.
                    LocalDateTime currentReservation = LocalDateTime.parse(order.getReservationDatetime().replace(" ", "T"));
                    LocalDateTime beforeReservation = LocalDateTime.parse(minOrder.getReservationDatetime().replace(" ", "T"));

                    /**
                     * 21-07-09
                     * 현재 주문의 예약시간과 이전 주문의 시간 중 시간 차이가 크면 우선 순위가 될 수 있도록 적용
                     * */
                    boolean bTimeCheck = false;
                    if (Math.abs(ChronoUnit.MINUTES.between(currentReservation, beforeReservation)) > 5){
                        //System.out.println("시간 차이가 5분을 초과합니다. => 아래 구문 => " + currentReservation.isBefore(beforeReservation));
                        log.info("시간 차이가 5분을 초과하였습니다. 현재 시간 => " + currentReservation + " ## 이전 시간 => " + beforeReservation);
                        bTimeCheck = true;
                    }

                    //System.out.println("거리를 구합니다. => " + beforeOrderDistance + " ### 현재 거리 => " + currentOrderDistance);
                    log.info("현재 주문에 대한 거리 계산 값 => " + currentOrderDistance + " ### 이전 주문에 대한 거리 계산 값" + beforeOrderDistance);

                    // 거리차이가 나거나 시간 차이가 5분 이상 나는 경우 교체
                    if ((beforeOrderDistance > currentOrderDistance && !bTimeCheck)){
                        minOrder = order;
                        beforeOrderDistance = currentOrderDistance;
                        log.info("거리 차이로 인하여 최소 주문의 값이 변경 되었습니다.");
                    }else if (bTimeCheck){
                        // 시간이 5분 이상 차이가 나는 경우 순서를 바꾼다.
                        if (currentReservation.isBefore(beforeReservation)){
                            minOrder = order;
                            beforeOrderDistance = currentOrderDistance;
                            log.info("예약 시간이 5분 이상 차이로 인하여 값이 변경 되었습니다.");
                        }
                    }
                }

                log.info("20. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 주문 정보 구하기 종료");

                log.info("21. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 매장과 주문 정보에 대한 내용 등록 시작");

                // 주문 목적지의 위치가 가까운 경우 다음 루트는 주문 경로가 되어야한다.
                if ((beforeStoreDistance > beforeOrderDistance && beforeOrderDistance > -1) || beforeStoreDistance == -1){
                    log.info("22. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 주문지 등록");
                    // 주문정보입니다.
                    route.setRouteType(1);
                    route.setId(riderInfo.getId());
                    route.setName(riderInfo.getName());
                    route.setRouteId(minOrder.getRegOrderId());
                    route.setAddress(minOrder.getAddress());
                    route.setLatitude(minOrder.getLatitude());
                    route.setLongitude(minOrder.getLongitude());
                    route.setReservationDatetime(minOrder.getReservationDatetime());
                    route.setDistance(String.valueOf(beforeOrderDistance));
                    route.setBrandCode(minOrder.getStore().getBrandCode());
                    route.setBrandName(minOrder.getStore().getBrandName());
                    route.setOrder(new Order());
                    route.getOrder().setStatus(minOrder.getStatus());

                    usedOrderId.add(minOrder.getId());
                    log.info("23. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 주문 목적지 등록 => " + route.getRouteId());
                }
                // 스토어 픽업지가 가까운 경우 다음 루트는 매장 경로가 되어야한다.
                else if((beforeStoreDistance <= beforeOrderDistance && beforeStoreDistance > -1) || beforeOrderDistance == -1){
                    log.info("24. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 목적지 등록");
                    // 매장에 대한 주문 개수를 구한다.
                    List<String> orderID = new ArrayList<>();
                    String minStoreID = minStore.getId();

                    orderList.stream().filter(x -> x.getStoreId().equals(minStoreID)).forEach(x -> orderID.add(x.getRegOrderId()));

                    for (int j = 0; j < orderID.size(); j++) {
                        String strOrderID = orderID.get(j);                     // 21-04-20 요청사항에 따라, 픽업지에도 주문 상태를 넣어줘야한다.
                        if (j != orderID.size() - 1){
                            // 중복 매장 개수를 등록해야하는 경우를 위해 작업
                            RiderRouteInfo addRoute = new RiderRouteInfo();
                            addRoute.setRouteRank(route.getRouteRank());

                            addRoute.setRouteType(0);
                            addRoute.setId(riderInfo.getId());
                            addRoute.setName(riderInfo.getName());
                            //route.setRouteId(minStore.getId());
                            addRoute.setRouteId(orderID.get(j));
                            addRoute.setRouteName(minStore.getStoreRealName());
                            addRoute.setAddress(minStore.getAddress());
                            addRoute.setLatitude(minStore.getLatitude());
                            addRoute.setLongitude(minStore.getLongitude());
                            addRoute.setDistance(String.valueOf(beforeStoreDistance));
                            addRoute.setBrandCode(minStore.getBrandCode());
                            addRoute.setBrandName(minStore.getBrandName());

                            // 21-04-20 스토어에도 주문 상태 값을 넣어달라는 요청으로 인하여 상태값 주임
                            addRoute.setOrder(new Order());
                            addRoute.getOrder().setStatus(orderList.stream().filter(x -> x.getRegOrderId().equals(strOrderID)).findFirst().get().getStatus());

                            routeInfoList.add(addRoute);

                            addRoute = null;
                        }else{
                            route.setRouteType(0);
                            route.setId(riderInfo.getId());
                            route.setName(riderInfo.getName());
                            //route.setRouteId(minStore.getId());
                            route.setRouteId(orderID.get(j));
                            route.setRouteName(minStore.getStoreRealName());
                            route.setAddress(minStore.getAddress());
                            route.setLatitude(minStore.getLatitude());
                            route.setLongitude(minStore.getLongitude());
                            route.setDistance(String.valueOf(beforeStoreDistance));
                            route.setBrandCode(minStore.getBrandCode());
                            route.setBrandName(minStore.getBrandName());

                            // 21-04-20 스토어에도 주문 상태 값을 넣어달라는 요청으로 인하여 상태값 주임
                            route.setOrder(new Order());
                            route.getOrder().setStatus(orderList.stream().filter(x -> x.getRegOrderId().equals(strOrderID)).findFirst().get().getStatus());

                        }

                        usedStoreId.add(minStore.getId());
                    }

                    log.info("25. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 스토어 목적지 등록 (개수) => " + minStore.getId() + "(" + orderID.size() + ")");
                }

                routeInfoList.add(route);

                log.info("26. 싸이클 정보 : " + riderInfo.getId() + " #### i = " + i + " #### 매장과 주문 정보에 대한 내용 등록 완료");
            }
        }

        resultMap.put("routeCount", routeInfoList.size());
        resultMap.put("routeInfo", routeInfoList);

        log.info("27. 싸이클 정보 : " + riderInfo.getId() + " #### 최종 결과물 => " + resultMap);

        return resultMap;
    }

    /**
     * 2021-03-19 요청에 따른 전체 경로 프로세스 변경
     * */
    @Override
    public Map getTotalRouteInfos2(Rider rider) throws AppTrException {
        log.info("getTotalRouteInfos2를 이용하였습니다.");
        return getTotalRouteInfos(rider);
    }

    private String getRndNumber(int rndLang){
        // 랜덤함수를 이용하여 추출할 문자 집합소
        StringBuilder sbRndString = new StringBuilder();
        StringBuilder sbReturn = new StringBuilder(rndLang);
        SecureRandom random = new SecureRandom();

//        sbRndString.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toUpperCase());
        sbRndString.append("0123456789");

        for (int i = 0; i < rndLang; i++) {
            sbReturn.append(sbRndString.charAt(
                    random.nextInt(sbRndString.length())
            ));
        }

        return sbReturn.toString();
    }

    private String sendSMSAPI(String sendUrl, Map<String, String> map){

        String inputLine = null;
        StringBuffer outResult = new StringBuffer();

        try {
            log.debug("REST API START");

            URL url = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            StringBuffer buffer = new StringBuffer();

            // form Data 정렬
            if (map != null){
                Set key = map.keySet();

                for (Iterator iterator = key.iterator(); iterator.hasNext();){
                    String keyName = (String) iterator.next();
                    String valueName = map.get(keyName);

                    buffer.append(keyName).append("=").append(valueName).append("&");
                }
            }


            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "BIG5");
            PrintWriter writer = new PrintWriter(os);
            writer.write(buffer.toString());
            writer.flush();

            // 결과값을 리턴을 받는다.
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ((inputLine = in.readLine()) != null){
                outResult.append(inputLine + "\n");
            }

            conn.disconnect();

            log.debug("sendApplySMS Result => " + outResult);

            log.debug("REST API END");
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return outResult.toString();

    }

    /**
     * 21-03-16
     * 라이더의 당일 활동에 따른 주문 내역
     * */
    @Secured({"ROLE_RIDER"})
    @Override
    public RiderActiveInfo getRiderActiveInfo(Rider rider) throws AppTrException {

        if (StringUtils.isEmpty(rider.getToken())){
            throw new AppTrException(getMessage(ErrorCodeEnum.E00040), ErrorCodeEnum.E00040.name());
        }

        return riderMapper.selectRiderActiveInfo(rider);
    }
}

