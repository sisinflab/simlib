package it.poliba.sisinflab.simlib.datamodel.pathmodel;

import it.poliba.sisinflab.simlib.input.Statement;
import it.poliba.sisinflab.simlib.input.pathfile.PathTriple;
import it.poliba.sisinflab.simlib.datamodel.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

/**
 * Extends the {@link Graph} class, implementing a {@link Path} based data model, in which every {@link Node}
 * object is an instance of {@link PathModelNode} and besides the information of its 1-hop neighbors, it also has
 * information about n-hop neighbors in the form of couples ({@link Path} -- reachable {@link Node}s
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class PathGraph extends Graph{

    /**
     * Keeps track of all the paths in the input file
     */
    private HashSet<Path> paths;

    public PathGraph(){
        super();
        paths = new HashSet<>();
    }

    @Override
    public Node createNode(String id){
        if(!nodes.containsKey(id)){
            nodes.put(id, new PathModelNode(id, this));
        }
        return nodes.get(id);
    }

    public HashSet<Path> getPaths() {
        return paths;
    }

    public void setPaths(HashSet<Path> paths) {
        this.paths = paths;
    }

    /**
     * Parses a list of statements filling the graph data model
     *
     * @param pathTriplesList a list of statements
     */
    @Override
    public void build(List<Statement> pathTriplesList, List<String> itemsIDs) {
        statements = pathTriplesList;
        System.out.println("Building graph...");
        pathTriplesList.stream()
                .forEach(this::processTriple);

        markItems(itemsIDs);

        System.out.println("Graph building complete");
        System.out.println("Nodes: " + nodes.size());
    }

    /**
     * Processes a Triple adding its components to the Graph data model
     *
     * @param s a given statement
     */
    private void processTriple(Statement s){

        PathTriple t = (PathTriple) s;

        if(!nodes.containsKey(t.getSubject())){
            nodes.put(t.getSubject(), new PathModelNode(t.getSubject(), this));
        }
        if(!nodes.containsKey(t.getObject())){
            nodes.put(t.getObject(), new PathModelNode(t.getObject(), this));
        }
        for(String p : t.getPredicates()){
            if(!nodes.containsKey(p)){
                nodes.put(p, createNode(p));
            }
        }
        /*
         *  this should be able to avoid creation of new nodes when already existing
         *  moreover, Node objects in the arrows list and in the nodes and items HashMaps should have consistent reference
         */
        PathModelNode tail = (PathModelNode) nodes.get(t.getSubject());
        PathModelNode head = (PathModelNode) nodes.get(t.getObject());

        LinkedList<Node> propList = new LinkedList<>(t.getPredicates().stream().map(p -> nodes.get(p))
                .collect(toList()));

        if(propList.size() == 1){
            tail.addArrow(createArrow(propList.getFirst(), Arrow.DIR_OUT), head);
            head.addArrow(createArrow(propList.getFirst(), Arrow.DIR_IN), tail);
        }

        LinkedList<Arrow> arrowsOutList = new LinkedList<>(propList.stream().map(p -> createArrow(p, Arrow.DIR_OUT))
                .collect(toList()));

        LinkedList<Arrow> arrowsInList = new LinkedList<>(propList.stream().map(p -> createArrow(p, Arrow.DIR_IN))
                .collect(toList()));


        tail.addPath(createPath(arrowsOutList), head);

        //make sure that the triple extractor uses UNIQUE identifiers for each resource in the graph (it's not happening in metadata.txt)

        head.addPath(createPath(arrowsInList), tail);

        paths.add(createPath(arrowsOutList));

    }




    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Node, Integer> getPathObjectsStream(Path path) {

        return items.entrySet().parallelStream()
                .filter(e -> ((PathModelNode) e.getValue()).getPaths().containsKey(path)) //get only nodes with a "property" outArrow
                .map(e -> ((PathModelNode) e.getValue()).getPaths().get(path)) //for each Node, get HashSet of Nodes that are objects for "property"
                .flatMap(HashSet::stream) //create a single Stream with all the Nodes contained in the previous HashSets
                .collect(groupingBy(n -> n, summingInt(c -> 1)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Path, Map<Node, Integer>> getPathsObjects(Set<Path> paths) {
        return paths.parallelStream().collect(Collectors.toMap(
                p -> p,
                p -> getPathObjectsStream(p)
        ));
    }

}
