package it.poliba.sisinflab.simlib.similarity;


import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.datamodel.*;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.input.StatementFileReader;
import it.poliba.sisinflab.simlib.datamodel.pathmodel.PathGraph;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Implementation of the vector space model for knowledge graphs, in order to compute similarity values between items
 * <p>
     * (cfr. T. Di Noia, R.Mirizzi, V.C. Ostuni, D. Romito, M.Zanker
     * <a href="http://sisinflab.poliba.it/publications/2012/DMORZ12/">Linked Open Data to support Content-based
     * Recommender Systems</a>, 8th International Conference on Semantic Systems (I-SEMANTICS 2012), 2012)
 * </p>
 * @author Giorgio Basile
 * @since 1.0
 */
public class VectorSpaceModel extends SimilarityMetric{

    private Map<Path, Map<Node, Integer>> pathsObjects;
    private Map<Path, Double> pathsWeights;
    private Set<Path> paths;
    private double weightSum;


    public VectorSpaceModel(Graph graph, Set<Path> featuredPaths) throws InvalidGraphException, IOException{
        super(graph);
        this.paths = featuredPaths;
        this.pathsObjects = graph.getPathsObjects(paths);
        this.pathsWeights = defaultPathsWeights();
        this.weightSum = pathsWeights.entrySet().parallelStream()
                .collect(summingDouble(Map.Entry::getValue));
    }

    public VectorSpaceModel(Graph graph, HashMap<Path, Double> pathsWeights) throws InvalidGraphException, IOException{
        super(graph);

        this.paths = pathsWeights.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(toSet());


        this.pathsObjects = graph.getPathsObjects(this.paths);
        this.pathsWeights = pathsWeights;
    }


    /**
     * Computes weights for each path, in order to compute a similarity value between two items as a weighted mean
     * between the similarities of the paths they have in common
     * TODO: by now it just uses 1 as weight for each property
     *
     * @return the map which entries are the couples path-weight
     */
    private Map<Path, Double> defaultPathsWeights() {
        Map<Path, Double> propertiesWeights = new HashMap<>();

        for (Path path : paths) {
            propertiesWeights.put(path, 1D);
        }

        return propertiesWeights;
    }


    /**
     * Computes the weight vector for a given item, with respect to a specific path
     *
     * @param item a given item
     * @param path a given path
     * @return the weights vector of the item for the specific path
     */
    private double[] buildWeightVector(Node item, Path path) {
        Map<Node, Integer> pathObjects = pathsObjects.get(path);
        int numItems = graph.getItems().size();
        /*
        * One-step transitivity: suppose to have the following RDF statements:
        * dbpedia:Righteous_Kill dcterms:subject dbpedia:Category:Serial_killer_films .
        * dbpedia:Category:Serial_killer_films skos:broader dbpedia:Category:Crime_films .
        *
        * Starting from dbpedia:Category:Serial_killer_films we have that dbpedia:Category:Crime_films is at
        * a distance of one step. Hence, by considering a one-step transitivity, we have that:
        * dbpedia:Righteous_Kill dcterms:subject dbpedia:Category:Crime_films
        **/
        HashSet<Node> nodeSet = new HashSet<>();
        Path subPath = graph.createPath(path.getArrowsList());
        while(subPath.getArrowsList().size() > 0){
            nodeSet.addAll(item.getNeighbors(subPath));
            subPath.getArrowsList().removeLast();
        }
        /*
         * tf: frequency of the node n as the object of an RDF triple having p as property and the node i as
         * subject (the movie). Actually, this term can be either 0 (if i is not related to n via p) or 1,
         * since two identical triples can not coexist in an RDF graph.
         *
         * idf: the logarithm of the ratio between M, that is the total number of movies in the collection,
         * and the number of movies that are linked to the resource n, by means of the predicate p
         *
         * weight(path, object) = tf * idf;
         *
         * weight(path) = [weight(path, obj1, weight(path, obj2), .....]
         * */

        return pathObjects.entrySet().parallelStream()
                .mapToDouble(e -> nodeSet.contains(e.getKey()) ? (Math.log(numItems / (double) e.getValue())) : 0D)
                .toArray();
        /*double[] w = new double[pathObjects.size()];
        int i = 0;
        for(Map.Entry<Node, Integer> e : pathObjects.entrySet()){
            w[i] = nodeSet.contains(e.getKey()) ? (Math.log(numItems / (double) e.getValue())) : 0D;
            i++;
        }
        return w;*/

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public double computePathSimilarity(Node item1, Node item2, Path path) {
        //long startTime = System.currentTimeMillis();

        //stream implementation doesn't seem to speed up the computation
        /*List<Node> items = Arrays.asList(item1, item2);
        List<double[]> vectors = items.parallelStream().map(i -> buildWeightVector(i, path, pathsObjects.get(path)))
                            .collect(toList());*/

        double[] weight1 = buildWeightVector(item1, path);
        double[] weight2 = buildWeightVector(item2, path);

        /*long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Execution time prop: " + elapsedTime);*/

        return Utilities.cosineSimilarity(weight1, weight2);
        //return Utilities.cosineSimilarity(vectors.get(0), vectors.get(1));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double computeSimilarity(Node item1, Node item2) {
        double sim = 0;
        for (Path path : paths) {
            Arrow firstArrow = path.getArrowsList().getFirst();

            if (item1.hasArrow(firstArrow) && item2.hasArrow(firstArrow)) {
                sim += pathsWeights.get(path) * computePathSimilarity(item1, item2, path);
            }
        }
        if(weightSum != 0) {
            sim /= weightSum;
        }
        //using few paths, this is super slow than the Collectors implementation
        /*sim = paths.parallelStream().filter(path -> { Arrow firstArrow = path.getArrowsList().getFirst();
            return item1.hasArrow(firstArrow) && item2.hasArrow(firstArrow);
        }).collect(summingDouble(p -> pathsWeights.get(p) * computePathSimilarity(item1, item2, p)));

        if(weightSum != 0)
            sim /= weightSum;*/


        return sim;
    }
}
