package net.chuyang.apptracer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum TaskProcessor {
	INSTANCE;
	
	private ExecutorService exec = Executors.newCachedThreadPool();
	private Map<String, DirectCommandToFileTask> taskMap = new HashMap<String, DirectCommandToFileTask>();
	
	public File createTask(String command, String fileName){
		StringBuilder sb = new StringBuilder();
		String fullPathFileName = sb.append(Configuration.getAppFolderPath()).append(fileName).toString();
		DirectCommandToFileTask task = new DirectCommandToFileTask(command, fullPathFileName);
		taskMap.put(fileName, task);
		exec.execute(task);
		return new File(fullPathFileName);
	}
}
