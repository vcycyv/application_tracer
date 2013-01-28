import static com.sun.btrace.BTraceUtils.*;
import com.sun.btrace.annotations.*;
 
@BTrace public class Script{
	@TLS static long beginTime;
 
	@OnMethod(
		clazz="${clazz}",
		method="${method}"
	)
	public static void traceExecuteBegin(){
		beginTime=timeMillis();
	}
	
	/*@OnMethod(
		clazz="${clazz}",
		method="${method}",
		location=@Location(Kind.RETURN)
	)
	public static void traceExecute(@Self ${clazz} instance, @Return ${returnType} result){
		println(strcat(strcat(strcat(strcat(str(timeMillis()), "  ____  "), str(result)), "  ____  "), str(timeMillis()-beginTime)));
	}*/
}
