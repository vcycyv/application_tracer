package net.chuyang.apptracer;

public class Constants {
	public static final String TEMPLATE_RETURN_VALUE_FILE = "ReturnValue.txt";
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String BTRACE_COMMAND_PATH = USER_DIR + FILE_SEPARATOR + "btrace" + FILE_SEPARATOR + "bin" + FILE_SEPARATOR + "btrace";
	public static final String TARGET_CLASSPATH = USER_DIR + FILE_SEPARATOR + "target";
	public static final String OUTPUT_PATH = USER_DIR + FILE_SEPARATOR + "output" + FILE_SEPARATOR + "execution_info.txt";
}
