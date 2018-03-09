package kr.co.deliverydispatch.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class SecurityUser implements UserDetails {
    /**
     * <p>상점 고유 키 (DB Column : id)
     */
    private int storeSeq;

    /**
     * <p>상점 ChatSeq (DB Column : chat_user_id)
     */
    private int storeChatSeq;
    /**
     * <p>상점 아이디 (DB Column : login_id)
     */
    private String storeId;
    /**
     * <p>상점 비밀번호 (DB Column : login_pw)
     */
    private String storePassword;
    /**
     * <p>상점 이름 (DB Column : name)
     */
    private String storeName;
    /**
     * <p>상점 STATUS (DB Column : store_status)
     */
    private String storeStoreStatus;
    /**
     * <p>상점 AssignmentState (DB Column : assignment_state)
     */
    private String storeAssignmentState;
    /**
     * <p>상점 AssignmentLimit (DB Column : assignment_limit)
     */
    private String storeAssignmentLimit;

    /**
     * <p>상점 accessToken (DB Column : access_token)
     */
    private String storeAccessToken;

    /**
     * <p>상점 권한
     */
    private String authLevel;

    /**
     * <p>Auth Collection
     */
    private Collection<? extends GrantedAuthority> authoritiesd;
    /**
     * @param storeSeq 상점 고유 키
     * @param storeId 상점 아이디
     * @param storePassword 상점 비밀번호
     * @param storeName 상점 이름
     * @param storeStoreStatus 상점 STATUS
     * @param storeAssignmentState 상점 AssignmentState
     * @param authLevel 상점 권한 레벨
     * @param authoritiesd 상점 권한 콜렉션
     * @author JIN
     */
    public SecurityUser(int storeSeq,
                        int storeChatSeq,
                        String storeId,
                        String storePassword,
                        String storeName,
                        String storeStoreStatus,
                        String storeAssignmentState,
                        String storeAccessToken,
                        String authLevel,
                        Collection<? extends GrantedAuthority> authoritiesd) {
        this.storeSeq = storeSeq;
        this.storeChatSeq = storeChatSeq;
        this.storeId = storeId;
        this.storePassword = storePassword;
        this.storeName = storeName;
        this.storeStoreStatus = storeStoreStatus;
        this.storeAssignmentState = storeAssignmentState;
        this.storeAccessToken = storeAccessToken;
        this.authLevel = authLevel;
        this.authoritiesd = authoritiesd;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authoritiesd;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    @Override
    public String getPassword() {
        return storePassword;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    @Override
    public String getUsername() {
        return storeId;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return false;
    }
}
