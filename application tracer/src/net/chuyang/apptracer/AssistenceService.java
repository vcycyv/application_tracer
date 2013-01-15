package net.chuyang.apptracer;

import java.util.ArrayList;
import java.util.List;

public class AssistenceService {
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
	
	public static class ProcessVO{
		public int pid;
		public String processName;
	}
	
	public static void main(String[] args){
		new AssistenceService().getJavaProcess();
	}
}
