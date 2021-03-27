public class Vertex {
    private int id;
    private int x;
    private int y;

    public Vertex(int id,int x,int y){
        this.id=id;
        this.x=x;
        this.y=y;
    }

    public int getId(){
        return this.id;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public String toString(){
        return this.id+": "+this.x+","+this.y;
    }
}
