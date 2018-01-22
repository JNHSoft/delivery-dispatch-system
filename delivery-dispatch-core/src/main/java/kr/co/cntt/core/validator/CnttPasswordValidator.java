package kr.co.cntt.core.validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

import kr.co.cntt.core.annotation.CnttPassword;

/**
 * <p>kr.co.cntt.core.validator
 * <p>CnttPasswordValidator.java
 * <p>패스워드 규칙
 * <p>1. 최소 8자 ~ 최대 20자 이내로 입력합니다.
 * <p>2. 반드시 영문, 숫자, 특수문자가 각 1자리 이상 포함되어야 합니다.
 * <p>3. 3자리 이상 연속되는 숫자 또는 문자열은 사용할 수 없습니다. (예:123, 321, 012, abc, cba)
 * <p>4. 3자리 이상 동일한 숫자 또는 문자열은 사용할 수 없습니다. (예:000, 111, 222, ,aaa, bbb)
 * <p>5. 아이디와 연속한 3자리 이상 일치하는 비밀번호는 사용할 수 없습니다.
 * @author JIN
 */
public class CnttPasswordValidator implements ConstraintValidator<CnttPassword, Object> {
	/**
	 * <p>아이디 필드
	 * @author JIN
	 */
	private String idField;
	/**
	 * <p>비밀번호 필드
	 * @author JIN
	 */
	private String passwordField;
	/**
	 * <p>기본 에러 메시지
	 * @author JIN
	 */
	private String defaultErrorMessage;
	/**
	 * <p>정규표현식(영문, 숫자, 특수문자 포함 8 ~ 20자)
	 * @author JIN
	 */
	private static final String DEFAULT_REGULAR_EXPRESSION = "^(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`])(?=.*[a-zA-Z]).{8,20}$"; 
	/**
	 * <p>3자리 이상 연속되는 숫자 또는 문자열은 사용할 수 없습니다. (예:123, 321, 012, abc, cba) 에러 메세지
	 * @author JIN
	 */
	private static final String CONTINUITY_THREE_MESSAGE = "3자리 이상 연속되는 숫자 또는 문자열은 사용할 수 없습니다.";
	/**
	 * <p>정규표현식(3자리 이상 동일한 숫자 또는 문자열은 사용할 수 없습니다. (예:000, 111, 222, ,aaa, bbb))
	 * @author JIN
	 */
	private static final String SAME_THREE_REGULAR_EXPRESSION = "(.)\\1{2,}";
	/**
	 * <p>3자리 이상 동일한 숫자 또는 문자열은 사용할 수 없습니다. (예:000, 111, 222, ,aaa, bbb) 에러 메세지
	 * @author JIN
	 */
	private static final String SAME_THREE_MESSAGE = "3자리 이상 동일한 숫자 또는 문자열은 사용할 수 없습니다.";
	/**
	 * <p>아이디와 연속한 3자리 이상 일치하는 비밀번호는 사용할 수 없습니다. 에러 메세지
	 * @author JIN
	 */
	private static final String SAME_ID_THREE_MESSAGE = "아이디와 연속한 3자리 이상 일치하는 비밀번호는 사용할 수 없습니다.";
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(CnttPassword cnttPassword) {
		this.idField = cnttPassword.idField();
		this.passwordField = cnttPassword.passwordField();
		this.defaultErrorMessage = cnttPassword.message();
	}
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Object objectValue, ConstraintValidatorContext constraintValidatorContext) {
		boolean result = false;
		try {
			String id = (String)new BeanWrapperImpl(objectValue).getPropertyValue(idField);
			String password = (String)new BeanWrapperImpl(objectValue).getPropertyValue(passwordField);
			// 0. null, 미입력 방어코드
			if (this.isNullOrEmpty(password)) {
				constraintValidatorContext.buildConstraintViolationWithTemplate(defaultErrorMessage).addPropertyNode(passwordField).addConstraintViolation();
				return result;
			}
			// 1. 최소 8자 ~ 최대 20자 이내로 입력합니다.
			// 2. 반드시 영문, 숫자, 특수문자가 각 1자리 이상 포함되어야 합니다.
			if (!this.isDefaultMatcher(password)) {
				constraintValidatorContext.buildConstraintViolationWithTemplate(defaultErrorMessage).addPropertyNode(passwordField).addConstraintViolation();
				return result;
			}
			// 기본 메시지 안쓴다.
			constraintValidatorContext.disableDefaultConstraintViolation();
			// 3. 3자리 이상 연속되는 숫자 또는 문자열은 사용할 수 없습니다. (예:123, 321, 012, abc, cba)
			if (this.isContinuityThree(password)) {
				constraintValidatorContext.buildConstraintViolationWithTemplate(CONTINUITY_THREE_MESSAGE).addPropertyNode(passwordField).addConstraintViolation();
				return result;
			}
			// 4. 3자리 이상 동일한 숫자 또는 문자열은 사용할 수 없습니다. (예:000, 111, 222, ,aaa, bbb)
			if (this.isSameThree(password)) {
				constraintValidatorContext.buildConstraintViolationWithTemplate(SAME_THREE_MESSAGE).addPropertyNode(passwordField).addConstraintViolation();
				return result;
			}
			// 5. 아이디와 연속한 3자리 이상 일치하는 비밀번호는 사용할 수 없습니다.
			boolean isEmail = id.indexOf("@") == -1 ? false : true;
			if (this.isSameIdThree(password, id, isEmail)) {
				constraintValidatorContext.buildConstraintViolationWithTemplate(SAME_ID_THREE_MESSAGE).addPropertyNode(passwordField).addConstraintViolation();
				return result;
			}
			// 모두 통과 했다면 참 리턴
			result = true;
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	/**
	 * <p>isNullOrEmpty
	 * <p>null, 미입력 방어
	 * @param password 사용자 입력 비밀번호
	 * @return 검증 결과
	 * @author JIN
	 */
	private boolean isNullOrEmpty(String password) {
		return StringUtils.isEmpty(password);
	}
	/**
	 * <p>isDefaultMatcher
	 * <p>최소 8자 ~ 최대 20자 이내
	 * <p>영문, 숫자, 특수문자가 각 1자리 이상 포함
	 * @param password 사용자 입력 비밀번호
	 * @return 검증 결과
	 * @author JIN
	 */
	private boolean isDefaultMatcher(String password) {
		return Pattern.compile(DEFAULT_REGULAR_EXPRESSION).matcher(password).find();
	}
	/**
	 * <p>isContinuityThree
	 * <p>3자리 이상 연속되는 숫자 또는 문자열은 사용할 수 없습니다.
	 * @param password 사용자 입력 비밀번호
	 * @return 검증 결과
	 * @author JIN
	 */
	private boolean isContinuityThree(String password) {
		boolean isContinuityThree = false;
		byte[] passwordBytes = password.getBytes();
		int passwordBytesLength = passwordBytes.length;
		for (int i = 0; i < passwordBytesLength - 2; ++i) {
			if (passwordBytes[i] + 1 == passwordBytes[i+1] && passwordBytes[i+1] + 1 == passwordBytes[i+2]) {
				isContinuityThree = true;
				break;
			}
			if (passwordBytes[i] - 1 == passwordBytes[i+1] && passwordBytes[i+1] - 1 == passwordBytes[i+2]) {
				isContinuityThree = true;
				break;
			}
		}
		return isContinuityThree;
	}
	/**
	 * <p>isSameThree
	 * <p>3자리 이상 동일한 숫자 또는 문자열은 사용할 수 없습니다.
	 * @param password 사용자 입력 비밀번호
	 * @return 검증 결과
	 * @author JIN
	 */
	private boolean isSameThree(String password) {
		return Pattern.compile(SAME_THREE_REGULAR_EXPRESSION).matcher(password).find();
	}
	/**
	 * <p>isSameIdThree
	 * <p>아이디와 연속한 3자리 이상 일치하는 비밀번호는 사용할 수 없습니다.
	 * @param password 사용자 입력 비밀번호
	 * @param idValue 사용자 입력 아이디
	 * @param isEmail 아이디 이메일 형식 여부
	 * @return 검증 결과
	 * @author JIN
	 */
	private boolean isSameIdThree(String password, String id, boolean isEmail) {
		boolean isSameIdThree = false;
		if (!StringUtils.isEmpty(id)) {
			String originalId = isEmail ? id.substring(0, id.indexOf("@")) : id;
			int idLength = originalId.length() - 2;
			for (int i = 0; i < idLength; i++) {
				if (password.indexOf(originalId.substring(i, i + 3)) != -1) {
					isSameIdThree = true;
					break;
				}
			}
		}
		return isSameIdThree;
	}
}
