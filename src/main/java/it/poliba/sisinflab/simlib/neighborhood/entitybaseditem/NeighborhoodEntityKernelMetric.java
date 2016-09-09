package it.poliba.sisinflab.simlib.neighborhood.entitybaseditem;

import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.neighborhood.KernelMetric;
import it.poliba.sisinflab.simlib.neighborhood.NeighborGraph;
import it.poliba.sisinflab.simlib.neighborhood.entitybaseditem.EntityWeightFeature;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Corrado on 15/04/2016.
 */
public class NeighborhoodEntityKernelMetric extends KernelMetric{

    private int distance;

    public NeighborhoodEntityKernelMetric(Graph graph, int distance){
        super(graph);
        this.distance = distance;
    }

    public NeighborhoodEntityKernelMetric(Graph graph,int distance, String fileType) throws InvalidGraphException, IOException {
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

        double[] weight1 = computeSingleVector(item1);
        double[] weight2 = computeSingleVector(item2);
        
        String[] name1 = item1.getId().split("/");
        int n1 = name1.length;
        String[] name2 = item2.getId().split("/");
        int n2 = name2.length;
        
        System.out.println("normalized Kernel(" + name1[n1-1] + "," + name2[n2-1] +") = "+ Utilities.cosineSimilarity(weight1,weight2));

       return Utilities.dotProduct(weight1, weight2);
        
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

        double[] weight1 = computeSingleVector(item1);
        
        Map<Node, Double> rank =
                graph.getItems().entrySet().parallelStream()
                        .filter(e -> !e.getValue().equals(item1))
                        .collect(Collectors.toMap(
                                e -> e.getValue(),
                                e -> computeKernelModify(item1, weight1, e.getValue())
                        ));
        return Utilities.sortByValues(rank);
    }
    
    public double[] computeSingleVector(Node item) {
        
        NeighborGraph ng = new NeighborGraph(item,distance);

        Set<String> nodes = graph.getNodes().keySet();
       
        ReachableEntities reachablentities = new ReachableEntities(ng);
        System.out.println("\033[31m                           ...ENTITY-BASED ITEM NEIGHBORHOOD MAPPPING...ITEM: "+item.getId());
        EntityWeightFeature entityweightfeature = new EntityWeightFeature(reachablentities,nodes);
        entityweightfeature.calculateWeight();
        
        double[] weight = entityweightfeature.getWeights().values().parallelStream().mapToDouble(e->e).toArray();

        return weight;
        
    }
    
    public double computeKernelModify(Node item1,double[] weight1, Node item2) {
        
        double[] weight2 = computeSingleVector(item2);

        String[] name1 = item1.getId().split("/");
        int n1 = name1.length;
        String[] name2 = item2.getId().split("/");
        int n2 = name2.length;
        
        System.out.println("normalized Kernel(" + name1[n1-1] + "," + name2[n2-1] +") = "+ Utilities.cosineSimilarity(weight1,weight2));

        return Utilities.cosineSimilarity(weight1,weight2);
        
    }

}
