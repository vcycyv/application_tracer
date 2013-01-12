public class CaseObject{
 
   public boolean execute(int sleepTime) throws Exception{
       System.out.println("sleep: "+sleepTime);
       Thread.sleep(sleepTime);
       return true;
   }
}
