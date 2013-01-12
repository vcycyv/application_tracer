import static com.sun.btrace.BTraceUtils.*;
import com.sun.btrace.annotations.*;
 
@BTrace public class TraceMethodArgsAndReturn{
   @OnMethod(
      clazz="CaseObject",
      method="execute",
      location=@Location(Kind.RETURN)
   )
   public static void traceExecute(@Self CaseObject instance, @Return boolean result){
     println("call CaseObject.execute");
     println(strcat("return value is:",str(result)));
   }
}

