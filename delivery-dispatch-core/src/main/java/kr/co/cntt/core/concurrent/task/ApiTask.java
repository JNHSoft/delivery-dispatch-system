package kr.co.cntt.core.concurrent.task;

public abstract class ApiTask implements IServerTask {
	public abstract void requestAPI();
	@Override
	public void processTask() {
		this.requestAPI();
	}
}
