package kr.co.cntt.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import kr.co.cntt.core.annotation.PermitValue;

/**
 * <p>kr.co.cntt.core.validator
 * <p>PermitValueValidator.java
 * <p>허용된 값인지 검증
 * @author JIN
 */
public class PermitValueValidator implements ConstraintValidator<PermitValue, Object> {
	/**
	 * <p>허용 값 배열
	 * @author JIN
	 */
	private String[] permitValues;
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(PermitValue permitValue) {
		this.permitValues = permitValue.value();
	}
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		// 허용된값인지 확인
		boolean result = false;
		try {
			if (value == null) {
				return result;
			}
			for (String permitValue : permitValues) {
				if (permitValue.equals(value)) {
					result = true;
					break;
				}
			}
		} catch(Exception e) {
			throw e;
		}
		return result;
	}
}
