package it.poliba.sisinflab.simlib.neighborhood.pathbaseditem;

import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.input.StatementFileReader;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.neighborhood.KernelMetric;
import it.poliba.sisinflab.simlib.neighborhood.NeighborGraph;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Corrado on 15/04/2016.
 */
public class NeighborhoodPathKernelMetric extends KernelMetric{

    private int distance;
    
    public static final String MEAN_ARITHMETIC = "mean_arithmetic";
    public static final String MEAN_GEOMETRIC = "mean_geometric";

    public NeighborhoodPathKernelMetric(Graph graph, int distance){
        super(graph);
        this.distance = distance;
    }

    public NeighborhoodPathKernelMetric(Graph graph, int distance, String fileType) throws InvalidGraphException, IOException {
        super(graph);
        if(distance < 1){
            throw new IllegalArgumentException("distance must be at least 1");
        }
        this.distance = distance;

    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }


    @Override
    public double computeKernel(Node item1, Node item2) {
        
        NeighborGraph ng1 = new NeighborGraph(item1,distance);
        NeighborGraph ng2 = new NeighborGraph(item2,distance);
        
        ReachablePaths reachablepaths1 = new ReachablePaths(ng1);
        ReachablePaths reachablepaths2 = new ReachablePaths(ng2);
        
        double[] weight1 = computeSingleVector(item1,reachablepaths1,reachablepaths2);
        double[] weight2 = computeSingleVector(item2,reachablepaths2,reachablepaths1);
        
        //System.out.println("DotProduct weights: "+Utilities.dotProduct2(weight1, weight2));
        System.out.println("Norm 1 weights: "+ Utilities.norm(weight1));
        System.out.println("Norm 2 weights: "+ Utilities.norm(weight2));
        
        String[] name1 = item1.getId().split("/");
        int n1 = name1.length;
        String[] name2 = item2.getId().split("/");
        int n2 = name2.length;
        System.out.println("normalized Kernel(" + name1[n1-1] + "," + name2[n2-1] +") = "+ Utilities.cosineSimilarity(weight1,weight2));
        System.out.println();
        System.out.println();
        return Utilities.dotProduct(weight1,weight2);
    }
   

    @Override
    public Map<Node, Double> computeFeatureMap(Node n) {
        return null;
    }


    @Override
    public double computeKernel(String idItem1, String idItem2) {
        Node item1 = graph.getItems().get(idItem1);
        Node item2 = graph.getItems().get(idItem2);
        return computeKernel(item1, item2);
    }

    
    @Override
    public Map<Node, Double> computeKernelRank(String itemID) {
        Node item1 = graph.getNode(itemID);
 
        NeighborGraph ng1 = new NeighborGraph(item1,distance);
        
        ReachablePaths reachablepaths1 = new ReachablePaths(ng1);     
        
        Map<Node, Double> rank =
                graph.getItems().entrySet().stream()
                        .filter(e -> !e.getValue().equals(item1))
                        .collect(Collectors.toMap(
                                e -> e.getValue(),
                                e -> computeKernelModify(item1,reachablepaths1, e.getValue())
                        ));
        return Utilities.sortByValues(rank);
    }
    
    public double computeKernelModify(Node item1,ReachablePaths reachablepaths1, Node item2) {
        
        NeighborGraph ng2 = new NeighborGraph(item2,distance);
        ReachablePaths reachablepaths2 = new ReachablePaths(ng2); 
        
        double[] weight1 = computeSingleVector(item1,reachablepaths1,reachablepaths2);
        double[] weight2 = computeSingleVector(item2,reachablepaths2,reachablepaths1);

        String[] name1 = item1.getId().split("/");
        int n1 = name1.length;
        String[] name2 = item2.getId().split("/");
        int n2 = name2.length;
        
        System.out.println("normalized Kernel(" + name1[n1-1] + "," + name2[n2-1] +") = "+ Utilities.cosineSimilarity(weight1,weight2));

        return Utilities.cosineSimilarity(weight1,weight2);
        
    }
    
    public double[] computeSingleVector(Node item,ReachablePaths reachablepaths1,ReachablePaths reachablepaths2) {
        
        //System.out.println("\033[31m                            ...PATH-BASED ITEM NEIGHBORHOOD MAPPPING...ITEM: "+item.getId());
        PathWeightFeature pathweightfeature = new PathWeightFeature(distance,reachablepaths1);
        //LinkedList<EntityPath> subpaths1 = reachablepaths1.getEntitysubpaths();
        //LinkedList<EntityPath> subpaths2 = reachablepaths2.getEntitysubpaths();
        HashSet<EntityPath> allsubpaths = new HashSet<>();
        reachablepaths1.getEntitysubpaths().forEach(e->allsubpaths.add(e));
        reachablepaths2.getEntitysubpaths().forEach(e->allsubpaths.add(e));        
        
        pathweightfeature.calculateWeight(MEAN_ARITHMETIC,allsubpaths);
        double[] weight = pathweightfeature.getWeights().values().parallelStream().mapToDouble(e->e).toArray();

        return weight;
        
    }
    
}
