package it.poliba.sisinflab.simlib.datamodel;

import it.poliba.sisinflab.simlib.input.Statement;
import it.poliba.sisinflab.simlib.input.triplefile.Triple;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

/**
 * Represents a graph structure.
 *
 * It represents a graph through its {@link #nodes} and keeps track of which ones are also graph {@link #items}.
 * {@link #items} are nodes for which we want to calculate similarity or relatedness values
 * (i.e. a movie, a song, a book etc.)
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class Graph{

    /**
     * It keeps track of all the graph nodes, using as keys their IDs in order to have a fast retrieval
     * through the {@link HashMap} indexing
     */
    protected HashMap<String, Node> nodes;
    protected HashMap<String, Node> items;

    protected HashSet<Node> properties;
    protected List<Statement> statements;

    protected Graph(){
        nodes = new HashMap<>();
        items = new HashMap<>();

        properties = new HashSet<>();
    }

    /*************** FACTORY METHODS ****************/

    public Node createNode(String id){
        if(!nodes.containsKey(id)){
            nodes.put(id, new Node(id, this));
        }
        return nodes.get(id);
    }


    public Arrow createArrow(Node p, String direction){
        return new Arrow(p, direction);
    }

    public Path createPath(LinkedList<Arrow> arrowsList){
        return new Path(arrowsList);
    }

    public Path createPath(Arrow... arrows){
        LinkedList<Arrow> arrowsList = new LinkedList<>(Arrays.asList(arrows));
        return createPath(arrowsList);
    }

    public Path createPath(String... properties){
        LinkedList<Arrow> arrowsList = new LinkedList<>();
        for(String property : properties){
            Node p = createNode(property);
            arrowsList.add(createArrow(p, Arrow.DIR_OUT));
        }

        return createPath(arrowsList);
    }

    public Path propertyStringtoPath(String property, String direction) {

        return createPath(createArrow(createNode(property), direction));
    }


    public Node getNode(String id){
        return nodes.get(id);
    }

    public Node getItem(String id) {
        return items.get(id);
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }
    public Map<String,Node> getNodes(Collection<String> targetNodes) {
        return this.getNodes().entrySet().parallelStream()
                .filter(e -> targetNodes.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() ));
    }

    public Map<String,Node> getNodesFrom(Set<String> sourceNodes, String property) {

        return this.getNodes().entrySet().stream()
                .filter(e -> sourceNodes.contains(e.getKey()))
                .map(e -> e.getValue().getNeighbors(this.createPath(property)))
                .flatMap(Set::stream)
                .distinct()
                .collect(Collectors.toMap(n -> n.getId(), n -> n));

    }

    public void setNodes(HashMap<String, Node> nodes) {
        this.nodes = nodes;
    }

    public HashMap<String, Node> getItems() {
        return items;
    }

    public void setItems(HashMap<String, Node> items) {
        this.items = items;
    }

    public HashSet<Node> getProperties() {
        return properties;
    }
    public Set<Node> getPropertiesStartingFrom(Map<String, Node> items) {
        return items.values().stream()
                .flatMap(n -> n.getArrows(Arrow.DIR_OUT).keySet().stream())
                .map(n -> n.getProperty())
                .collect(Collectors.toSet());

    }

    public void setProperties(HashSet<Node> properties) {
        this.properties = properties;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    protected void build(List<Statement> tripleList){
        statements = tripleList;
        System.out.println("Building graph...");

        tripleList.stream()
                .forEach(this::processTriple);

        System.out.println("Graph building complete");
        System.out.println("Nodes: " + nodes.size());
    }

    protected void build(List<Statement> statements, List<String> itemsIDs){
        build(statements);
        markItems(itemsIDs);
        System.out.println("Items: " + items.size());
    }

    public void markItems(List<String> itemsIDs){
        for(String itemID : itemsIDs){
            Node n = nodes.get(itemID);
            if(n != null){
                items.put(itemID, n);
            }else{
                System.out.println("Node " + itemID + " not found in the graph");
            }
        }
    }

    /**
     * Processes a Triple adding its components to the Graph data model
     *
     * @param s a given statement
     */
    private void processTriple(Statement s){
        Triple t = (Triple) s;

        if(!nodes.containsKey(t.getSubject())){
            nodes.put(t.getSubject(), createNode(t.getSubject()));
        }
        if(!nodes.containsKey(t.getObject())){
            nodes.put(t.getObject(), createNode(t.getObject()));
        }
        if(!nodes.containsKey(t.getPredicate())){
            nodes.put(t.getPredicate(), createNode(t.getPredicate()));
        }
        /*
         *  this should be able to avoid creation of new nodes when already existing
         *  moreover, Nodes object in the arrows list, in the nodes and items HashMaps should have consistent reference
         */
        Node tail = nodes.get(t.getSubject());
        Node head = nodes.get(t.getObject());

        Node p = nodes.get(t.getPredicate());

        tail.addArrow(new Arrow(p, Arrow.DIR_OUT), head);

        //make sure that the triple extractor uses UNIQUE identifiers for each resource in the graph (it's not happening in metadata.txt)

        head.addArrow(new Arrow(p, Arrow.DIR_IN), tail);

        properties.add(p);
    }

    /**
     * Returns the nodes reachable following a given set of paths. For each path, there's the associated set of nodes
     * with the number of times they are reached.
     *
     * @param paths the given set of paths
     * @return a map which entries are each of the given paths with the associated reachable nodes with their frequencies
     */
    public Map<Path, Map<Node, Integer>> getPathsObjects(Set<Path> paths) {

        Map<Path, Map<Node, Integer>> pathsObjects = new HashMap<>();
        for (Path p : paths) {
            pathsObjects.put(p, getPathObjectsStream(p));
        }

        return pathsObjects;
    }

    /**
     * Returns the set of nodes reachable following a given path (starting from anyone of the graph items),
     * giving also the number of times they are reached.
     *
     * @param path the given path
     * @return a map which entries are the reachable nodes with their associated frequency
     * @TODO I think the name is wrong: the method does not return a stream.
     */
    public Map<Node, Integer> getPathObjectsStream(Path path) {

        return items.entrySet().parallelStream()
                .map(e -> {
                    //for the one-step transitivity purpose, we need to get the neighbors using each possible subpath
                    //this is different from the collectPathObjects methods in the Node class, because in that case
                    //we don't collect nodes from which we can't follow the full path
                    HashSet<Node> nodes = new HashSet<>();
                    Path subPath = new Path(path.getArrowsList());
                    while(subPath.getArrowsList().size() > 0){
                        nodes.addAll(e.getValue().getNeighbors(subPath));
                        subPath.getArrowsList().removeLast();
                    }
                    return nodes;
                }) //for each Node, get HashSet of Nodes that are objects for "path"
                .flatMap(HashSet::stream) //create a single Stream with all the Nodes contained in the previous HashSets
                .collect(groupingBy(n -> n, summingInt(c -> 1)));
    }

    public int getArrowOccurrences(Arrow arrow){
        return getNodes().entrySet().parallelStream()
                .collect(summingInt(e -> e.getValue().getArrowObjects(arrow).size()));
    }


    public int getStatementsCount() {
        return getStatements().size();
    }


    public List<Path> findTopKShortestPaths(Node start, Node destination, int k, int maxDepth){
        return start.findTopKShortestPaths(destination, k, maxDepth);
    }
    public HashMap<Node, List<Path>> findTopKShortestPaths(Node start, Collection<Node> destinations, int k, int maxDepth){
        return start.findTopKShortestPaths(destinations, k, maxDepth);
    }


    public boolean contains(String nodeID){
        return this.nodes.containsKey(nodeID);
    }

    public void removeProperties(Set<String> s, Map<String, Node> items){
        List<Arrow> p_out = s.stream().map(p -> this.createArrow(this.createNode(p), Arrow.DIR_OUT)).collect(Collectors.toList());
        List<Arrow> p_in = s.stream().map(p -> this.createArrow(this.createNode(p), Arrow.DIR_IN)).collect(Collectors.toList());

        items.values().parallelStream().forEach(n -> {
           p_out.stream().forEach(p -> n.removeArrow(p));
           p_in.stream().forEach(p -> n.removeArrow(p));
        });

//      s.stream().forEach(this::removeProperty);

        removeNodesWithNoLinks();
    }

    private void removeNodesWithNoLinks() {
        List<String> nodeIDs = nodes.entrySet().stream()
                .filter(e -> !properties.contains(e.getValue()))  //ATTENTION! properties have often no link
                .filter(e -> e.getValue().arrows.size() == 0)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
        nodeIDs.stream()
                .forEach(this::removeNode);
    }

    public void removeProperty(String propID){
        if(nodes.containsKey(propID)){
            properties.remove(nodes.get(propID));
            this.nodes.remove(propID);
        }
    }
    public void removeNode(String nodeID){
        if(nodes.containsKey(nodeID)){
            this.nodes.remove(nodeID);
            this.items.remove(nodeID);
        }
    }

    public HashMap<Integer, Collection<Node>> getNeighborsDifferentDepths(Node n, int maxDepth, String direction){
        HashMap<Integer, Collection<Node>> neighbors = new HashMap<>();
        for (int i = 1 ; i < maxDepth + 1 ; i++){

//            HashSet<Node> neighs = n.getNeighbors(i, direction);
//            neighs.remove(n);
            neighbors.put(i, n.getNeighborsWithRepetition(i, direction));
        }
        return neighbors;
    }
}
