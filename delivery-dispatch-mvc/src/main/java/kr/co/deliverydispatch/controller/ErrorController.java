package kr.co.deliverydispatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.co.cntt.core.controller.AbstractErrorController;
import kr.co.cntt.core.trace.NotMonitor;

@Controller
@NotMonitor
public class ErrorController extends AbstractErrorController {
	
	@RequestMapping(value = ERROR_DEFAULT, method = RequestMethod.GET)
	public String defaultError() {
		return ERROR_DEFAULT;
	}

	@RequestMapping(value = ERROR_401, method = RequestMethod.GET)
	public String error401() {
		return ERROR_401;
	}

	@RequestMapping(value = ERROR_403, method = RequestMethod.GET)
	public String error403() {
		return ERROR_403;
	}

	@RequestMapping(value = ERROR_404, method = RequestMethod.GET)
	public String error404() {
		return ERROR_404;
	}
	
	@RequestMapping(value = ERROR_500, method = RequestMethod.GET)
	public String error500() {
		return ERROR_500;
	}
}
