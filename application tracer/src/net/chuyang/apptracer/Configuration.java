package net.chuyang.apptracer;

public enum Configuration {
	INSTANCE;
	
	private int targetPort = 0;

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}
}
