package it.poliba.sisinflab.simlib.datamodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keeps tracks of a particular exploration represented by the path and the reached nodes
 * and allows expansion of all the those nodes
 *
 * @author Paolo Tomeo
 * @since 1.0
 */
public class Exploration {

    Path path;
    HashSet<Node> nodes;

    public Exploration(Path path, HashSet<Node> nodes) {
        this.path = path;
        this.nodes = nodes;
    }

    public Path getPath() {
        return path;
    }

    public HashSet<Node> getNodes() {
        return nodes;
    }

    /**
     *
     * @return list of explorations
     */
    public List<Exploration> expand(Set<Node> nodesToBeSkipped){
        return this.getNodes().stream()
                .filter(n -> !nodesToBeSkipped.contains(n))
                .map( n ->  n.getArrows().entrySet())
                .flatMap( a -> a.stream() )
                .map(e -> new Exploration(new Path(path, e.getKey()), e.getValue()))
                .collect(Collectors.toList());

    }

}
