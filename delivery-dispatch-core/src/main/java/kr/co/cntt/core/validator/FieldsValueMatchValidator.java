package kr.co.cntt.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

import kr.co.cntt.core.annotation.FieldsValueMatch;

/**
 * <p>kr.co.cntt.core.validator
 * <p>FieldsValueMatchValidator.java
 * <p>필드간 값 비교
 * <p>FieldsValueMatch Annotation Validator
 * @author JIN
 */
public class FieldsValueMatchValidator implements ConstraintValidator<FieldsValueMatch, Object> {
	/**
	 * <p>오리지날 필드
	 * @author JIN
	 */
	private String originalField;
	/**
	 * <p>비교할 필드
	 * @author JIN
	 */
	private String compareField;
	/**
	 * <p>에러 메시지
	 * @author JIN
	 */
	private String errorMessage;
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(FieldsValueMatch fieldsValueMatch) {
		this.originalField = fieldsValueMatch.originalField();
		this.compareField = fieldsValueMatch.compareField();
		this.errorMessage = fieldsValueMatch.message();
	}
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Object objectValue, ConstraintValidatorContext constraintValidatorContext) {
		// originalField 와 compareField 값 비교
		try {
			Object originalFieldValue = new BeanWrapperImpl(objectValue).getPropertyValue(originalField);
			Object compareFieldValue = new BeanWrapperImpl(objectValue).getPropertyValue(compareField);
			
			boolean result = originalFieldValue == null 
					&& compareFieldValue == null || originalFieldValue != null 
					&& originalFieldValue.equals(compareFieldValue);
			
			if (!result) {
				// 에러 메시지 전달
				constraintValidatorContext.buildConstraintViolationWithTemplate(errorMessage).addPropertyNode(compareField).addConstraintViolation();
			}
			
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
}
