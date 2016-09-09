package it.poliba.sisinflab.simlib.relatedness;

import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.datamodel.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toSet;

/**
 * Implementation of the Reword relatedness measure
 * <p>
 * (cfr. G. Pirrò
 * <a href="http://www.aaai.org/ocs/index.php/AAAI/AAAI12/paper/viewFile/4923/5129">
 *     REWOrD: Semantic Relatedness in the Web of Data
 * </a>
 * , Proceedings of the 26th Conference on Artificial Intelligence (AAAI), Toronto, Ontario, Canada)
 * </p>
 * @author Giorgio Basile
 * @since 1.0
 */
public class Reword extends RelatednessMeasure {

    private static final int DEFAULT_K = 3;

    public Reword(Graph graph){
        super(graph);
    }

    /**
     * Builds the weights array for the node n, computing the pf-itf on each featured property
     *
     * @param n a given node
     * @param tu the total number of triples in which n appears.
     * @param tpMap contains the tp values for each featured property p, where tp is the total number of triples having p as a predicate
     * @param direction specifies the incoming or outgoing direction of the pf computation
     * @return the weights array
     */
    private HashMap<Node, Double> buildWeights(Node n, Set<Node> featuredProps, int tu, HashMap<Node, Integer> tpMap, String direction){
        HashMap<Node, Double> weights = new HashMap<>();
        for(Node p : featuredProps){
            int tp = tpMap.get(p);
            weights.put(p, computePfItf(n, graph.createArrow(p, direction), tu, tp));
        }
        return weights;
    }

    /**
     * Given an item, computes the total number of triples in which the item appears.
     *
     * @param item a given item
     * @return the total number of triples in which the item appears
     */
    private int computeTu(Node item){
        return item.getArrows().entrySet().parallelStream()
                .collect((summingInt(e -> e.getValue().size())));

    }

    /**
     * Given an item and an arrow (with a given direction) computes the number of triples having the item as subject
     * (or object if the arrow has an incoming direction)
     *
     * @param item a given item
     * @param arrow a given arrow
     * @return the Tu(p) value
     */
    private int computeTup(Node item, Arrow arrow){
        return item.getArrowObjects(arrow).size();
    }

    private int computeTp(Node property){
        Arrow arrow = graph.createArrow(property, Arrow.DIR_OUT);

        return graph.getArrowOccurrences(arrow);
    }

    /**
     * Computes the T(p) for each one of the featured properties
     *
     * @return
     */
    private HashMap<Node, Integer> computeAllTp(Set<Node> featuredProps){
        return (HashMap<Node, Integer>) featuredProps.parallelStream()
                .collect(Collectors.toMap(
                        p -> p,
                        p -> computeTp(p)
                ));
            //tpMap.put(property, computeTp(property));
    }


    private double computePfItf(Node item, Arrow arrow, int tu, int tp){

        int tup = computeTup(item, arrow);
        double pf = tup / (double) tu;

        int t = graph.getStatementsCount();
        double itf = Math.log(t / (double) tp);

        return pf * itf;

    }

    private Set<Node> computeFeatureSpace(Node item1, Node item2){
        HashSet<Node> featuredProps = new HashSet<>();
        featuredProps.addAll(item1.getArrows().keySet().parallelStream()
                .map(Arrow::getProperty).collect(toSet()));
        featuredProps.addAll(item2.getArrows().keySet().parallelStream()
                .map(Arrow::getProperty).collect(toSet()));
        return featuredProps;
    }

    /**************PATH INFORMATIVENESS**************/

    /**
     * Computes the set of paths of length at most k connecting two items.
     * For instance, if k = 3 then all the paths of length 1, 2 and 3 will be considered.
     *
     * @param item1 a given node
     * @param item2 a given node
     * @param k a given length
     * @return the set of paths of length at most k connecting two items
     */
    private ArrayList<Path> kbgpReachability(Node item1, Node item2, int k){
        HashSet<Path> itemsPaths = new HashSet<>();

        itemsPaths.addAll(item1.findPaths(item2, k));

        return new ArrayList<>(itemsPaths);
    }

