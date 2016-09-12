package it.poliba.sisinflab.simlib;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.output.OutputWriter;
import it.poliba.sisinflab.simlib.relatedness.KatzRelatedness;

import java.io.File;
import java.util.*;

/**
 *  @author Paolo Tomeo
 */
public class MainTestKatz {

    public static String target_folder ;
    // OUTPUT
    public static String  output_file ;

    public static void main(String[] args) throws Exception {

        target_folder = "data/movielens/";

        output_file = target_folder + "katz_sim";

        List<String> graphFiles = new ArrayList<>();
        graphFiles.add(target_folder + "graph_wikidata.txt");

        OutputWriter w = new OutputWriter( output_file, true);

        //<ID, URI>
        Map<String, String> itemsMapping = GraphFactory.readMappingItems(new File(target_folder + "mapping2wikidata.tsv"));

        Graph graph = GraphFactory.create(graphFiles, GraphFactory.TRIPLE_GRAPH);
        KatzRelatedness katz = new KatzRelatedness(graph,  2, 4, 0.75 );

        Map<String, Node> targets = graph.getNodes(itemsMapping.values());

        itemsMapping.entrySet()
                .parallelStream().
                forEach(e -> {
                    System.out.println(e);
                    Map<Node, Double> map = katz.computeRelatednessRank(e.getValue(), targets);
                    w.writeValues(e.getKey(), map);
                });
    }
}
