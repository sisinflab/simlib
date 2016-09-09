package it.poliba.sisinflab.simlib.similarity;

import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the PathSim similarity
 * <p>
 *     (cfr. Yizhou Sun , Jiawei Han , Xifeng Yan , Philip S. Yu , Tianyi Wu
 *     <a href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.220.2455">
 *         Pathsim: Meta path-based top-k similarity search in heterogeneous information networks
 *     </a>, VLBD 2011)
 *
 * </p>
 *
 *
 * @author Paolo Tomeo
 * @since 1.0
 */
public class PathSim extends SimilarityMetric {
    @Override
    public double computePathSimilarity(Node item1, Node item2, Path path) {
        HashMap<Node, Integer> freqs = item1.getNeighborsFrequency(path);
        Integer freq = freqs.getOrDefault(item2, 0);
        Integer freq1 = freqs.getOrDefault(item1, 1);
        Integer freq2 = item2.getNeighborsFrequency(path).get(item2);

        return 2 * freq /(freq1 + freq2);
    }

    @Override
    public double computeSimilarity(Node item1, Node item2) {
        return 0;
    }
}