    private ArrayList<Path> kbgpReachabilityOld(Node item1, Node item2, int k){
        HashSet<Path> paths = new HashSet<>();

        while(k > 0){
            paths.addAll(item1.findPathsOld(item2, k));
            k--;
        }
        /*k = 3;
        while(k > 0){
            paths.addAll(item2.findPathsOld(item1, k));
            k--;
        }*/

        return new ArrayList<>(paths);
    }

    /**
     * Computes the informativeness of a given path between two nodes
     *
     * @param subject a given node
     * @param object a given node
     * @param path a given path
     * @param tuSubject the Tu of the subject node
     * @param tuObject the Tu of the object node
     * @return the informativeness of a given path between two nodes
     */
    private double pathInformativeness(Node subject, Node object, Path path, int tuSubject, int tuObject, HashMap<Node, Integer> tpMap){
        double info = 0;
        for(Arrow arrow : path.getArrowsList()){
            Node prop = arrow.getProperty();
            if(!tpMap.containsKey(prop)){
                tpMap.put(prop, computeTp(prop));
                //System.out.println("New tp for: " + prop.getId());
            }
            info += arrowInformativeness(subject, object, prop, tuSubject, tuObject, tpMap.get(prop));
        }
        info /= path.getArrowsList().size();

        return info;
    }

    /**
     * Computes the informativeness of a predicate that directly links two items
     *
     * @param subject a given node
     * @param object a given node
     * @param property a given predicate that links subject and object
     * @param tuSubject the Tu of the subject node
     * @param tuObject the Tu of the object node
     * @param tp total number of triples having property as predicate
     * @return the informativeness of {@code property}
     */
    private double arrowInformativeness(Node subject, Node object, Node property, int tuSubject, int tuObject, int tp){

        double pfItfo = computePfItf(subject, graph.createArrow(property, Arrow.DIR_OUT), tuSubject, tp);
        double pfItfi = computePfItf(object, graph.createArrow(property, Arrow.DIR_IN), tuObject, tp);

        return (pfItfo + pfItfi) / 2;
    }

    /**
     * Given two nodes, it adds the path informativeness to their weights array, considering the most informative
     * path between them
     *
     * @param subject a given node
     * @param object a given node
     * @param tuSubject the subject Tu
     * @param tuObject the object Tu
     * @param itemPaths the paths linking the two items
     * @param subjectWeights the subject node weights array
     * @param objectWeights the object node weights array
     */
    private void addPathInformativeness(Node subject, Node object, Set<Node> featuredProps, int tuSubject, int tuObject, HashMap<Node, Integer> tpMap,
                                        ArrayList<Path> itemPaths, HashMap<Node, Double> subjectWeights, HashMap<Node, Double> objectWeights){
        //find path with max informativeness
        Path bestPath = null;
        double bestInfo = -1;
        for(int i = 0; i < itemPaths.size(); i++) {
            Path path = itemPaths.get(i);
            double pathInfo = pathInformativeness(subject, object, path, tuSubject, tuObject, tpMap);
            if (pathInfo > bestInfo) {
                bestPath = path;
                bestInfo = pathInfo;
            }
        }
        if(bestPath != null) {
            //add informativeness to the single predicates
            for (Arrow arrow : bestPath.getArrowsList()) {
                Node prop = arrow.getProperty();
                double arrowInfo = arrowInformativeness(subject, object, prop, tuSubject, tuObject, tpMap.get(prop));
                if (featuredProps.contains(prop)) {
                    //add informativeness to this predicate
                    subjectWeights.put(prop, subjectWeights.get(prop) + arrowInfo);
                    objectWeights.put(prop, objectWeights.get(prop) + arrowInfo);
                    //System.out.println("Added info to prop: " + prop.getId());
                } else {
                    //add a new dimension (predicate)
                    subjectWeights.put(prop, arrowInfo);
                    objectWeights.put(prop, arrowInfo);
                    //System.out.println("Added dimension: " + prop.getId());
                }
            }
        }
    }


