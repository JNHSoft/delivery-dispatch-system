package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.admin.AssignAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("assignAdminService")
public class AssignAdminServiceImpl implements AssignAdminService {

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    /**
     * RedisService
     */
    @Autowired
    private RedisService redisService;

    /**
     * Admin DAO
     */
    private AdminMapper adminMapper;

    /**
     * Store DAO
     */
    private StoreMapper storeMapper;

    /**
     * Rider DAO
     */
    private RiderMapper riderMapper;

    /**
     * Order DAO
     */
    private OrderMapper orderMapper;


    /**
     * @param adminMapper Admin D A O
     */
    @Autowired
    public AssignAdminServiceImpl(AdminMapper adminMapper, StoreMapper storeMapper, RiderMapper riderMapper, OrderMapper orderMapper) {
        this.adminMapper = adminMapper;
        this.storeMapper = storeMapper;
        this.riderMapper = riderMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public int putAdminAssignmentStatus(Admin admin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            admin.setRole("ROLE_ADMIN");
        }

        return adminMapper.updateAdminInfo(admin);
    }
    // 서드 파티 등록
    @Override
    public int postThirdParty(ThirdParty thirdParty) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            thirdParty.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertThirdParty(thirdParty);
    }
    // 서드 파티 수정
    @Override
    public int updateThirdParty(ThirdParty thirdParty) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            thirdParty.setRole("ROLE_ADMIN");
        }

        return adminMapper.updateThirdParty(thirdParty);
    }


    // 서드 파티 삭제
    @Override
    public int deleteThirdParty(ThirdParty thirdParty) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            thirdParty.setRole("ROLE_ADMIN");
        }

        int result = adminMapper.deleteThirdParty(thirdParty);

        if (result > 0){
            // store의 Third Party 정보를 가져온다.
            List<Store> updateStoreInfoList = adminMapper.selectThirdPartyStoreList(thirdParty);

            for (Store storeInfo:updateStoreInfoList
                 ) {
                // 배열로 변환한다.
                List<String> thirdPartyList = Arrays.stream(storeInfo.getThirdParty().split("|")).collect(Collectors.toList());

                thirdPartyList.removeIf(x -> x.equals("|"));

                long findThirdParty = thirdPartyList.stream().filter(x -> x.equals(thirdParty.getId())).count();

                if (findThirdParty > 0){
                    log.info("스토어에서 삭제 전 storeInfo.id = " + storeInfo.getId() + "Third Party List => " + storeInfo.getThirdParty());
                    String updateThirdPartyList = String.join("|", thirdPartyList.stream().filter(x -> !x.equals(thirdParty.getId())).collect(Collectors.toList()));

                    // 스토어의 ThirdParty 값 변경
                    storeInfo.setThirdParty(updateThirdPartyList);
                    storeInfo.setToken(thirdParty.getToken());

                    result += adminMapper.updateThirdPartyStoreInfo(storeInfo);

                    System.out.println("스토어에서 삭제 후 storeInfo.id = " + storeInfo.getId() + "updateThirdPartyList => " + updateThirdPartyList);
                }
            }
        }

        return result;
    }

    // 서드파티 리스트
    @Override
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            thirdParty.setRole("ROLE_ADMIN");
        }

        return storeMapper.selectThirdParty(thirdParty);
    }
    // 우선 배정 사유 리스트
    @Override
    public List<Reason> getAssignedAdvance(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return orderMapper.selectOrderFirstAssignmentReason(reason);
    }
    // 우선 배정 사유 추가
    @Override
    public int postAssignedAdvance(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertOrderFirstAssignmentReason(reason);
    }

    // 우선 배정 사유 수정
    @Override
    public int putAssignedAdvance(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.updateOrderFirstAssignmentReason(reason);
    }

    // 우선 배정 사유 삭제
    @Override
    public int deleteAssignedAdvance(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.deleteOrderFirstAssignmentReason(reason);
    }

    // 배정 거절 사유 리스트
    @Override
    public List<Reason> getassignedReject(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return riderMapper.selectRejectReason(reason);
    }


    // 배정 거절 사유 추가
    @Override
    public int postAssignedReject(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertRejectReason(reason);
    }

    // 배정 거절 사유 수정
    @Override
    public int putAssignedReject(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.updateRejectReason(reason);
    }

    // 배정 거절 사유 삭제
    @Override
    public int deleteRejectReason(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.deleteRejectReason(reason);
    }




}
