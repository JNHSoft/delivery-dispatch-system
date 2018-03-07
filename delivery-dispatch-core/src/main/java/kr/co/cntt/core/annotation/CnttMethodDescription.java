package kr.co.cntt.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>kr.co.cntt.core.annotation
 * <p>CnttMethodDescription.java
 * <p>Adds a textual description to bean definitions derived from
 * @see org.springframework.beans.factory.config.BeanDefinition#getDescription()
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CnttMethodDescription {
    /**
     * <p>value
     * <p>The textual description to associate with the bean definition.
     * @author JIN
     */
    String value();
}
