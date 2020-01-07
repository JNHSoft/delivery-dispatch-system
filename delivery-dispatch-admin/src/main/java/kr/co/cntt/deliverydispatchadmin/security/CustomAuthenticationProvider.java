package kr.co.cntt.deliverydispatchadmin.security;

import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.util.MD5Encoder;
import kr.co.cntt.core.util.ShaEncoder;
import kr.co.cntt.deliverydispatchadmin.enums.SecurityEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>kr.co.cntt.deliverydispatchadmin.security;
 * <p>CustomAuthenticationProvider.java
 * <p>스프링 시큐리티 로그인 체크 및 권한 설정  CustomAuthenticationProvider 구현
 * @author JIN
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    /**
     * <p>관리자 로그인 차단 카운트 == 5
     */
    private static final int ADMIN_LOGIN_LOCK_COUNT = 5;
    /**
     * <p>관리자 비밀번호 변경 30일
     */
    private static final int ADMIN_PASSWORD_MODIFY_LIMIT_DAY = 30;
    /**
     * <p>관리자 D A O
     */
    @Autowired
    private AdminMapper adminMapper;
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String adminId = (String) authentication.getPrincipal();
        String adminPassword = (String) authentication.getCredentials();
        // 파람 SET
        Admin parameterAdmin = new Admin();
        parameterAdmin.setLoginId(adminId);
        MD5Encoder md5 = new MD5Encoder();
        ShaEncoder sha = new ShaEncoder(512);
        //parameterAdmin.setLoginPw(sha.encode(md5.encode(adminPassword)));
        parameterAdmin.setLoginPw(sha.encode(adminPassword));

        log.info("========================> adminId : {}", adminId);
        log.info("========================> adminPassword : {}", adminPassword);
        log.info("========================> sha.encode(adminPassword) : {}", sha.encode(adminPassword));

        // DB 조회
        Admin returnAdmin = adminMapper.loginAdminInfo(parameterAdmin);
        if (returnAdmin != null) {
            // 로그인 성공
            int adminSeq =  Integer.parseInt(returnAdmin.getId());
            int adminChatUserId = Integer.parseInt(returnAdmin.getChatUserId());

            String adminName = returnAdmin.getName();
            String adminState = returnAdmin.getState();
            String adminAssignmentStatus = returnAdmin.getAssignmentStatus();
            String adminAccessToken = returnAdmin.getAccessToken();

            // 20.01.07 Brand Check
            String adminBrandCode = returnAdmin.getBrandCode();
            String adminBrandName = returnAdmin.getBrandName();

            String authLevel = "1"; // ADMIN
            List<GrantedAuthority> roles = null;
            try {
                roles = this.getAuthorization(authLevel);
            } catch (Exception e) {
                throw new AuthenticationCredentialsNotFoundException("LOGIN AUTHORIZATION FAIL");
            }
            SecurityUser securityUser = new SecurityUser(adminSeq, adminChatUserId, adminId, adminPassword, adminName, adminState, adminAssignmentStatus, adminAccessToken, authLevel, adminBrandCode, adminBrandName, roles);
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(adminId, adminPassword, roles);
            result.setDetails(securityUser);
            return result;
        } else {
            // 로그인 실패
            throw new BadCredentialsException("Bad credentials");
        }
    }
    /**
     * <p>getAuthorization
     * <p>로그인
     * <p>성공시 권한 설정
     * @param authLevel 권한 레벨
     * @return Auth Collection
     * @throws Exception
     */
    private List<GrantedAuthority> getAuthorization(String authLevel) throws Exception {
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        // 로그인 성공 - 기본 권한 부여
        roles.add(new SimpleGrantedAuthority("ROLE_LOGIN"));
        // 로그인 성공 - 권한 레벨에 따라 권한 부여
        roles.add(new SimpleGrantedAuthority(SecurityEnum.getSecurityEnum(authLevel).getRoleName()));
        return roles;
    }
    /**
     * <p>checkAdminLoginLockCount
     * <p>관리자 계정 잠금 여부 확인
     * @param failCount 실패 카운트
     * @return 실패 카운트
     */

    private int checkAdminLoginLockCount(int failCount) {
        if (failCount >= ADMIN_LOGIN_LOCK_COUNT) {
            // 5번 로그인 실패 == 차단
            throw new LockedException("user account status locked");
        }
        return failCount;
    }
}
