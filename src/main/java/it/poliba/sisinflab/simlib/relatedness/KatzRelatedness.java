package it.poliba.sisinflab.simlib.relatedness;

import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.Path;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements a relatedness metric based on Katz centrality measure proposed in
 *
 * <p>
 *     (cfr. Ioana Hulpu̧s, Narumol Prangnawarat, Conor Hayes
 *     <a href="">
 *         Path-based Semantic Relatedness on Linked Data and its use to Word and Entity Disambiguation
 *     </a>, ISWC 2015)
 *
 * </p>
 *
 * @author Paolo Tomeo
 */
public class KatzRelatedness extends RelatednessMeasure {
    int k = 4;
    int maxDepth = 6;
    double alpha = 0.25;

    public KatzRelatedness(Graph graph) {
        super(graph);
    }

    /**
     *
     * @param graph
     * @param k max number of top shortest paths between each couple of nodes
     * @param maxDepth search max depth
     * @param alpha constant probability of an hop
     */
    public KatzRelatedness(Graph graph, int k, int maxDepth, double alpha) {
        super(graph);
        this.k = k;
        this.maxDepth = maxDepth;
        this.alpha = alpha;
    }

    @Override
    public double computeRelatedness(Node item1, Node item2) {
        List<Path> shortestPaths = graph.findTopKShortestPaths(item1, item2, k, maxDepth);

//        System.out.println(shortestPaths);
        return shortestPaths.parallelStream()
                .mapToDouble(p -> Math.pow(alpha, p.length()))
                .sum() / k;
                //.average()
                //.orElse(0);
    }
    public double computeRelatedness(List<Integer> paths) {
        return paths.parallelStream()
                .mapToDouble(p -> Math.pow(alpha, p))
                .sum() / k;
    }

    
    @Override
    public Map<Node, Double> computeRelatednessRank(Node n, Map<String, Node> targetNodes){
        int halfMaxDepth = maxDepth / 2;
        if((maxDepth % 2) > 0)
            halfMaxDepth++;


        Map<Node, List<Integer>> shortestPaths = new HashMap<>();
        targetNodes.values().stream().forEach(d -> shortestPaths.put(d, new ArrayList<>()));


        HashMap<Integer, Collection<Node>> neighbors = graph.getNeighborsDifferentDepths(n, halfMaxDepth, Arrow.DIR_OUT);
        //System.out.println(neighbors);

        int finalHalfMaxDepth = halfMaxDepth;
        targetNodes.values().stream().forEach(t -> {
            List<Integer> spt = shortestPaths.get((t));
            HashMap<Integer, Collection<Node>> targetNeighbors = graph.getNeighborsDifferentDepths(t, finalHalfMaxDepth,  Arrow.DIR_OUT);
            for (int i = 1; i < finalHalfMaxDepth + 1 && spt.size() < k  ; i++){
                //if neighbors map contains directly the target node t
                if (neighbors.get(i).contains(t))
                    spt.add(i);

                //look for neighbors in common
                for (int j = 1; j < finalHalfMaxDepth - i + 1 && spt.size() < k ; j++){
                    int sum = i + j;
                    neighbors.get(i).stream()
                            .filter(targetNeighbors.get(j)::contains)
                            .forEach(nn -> spt.add(sum));
                }
            }

        });

        shortestPaths.values()
                .forEach(Collections::sort);
        shortestPaths.values().stream()
                .filter(v -> v.size() > k-1)
                .forEach(v -> v.retainAll(v.subList(0, k -1)));


        Map<Node, Double> rank =
                targetNodes.entrySet().parallelStream()
                        .filter(e -> !e.getValue().equals(n))
                        .map(e -> new AbstractMap.SimpleEntry<>(e.getValue(), computeRelatedness(shortestPaths.get(e.getValue()))))
                        .filter(e -> e.getValue() > 0 )
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e -> e.getValue()
                        ));

        return Utilities.sortByValues(rank);
    }


}