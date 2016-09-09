package it.poliba.sisinflab.simlib;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.output.OutputWriter;
import it.poliba.sisinflab.simlib.relatedness.KatzRelatedness;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tomeo on 20/05/16.
 */
public class MainTestKatz {

    public static final String book_folder = "./data/facebook/book/dbpedia/";
    public static final String movie_folder = "./data/facebook/movie/dbpedia/";
    public static final String music_folder = "./data/facebook/music/dbpedia/";

//    public static final String METADATA = working_folder + "graph";
//    public static final String ITEMS = working_folder + "mapping_reduced";

    //CROSS-DOMAIN

    public static String source_folder ;
    public static String target_folder ;
    // OUTPUT
    public static String  output_file ;

    public static void main(String[] args) throws Exception {
        Map<String, String> domains = new HashMap<>();
        domains.put("book", book_folder);
        domains.put("movie", movie_folder);
        domains.put("music", music_folder);

        source_folder = domains.get(args[0]);
        target_folder = domains.get(args[1]);

        output_file = "./data/facebook/dbpedia_two_hops/" + args[0] + "_" + args[1] + "_katz";



        List<String> graphFiles = new ArrayList<>();
        graphFiles.add(target_folder + "graph");
        graphFiles.add(source_folder + "graph");

        OutputWriter w = new OutputWriter( output_file, true);

        Set<String> alreadyComputed = readAlreadyComputed(output_file);

        //<ID, URI>
        Map<String, String> sources_items = GraphFactory.readMappingItems(new File(source_folder + "mapping_reduced"));
        Map<String, String> itemsMapping = GraphFactory.readMappingItems(new File(target_folder + "mapping_reduced"));
        //<URI, ID>
        Map<String, String> itemsMappingINVERSE = GraphFactory.readInverseMappingItems(new File(target_folder + "mapping_reduced"));

        Graph graph = GraphFactory.create(graphFiles, GraphFactory.TRIPLE_GRAPH);
        KatzRelatedness katz = new KatzRelatedness(graph, 3 , 4, 0.75 );

        Map<String, Node> targets = graph.getNodes(itemsMapping.values());

        sources_items.entrySet()
                .parallelStream()
//                .stream()
                .filter(e -> graph.contains(e.getValue()))
                .filter(e -> !alreadyComputed.contains(e))
                .forEach( e -> {
                    System.out.println(e.getValue());
                    Map<Node, Double> map = katz.computeRelatednessRank(e.getValue(), targets);
                    w.writeValues(e.getKey(), map, itemsMappingINVERSE);
                    //Utilities.printTopN(map, 50);
                });
        w.close();
    }

    private static Set<String> readAlreadyComputed(String output_file) {
        Set<String> ids = new HashSet<>();
        try {
            ids = Files.lines(Paths.get(output_file), Charset.defaultCharset())
                    .map(l -> l.split("\t")[0])
            .collect(Collectors.toSet())
                    ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ids;
    }


    public static void  main_(String[] args) throws Exception {
        try {
            Map<String, String> domains = new HashMap<>();
            domains.put("book", book_folder);
            domains.put("movie", movie_folder);
            domains.put("music", music_folder);

            source_folder = domains.get(args[0]);
            target_folder = domains.get(args[1]);

            List<String> graphFiles = new ArrayList<>();
            graphFiles.add(target_folder + "graph");
            graphFiles.add(source_folder + "graph");

            Graph graph = GraphFactory.create(graphFiles, GraphFactory.TRIPLE_GRAPH);


            Map<String, String> itemsMapping = GraphFactory.readMappingItems(new File(target_folder + "mapping_reduced"));

            Map<String, Node> targets = graph.getNodes(itemsMapping.values());


            System.out.println();

            KatzRelatedness katz = new KatzRelatedness(graph,  10 , 4, 0.75  );

            long startTime = System.currentTimeMillis();

//            System.out.println(katz.computeRelatedness( "http://www.wikidata.org/entity/Q2306", "http://www.wikidata.org/entity/Q492029"));
            Map<Node, Double> map = katz.computeRelatednessRank("http://dbpedia.org/resource/1Q84", targets);

//            Utilities.printTopNWikidata(map, 50);
            Utilities.printTopN(map, 50);

//            System.out.println( katz.computeRelatedness("http://www.wikidata.org/entity/Q580361", "http://www.wikidata.org/entity/Q748030"));


            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Execution time item: " + elapsedTime / 1000D);

        }catch (IOException | InvalidGraphException e){
            e.printStackTrace();
        }
    }

}
