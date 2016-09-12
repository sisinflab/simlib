package it.poliba.sisinflab.simlib.relatedness;


import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by giorgio on 24/01/2016.
 */
public abstract class RelatednessMeasure {

    protected Graph graph;

    public RelatednessMeasure(Graph graph) {
        this.graph = graph;
    }

    public double computeRelatedness(String item1, String item2){
        return computeRelatedness(graph.getNode(item1), graph.getNode(item2));
    }

    public abstract double computeRelatedness(Node n1, Node n2);

    /**
     * Given the ID of a node n, it ranks all the other items in the graph with respect to their relatedness with n
     *
     * @param n the given item
     * @return a ranked map item-relatedness value
     */
    public Map<Node, Double> computeRelatednessRank(Node n, Map<String, Node> targetNodes){


        Map<Node, Double> rank =
               targetNodes.entrySet().parallelStream()
                        .filter(e -> !e.getValue().equals(n))
                        .map(e -> new AbstractMap.SimpleEntry<>(e.getValue(), computeRelatedness(n, e.getValue())))
                        .filter(e -> e.getValue() > 0 )
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e -> e.getValue()
                        ));

        return Utilities.sortByValues(rank);
    }

    public Map<Node, Double> computeRelatednessRank(String nodeId){
        return computeRelatednessRank(graph.getNode(nodeId), null);
    }

    public Map<Node, Double> computeRelatednessRank(String nodeId, List<String> targetNodes){
        Map<String, Node> targets = graph.getItems();
        if(targetNodes != null)
            targets = graph.getNodes(targetNodes);
        return computeRelatednessRank(graph.getNode(nodeId), targets);
    }
    public Map<Node, Double> computeRelatednessRank(String nodeId,  Map<String, Node> targetNodes){
        if (graph.contains(nodeId))
            return computeRelatednessRank(graph.getNode(nodeId), targetNodes);
        else
            return new HashMap<>();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
