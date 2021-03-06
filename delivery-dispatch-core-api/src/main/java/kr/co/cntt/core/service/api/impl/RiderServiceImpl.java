package kr.co.cntt.core.service.api.impl;

import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.model.common.Common;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.redis.Content;
import kr.co.cntt.core.model.rider.Rider;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public int updateRiderSession(String token) {
        return riderMapper.updateRiderSession(token);
    }

    // rider ?????? ??????
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
    // rider ?????? ??????
    @Secured({"ROLE_ADMIN", "ROLE_STORE" , "ROLE_RIDER"})
    @Override
    public int updateRiderInfo(Rider rider) throws AppTrException {
//        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
            rider.setId(null);
            rider.setIsAdmin(null);
            rider.setCode(null);
            rider.setSubGroupRiderRel(null);
            // ?????? ???????????? ????????? ???????????? ?????? ?????? Nick
            ShaEncoder sha = new ShaEncoder(512);
            rider.setCurrentPw(sha.encode(rider.getCurrentPw()));
            Rider tempRider = riderMapper.getRiderInfo(rider);
//            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+riderMapper.getRiderInfo(rider));
//            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+rider.getCurrentPw());
            if(tempRider.getLoginPw().equals(rider.getCurrentPw())){
                rider.setLoginPw(sha.encode(rider.getNewPw()));
            } else{
                return 0;
            }

        } else if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            rider.setCode(null);
//            rider.setSubGroupRiderRel(null);
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

    // rider ???/??????
    @Override
    public int updateWorkingRider(Rider rider) throws AppTrException {

        // Role ??????
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().toString().equals("[ROLE_RIDER]")) {
            rider.setAccessToken(rider.getToken());
        } else if (authentication.getAuthorities().toString().equals("[ROLE_USER]")) {
            // rider ??? ?????? user ??? ??????????????? token ??? null ??? ??????
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

    // rider ?????? ?????? ??????
    @Override
    public int updateRiderLocation(Rider rider) throws AppTrException {
        // Role ?????? token ?????? ?????? ??????
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

    // rider ??? ?????? ?????? ?????? ??????
    @Override
    public Rider getRiderLocation(Rider rider) throws AppTrException {
        // Role ?????? token ?????? ?????? ??????
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

    // store , admin rider ??? ?????? ?????? ??????
    @Secured({"ROLE_ADMIN", "ROLE_STORE"})
    @Override
    public Map getRidersLocation(Rider rider) throws AppTrException {
        // list ??????
        List<Rider> A_Rider = new ArrayList<>();
        List<Rider> S_Rider = new ArrayList<>();

        // Role ??????
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().equals("[ROLE_STORE]")) {
            // token ??? ??????
            rider.setAccessToken(rider.getToken());
            // Store
            S_Rider = riderMapper.getStoreRidersLocation(rider);
        } else if (authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            rider.setAccessToken(rider.getToken());

            A_Rider = riderMapper.getAdminRidersLocation(rider);
        }
        // map ?????? ????????????
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

    //?????? ?????? ??????
    @Override
    public Map getRiderAssignmentStatus(Rider rider){
        // Role ?????? token ?????? ?????? ??????
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

    //????????? ?????????
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

    //?????? ?????? ??????
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


}

