package it.poliba.sisinflab.simlib.datamodel.pathmodel;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.Path;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Extends the {@link Node} class supporting a {@link Path} based data model
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class PathModelNode extends Node{

	/**
	 * Each key is an {@link Path} object. For each path there's a set of nodes which are linked
	 * to the current node through this path. i.e.
	 * <ul>
	 *     <li>dbr:Star_Wars_(film) dct:subject--skos:broader dbc:Films_by_American_directors</li>
	 *     <li>dbr:Star_Wars_(film) dct:subject--skos:broader dbc:20th_Century_Fox</li>
	 * </ul>
	 * dbr:Star_Wars_(film) will have an entry with key dct:subject--skos:broader (as a {@link Path}
	 * object which arrows have an outgoing direction) and as value the {@link HashSet} filled with
	 * dbc:Films_by_American_directors and dbc:20th_Century_Fox
	 **/
	private HashMap<Path, HashSet<Node>> paths;

	public PathModelNode(String id, PathGraph graphRef) {
		super(id, graphRef);
		this.paths = new HashMap<>();
	}

	public HashMap<Path, HashSet<Node>> getPaths() {
		return paths;
	}

	public void setPaths(HashMap<Path, HashSet<Node>> paths) {
		this.paths = paths;
	}

	public HashMap<Path, HashSet<Node>> getPaths(String direction){
		return (HashMap<Path, HashSet<Node>>) paths.entrySet().stream()
				.filter(p -> p.getKey().getType().equals(direction))
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public void addPath(Path path, PathModelNode n){
		if (!paths.containsKey(path)) {
			paths.put(path, new HashSet<>());
		}
		HashSet<Node> a = paths.get(path);
		a.add(n);
	}

	public int getPathsCount(String direction){

		HashMap<Path, HashSet<Node>> dirPaths = getPaths(direction);

		return dirPaths.entrySet().stream()
				.collect(summingInt(e -> e.getValue().size()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasPath(Path path){
		return getPaths().containsKey(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashSet<Node> getNeighbors(Path path) {
		return getPaths().get(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashSet<Node> getNeighbors(int distance, String direction){

		//it will filter all the nodes paths keeping only the ones that have length == distance
		//and ONLY if direction != DIR_UND, it will filter also on the required direction, considering the overall
		//path direction
		return (HashSet<Node>) paths.entrySet().parallelStream()
				.filter(e -> e.getKey().getArrowsList().size() == distance && (direction.equals(Path.DIR_UND) || e.getKey().getType().equals(direction)))
				.map(Map.Entry::getValue)
				.flatMap(Set::stream)
				.collect(toSet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashSet<Node> getNeighbors(Set<Path> featuredPaths){

		return (HashSet<Node>) paths.entrySet().parallelStream()
				.filter(e -> featuredPaths.contains(e.getKey()))
				.map(Map.Entry::getValue)
				.flatMap(Set::stream)
				.collect(toSet());
	}


}
