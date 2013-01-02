package net.chuyang.apptracer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Utils {
	public static String getCommandOutput(String command) {
		try {
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			StringBuffer output = new StringBuffer("");
			if (p != null) {
				BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String buf = "";
				try {
					while ((buf = is.readLine()) != null) {
						output.append(buf);
						output.append(System.getProperty("line.separator"));
					}
					is.close();
					is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					while ((buf = is.readLine()) != null) {
						output.append(buf);
						output.append(System.getProperty("line.separator"));
					}
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return output.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
