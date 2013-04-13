import java.util.Random;

public class CaseObject2{
 
   public boolean execute(int sleepTime) throws Exception{
       System.out.println("sleep: "+sleepTime);
       Thread.sleep(sleepTime);
       return new Random().nextBoolean();
   }
   
   public int executeInt(int sleepTime) throws InterruptedException{
	   System.out.println("sleep: "+ sleepTime);
       Thread.sleep(sleepTime);
	   return new Random().nextInt(1000);
   }
}
