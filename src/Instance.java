import java.util.ArrayList;
import java.util.Arrays;

public class Instance {
    private String name;
    private int N;
    private ArrayList<Vertex> vertices;
    private double[][] dist;

    public Instance(String name,int N){
        this.name=name;
        this.N=N;
        vertices=new ArrayList<>();
        dist=new double[N][N];
    }

    public void setDist(int i,int j,double dist){
        this.dist[i][j]=dist;
    }

    public double getDist(int i,int j){
        return dist[i][j];
    }

    public void addVertex(Vertex v){
        this.vertices.add(v);
    }

    public void addVertex(ArrayList<Vertex> vertices){
        this.vertices.addAll(vertices);
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public String getName(){
        return this.name;
    }

    public int getN(){
        return this.N;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Instance: "+this.name+"Distance matrix > \r\n");
        for(int i = 0; i < N; ++i){
            sb.append(Arrays.toString(dist[i]) + "\r\n");
        }
        return sb.toString();
    }
}


