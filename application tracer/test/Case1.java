import java.util.Random;
public class Case1{
 
   public static void main(String[] args) throws Exception{
      Random random=new Random();
      CaseObject2 object=new CaseObject2();
      boolean result=false;
      while(!result){
         result=object.execute(random.nextInt(1000));
         Thread.sleep(1000);
      }
   }
 
}
