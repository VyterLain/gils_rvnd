import java.io.IOException;
import java.util.ArrayList;

/***program GILS-RVND, made by Fang Yuxin***/
public class Main {
    public static void main(String[] args){
        ArrayList<Instance> instances;
        try{
            instances=ReadData.readInstance("data");
        }catch (IOException e){
            return;
        }
        float[] R=new float[26];
        for(int i=1;i<=25;i++){
            R[i]=(float)(R[i-1]+0.01);
        }
        for(Instance instance:instances){
            System.out.println(instance.getName()+">>>");
            Algorithm algorithm=new Algorithm(10,Math.min(100,instance.getN()),R);
            Solution solution=algorithm.run(instance);
            System.out.println("best route: "+solution.getRoute());
            System.out.println("best cost: "+solution.getRealCost());
            //break;
        }
    }
}
