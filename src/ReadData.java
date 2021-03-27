import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReadData {
    /**
     * read all instances
     * @param dataPath input the path
     * @return a list of instances
     * @throws IOException File not found
     */
    public static ArrayList<Instance> readInstance(String dataPath) throws IOException{
        File folder=new File(dataPath);
        ArrayList<Instance> instances=new ArrayList<>();
        if(folder.isDirectory()){
            System.out.println("loading data...");
            File[] listOfFiles=folder.listFiles();
            for(int i=0;i<listOfFiles.length;i++){
                if(listOfFiles[i].isFile()){
                    System.out.println("   "+i+": "+listOfFiles[i].getName());
                    Instance instance=readInstance(listOfFiles[i]);
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    /**
     * read an instance
     * @param file input File
     * @return a instance
     * @throws IOException File not found
     */
    public static Instance readInstance(File file) throws IOException{
        BufferedReader in=new BufferedReader(new FileReader(file));
        String line;
        String caculateType="";
        StringTokenizer tokenizer;
        Instance instance=null;

        while ((line = in.readLine()) != null) {
            if(line.contains("DIMENSION")){
                int N = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
                instance=new Instance(file.getName(),N);
            }else if(line.contains("ATT"))
                caculateType="att";
            else if(line.contains("EUC_2D"))
                caculateType="euc_2d";
            else if(line.contains("LOWER_DIAG_ROW"))
                caculateType="lower_diag_row";
            else if(line.contains("EDGE_WEIGHT_SECTION")||line.contains("NODE_COORD_SECTION")){
                break;
            }
        }

        if(instance==null){
            System.out.println("fail to read instance: "+file.getName());
            return null;
        }

        int[] distIndex=new int[]{0,0};
        //int test=8;

        while ((line=in.readLine())!=null){
            //test++;
            if(line.equals("EOF"))
                break;
            tokenizer=new StringTokenizer(line);
            if(caculateType.equals("euc_2d")||caculateType.equals("att")){
                int id = Integer.parseInt(tokenizer.nextToken())-1;
                int x = Integer.parseInt(tokenizer.nextToken());
                int y = Integer.parseInt(tokenizer.nextToken());
                Vertex v=new Vertex(id,x,y);
                instance.addVertex(v);
            }else if(caculateType.equals("lower_diag_row")){
                try {
                    while (tokenizer.hasMoreTokens()) {
                        double dist = Double.parseDouble(tokenizer.nextToken());
                        calculateLowerDiagRow(instance, distIndex, dist);
                    }
                }catch (NumberFormatException e){
                    break;
                }
            }
        }

        if(caculateType.equals("euc_2d")){
            caculateEur2D(instance);
        }else if(caculateType.equals("att")){
            calculateAtt(instance);
        }

        return instance;
    }

    /**
     * private, to init dist of an instance with data display type-lower diag row
     * @param inst an instance
     * @param distIndex dist index
     * @param dist dist number
     */
    private static void calculateLowerDiagRow(Instance inst,int[] distIndex,double dist){
        if(distIndex[1]<=distIndex[0]){
            inst.setDist(distIndex[0],distIndex[1],dist);
            inst.setDist(distIndex[1],distIndex[0],dist);
            distIndex[1]++;
        }else{
            distIndex[0]++;
            distIndex[1]=0;
            inst.setDist(distIndex[0],distIndex[1],dist);
            inst.setDist(distIndex[1],distIndex[0],dist);
            distIndex[1]++;
        }
    }

    /**
     * private, caculate dist of an instance with data display type-att
     * @param inst an instance
     */
    private static void calculateAtt(Instance inst){
        ArrayList<Vertex> vertices=inst.getVertices();
        if(vertices.size()!=0){
            for(int i=0;i<inst.getN();i++){
                for(int j=0;j<inst.getN();j++){
                    Vertex a=vertices.get(i);
                    Vertex b=vertices.get(j);
                    double rij=Math.sqrt((Math.pow(a.getX()-b.getX(),2)+Math.pow(a.getY()-b.getY(),2))/10.0);
                    double tij=Math.floor(rij+0.5);
                    if(tij<rij){
                        inst.setDist(i,j,tij+1);
                    }else{
                        inst.setDist(i,j,tij);
                    }
                }
            }
        }
    }

    /**
     * private, caculate dist of an instance with data display type-eur 2d
     * @param inst an instance
     */
    private static void caculateEur2D(Instance inst){
        ArrayList<Vertex> vertices=inst.getVertices();
        if(vertices.size()!=0) {
            for (int i = 0; i < inst.getN(); i++) {
                for (int j = i; j < inst.getN(); j++) {
                    Vertex a = vertices.get(i);
                    Vertex b = vertices.get(j);
                    inst.setDist(i,j,Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2)));
                    inst.setDist(j,i,inst.getDist(i,j));
                }
            }
        }
    }
}
