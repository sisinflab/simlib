package it.poliba.sisinflab.simlib.similarity;


import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.datamodel.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Computes similarity values between items
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public abstract class SimilarityMetric {

    protected Graph graph;

    public SimilarityMetric(Graph graph){
        this.graph = graph;
    }

    public SimilarityMetric() {}

    /**
     * Computes a similarity value between two items considering only a given path
     *
     * @param item1 first item
     * @param item2 second item
     * @param path a given path
     * @return the similarity value between the two items
     */
    public abstract double computePathSimilarity(Node item1, Node item2, Path path);

    /**
     * Computes a similarity value between two items
     *
     * @param item1 first item
     * @param item2 second item
     * @return the similarity value between the two items
     */
    public abstract double computeSimilarity(Node item1, Node item2);

    /**
     * Computes a similarity value between two items, given their IDs
     *
     * @param idItem1 first item ID
     * @param idItem2 second item ID
     * @return the similarity value between the two items
     */
    public double computeSimilarity(String idItem1, String idItem2){
        return computeSimilarity(getNode(idItem1), getNode(idItem2));
    }

    /**
     * Given an item n, it ranks all the other items in the graph with respect to their similarity with n
     *
     * @param n a given item
     * @return a ranked map item-similarity value
     */
    public Map<Node, Double> computeSimilarityRank(Node n){
        Map<Node, Double> rank =
                graph.getItems().entrySet().parallelStream()
                        .filter(e -> !e.getValue().equals(n))
                        .collect(Collectors.toMap(
                                e -> e.getValue(),
                                e -> computeSimilarity(n, e.getValue())
                        ));

        return Utilities.sortByValues(rank);
    }

    /**
     * Given the ID of a node n, it ranks all the other items in the graph with respect to their similarity with n
     *
     * @param nodeId the item ID
     * @return a ranked map item-similarity value
     */
    public Map<Node, Double> computeSimilarityRank(String nodeId){
        return computeSimilarityRank(getNode(nodeId));
    }

    /**
     * Computes the similarity value between all the exisiting couples of items in the graph. For perfomances evaluation only.
     */
    public void computeAllSimilarities() {
        //for(Map.Entry<String, Node> entry : graph.getItems().entrySet()) {
        graph.getItems().entrySet().parallelStream().forEach(entry -> {

            Node item = entry.getValue();
            long startTime = System.currentTimeMillis();
            System.out.println("Computing similarities for item: " + item.getId() + " ...");
            computeSimilarityRank(item);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;

            System.out.println("Execution time item: " + item.getId() + " " + (elapsedTime / 1000D));

        });
    }

    protected Node getItem(String idItem){
        return  graph.getItem(idItem);
    }

    protected Node getNode(String idItem){
        return  graph.getNode(idItem);
    }

    /*protected void validatePath(Path path){
        for(Arrow arrow : path.getArrowsList()){
            String propID = arrow.getProperty().getId();
            Node graphNode = getNode(propID);
            if(graphNode == null){
                graph.getNodes().put(propID, arrow.getProperty());
                System.out.println("Adding property node " + propID + " to graph");
            }else{
                arrow.setProperty((Property) graphNode);
            }
        }
    }

    protected Set<Path> validatePaths(Set<Path> paths){
        for(Path p : paths){
            validatePath(p);
        }
        return paths;
    }*/

}
