package kr.co.deliverydispatch.security;

import kr.co.cntt.core.mapper.StoreMapper;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.util.MD5Encoder;
import kr.co.cntt.core.util.ShaEncoder;
import kr.co.deliverydispatch.enums.SecurityEnum;
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
 * <p>kr.co.cntt.deliverydispatchstore.security;
 * <p>CustomAuthenticationProvider.java
 * <p>스프링 시큐리티 로그인 체크 및 권한 설정  CustomAuthenticationProvider 구현
 * @author JIN
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    /**
     * <p>상점 로그인 차단 카운트 == 5
     */
    private static final int STORE_LOGIN_LOCK_COUNT = 5;
    /**
     * <p>상점 비밀번호 변경 30일
     */
    private static final int STORE_PASSWORD_MODIFY_LIMIT_DAY = 30;
    /**
     * <p>상점 D A O
     */
    @Autowired
    private StoreMapper storeMapper;
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
        String storeId = (String) authentication.getPrincipal();
        String storePassword = (String) authentication.getCredentials();
        // 파람 SET
        Store parameterStore = new Store();
        parameterStore.setLoginId(storeId);
        MD5Encoder md5 = new MD5Encoder();
        ShaEncoder sha = new ShaEncoder(512);
        //parameterStore.setLoginPw(sha.encode(md5.encode(storePassword)));
        parameterStore.setLoginPw(sha.encode(storePassword));

        log.info("========================> storeId : {}", storeId);
        log.info("========================> storePassword : {}", storePassword);
        log.info("========================> sha.encode(storePassword) : {}", sha.encode(storePassword));

        // DB 조회
        Store returnStore = storeMapper.loginStoreInfo(parameterStore);
        if (returnStore != null) {
            // 로그인 성공
            int storeSeq =  Integer.parseInt(returnStore.getId());
            int storeChatUserId = Integer.parseInt(returnStore.getChatUserId());

            String storeName = returnStore.getLoginId();
            String storeStoreStatus = returnStore.getStoreStatus();
            String storeAssignmentStatus = returnStore.getAssignmentStatus();
            String storeAccessToken = returnStore.getAccessToken();

            String authLevel = "2"; // STORE
            List<GrantedAuthority> roles = null;
            try {
                roles = this.getAuthorization(authLevel);
            } catch (Exception e) {
                throw new AuthenticationCredentialsNotFoundException("LOGIN AUTHORIZATION FAIL");
            }
            SecurityUser securityUser = new SecurityUser(storeSeq, storeChatUserId, storeId, storePassword, storeName, storeStoreStatus, storeAssignmentStatus, storeAccessToken, authLevel, roles);
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(storeId, storePassword, roles);
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
     * <p>checkStoreLoginLockCount
     * <p>상점 계정 잠금 여부 확인
     * @param failCount 실패 카운트
     * @return 실패 카운트
     */

    private int checkStoreLoginLockCount(int failCount) {
        if (failCount >= STORE_LOGIN_LOCK_COUNT) {
            // 5번 로그인 실패 == 차단
            throw new LockedException("user account status locked");
        }
        return failCount;
    }
}
