package it.poliba.sisinflab.simlib;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.Path;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.input.StatementFileReader;
import it.poliba.sisinflab.simlib.similarity.JaccardSimilarityMetric;
import it.poliba.sisinflab.simlib.similarity.SimilarityMetric;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * JaccardSimilarity test application
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class MainTestJacc {

    public static final String METADATA = "data/movies/metadata.txt";
    public static final String ITEMS = "data/movies/mapping_completo";
    public static final String FEATURED_PATHS = "data/movies/featuredPaths.txt";


    public static void main(String[] args) {
        try {
            int distance = 2;

            File graphFile = new File(METADATA);
            File itemsFile = new File(ITEMS);

            List<String> itemsIDs = GraphFactory.readItems(itemsFile);
            Graph graph = GraphFactory.create(Arrays.asList(METADATA), itemsIDs, GraphFactory.TRIPLE_GRAPH);
            HashSet<Path> featuredPaths = new HashSet<>(GraphFactory.readPaths(new File(FEATURED_PATHS), graph));

            System.out.println();

            SimilarityMetric jaccardSimilarity = new JaccardSimilarityMetric(graph, featuredPaths);

            long startTime = System.currentTimeMillis();

            System.out.println("Computing similarity rank on Saw_II...");
            Map< Node, Double> map = jaccardSimilarity.computeSimilarityRank("http://dbpedia.org/resource/Saw_II");

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Execution time item: " + elapsedTime / 1000D);

            Utilities.printTopN(map, 20);

            //System.out.println("Most similar item: " + map.entrySet().iterator().next().getKey().getId() + ", value: " + map.entrySet().iterator().next().getValue());

        }catch (IOException | InvalidGraphException e){
            e.printStackTrace();
        }
    }

}
