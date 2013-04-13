import java.util.Random;
public class Case1{
   public static void main(String[] args) throws Exception{
      Random random=new Random();
      CaseObject2 object=new CaseObject2();
      while(true){
    	 if(args.length ==0 || "int".equalsIgnoreCase(args[0]))
    		 object.executeInt(random.nextInt(1000));
    	 else
    		 object.execute(random.nextInt(1000));
         Thread.sleep(1000);
      }
   }
}
