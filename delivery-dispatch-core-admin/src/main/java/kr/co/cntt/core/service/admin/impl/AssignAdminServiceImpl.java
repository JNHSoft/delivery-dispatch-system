package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.mapper.OrderMapper;
import kr.co.cntt.core.mapper.RiderMapper;
import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.reason.Reason;
import kr.co.cntt.core.model.thirdParty.ThirdParty;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.admin.AccountAdminService;
import kr.co.cntt.core.service.admin.AssignAdminService;
import kr.co.cntt.core.util.MD5Encoder;
import kr.co.cntt.core.util.ShaEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public int postThirdParty(ThirdParty thirdParty) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            thirdParty.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertThirdParty(thirdParty);
    }

    @Override
    public List<ThirdParty> getThirdParty(ThirdParty thirdParty) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            thirdParty.setRole("ROLE_ADMIN");
        }

        return storeMapper.selectThirdParty(thirdParty);
    }

    @Override
    public List<Reason> getAssignedAdvance(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return orderMapper.selectOrderFirstAssignmentReason(reason);
    }

    @Override
    public List<Reason> getassignedReject(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return riderMapper.selectRejectReason(reason);
    }

    @Override
    public int postAssignedAdvance(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertOrderFirstAssignmentReason(reason);
    }

    @Override
    public int postAssignedReject(Reason reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            reason.setRole("ROLE_ADMIN");
        }

        return adminMapper.insertRejectReason(reason);
    }

}
