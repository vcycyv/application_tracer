package net.chuyang.apptracer;

import static net.chuyang.apptracer.Constants.BTRACE_COMMAND_PATH;
import static net.chuyang.apptracer.Constants.FILE_SEPARATOR;
import static net.chuyang.apptracer.Constants.TARGET_CLASSPATH;
import static net.chuyang.apptracer.Constants.TEMPLATE_RETURN_VALUE_FILE;
import static net.chuyang.apptracer.Constants.USER_DIR;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.chuyang.apptracer.codegen.ReturnValueVO;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public enum TaskProcessor {
	INSTANCE;
	
	private Logger logger = Logger.getLogger(getClass());
	
	private ExecutorService exec = Executors.newCachedThreadPool();
	private Map<String, DirectCommandToFileTask> taskMap = new HashMap<String, DirectCommandToFileTask>();
	
	
	public File handleReturnValueTask(ReturnValueVO vo, String classPath){
		String outScriptFileName = new StringBuilder(vo.getClazz()).append("_").append(vo.getMethod()).append(".java").toString();
		generateScriptFile(vo, outScriptFileName);
		String command = getCommand(outScriptFileName, classPath);
		
		String outLogFileName = new StringBuilder(vo.getClazz()).append("_").append(vo.getMethod()).append("_").append("returnvalue.txt").toString();
		DirectCommandToFileTask task = createTask(command, outLogFileName);
		String id = UUID.randomUUID().toString();
		taskMap.put(id, task);
		return task.getFile();
	}

	private DirectCommandToFileTask createTask(String command, String fileName){
		StringBuilder sb = new StringBuilder();
		String fullPathFileName = sb.append(Constants.OUTPUT_PATH).append(fileName).toString();
		DirectCommandToFileTask task = new DirectCommandToFileTask(command, fullPathFileName);
		taskMap.put(fileName, task);
		exec.execute(task);
		return task;
	}
	
	private String getCommand(String scriptFileName, String classPath){
		//To process path for space so they can be used in Runtime.getRuntime().exec().
		String braceCmdPath = BTRACE_COMMAND_PATH.replaceAll(" ", "\" \"");
		classPath = classPath.replaceAll(" ", "\" \"");
		String targetPath = TARGET_CLASSPATH.replaceAll(" ", "\" \"");
		
		return new StringBuilder().append(braceCmdPath).append(" -cp ").append(classPath)
				.append(" ").append(Configuration.INSTANCE.getTargetPort()).append(" ").append(targetPath).append(FILE_SEPARATOR).append(scriptFileName).toString();
	}
	
	private void generateScriptFile(ReturnValueVO vo, String outFileName) {
		String script = getReturnValueScript(vo);
		String outScriptFileName = new StringBuilder(TARGET_CLASSPATH).append(FILE_SEPARATOR).append(outFileName).toString();
		try {
			FileUtils.writeStringToFile(new File(outScriptFileName), script);
		} catch (IOException e) {
			String errMsg = Utils.getlocalizedString("taskprocessor.fail.write.file.fmt)");
			errMsg = MessageFormat.format(errMsg, outScriptFileName);
			logger.log(Level.ERROR, errMsg);
			throw new RuntimeException(e);
		}
	}
	
	private String getReturnValueScript(ReturnValueVO vo) {
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
