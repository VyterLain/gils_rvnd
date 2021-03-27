import java.util.ArrayList;

public class Solution {
    private ArrayList<Integer> route;
    private Instance instance;

    private Cost[][] costs;

    //private double[][] T;
    //private double[][] C;
    //private int[][] W;

    public Solution(Instance instance){
        this.instance=instance;
        this.route=new ArrayList<>();
        costs=new Cost[instance.getN()+1][instance.getN()+1];
        initCost();
    }

    public Solution(Instance instance,ArrayList<Integer> route){
        this.instance=instance;
        this.route=new ArrayList<>(route);
        costs=new Cost[instance.getN()+1][instance.getN()+1];
        initCost();
        calculateCost();
    }

    public Solution(Instance instance,Solution solution){
        this.instance=instance;
        this.route=new ArrayList<>(solution.getRoute());
        costs=new Cost[instance.getN()+1][instance.getN()+1];
        //for(int i=0;i<=instance.getN();i++){
        //    System.arraycopy(solution.getAllCost()[i],0,costs[i],0,instance.getN()+1);
        //}
        initCost();
        calculateCost();
    }

    /**
     * use route to calculate the cost
     */
    public void calculateCost(){
        for(int i=0;i<route.size();i++){
            for(int j=i+1;j<route.size();j++){
                costs[i][j].T=costs[j][i].T=costs[i][j-1].T+instance.getDist(route.get(j-1),route.get(j));
                costs[i][j].W=costs[j][i].W=costs[i][j-1].W+costs[j][j].W;
                costs[i][j].C=costs[i][j-1].C+costs[j][j].C+costs[j][j].W*(costs[i][j-1].T+instance.getDist(route.get(j-1),route.get(j)));
                costs[j][i].C=costs[j][j].C+costs[j-1][i].C+costs[j-1][i].W*(costs[j][j].T+instance.getDist(route.get(j),route.get(j-1)));
                //T[i][j]=T[j][i]=T[i][j-1]+instance.getDist(j-1,j);
                //W[i][j]=W[j][i]=W[i][j-1]+W[j][j];
                //C[i][j]=C[i][j-1]+C[j][j]+W[j][j]*(T[i][j-1]+instance.getDist(j-1,j));
                //C[j][i]=C[j][j]+C[j-1][i]+W[j-1][i]*(T[j][j]+instance.getDist(j,j-1));
            }
        }
    }

    /**
     * initialize T C W
     */
    private void initCost(){
        //int routeSize=instance.getN()+1;
        //T=new double[routeSize][routeSize];
        //C=new double[routeSize][routeSize];
        //W=new int[routeSize][routeSize];
        for(int i=0;i<=instance.getN();i++){
            for(int j=0;j<=instance.getN();j++){
                if(i==j) costs[i][j]=new Cost(0,0,1);
                else costs[i][j]=new Cost();
            }
            //W[i][i]=1;
        }
        costs[0][0].changeValue(0,0,0);
        //T[0][0]=0;C[0][0]=0;W[0][0]=0;
        costs[instance.getN()][instance.getN()].changeValue(0,0,0);
        //T[routeSize-1][routeSize-1]=0;W[routeSize-1][routeSize-1]=0;C[routeSize-1][routeSize-1]=0;
    }

    public Cost getCost(int i, int j){
        return costs[i][j];
    }

    public double getRealCost(){
        double sum=0;
        for(int i=0;i<instance.getN()+1;i++){
            for(int j=0;j<i;j++){
                sum+=instance.getDist(route.get(j),route.get(j+1));
            }
        }
        return sum;
    }

    public ArrayList<Integer> getRoute(){
        return this.route;
    }

    public void setRoute(ArrayList<Integer> route){
        this.route=new ArrayList<>(route);
    }

    public Cost[][] getAllCost(){
        return this.costs;
    }

    public boolean isRouteValid(){
        if(route.get(0)!=0||route.get(route.size()-1)!=0||route.size()!=instance.getN()+1){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("The solution of the ");
        sb.append(instance.getName());
        sb.append(":\n\t");
        for(int i:route){
            sb.append(i+"->");
        }
        sb.delete(sb.length()-2,sb.length());
        return sb.toString();
    }
}
