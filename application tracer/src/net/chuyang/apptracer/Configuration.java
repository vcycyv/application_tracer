package net.chuyang.apptracer;

public enum Configuration {
	INSTANCE;
	
	private String targetPort = "5516";
	
	public String getAppFolderPath(){
		String workFolder = System.getProperty("apptracer.work.folder");
		if(workFolder == null)
			return System.getProperty("java.io.tmpdir");
		else
			return workFolder;
	}
	
	public String getClasspath(){
		//TODO - to implement getClassPath
		return Constants.TARGET_CLASSPATH;
	}
	
	public String getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}
}
