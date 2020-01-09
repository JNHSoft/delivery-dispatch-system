package kr.co.cntt.deliverydispatchadmin.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUser implements UserDetails {
    /**
     * <p>관리자 고유 키 (DB Column : id)
     */
    private int adminSeq;

    /**
     * <p>관리자 ChatSeq (DB Column : chat_user_id)
     */
    private int adminChatSeq;
    /**
     * <p>관리자 아이디 (DB Column : login_id)
     */
    private String adminId;
    /**
     * <p>관리자 비밀번호 (DB Column : login_pw)
     */
    private String adminPassword;
    /**
     * <p>관리자 이름 (DB Column : name)
     */
    private String adminName;
    /**
     * <p>관리자 STATE (DB Column : state)
     */
    private String adminState;
    /**
     * <p>관리자 AssignmentState (DB Column : assignment_state)
     */
    private String adminAssignmentState;
    /**
     * <p>관리자 AssignmentLimit (DB Column : assignment_limit)
     */
    private String adminAssignmentLimit;

    /**
     * <p>관리자 accessToken (DB Column : access_token)
     */
    private String adminAccessToken;

    /**
     * <p>관리자 권한
     */
    private String authLevel;

    /**
     * 20.01.07 <p> 브랜드 코드
     * */
    private String adminBrandCode;

    /**
     * <p> 브랜드 명
     * */
    private String adminBrandName;

    /**
     * <p> 브랜드 이미지 Path
     * */
    private String adminImg;

    /**
     * <p>Auth Collection
     */
    private Collection<? extends GrantedAuthority> authoritiesd;
    /**
     * @param adminSeq 관리자 고유 키
     * @param adminId 관리자 아이디
     * @param adminPassword 관리자 비밀번호
     * @param adminName 관리자 이름
     * @param adminState 관리자 STATE
     * @param adminAssignmentState 관리자 AssignmentState
     * @param authLevel 관리자 권한 레벨
     * @param adminBrandCode 관리자의 브랜드 코드
     * @param adminBrandName 관리자의 브랜드명
     * @param authoritiesd 관리자 권한 콜렉션
     * @author JIN
     */
    public SecurityUser(int adminSeq,
                        int adminChatSeq,
                        String adminId,
                        String adminPassword,
                        String adminName,
                        String adminState,
                        String adminAssignmentState,
                        String adminAccessToken,
                        String authLevel,
                        String adminBrandCode,
                        String adminBrandName,
                        String adminImg,
                        Collection<? extends GrantedAuthority> authoritiesd) {
        this.adminSeq = adminSeq;
        this.adminChatSeq = adminChatSeq;
        this.adminId = adminId;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
        this.adminState = adminState;
        this.adminAssignmentState = adminAssignmentState;
        this.adminAccessToken = adminAccessToken;
        this.authLevel = authLevel;
        this.authoritiesd = authoritiesd;

        this.adminBrandCode = adminBrandCode;
        this.adminBrandName = adminBrandName;
        this.adminImg = adminImg;
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
        return adminPassword;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    @Override
    public String getUsername() {
        return adminId;
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
