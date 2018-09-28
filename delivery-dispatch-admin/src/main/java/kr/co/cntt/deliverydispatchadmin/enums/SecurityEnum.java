package kr.co.cntt.deliverydispatchadmin.enums;

import lombok.Getter;

/**
 * <p>kr.co.cntt.deliverydispatchadmin.enums
 * <p>SecurityEnum.java
 * <p>Security Enum 객체
 */
@Getter
public enum SecurityEnum {
    DEVLOPER("9", "ROLE_GOD", "/store"),
    /**
     * <p>시스템(최고 관리자)
     * @author JIN
     */
    SUPER("0", "ROLE_SUPER", "/order"),
    /**
     * <p>일반 관리자
     * @author JIN
     */
    ADMIN("1", "ROLE_ADMIN", "/store"),
    /**
     * <p>상점
     * @author JIN
     */
    STORE("2", "ROLE_STORE", "/order"),
    /**
     * <p>라이더
     * @author JIN
     */
    RIDER("3", "ROLE_RIDER", "/order")
    ;
    /**
     * <p>권한 레벨
     * @author JIN
     */
    private String value;
    /**
     * <p>권한 이름
     * @author JIN
     */
    private String roleName;
    /**
     * <p>권한별 첫 페이지
     * @author JIN
     */
    private String firstPage;
    /**
     * @param value 권한 레벨
     * @param roleName 권한 이름
     * @param firstPage 권한별 첫 페이지
     * @author JIN
     */
    private SecurityEnum(final String value
            , final String roleName
            , final String firstPage) {
        this.value = value;
        this.roleName = roleName;
        this.firstPage = firstPage;
    }
    /**
     * <p>getSecurityEnum
     * <p>
     * <p>내용 (생략 가능)
     * @param 권한 레벨
     * @return 권한에 맞는 enum 객체
     * @author JIN
     */
    public static SecurityEnum getSecurityEnum(String authLevel) {
        SecurityEnum returnSecurityEnum = null;
        for (SecurityEnum se : SecurityEnum.values()) {
            if (se.getValue().equals(authLevel)) {
                returnSecurityEnum = se;
                break;
            }
        }
        return returnSecurityEnum;
    }
}
