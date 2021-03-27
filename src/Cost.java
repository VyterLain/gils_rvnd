public class Cost {
    double T;
    double C;
    int W;

    public Cost(){
        this.T=0.0;
        this.C=0.0;
        this.W=0;
    }

    public Cost(double T,double C,int W){
        this.T=T;
        this.C=C;
        this.W=W;
    }

    public Cost(Cost cost){
        this.T=cost.T;
        this.C=cost.C;
        this.W=cost.W;
    }

    public void changeValue(double T,double C,int W){
        this.C=C;
        this.T=T;
        this.W=W;
    }
}
