package kr.co.cntt.core.service.admin.impl;

import kr.co.cntt.core.fcm.AndroidPushNotificationsService;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.redis.service.RedisService;
import kr.co.cntt.core.service.admin.AccountAdminService;
import kr.co.cntt.core.util.MD5Encoder;
import kr.co.cntt.core.util.ShaEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("accountAdminService")
public class AccountAdminServiceImpl implements AccountAdminService {

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
     * @param adminMapper Admin D A O
     */
    @Autowired
    public AccountAdminServiceImpl(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    @Override
    public Admin getAdminAccount(Admin admin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            admin.setRole("ROLE_ADMIN");
        }

        List<Admin> S_Admin = adminMapper.selectAdminInfo(admin);

        return S_Admin.get(0);
    }

    @Override
    public int putAdminAccount(Admin admin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().toString().matches(".*ROLE_ADMIN.*")) {
            admin.setRole("ROLE_ADMIN");
        }

        MD5Encoder md5 = new MD5Encoder();
        ShaEncoder sha = new ShaEncoder(512);
        Admin parameterAdmin = new Admin();

        parameterAdmin.setLoginPw(sha.encode(admin.getLoginPw()));
        parameterAdmin.setToken(admin.getToken());
        parameterAdmin.setRole("ROLE_ADMIN");

        return adminMapper.updateAdminInfo(parameterAdmin);
    }

}
