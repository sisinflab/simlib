package it.poliba.sisinflab.simlib.datamodel;

import it.poliba.sisinflab.simlib.Utilities;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Represents a graph node which keeps track of all the incoming/outgoing arrows from/towards other nodes
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class Node {

    protected String id;
    protected Graph graphRef;

    /**
     * Each key is an {@link Arrow} object. For each arrow there's a set of nodes which are linked
     * to the current node through this arrow. i.e.
     * <ul>
     *     <li>dbr:Star_Wars_(film) dbo:starring dbr:Alec_Guinness</li>
     *     <li>dbr:Star_Wars_(film) dbo:starring dbr:Peter_Cushing</li>
     * </ul>
     * dbr:Star_Wars_(film) will have an entry with key dbo:starring (as an {@link Arrow} object with an outgoing
     * direction) and as value the {@link HashSet} filled with dbr:Alec_Guinness and dbr:Peter_Cushing
     * */
    protected HashMap<Arrow, HashSet<Node>> arrows;

    protected Node(String id, Graph graphRef){
        this.id = id;
        this.graphRef = graphRef;
        this.arrows = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Node){
            Node n = (Node) o;
            return n.getId().equals(id);
        }
        return false;
    }

    public HashMap<Arrow, HashSet<Node>> getArrows(){
        return arrows;
    }

    /**
     * Returns all the arrows having a given {@param direction}
     *
     * @param direction a specified direction
     * @return the arrows having that direction
     */
    public HashMap<Arrow, HashSet<Node>> getArrows(String direction){
        return (HashMap<Arrow, HashSet<Node>>) getArrows().entrySet().stream()
                .filter(a -> a.getKey().getDirection().equals(direction))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void addArrow(Arrow arrow, Node object) {
        if (!getArrows().containsKey(arrow)) {
            getArrows().put(arrow, new HashSet<>());
        }
        getArrows().get(arrow).add(object);
    }

    private int getArrowsCount(){
        int count = 0;
        for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
            count += e.getValue().size();
        }
        return count;
    }

    private int getArrowsCount(String direction){

        HashMap<Arrow, HashSet<Node>> dirArrows = getArrows(direction);

        return dirArrows.entrySet().stream()
                .collect(summingInt(e -> e.getValue().size()));
    }

    public boolean hasArrow(Arrow arrow){
        return getArrows().containsKey(arrow);
    }

    public void removeArrow(Arrow arrow){
        this.arrows.remove(arrow);
    }

    /**
     * Returns all the nodes reachable through a given arrow
     *
     * @param arrow a given arrow
     * @return all the nodes reachable through that arrow
     * @TODO the name of this method is not explicative
     */
    public HashSet<Node> getArrowObjects(Arrow arrow){
        HashSet<Node> nodes = getArrows().get(arrow);
        if(nodes == null)
            nodes = new HashSet<>();
        return nodes;
    }


    /************SEARCH ALGORITHMS**************/

    /**
     * Checks if a node can follow a given path
     *
     * @param path a given path
     * @return <code>true</code> if the node can follow the path, <code>false</code> otherwise
     */
    public boolean hasPath(Path path){
        HashMap<Arrow, HashSet<Node>> neighborArrows;
        LinkedList<Node> fringe = new LinkedList<>();
        fringe.addLast(this);
        Iterator<Arrow> iter = path.getArrowsList().iterator();
        if(iter.hasNext()){
            Arrow nextArrow = iter.next();
            Node n = fringe.getLast();
            fringe.removeLast();
            neighborArrows = n.getArrows();
            if (neighborArrows.containsKey(nextArrow)) {
                neighborArrows.get(nextArrow).forEach(fringe::addLast);
                while (fringe.size() != 0) {
                    //create a subPath removing the first property
                    Path subPath = new Path(path.getArrowsList());
                    subPath.removeFirst();
                    //remove the node to explore from the fringe and start the exploration from it
                    n = fringe.getLast();
                    fringe.removeLast();
                    //restart the algorithm using n as the first node and subPath as the exploration path
                    boolean found = n.hasPath(subPath);
                    if (found) {
                        return true;
                    }
                }
            }
        }
        //this will be true only when the algorithm reaches a node following the complete path, so the root node will return true as well
        return fringe.size() > 0;
    }

    /**
     * Returns the set of nodes reachable following a given path
     *
     * @param path a given path
     * @return the set of nodes reachable through that path
     */
    public HashSet<Node> getNeighbors(Path path){
        HashMap<Arrow, HashSet<Node>> neighborArrows;
        HashSet<Node> leafs = new HashSet<>();
        LinkedList<Node> fringe = new LinkedList<>();
        fringe.addLast(this);
        Iterator<Arrow> iter = path.getArrowsList().iterator();
        if(!iter.hasNext()){
            leafs.add(this);
        }else{
            Arrow nextArrow = iter.next();
            Node n = fringe.getLast();
            fringe.removeLast();
            neighborArrows = n.getArrows();
            if(neighborArrows.containsKey(nextArrow)){
                neighborArrows.get(nextArrow).forEach(fringe::addLast);
                while(fringe.size() != 0) {
                    //create a subPath removing the first property
                    Path subPath = new Path(path.getArrowsList());
                    subPath.removeFirst();
                    //remove the node to explore from the fringe and start the exploration from it
                    n = fringe.getLast();
                    fringe.removeLast();
                    //restart the algorithm using n as the first node and subPath as the exploration path
                    HashSet<Node> found = n.getNeighbors(subPath);
                    if(found.size() > 0) {
                        leafs.addAll(found);
                    }
                }

            }
        }
        return leafs;
    }

    /**
     * Returns the set of nodes to go through following a given path
     *
     * @param path a given path
     * @return the set of collected nodes
     */
    public HashSet<Node> collectPathNodes(Path path){
        HashMap<Arrow, HashSet<Node>> arrows;
        HashSet<Node> nodes = new HashSet<>();
        LinkedList<Node> fringe = new LinkedList<>();
        fringe.addLast(this);
        Iterator<Arrow> iter = path.getArrowsList().iterator();
        if(!iter.hasNext()){
            nodes.add(this);
        }else{
            Arrow nextArrow = iter.next();
            Node n = fringe.getLast();
            fringe.removeLast();
            arrows = n.getArrows();
            if(arrows.containsKey(nextArrow)){
                arrows.get(nextArrow).forEach(fringe::addLast);
                while(fringe.size() != 0) {
                    Path subPath = new Path(path.getArrowsList());
                    subPath.removeFirst();

                    n = fringe.getLast();
                    fringe.removeLast();
                    //restarts the algorithm on one of its neighbors
                    HashSet<Node> found = n.collectPathNodes(subPath);
                    if(found.size() > 0) {
                        nodes.add(n);
                        nodes.addAll(found);
                    }
                }
            }
        }
        return nodes;
    }

    /**
     * Finds neighbors on a given {@code distance} following a given direction
     * @param distance distance of the required neighbors from this node
     * @param direction a given direction
     * @return the set of neighbors
     */
    public HashSet<Node> getNeighbors(int distance, String direction){

        HashSet<Node> neighbors = new HashSet<>();
        if(distance == 1){
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)) {
                    neighbors.addAll(e.getValue());
                }
            }
        }else{
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)){
                    for(Node n : e.getValue()) {
                        neighbors.addAll(n.getNeighbors(distance - 1, direction));
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Finds neighbors on a given {@code distance} following a given direction
     * Return a collection, not a set
     * @param distance distance of the required neighbors from this node
     * @param direction a given direction
     * @return the collection of neighbors with possible repetition
     */
    public Collection<Node> getNeighborsWithRepetition(int distance, String direction){

        Collection<Node> neighbors = new ArrayList<>();
        if(distance == 1){
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)) {
                    neighbors.addAll(e.getValue());
                }
            }
        }else{
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)){
                    for(Node n : e.getValue()) {
                        neighbors.addAll(n.getNeighbors(distance - 1, direction));
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Returns the set of nodes that are reachable following only a specific set of paths
     *
     * @param featuredPaths the set of paths to consider
     * @return the set of nodes at the given distance
     */
    public HashSet<Node> getNeighbors(Set<Path> featuredPaths){

        return (HashSet<Node>) featuredPaths.parallelStream()
                .map(this::getNeighbors)
                .flatMap(Set::stream)
                .collect(toSet());

		/*HashSet<Node> neighbors = new HashSet<>();
		for(Path p : featuredPaths){
			if(p.getPathList().size() == distance){
				neighbors.addAll(getPathObjects(p, out));
			}
		}

		return neighbors;*/
    }


    /**
     * Returns the set of nodes at most at a given distance following a given direction
     *
     * @param maxDepth a given depth
     * @param direction a given direction
     * @return the set of nodes at most at the given distance
     */
    public HashSet<Node> collectNeighbors(int maxDepth, String direction){
        HashSet<Node> neighbors = new HashSet<>();

        for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
            if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)) {
                neighbors.addAll(e.getValue());
            }
        }
        if(maxDepth != 1){
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)) {
                    for(Node n : e.getValue()){
                        neighbors.addAll(n.collectNeighbors(maxDepth - 1, direction));
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Returns the set of nodes that may be go through following only a specific set of paths
     *
     * @param featuredPaths the set of paths to consider
     * @return the set of nodes at most at the given distance
     */
    public HashSet<Node> collectNeighbors(Set<Path> featuredPaths){

        return (HashSet<Node>) featuredPaths.parallelStream()
                .map(this::collectPathNodes)
                .flatMap(Set::stream)
                .collect(toSet());

		/*HashSet<Node> neighbors = new HashSet<>();
		for(Path p : featuredPaths){
			if(p.getPathList().size() == distance){
				neighbors.addAll(collectPathNodes(p, out));
			}
		}

		return neighbors;*/
    }


    /**************FREQUENCY****************/

    /**
     * Returns the set of nodes reachable following a given path and their occurrences
     *
     * @param path a given path
     * @return the set of nodes reachable through that path with their occurrences
     */
    public HashMap<Node, Integer> getNeighborsFrequency(Path path){
        HashMap<Arrow, HashSet<Node>> neighborArrows;
        HashMap<Node, Integer> leafs = new HashMap<>();
        LinkedList<Node> fringe = new LinkedList<>();
        fringe.addLast(this);
        Iterator<Arrow> iter = path.getArrowsList().iterator();
        if(!iter.hasNext()){
            leafs.put(this, 1);
        }else{
            Arrow nextArrow = iter.next();
            Node n = fringe.getLast();
            fringe.removeLast();
            neighborArrows = n.getArrows();
            if(neighborArrows.containsKey(nextArrow)){
                neighborArrows.get(nextArrow).forEach(fringe::addLast);
                while(fringe.size() != 0) {
                    //create a subPath removing the first property
                    Path subPath = new Path(path.getArrowsList());
                    subPath.removeFirst();
                    //remove the node to explore from the fringe and start the exploration from it
                    n = fringe.getLast();
                    fringe.removeLast();
                    //restart the algorithm using n as the first node and subPath as the exploration path
                    HashMap<Node, Integer> found = n.getNeighborsFrequency(subPath);
                    if(found.size() > 0) {
                        for(Map.Entry<Node, Integer> f : found.entrySet()){
                            Utilities.incrementMapCount(leafs, f.getKey(), f.getValue());
                        }
                    }
                }

            }
        }
        return leafs;
    }

    /**
     * Returns the set of nodes to go through following a given path, along with their occurrences
     *
     * @param path a given path
     * @return the set of collected nodes with their frequency
     */
    public HashMap<Node, Integer> collectPathNodesFreq(Path path){
        HashMap<Arrow, HashSet<Node>> arrows;
        HashMap<Node, Integer> collectedNodes = new HashMap<>();
        LinkedList<Node> fringe = new LinkedList<>();
        fringe.addLast(this);
        Iterator<Arrow> iter = path.getArrowsList().iterator();
        if(!iter.hasNext()){
            collectedNodes.put(this, 1);
        }else{
            Arrow nextArrow = iter.next();
            Node n = fringe.getLast();
            fringe.removeLast();
            arrows = n.getArrows();
            if(arrows.containsKey(nextArrow)){
                arrows.get(nextArrow).forEach(fringe::addLast);
                while(fringe.size() != 0) {
                    Path subPath = new Path(path.getArrowsList());
                    subPath.removeFirst();

                    n = fringe.getLast();
                    fringe.removeLast();
                    //restarts the algorithm on one of its neighbors
                    HashMap<Node, Integer> found = n.collectPathNodesFreq(subPath);
                    if(found.size() > 0) {
                        Utilities.incrementMapCount(collectedNodes, n, 1);
                        for(Map.Entry<Node, Integer> f : found.entrySet()){
                            Utilities.incrementMapCount(collectedNodes, f.getKey(), f.getValue());
                        }
                    }
                }
            }
        }

        return collectedNodes;
    }

    /**
     * Finds neighbors on a given {@code distance} and their occurrences, following a given direction
     * @param distance distance of the required neighbors from this node
     * @param direction a given direction
     * @return the set of neighbors with their occurrences
     */
    public HashMap<Node, Integer> getNeighborsFrequency(int distance, String direction){

        HashMap<Node, Integer> neighbors = new HashMap<>();
        if(distance == 1){
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || e.getKey().getDirection().equals(Arrow.DIR_UND)) {
                    for(Node n : e.getValue()){
                        Utilities.incrementMapCount(neighbors, n, 1);
                    }
                }
            }
        }else{
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || e.getKey().getDirection().equals(Arrow.DIR_UND)){
                    for(Node n : e.getValue()) {
                        HashMap<Node, Integer> found = n.getNeighborsFrequency(distance - 1, direction);
                        for(Map.Entry<Node, Integer> f : found.entrySet()){
                            Utilities.incrementMapCount(neighbors, f.getKey(), f.getValue());
                        }
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Returns the set of nodes that are reachable following only a specific set of paths along with their occurrences
     *
     * @param featuredPaths the set of paths to consider
     * @return the set of nodes at the given distance with their occurrences
     */
    public HashMap<Node, Integer> getNeighborsFrequency(Set<Path> featuredPaths){

        return (HashMap<Node, Integer>) featuredPaths.parallelStream()
                .map(this::getNeighborsFrequency)
                .flatMap(m -> m.entrySet().stream())
                .collect(groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));

    }

    /**
     * Returns the set of nodes at most at a given distance following a given direction, along with their occurrences
     * following a given direction
     *
     * @param maxDepth a given depth
     * @param direction a given direction
     * @return the set of nodes at most at the given distance with their occurrences
     */
    public HashMap<Node, Integer> collectNeighborsFreq(int maxDepth, String direction){
        HashMap<Node, Integer> neighbors = new HashMap<>();

        for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
            if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)) {
                for(Node n : e.getValue()){
                    Utilities.incrementMapCount(neighbors, n, 1);
                }
            }
        }
        if(maxDepth != 1){
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                if(e.getKey().getDirection().equals(direction) || direction.equals(Arrow.DIR_UND)) {
                    for(Node n : e.getValue()){
                        HashMap<Node, Integer> found = n.getNeighborsFrequency(maxDepth - 1, direction);
                        for(Map.Entry<Node, Integer> f : found.entrySet()){
                            Utilities.incrementMapCount(neighbors, f.getKey(), f.getValue());
                        }
                    }
                }
            }
        }

        return neighbors;
    }

    /**
     * Returns the set of nodes that may be go through following only a specific set of paths, along with their occurrences
     *
     * @param featuredPaths the set of paths to consider
     * @return the set of nodes at most at the given distance with their occurrences
     */
    public HashMap<Node, Integer> collectNeighborsFreq(Set<Path> featuredPaths){

        return (HashMap<Node, Integer>) featuredPaths.parallelStream()
                .map(this::collectPathNodesFreq)
                .flatMap(m -> m.entrySet().stream())
                .collect(groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));

    }

    /**
     * Finds paths between this node and another one
     *
     * @param node the node to find paths towards
     * @param maxDepth search max depth
     * @return the set of path connecting the two nodes
     */
    //TODO: this is just a really quick and inefficient implementation
    public ArrayList<Path> findPaths(Node node, int maxDepth){

        ArrayList<Path> itemsPaths = new ArrayList<>();

        itemsPaths.addAll(
                getArrows().entrySet().parallelStream()
                        .filter(e -> e.getValue().contains(node))
                        .map(e -> graphRef.createPath(e.getKey()))
                        .collect(Collectors.toList())
        );
        if(maxDepth > 1) {
            for (Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()) {
                HashSet<Node> neighbors = e.getValue();
                //restart the algorithm using n as the first node and subPath as the exploration path
                for (Node n : neighbors) {
                    ArrayList<Path> paths = n.findPaths(node, maxDepth - 1);
                    for (Path p : paths) {
                        p.getArrowsList().addFirst(e.getKey());
                    }
                    itemsPaths.addAll(paths);
                }
            }

        }
        return itemsPaths;
    }

    public ArrayList<Path> findPathsOld(Node node, int maxDepth){
        ArrayList<Path> itemsPaths = new ArrayList<>();
        if(maxDepth == 1) {
            itemsPaths.addAll(
                    getArrows().entrySet().parallelStream()
                            .filter(e -> e.getValue().contains(node))
                            .map(e -> graphRef.createPath(e.getKey()))
                            .collect(Collectors.toList()));
        }else{
            for(Map.Entry<Arrow, HashSet<Node>> e : getArrows().entrySet()){
                HashSet<Node> neighbors = e.getValue();
                //restart the algorithm using n as the first node and subPath as the exploration path
                for(Node n : neighbors){
                    ArrayList<Path> paths = n.findPaths(node, maxDepth - 1);
                    for(Path p : paths){
                        p.getArrowsList().addFirst(e.getKey());
                    }
                    itemsPaths.addAll(paths);
                }
            }

        }
        return itemsPaths;
    }
    /**
     * Implement a breadth search to find the top-k shortest paths between this node and another one
     *
     * @param destination the node to find paths towards
     * @param k the maximum number of shortest paths retrievable
     * @param maxDepth search max depth
     * @return a list of paths connecting the two nodes
     */
    protected List<Path> findTopKShortestPaths(Node destination, int k, int maxDepth){
        ArrayList<Path> shortestPaths = new ArrayList<>();
        if(maxDepth <= 0 || k <= 0)
            return shortestPaths;

        Set<Node> alreadyExplored = new HashSet<>();
        alreadyExplored.add(this);
        alreadyExplored.add(destination);

        ArrayDeque<Exploration> deque = new ArrayDeque<>();
        getArrows().entrySet().stream().forEach(e ->
            deque.add(new Exploration(graphRef.createPath(e.getKey()), e.getValue())));

        int depth = 1;
        while (shortestPaths.size() < k && depth <= maxDepth && deque.size() > 0){
            Exploration expl = deque.pop();
            if(expl.getNodes().contains(destination)) {
                shortestPaths.add(expl.getPath());
            }
           deque.addAll(expl.expand(alreadyExplored));

           alreadyExplored.addAll(expl.getNodes());

           depth = expl.getPath().length();
        }
        if(shortestPaths.size() < k)
            return  shortestPaths;

        shortestPaths.sort((p1, p2) -> p1.length().compareTo(p2.length()));

        return shortestPaths.subList(0, k);

    }
    protected HashMap<Node, List<Path>> findTopKShortestPaths(Collection<Node> destinations, int k, int maxDepth){

        HashMap<Node, List<Path>> shortestPaths = new HashMap<>();
        destinations.stream().forEach(d -> shortestPaths.put(d, new ArrayList<>()));
//      ArrayList<Path> shortestPaths = new ArrayList<>();
        if(maxDepth <= 0 || k <= 0 )
            return shortestPaths;

        Set<Node> alreadyExplored = new HashSet<>();
        alreadyExplored.add(this);

        ArrayDeque<Exploration> deque = new ArrayDeque<>();
        getArrows().entrySet().stream().forEach(e ->
                deque.add(new Exploration(graphRef.createPath(e.getKey()), e.getValue())));

        int depth = 1;
        while (depth <= maxDepth && deque.size() > 0){
            Exploration expl = deque.pop();
            expl.getNodes().stream().filter(destinations::contains).forEach(n -> shortestPaths.get(n).add(expl.getPath()));

            deque.addAll(expl.expand(alreadyExplored));;
            alreadyExplored.addAll(expl.getNodes());

            depth = expl.getPath().length();
        }
        shortestPaths.values()
                .forEach(s -> s.sort((p1, p2) -> p1.length().compareTo(p2.length())));
        shortestPaths.values().forEach(v -> v.retainAll(v.subList(0, k -1)));

        return shortestPaths;

    }


/*    private ArrayList<Path> depthSearch(Arrow arrow, HashSet<Node> nodes, int maxDepth, Node destination) {
        Path path = new Path();
        path.add(arrow);
        ArrayList<Path>  paths = new ArrayList<>();
        if(nodes.contains(destination))
            paths.add(path);

        nodes.stream().filter(n -> !n.equals(destination)).map(n -> depthSearch(path.clone(), n.getArrows(),  ));
    }

    private ArrayList<Path> depthSearch(Path path, HashSet<Node> value, int n, int maxDepth, Node destination) {
        Path path = new Path();
        path.add(arrow);


    }*/

    @Override
    public String toString() {
        return this.getId();
    }
}
