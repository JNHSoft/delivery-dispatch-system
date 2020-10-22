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
import kr.co.cntt.core.model.sms.SmsApplyInfo;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
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
                            e.printStackTrace();
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
                            e.printStackTrace();
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
                e.printStackTrace();
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

        System.out.println(orgSMSData);

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
            smsParaMap.put("userid", "dmsP");                                    // 로그인 ID
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

        if(arrResult[2].split("=")[1].toString() == "1"){
            log.debug("API 발송 완료 ### => " + smsApplyInfo);
            map.put("message", "OK");
            map.put("smsResult", smsResult);
        }else{
            throw new AppTrException(getMessage(ErrorCodeEnum.SM00003, arrResult[2].split("=")[1]), ErrorCodeEnum.SM00003.name());
        }

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


            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(os);
            writer.write(buffer.toString());
            writer.flush();

            // 결과값을 리턴을 받는다.
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ((inputLine = in.readLine()) != null){
                outResult.append(inputLine + "\n");
            }

            conn.disconnect();

            System.out.println(outResult);

            log.debug("REST API END");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return outResult.toString();

    }
}

