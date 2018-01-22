package kr.co.deliverydispatch.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

@ControllerAdvice(annotations={CrossOrigin.class})
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice{
	
	/**
	 * constructor
	 */
	public JsonpAdvice() {
		super("callback");
	}
}
