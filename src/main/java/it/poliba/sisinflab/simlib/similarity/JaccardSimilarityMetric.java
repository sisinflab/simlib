package it.poliba.sisinflab.simlib.similarity;

import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.Path;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the Jaccard similarity coefficient algorithm
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class JaccardSimilarityMetric extends SimilarityMetric{

    private int distance;
    private Set<Path> paths;

    public JaccardSimilarityMetric(Graph graph, int distance){
        super(graph);
        if(distance < 1){
            throw new IllegalArgumentException("distance must be at least 1");
        }
        this.distance = distance;
    }

    public JaccardSimilarityMetric(Graph graph, HashSet<Path> featuredPaths){
        super(graph);
        this.paths = featuredPaths;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double computePathSimilarity(Node item1, Node item2, Path path) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double computeSimilarity(Node item1, Node item2) {
        //gather the nodes you find walking from an item to a given depth
        HashSet<Node> neighbors1;
        HashSet<Node> neighbors2;

        if(paths != null) {
            neighbors1 = item1.collectNeighbors(paths);
            neighbors2 = item2.collectNeighbors(paths);
        }else{
            neighbors1 = item1.collectNeighbors(distance, Arrow.DIR_OUT);
            neighbors2 = item2.collectNeighbors(distance, Arrow.DIR_OUT);
        }

        //compute intersection between the two sets
        HashSet<Node> intersection = new HashSet<>(neighbors1);
        intersection.retainAll(neighbors2);

        //compute union between the two sets
        HashSet<Node> union = new HashSet<>(neighbors1);
        union.addAll(neighbors2);

        /*HashSet<Node> simmetricDifference = new HashSet<>(union);
        simmetricDifference.removeAll(intersection);*/

        //J = (A ∩ B) / (A ∪ B)
        return intersection.size() / (double) union.size();
    }

}
