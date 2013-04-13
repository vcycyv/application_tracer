package net.chuyang.apptracer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class DataProcessService extends Service<String> {

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	protected Task<String> createTask() {
		return new Task<String>(){
			protected String call() {
				String rtnVal = Utils.getlocalizedString("DataProcessService.fail.txt");
				try {
					AssistenceService assistenceService = new AssistenceService();
					assistenceService.processLog();
					
					String rTemplate = FileUtils.readFileToString(new File(Constants.RESOURCE_R_TEMPLATE_PATH));
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
					String plotOutputPath = Constants.PATH + dateFormat.format(new Date()) + Constants.FILE_SEPARATOR;
					plotOutputPath = plotOutputPath.replaceAll("\\\\", "/");
					new File(plotOutputPath).mkdir();
					Map<String, String> map = new HashMap<String, String>();
					map.put("logfile", Constants.OUTPUT_DATA_PATH.replaceAll("\\\\", "/"));
					map.put("outputpath", plotOutputPath);
					String rScript = Utils.processCodeTemplate(rTemplate, map);
					FileUtils.writeStringToFile(new File(Constants.OUTPUT_R_SCRIPT_PATH), rScript);
					
					String rpath = System.getProperty("rpath");
					String[] command = new String[]{rpath + "Rscript.exe", Constants.OUTPUT_R_SCRIPT_PATH.replaceAll("\\\\", "/")};
					Process p = new ProcessBuilder(command).start();
					rtnVal = Utils.getlocalizedString("DataProcessService.success.fmt");
					rtnVal = MessageFormat.format(rtnVal, plotOutputPath);
				} catch (IOException e) {
					logger.error("Failed to analyse.", e);
				} 
				return rtnVal;
			}
		};
	}
}

