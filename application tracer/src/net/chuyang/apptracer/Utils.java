package net.chuyang.apptracer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.ResourceBundle;

public class Utils {
	/**
	 * For example, the code template is: I love ${food}, haha. 
	 * The paramMap may contain <food, apple>, then the result is I love apple, haha.
	 * 
	 * @param command
	 * @param paramMap key is place holder, the value is the String used to replace the place holder.
	 * @return
	 */
	public static String processCodeTemplate(String command, Map<String, String> paramMap){
		for(Map.Entry<String, String> entry : paramMap.entrySet()){
			String placeHolder = "\\$\\{" + entry.getKey() + "\\}";
			command = command.replaceAll(placeHolder, entry.getValue());
		}
		return command;
	}
	
	public static String getOutputFromOSCommand(String command){
		StringBuffer output = new StringBuffer("");
		try{
			Process p = Runtime.getRuntime().exec("cmd /c " + command);
			if (p != null) {
				BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String buf = "";
				try {
					while ((buf = is.readLine()) != null) {
						output.append(buf);
						output.append(System.getProperty("line.separator"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					is.close();
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return output.toString();
	}
	
	public static String getlocalizedString(String key){
		ResourceBundle rb = ResourceBundle.getBundle("net/chuyang/apptracer/i18n/RB");
		return rb.getString(key);
	}
}
