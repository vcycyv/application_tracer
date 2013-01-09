package net.chuyang.apptracer;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Utils {
	/**
	 * For example, the code template is: I love ${food}, haha. 
	 * The paramMap may contain <food, apple>, then the result is I love apple, haha.
	 * 
	 * @param templateCode
	 * @param paramMap key is place holder, the value is the String used to replace the place holder.
	 * @return
	 */
	public static String processCodeTemplate(String templateCode, Map<String, String> paramMap){
		for(Map.Entry<String, String> entry : paramMap.entrySet()){
			String placeHolder = "${" + entry.getKey() + "}";
			
			//validation
			int count = StringUtils.countMatches(templateCode, placeHolder);
			if(count != 1 ){
				throw new RuntimeException(entry.getKey() + " does not show up in code template or there are multiple occurence.");
			}
			
			templateCode = templateCode.replace(placeHolder, entry.getValue());
		}
		return templateCode;
	}
	
	public static void main(String[] args){
		String command = "cmd /c C:/tools/packages/btrace/bin/btrace -cp C:/tools/packages/btrace 6280  C:/tools/packages/btrace/TraceMethodArgsAndReturn.java";
		TaskProcessor.INSTANCE.createTask(command, "xyz");
	}
}
