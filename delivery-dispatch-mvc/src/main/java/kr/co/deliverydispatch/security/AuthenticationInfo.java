package kr.co.deliverydispatch.security;

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
     * <p>getStoreSeq
     * <p>SEQ 가져오기
     * @return 상점 고유 키
     */
    public Integer getStoreSeq() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreSeq();
    }
    /**
     * <p>getStoreSeq
     * <p>ChatSeq 가져오기
     * @return 상점 ChatSeq 고유 키
     */
    public Integer getStoreChatSeq() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreChatSeq();
    }
    /**
     * <p>getStoreId
     * <p>아이디 가져오기
     * @return 상점 아이디
     */
    public String getStoreId() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreId();
    }
    /**
     * <p>getStoreName
     * <p>이름 가져오기
     * @return 상점 이름
     * @author JIN
     */
    public String getStoreName() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreName();
    }
    /**
     * <p>getAuthLevel
     * <p>권한 레벨 가져오기
     * @return 상점 권한 레벨
     */
    public String getAuthLevel() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getAuthLevel();
    }
    /**
     * <p>getrStatus
     * <p>State 가져오기
     * @return 상점 State
     */
    public String getStoreStatus() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreStoreStatus();
    }
    /**
     * <p>getAssignmentLimit
     * <p>AssignmentLimit 가져오기
     * @return AssignmentLimit
     */
    public String getStoreAssignmentLimit() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreAssignmentLimit();
    }

    /**
     * <p>getAssignmentState
     * <p>AssignmentState 가져오기
     * @return 상점 AssignmentState
     */
    public String getStoreAssignmentState() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreAssignmentState();
    }

    /**
     * <p>getAssignmentState
     * <p>AccessToken 가져오기
     * @return 상점 AccessToken
     */
    public String getStoreAccessToken() {
        return getLoginInfoDetails() == null ? null : getLoginInfoDetails().getStoreAccessToken();
    }

}
