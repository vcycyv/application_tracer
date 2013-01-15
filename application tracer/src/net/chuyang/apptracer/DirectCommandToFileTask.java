package net.chuyang.apptracer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

public class DirectCommandToFileTask implements Runnable {
	private String command;
	private File file;

	private int flushLineNumber = 5;

	public DirectCommandToFileTask(String command, String filePath) {
		this.command = command;
		this.file = new File(filePath);
	}
	
	@Override
	public void run() {
		try {
			Process p = Runtime.getRuntime().exec("cmd /c " + command);
			StringBuffer output = new StringBuffer("");
			if (p != null) {
				BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String buf = "";
				try {
					int count = 0;
					while ((buf = is.readLine()) != null) {
						output.append(buf);
						output.append(System.getProperty("line.separator"));
						if(++count % flushLineNumber == 0){
							FileUtils.writeStringToFile(file, output.toString(), true);
							output.setLength(0);
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally{
					is.close();
				} 
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public File getFile(){
		return file;
	}
}
