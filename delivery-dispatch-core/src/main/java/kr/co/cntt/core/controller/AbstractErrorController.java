package kr.co.cntt.core.controller;

public abstract class AbstractErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

	public static final String ERROR_PATH = "/error";
	public static final String ERROR_DEFAULT = ERROR_PATH + "/default";
	public static final String ERROR_401 = ERROR_PATH + "/401";
	public static final String ERROR_403 = ERROR_PATH + "/403";
	public static final String ERROR_404 = ERROR_PATH + "/404";
	public static final String ERROR_500 = ERROR_PATH + "/500";

	public abstract String defaultError();
	public abstract String error401();
	public abstract String error403();
	public abstract String error404();
	public abstract String error500();
	
	@Override
	public String getErrorPath() {
		return ERROR_DEFAULT;
	}
}
