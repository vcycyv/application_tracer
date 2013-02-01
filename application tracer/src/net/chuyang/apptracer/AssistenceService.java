package net.chuyang.apptracer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class AssistenceService {
	private Logger logger = Logger.getLogger(getClass());
	
	public List<ProcessVO> getJavaProcess(){
		String commandOutput = Utils.getOutputFromOSCommand("jps");
		String[] processStrings = commandOutput.split(Constants.LINE_SEPARATOR);
		
		List<ProcessVO> rtnVal = new ArrayList<ProcessVO>();
		for(String processString: processStrings){
			String[] a = processString.split(" ");
			ProcessVO vo = new ProcessVO();
			if(a.length > 1){
				vo.pid = Integer.valueOf(a[0]);
				vo.processName = a[1];
			}else{
				vo.pid = Integer.valueOf(processString.trim());
				vo.processName = "";
			}
			rtnVal.add(vo);	
		}
		return rtnVal;
	}
	
	public List<Class> getClassesFromJar(String jarFilePath, List<String> paths){
		List<Class> rtnVal = new ArrayList<Class>();
		JarFile jarFile = null;
		try{
			jarFile = new JarFile(jarFilePath);
	        Enumeration e = jarFile.entries();
	
	        List<URL> urlList = new ArrayList<URL>();
	        for(String path : paths){
	        	URL url = new File(path).toURI().toURL();
	        	urlList.add(url);
	        }
	        urlList.add(new URL("jar:file:" + jarFilePath +"!/"));
	        URL[] urls = urlList.toArray(new URL[urlList.size()]);
	        URLClassLoader cl = URLClassLoader.newInstance(urls);
	
	        while (e.hasMoreElements()) {
	            JarEntry je = (JarEntry) e.nextElement();
	            if(je.isDirectory() || !je.getName().endsWith(".class")){
	                continue;
	            }
	            String className = je.getName().substring(0,je.getName().length()-".class".length());
	            className = className.replace('/', '.');
	            Class c = cl.loadClass(className);
	            rtnVal.add(c);
	        }
	        return rtnVal;
		}catch(Exception e){
			try{
				if(jarFile != null)
					jarFile.close();
			}catch(Exception e1){
				logger.error("Failed to close jarFile: " + jarFile.getName(), e1);
			}
			throw new RuntimeException(e);
		}
	}
	
	public int getLatestInvocationCount(){
		String content;
		try{
			File file = new File(Constants.OUTPUT_PATH);
			content = FileUtils.readFileToString(file);
			int index = content.lastIndexOf(Constants.APPTRACER_STARTED);
			String latestContent = content.substring(index);
			return StringUtils.countMatches(latestContent, Constants.LINE_SEPARATOR) - 1;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static class ProcessVO{
		public int pid;
		public String processName;
	}
	
	public static void main(String[] args){
		//new AssistenceService().getJavaProcess();
		//new AssistenceService().getClassesFromJar("C:/ws/_git/application_tracer/application tracer/target/test.jar");
	}
}
