package kr.co.cntt.core.model;

import org.springframework.mobile.device.Device;

public abstract class AbstractChannelInfo implements Dto {

	private static final long serialVersionUID = -8335936663741594236L;

	protected String channel;

	public String getChannel() {
		return this.channel;
	}
	
	public void setChannel(Device device) {
		this.channel = device.isMobile() ? "mobile" : "web";
	}
}
