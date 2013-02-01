package net.chuyang.apptracer;

import static net.chuyang.apptracer.Constants.APPTRACER_STARTED;
import static net.chuyang.apptracer.Constants.APPTRACER_STOPPED;
import static net.chuyang.apptracer.Constants.BTRACE_COMMAND_PATH;
import static net.chuyang.apptracer.Constants.FILE_SEPARATOR;
import static net.chuyang.apptracer.Constants.LINE_SEPARATOR;
import static net.chuyang.apptracer.Constants.TARGET_CLASSPATH;
import static net.chuyang.apptracer.Constants.TEMPLATE_RETURN_VALUE_FILE;
import static net.chuyang.apptracer.Constants.USER_DIR;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.chuyang.apptracer.codegen.ClassVO;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.thehecklers.dialogfx.DialogFX;

public class TaskProcessService extends Service<File>{
	private ClassVO classVO;
	private String classPath;
	private int port;
	
	private Logger logger = Logger.getLogger(getClass());
	private static final String SCRIPT_FILE_NAME = "Script.java";
	
	public ClassVO getClassVO() {
		return classVO;
	}
	public void setClassVO(ClassVO classVO) {
		this.classVO = classVO;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	protected Task<File> createTask() {
		return new Task<File>() {
            protected File call() {
            	String command = getCommand();
        		final File rtnVal = new File(Constants.OUTPUT_PATH);
        		try {
        			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        			FileUtils.writeStringToFile(rtnVal, LINE_SEPARATOR + APPTRACER_STARTED + df.format(new Date()) + LINE_SEPARATOR, true);
        			Process p = Runtime.getRuntime().exec("cmd /c " + command);
        			StringBuffer output = new StringBuffer("");
        			if (p != null) {
        				BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
        				String buf = "";
        				try {
        					int count = 0;
        					while ((buf = is.readLine()) != null) {
        						if(isCancelled()){
        							FileUtils.writeStringToFile(rtnVal, output.toString(), true);
        			    	        Platform.runLater(new Runnable() {
        			    	            @Override
        			    	            public void run() {
        			    	            	try {
												FileUtils.writeStringToFile(rtnVal, APPTRACER_STOPPED + df.format(new Date()) + LINE_SEPARATOR, true);
											} catch (IOException e) {
												logger.error("Filed to insert stop flag.", e);
											}
        			    	            	
        			    	            	AssistenceService assistService = new AssistenceService();
        			    	            	int counts = assistService.getLatestInvocationCount();
                							String msg = Utils.getlocalizedString("ApptracerController.invocation.count.msg.fmt");
                			    	        msg = MessageFormat.format(msg, counts);
        			    	            	DialogFX dialog = new DialogFX();
                			    	        dialog.setTitleText(Utils.getlocalizedString("Apptracer.title.txt"));
                			    	        dialog.setMessage(msg);
                			    	        dialog.showDialog();
        			    	            }
        			    	       });
        			    			
        							break;
        						}else{
	        						output.append(buf);
	        						output.append(LINE_SEPARATOR);
	        						if(++count % 5 == 0){
	        							FileUtils.writeStringToFile(rtnVal, output.toString(), true);
	        							output.setLength(0);
	        						}
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
        		return rtnVal;
            }
        };
	}
	
	private String getCommand(){
		generateScriptFile(classVO);
		//To process path for space so they can be used in Runtime.getRuntime().exec().
		String braceCmdPath = BTRACE_COMMAND_PATH.replaceAll(" ", "\" \"");
		String classPathProcessed = classPath.replaceAll(" ", "\" \"");
		String targetPath = TARGET_CLASSPATH.replaceAll(" ", "\" \"");
		
		return new StringBuilder().append(braceCmdPath).append(" -cp ").append(classPathProcessed)
				.append(" ").append(port).append(" ").append(targetPath).append(FILE_SEPARATOR).append(SCRIPT_FILE_NAME).toString();
	}
	
	private void generateScriptFile(ClassVO vo) {
		String script = getReturnValueScript(vo);
		String outScriptFileName = new StringBuilder(TARGET_CLASSPATH).append(FILE_SEPARATOR).append(SCRIPT_FILE_NAME).toString();
		try {
			FileUtils.writeStringToFile(new File(outScriptFileName), script);
		} catch (IOException e) {
			String errMsg = Utils.getlocalizedString("taskprocessor.fail.write.file.fmt)");
			errMsg = MessageFormat.format(errMsg, outScriptFileName);
			logger.log(Level.ERROR, errMsg);
			throw new RuntimeException(e);
		}
	}
	
	private String getReturnValueScript(ClassVO vo) {
		String script = "";
		String pathName = USER_DIR + FILE_SEPARATOR + "resource" + FILE_SEPARATOR + TEMPLATE_RETURN_VALUE_FILE;
		
		try{
			script = FileUtils.readFileToString(new File(pathName));
		}catch(IOException e){
			String errMsg = Utils.getlocalizedString("taskprocessor.fail.read.file.fmt)");
			errMsg = MessageFormat.format(errMsg, pathName);
			logger.log(Level.ERROR, errMsg);
			throw new RuntimeException(e);
		}
		
		String rtnVal = "";
		try {
			Map<String, String> map = BeanUtils.describe(vo);
			map.remove("class"); // The map created by BeanUtils.describe() always has an unwanted class key-value pare.
			rtnVal = Utils.processCodeTemplate(script, map);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rtnVal;
	}	
	
}
