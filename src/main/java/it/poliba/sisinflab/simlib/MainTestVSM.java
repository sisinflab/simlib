package it.poliba.sisinflab.simlib;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.Path;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.similarity.SimilarityMetric;
import it.poliba.sisinflab.simlib.similarity.VectorSpaceModel;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * VectorSpaceModel test application
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class MainTestVSM {

    public static final String METADATA = "./data/movies/wikidata/graph_wikidata.txt";
    public static final String ITEMS = "./data/movies/wikidata/mapping_wikidata.txt";
    public static final String FEATURED_PATHS = "./data/movies/wikidata/props_wikidata.txt";

    public static void main(String[] args) {
        try {
            File graphFile = new File(METADATA);
            File itemsFile = new File(ITEMS);

            List<String> itemsIDs = GraphFactory.readItems(itemsFile);

            Graph graph = GraphFactory.create(Arrays.asList(METADATA), itemsIDs, GraphFactory.TRIPLE_GRAPH);

            Set<Path> featuredPaths = GraphFactory.readPaths(new File(FEATURED_PATHS), graph);
            System.out.println();

            System.out.println("Computing similarities for item: " + "Saw_II -> http://www.wikidata.org/entity/Q270410" + "...");

            SimilarityMetric vsm = new VectorSpaceModel(graph, featuredPaths);

            long startTime = System.currentTimeMillis();

            Map<Node, Double> map = vsm.computeSimilarityRank("http://www.wikidata.org/entity/Q270410");
            //vsm.computeAllSimilarities();
            //System.out.println(vsm.computeSimilarity("http://dbpedia.org/resource/Saw_II", "http://dbpedia.org/resource/Saw_III"));


            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Execution time item: " + elapsedTime / 1000D);

            Utilities.printTopNWikidata(map, 20);

        }catch (IOException | InvalidGraphException e){
            e.printStackTrace();
        }
    }



}
