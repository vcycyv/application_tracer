package net.chuyang.apptracer;

import java.util.Map;
import java.util.ResourceBundle;

import net.chuyang.apptracer.codegen.ReturnValueVO;

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
			
			//validation
			/*int count = StringUtils.countMatches(command, placeHolder);
			if(count == 0 ){
				throw new RuntimeException(entry.getKey() + " does not show up in code template.");
			}*/
			
			command = command.replaceAll(placeHolder, entry.getValue());
		}
		return command;
	}
	
	public static String getlocalizedString(String key){
		ResourceBundle rb = ResourceBundle.getBundle("net.chuyang.apptracer.i18n.RB");
		return rb.getString(key);
	}
	
	public static void main(String[] args){
		ReturnValueVO vo = new ReturnValueVO();
		vo.setClazz("CaseObject");
		vo.setMethod("execute");
		vo.setReturnType("boolean");
		TaskProcessor.INSTANCE.handleReturnValueTask(vo);
	}
}
