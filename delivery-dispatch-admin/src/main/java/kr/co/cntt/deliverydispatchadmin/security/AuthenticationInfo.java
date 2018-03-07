package kr.co.cntt.deliverydispatchadmin.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationInfo {
    /**
     * <p>org.springframework.security.core.Authentication
     */
    private Authentication authentication;
    /**
     * <p>isAuthenticated
     * <p>객체 생성 && 유효성 검사
     * @return 검사 결과
     */
    private boolean isAuthenticated() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
    /**
     * <p>isLogin
     * @return 로그인 체크 여부
     */
    public boolean isLogin() {
        return isAuthenticated();
    }
    /**
     * <p>getLoginInfoDetails
     * <p>로그인 정보 가져오기
     * @return 시큐리티 유저 정보
     */
    private SecurityUser getLoginInfoDetails() {
        if (!isAuthenticated()) {
            return null;
        }
        Object details = authentication.getDetails();
        if (!(details instanceof SecurityUser)) {
            return null;
        }
        return (SecurityUser)details;
    }
    /**
     * <p>getAdminSeq
     * <p>SEQ 가져오기
     * @return 관리자 고유 키
     */
    public Integer getAdminSeq() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminSeq();
    }
    /**
     * <p>getAdminSeq
     * <p>ChatSeq 가져오기
     * @return 관리자 ChatSeq 고유 키
     */
    public Integer getAdminChatSeq() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminChatSeq();
    }
    /**
     * <p>getAdminId
     * <p>아이디 가져오기
     * @return 관리자 아이디
     */
    public String getAdminId() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminId();
    }
    /**
     * <p>getAdminName
     * <p>이름 가져오기
     * @return 관리자 이름
     * @author JIN
     */
    public String getAdminName() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminName();
    }
    /**
     * <p>getAuthLevel
     * <p>권한 레벨 가져오기
     * @return 관리자 권한 레벨
     */
    public String getAuthLevel() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAuthLevel();
    }
    /**
     * <p>getrState
     * <p>State 가져오기
     * @return 관리자 State
     */
    public String getAdminState() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminState();
    }
    /**
     * <p>getAssignmentLimit
     * <p>AssignmentLimit 가져오기
     * @return 관리자 연락처
     */
    public String getAdminAssignmentLimit() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminAssignmentLimit();
    }

    /**
     * <p>getAssignmentState
     * <p>AssignmentState 가져오기
     * @return 관리자 State
     */
    public String getAdminAssignmentState() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminAssignmentState();
    }

    /**
     * <p>getAssignmentState
     * <p>AssignmentState 가져오기
     * @return 관리자 State
     */
    public String getAdminAccessToken() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAdminAccessToken();
    }

}
