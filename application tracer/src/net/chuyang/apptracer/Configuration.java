package net.chuyang.apptracer;

public class Configuration {
	public static String getAppFolderPath(){
		String workFolder = System.getProperty("apptracer.work.folder");
		if(workFolder == null)
			return System.getProperty("java.io.tmpdir");
		else
			return workFolder;
	}
}
