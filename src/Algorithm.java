import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Algorithm {
    private int iter_max;
    private int iter_ils;
    private float[] R;
    private Random random;

    private Solution bestSolution;

    private Instance instance;

    // the default Neighborhood List
    // when the strategy of the list can optimize the solution
    // update the NL --> initialize the list
    // here, we have five strategy
    private ArrayList<Integer> neighborhoodList=new ArrayList<Integer>(){
        {
            add(0);
            add(1);
            add(2);
            add(3);
            add(4);
        }
    };

    public Algorithm(int iter_max,int iter_ils,float[] R){
        this.iter_ils=iter_ils;
        this.iter_max=iter_max;
        this.R=R;
        this.random=new Random();
    }

    public Algorithm(int iter_max,int iter_ils,float[] R,Random random){
        this.iter_ils=iter_ils;
        this.iter_max=iter_max;
        this.R=R;
        this.random=random;
    }

    /**
     * GILS-RVND
     * @param instance an instance ready to solve
     * @return the best solution
     */
    public Solution run(Instance instance){
        this.instance=instance;

        double bestCost=Double.MAX_VALUE;

        for(int iter=0;iter<iter_max;iter++){
            float alpha=R[random.nextInt(R.length)];
            Solution solution=construction(alpha);
            Solution solution_prime=new Solution(instance,solution);

            int iterILS=0;
            while (iterILS<iter_ils){

                RVND(solution);

                if(solution.getCost(0,instance.getN()).C < solution_prime.getCost(0,instance.getN()).C){
                    solution_prime=new Solution(instance,solution);
                    iterILS=0;
                }

                perturb(solution);
                iterILS++;
            }

            if(solution_prime.getCost(0,instance.getN()).C<bestCost){
                this.bestSolution=new Solution(instance,solution_prime);
                bestCost=solution_prime.getCost(0,instance.getN()).C;
                System.out.println("Iter "+iter+", solution: "+this.bestSolution.getCost(0,instance.getN()).C);
            }
        }

        return this.bestSolution;
    }

    /**
     * construction the initial route
     * @param alpha the level of greediness
     * @return initial solution
     */
    private Solution construction(float alpha){
        ArrayList<Integer> route=new ArrayList<>();
        ArrayList<Integer> candidateList=new ArrayList<>();

        //s+{0}
        route.add(0);

        //initialize candidate list, CL <- CL-{0}
        for(int i=1;i<instance.getN();i++)
            candidateList.add(i);

        //r, the last element of route
        int r=0;

        while(!candidateList.isEmpty()){
            //sort CL in ascending order according to their distance with respect to r
            candidateList.sort(new MyComparator(r));
            //update RCL considering only the alpha% best candidates of CL
            int candidatesNum=(int)(alpha*candidateList.size());

            //choose c(here, c is the index of c) of RCL at random
            int c=random.nextInt(candidatesNum==0?1:candidatesNum);

            //s+{c}
            route.add(candidateList.get(c));
            //r <- c
            r=candidateList.get(c);
            //CL <- CL-{r} = CL-{c}
            candidateList.remove(c);
        }

        route.add(0);

        Solution solution=new Solution(instance,route);

        //System.out.println("construction, solution: "+solution.getRoute());

        return solution;
    }

    /**
     * RVND
     * in this method, solution has the same address
     * every time we change the route of solution
     * it means the route is better than the before
     * @param solution origin solution ready to optimize
     */
    private void RVND(Solution solution){
        //System.out.println("start RVND: "+solution.getCost(0,instance.getN()).C);
        // initialize the Neighborhood List NL
        ArrayList<Integer> neighborhoodList=new ArrayList<>(this.neighborhoodList);
        // initialize re-optimization data structures on subsequences
        // this operation will be done in the improvement strategy
        while(!neighborhoodList.isEmpty()){
            // choose a neighborhood N at random
            int rnd=random.nextInt(neighborhoodList.size());
            switch ((neighborhoodList.get(rnd))){
                // whenever a given neighborhood of the set N fails to improve the current best solution
                // RVND randomly selects another neighborhood from the same set to continue the search
                // and remove this neighborhood from NL
                // when a neighborhood can improve our solution, we then update NL again
                case 0:
                    if(!swap(solution))
                        neighborhoodList.remove(rnd);
                    else
                        neighborhoodList=new ArrayList<>(this.neighborhoodList);
                    break;
                case 1:
                    if(!two_opt(solution))
                        neighborhoodList.remove(rnd);
                    else
                        neighborhoodList=new ArrayList<>(this.neighborhoodList);
                    break;
                case 2:
                    if(!reinsertion(solution,1))
                        neighborhoodList.remove(rnd);
                    else
                        neighborhoodList=new ArrayList<>(this.neighborhoodList);
                    break;
                case 3:
                    if(!reinsertion(solution,2))
                        neighborhoodList.remove(rnd);
                    else
                        neighborhoodList=new ArrayList<>(this.neighborhoodList);
                    break;
                case 4:
                    if(!reinsertion(solution,3))
                        neighborhoodList.remove(rnd);
                    else
                        neighborhoodList=new ArrayList<>(this.neighborhoodList);
                    break;
                    default:
                        break;
            }
        }

        //System.out.println("RVND solution: "+solution.getCost(0,instance.getN()).C);
    }

    /**
     * perturb, double bridge
     * @param solution the original solution
     */
    private void perturb(Solution solution){
        //divide route to four pieces
        //and avoid choosing 0(depot)
        //and avoid overlapping
        //[0,pos1-1]+[pos3,N-1]+[pos2,pos3-1]+[pos1,pos2-1]+[0]

        //System.out.println("start perturbing");

        int position1=1+random.nextInt(instance.getN()/4);
        int position2=1+position1+random.nextInt(instance.getN()/4);
        int position3=1+position2+random.nextInt(instance.getN()/4);

        ArrayList<Integer> newRoute=new ArrayList<>();

        for(int i=0;i<position1;i++){
            newRoute.add(solution.getRoute().get(i));
        }

        for(int i=position3;i<instance.getN();i++){
            newRoute.add(solution.getRoute().get(i));
        }

        for(int i=position2;i<position3;i++){
            newRoute.add(solution.getRoute().get(i));
        }

        for(int i=position1;i<position2;i++){
            newRoute.add(solution.getRoute().get(i));
        }

        newRoute.add(0);
        solution.setRoute(newRoute);

        if(!solution.isRouteValid()){
            System.out.println("route is not valid");
            System.exit(0);
        }

        solution.calculateCost();
    }

    /**
     * two customers of the tour are interchanged
     * @param solution origin solution, if there is a better solution, this one will be changed to it
     * @return if we find a better one than before
     */
    private boolean swap(Solution solution){
        double bestCost=Double.MAX_VALUE;
        int[] bestSwap=new int[2];

        //System.out.println("start swapping");

        for(int i=1;i<instance.getN()-1;i++){
            for(int j=i+1;j<instance.getN();j++){
                Cost cost=new Cost(solution.getCost(0,i-1));
                cost=concatenation(solution.getRoute().get(i-1),solution.getRoute().get(j),cost,solution.getCost(j,j));
                if(j!=i+1) {
                    cost=concatenation(solution.getRoute().get(j), solution.getRoute().get(i + 1), cost, solution.getCost(i + 1, j - 1));
                    cost=concatenation(solution.getRoute().get(j - 1), solution.getRoute().get(i), cost, solution.getCost(i, i));
                }else
                    cost=concatenation(solution.getRoute().get(j), solution.getRoute().get(i), cost, solution.getCost(i, i));
                cost=concatenation(solution.getRoute().get(i),solution.getRoute().get(j+1),cost,solution.getCost(j+1,instance.getN()));

                if(cost.C<bestCost) {
                    bestCost = cost.C;
                    bestSwap[0]=i;
                    bestSwap[1]=j;
                }
            }
        }

        if(bestCost<solution.getCost(0,instance.getN()).C){
            //System.out.println("start swap");
            //System.out.println(solution.getCost(0,instance.getN()).C);

            swap(solution.getRoute(),bestSwap[0],bestSwap[1]);

            //System.out.println(solution.getCost(0,instance.getN()).C);

            if(!solution.isRouteValid()){
                System.out.println("route is not valid");
                System.exit(0);
            }

            solution.calculateCost();

            //System.out.println("swap got it");

            return true;
        }

        return false;
    }

    private void swap(ArrayList<Integer> route,int i,int j){
        int temp=route.get(i);
        route.set(i,route.get(j));
        route.set(j,temp);
    }

    /**
     * two non-adjacent arcs are removed and another two are inserted in order to build a new feasible tour
     * @param solution origin solution
     * @return best one
     */
    private boolean two_opt(Solution solution){
        double bestCost=Double.MAX_VALUE;
        int[] bestMove=new int[2];

        //System.out.println("start two-opt");

        // if j==i+1, this case can be seen as a swap problem
        for(int i=1;i<instance.getN()-2;i++){
            for(int j=i+2;j<instance.getN();j++){
                Cost cost=new Cost(solution.getCost(0,i-1));
                cost=concatenation(solution.getRoute().get(i-1),solution.getRoute().get(j),cost,solution.getCost(j,i));
                cost=concatenation(solution.getRoute().get(i),solution.getRoute().get(j+1),cost,solution.getCost(j+1,instance.getN()));

                if(cost.C<bestCost){
                    bestCost=cost.C;
                    bestMove[0]=i;
                    bestMove[1]=j;
                }
            }
        }

        if(bestCost<solution.getCost(0,instance.getN()).C){

            //System.out.println(solution.getRealCost());

            two_opt(solution.getRoute(),bestMove[0],bestMove[1]);

            if(!solution.isRouteValid()){
                System.out.println("route is not valid");
                System.exit(0);
            }

            solution.calculateCost();

            return true;
        }

        return false;
    }

    private void two_opt(ArrayList<Integer> route,int i,int j){
        while(i<j){
            int temp=route.get(i);
            route.set(i,route.get(j));
            route.set(j,temp);
            i++;
            j--;
        }
    }

    /**
     * n adjacent customers are reallocated to another position of the tour
     * @param solution origin solution
     * @param n the number of customers ready to reallocate
     * @return best one
     */
    private boolean reinsertion(Solution solution, int n){
        double bestCost=Double.MAX_VALUE;
        int[] bestReinsert=new int[2];

        //System.out.println("start reinsertion");

        for(int i=1;i<instance.getN()-n+1;i++){
            for(int j=1;j<instance.getN()-n+1;j++){
                if(i!=j){
                    Cost cost;

                    if(i<j){
                        cost=new Cost(solution.getCost(0,i-1));
                        cost=concatenation(solution.getRoute().get(i-1),solution.getRoute().get(i+n),cost,solution.getCost(i+n,j));
                        cost=concatenation(solution.getRoute().get(j),solution.getRoute().get(i),cost,solution.getCost(i,i+n-1));
                        cost=concatenation(solution.getRoute().get(i+n-1),solution.getRoute().get(j+1),cost,solution.getCost(j+1,instance.getN()));
                    }else{
                        cost=new Cost(solution.getCost(0,j));
                        cost=concatenation(solution.getRoute().get(j),solution.getRoute().get(i),cost,solution.getCost(i,i+n-1));
                        cost=concatenation(solution.getRoute().get(i+n-1),solution.getRoute().get(j+1),cost,solution.getCost(j+1,i-1));
                        cost=concatenation(solution.getRoute().get(i-1),solution.getRoute().get(i+n),cost,solution.getCost(i+n,instance.getN()));
                    }

                    if(cost.C<bestCost){
                         bestCost=cost.C;
                         bestReinsert[0]=i;
                         bestReinsert[1]=j;
                    }
                }
            }
        }

        if(bestCost<solution.getCost(0,instance.getN()).C){

            //System.out.println(solution.getRealCost());

            reinsert(solution.getRoute(),n,bestReinsert[0],bestReinsert[1]);
            //if(bestReinsert[0]<bestReinsert[1]){
            //    solution.calculateCost(bestReinsert[0],bestReinsert[1]);
            //}else{
            //    solution.calculateCost(bestReinsert[1],bestReinsert[0]);
            //}

            if(!solution.isRouteValid()){
                System.out.println("route is not valid");
                System.exit(0);
            }

            solution.calculateCost();

            //System.out.println(solution.getRealCost());

            return true;
        }

        return false;
    }

    private void reinsert(ArrayList<Integer> route,int n, int i, int j){
        ArrayList<Integer> tempArr=new ArrayList<>(route.subList(i,i+n));
        if(i<j) {
            for (int k = 0; k < n; k++) {
                route.remove(i);
                route.add(j, tempArr.get(k));
            }
        }else{
            for(int k=0;k<n;k++){
                route.remove(i++);
                route.add(j+++1,tempArr.get(k));
            }
        }
    }

    /**
     * concatenation operator
     * @param delta_tail the last one of seq delta  -->  delta_tail=route[index]
     * @param delta_prime_head the first one of seq delta_prime
     * @param delta seq delta
     * @param delta_prime seq delta_prime
     * @return new cost
     */
    private Cost concatenation(int delta_tail,int delta_prime_head,Cost delta,Cost delta_prime){
        Cost newCost=new Cost();
        newCost.T=delta.T+delta_prime.T+instance.getDist(delta_tail,delta_prime_head);
        newCost.W=delta.W+delta_prime.W;
        newCost.C=delta.C+delta_prime.C+delta_prime.W*(delta.T+instance.getDist(delta_tail,delta_prime_head));
        return newCost;
    }

    class MyComparator implements Comparator<Integer>{
        private int r;

        private MyComparator(int r){
            super();
            this.r=r;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return (int)instance.getDist(r,o1)-(int)instance.getDist(r,o2);
        }
    }
}
