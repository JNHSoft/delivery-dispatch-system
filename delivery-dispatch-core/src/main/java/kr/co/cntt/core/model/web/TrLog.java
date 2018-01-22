package kr.co.cntt.core.model.web;

import javax.servlet.http.HttpServletRequest;

import kr.co.cntt.core.concurrent.task.ILog;
import kr.co.cntt.core.model.AbstractPagination;
import kr.co.cntt.core.util.AgentUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrLog extends AbstractPagination  implements ILog{

	private static final long serialVersionUID = -8141411378679250481L;
	
	/** request ip */
	private String ip;
	/** request channel 구분 */
	private String channel;
	/** request uri */
	private String uri;
	/** 요청 클래스명*/
	private String clazz;
	/** 요청 메소드명 */
	private String method;
	/** 요청 메세지 */
	private String requestMessage;
	/** 응답 메세지 */
	private String responseMessage;
	/** 성공결과 */
	private String result;
	/** 요청시간 */
	private String stime;
	/** 응답시간 */
	private String etime;
	/** 소요시간 */
	private Long elapsed;
	
	public TrLog(HttpServletRequest req) {
		this.ip = AgentUtil.getIp(req);
		this.channel = AgentUtil.getChannel(req);
		this.uri = req.getRequestURI();
		this.requestMessage = "";	// binary must be not null
		this.responseMessage = "";	// binary must be not null
	}

	public String getLogInfo() {
		return "[TrLog][ip=" + ip + ", channel=" + channel + ", uri=" + uri + ", clazz=" + clazz + ", method=" + method
				+ ", requestMessage=" + requestMessage + ", responseMessage=" + responseMessage + ", result=" + result
				+ ", stime=" + stime + ", etime=" + etime + ", elapsed=" + elapsed + "]";
	}
	
}
