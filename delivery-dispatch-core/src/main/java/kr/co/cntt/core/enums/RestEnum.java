package kr.co.cntt.core.enums;

/**
 * <p>kr.co.cntt.core.enums
 * <p>RestEnum.java
 * <p>API 통신 enum 객체
 * @author JIN
 */
public enum RestEnum {
	/**
	 * <p>APP API 성공 코드
	 * @author JIN
	 */
	RESPONSE_SUCCESS_CODE("2000"),
	/**
	 * <p>회원 관련 api ref name
	 * @author JIN
	 */
	MEMBER_REF_NAME("outuser"),
	/**
	 * <p>회원 관련 에러  prefix
	 * @author JIN
	 */
	MEMBER_ERROR_MESSAGE("회원 정보 갱신"),
	/**
	 * <p>회원 등록
	 * @author JIN
	 */
	INSERT_MEMBER("I"), 
	/**
	 * <p>회원 휴면 복구
	 * @author JIN
	 */
	RESTORE_MEMBER("R"),
	/**
	 * <p>회원 수정
	 * @author JIN
	 */
	UPDATE_MEMBER("M"),
	/**
	 * <p>회원 탈퇴
	 * @author JIN
	 */
	DELETE_MEMBER("D"),
	/**
	 * <p>회원 로그인 로그
	 * @author JIN
	 */
	LOGIN_LOG("L"),
	/**
	 * <p>매장 관련 api ref name
	 * @author JIN
	 */
	BRANCH_REF_NAME("recBranchIdxStateInfo"),
	/**
	 * <p>매장 관련 에러  prefix
	 * @author JIN
	 */
	BRANCH_ERROR_MESSAGE("매장 정보 갱신"),
	/**
	 * <p>주문 관련 에러  prefix
	 * @author JIN
	 */
	ORDER_ERROR_MESSAGE("주문 정보 전송"),
	;
	/**
	 * <p>값
	 * @author JIN
	 */
	private String value;
	
	/**
	 * @param value 값
	 * @author JIN
	 */
	private RestEnum(final String value) {
		this.setValue(value);
	}
	/**
	 * <p>getValue
	 * @return the value
	 * @author JIN
	 */
	public String getValue() {
		return value;
	}
	/**
	 * <p>setValue
	 * @param value the value to set
	 * @author JIN
	 */
	private void setValue(String value) {
		this.value = value;
	}
}