    /************RELATEDNESS COMPUTATION*************/

    /**
     * Returns a weights array from a predicate-weight map
     *
     * @param weights the weights map
     * @return the weights array
     */
    private double[] mapToWeightsArray(HashMap<Node, Double> weights){
        double[] weightsArray = new double[weights.size()];
        int i = 0;
        for(Map.Entry<Node, Double> entry : weights.entrySet()){
            weightsArray[i] = entry.getValue();
            i++;
        }
        return weightsArray;

    }

    private HashMap<Node, Double> weightsMean(HashMap<Node, Double> weights1, HashMap<Node, Double> weights2, Set<Node> featuredProps){
        HashMap<Node, Double> weightsMean = new HashMap<>();
        for(Node property : featuredProps){
            double val1 = weights1.get(property);
            double val2 = weights2.get(property);
            double mean = (val1 + val2) / 2;
            weightsMean.put(property, mean);
        }
        return weightsMean;
    }



    /**
     * Computes the relatedness between two nodes, considering the most informative path
     * between them with length at most K
     *
     * @param item1 the first node
     * @param item2 the second node
     * @param k max length of the considered paths between the two nodes
     * @return the relatedness value between the two nodes
     */
    public double computeRelatedness(Node item1, Node item2, int k){

        int tu1 = computeTu(item1);
        int tu2 = computeTu(item2);

        Set<Node> featuredProps = computeFeatureSpace(item1, item2);

        HashMap<Node, Integer> tpMap = computeAllTp(featuredProps);

        /*HashMap<Node, Double> weights1 = weightsMean(
                buildWeights(item1, featuredProps, tu1, tpMap, Arrow.DIR_IN),
                buildWeights(item1, featuredProps, tu1, tpMap, Arrow.DIR_OUT), featuredProps);
        HashMap<Node, Double> weights2 = weightsMean(
                buildWeights(item2, featuredProps, tu2, tpMap, Arrow.DIR_IN),
                buildWeights(item2, featuredProps, tu2, tpMap, Arrow.DIR_OUT), featuredProps);*/

        HashMap<Node, Double> weights1 = buildWeights(item1, featuredProps, tu1, tpMap, Arrow.DIR_OUT);
        HashMap<Node, Double> weights2 = buildWeights(item2, featuredProps, tu2, tpMap, Arrow.DIR_OUT);

        ArrayList<Path> itemsPaths = kbgpReachability(item1, item2, k);
        addPathInformativeness(item1, item2, featuredProps, tu1, tu2, tpMap, itemsPaths, weights1, weights2);

        return Utilities.cosineSimilarity(mapToWeightsArray(weights1), mapToWeightsArray(weights2));
    }

    public double computeRelatedness(String idItem1, String idItem2){
        Node item1 = graph.getNode(idItem1);
        Node item2 = graph.getNode(idItem2);

        return computeRelatedness(item1, item2);
    }

    /**
     * Computes the relatedness between two nodes, considering the most informative path
     * between them with max length equals to {@link #DEFAULT_K}
     *
     * @param item1 the first node
     * @param item2 the second node
     * @return the relatedness value between the two nodes
     */
    public double computeRelatedness(Node item1, Node item2){
        return computeRelatedness(item1, item2, DEFAULT_K);
    }

    /**
     * {@inheritDoc}
     */
    public Map<Node, Double> computeRelatednessRank(String itemID) {
        Node item1 = graph.getNode(itemID);

        Map<Node, Double> rank =
                graph.getItems().entrySet().parallelStream()
                        .filter(e -> !e.getValue().equals(item1))
                        .collect(Collectors.toMap(
                                e -> e.getValue(),
                                e -> computeRelatedness(item1, e.getValue())
                        ));

        return Utilities.sortByValues(rank);
    }

}
