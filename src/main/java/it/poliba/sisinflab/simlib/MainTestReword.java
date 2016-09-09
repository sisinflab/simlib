package it.poliba.sisinflab.simlib;

import it.poliba.sisinflab.simlib.datamodel.*;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.input.StatementFileReader;
import it.poliba.sisinflab.simlib.relatedness.KatzRelatedness;
import it.poliba.sisinflab.simlib.relatedness.RelatednessMeasure;
import it.poliba.sisinflab.simlib.relatedness.Reword;
import org.apache.jena.base.Sys;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * VectorSpaceModel test application
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class MainTestReword {

    public static final String METADATA = "data/kore/graph_wikidata.txt";
    public static final String ITEMS = "data/kore/mapping_wikidata.txt";
    public static final String FEATURED_PATHS = "data/movies/featuredPaths.txt";
    public static final String FEATURED_PROPS = "data/kore/props_wikidata.txt";

    /*public static final String METADATA = "data/wikidata_music/graphs/graph_wikidata_full.txt";
    public static final String ITEMS = "data/wikidata_music/mappings/mapping_wikidata_composition.txt";
    public static final String FEATURED_PROPS = "data/wikidata_music/properties/properties_wikidata_full.txt";*/

    public static void main(String[] args) {
        try {
            File graphFile = new File(METADATA);
            File itemsFile = new File(ITEMS);

            List<String> itemsIDs = GraphFactory.readItems(itemsFile);
            Graph graph = GraphFactory.create(Arrays.asList(METADATA), itemsIDs, GraphFactory.TRIPLE_GRAPH);

            System.out.println();

            RelatednessMeasure reword = new Reword(graph);
            //Reword reword = new Reword(graphFile, itemsIDs, featuredProps, StatementFileReader.TAB_TRIPLES);

            long startTime = System.currentTimeMillis();

            Map<Node, Double> map = computeRelatednessRank(reword, "http://www.wikidata.org/entity/Q312"); //APPLE
            //Map<Node, Double> map = computeRelatednessRank(reword, "http://www.wikidata.org/entity/Q95"); //GOOGLE
            //Map<Node, Double> map = computeRelatednessRank(reword, "http://www.wikidata.org/entity/Q380"); //FACEBOOK
            //Map<Node, Double> map = reword.computeRelatednessRank("http://www.wikidata.org/entity/Q15862");
            //System.out.println(reword.computeRelatedness("http://www.wikidata.org/entity/Q15862", "http://www.wikidata.org/entity/Q187745"));
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Execution time item: " + elapsedTime / 1000D);

            Utilities.printTopNWikidata(map, 500);

        }catch (IOException | InvalidGraphException e){
            e.printStackTrace();
        }
    }

    public static Map<Node, Double> computeRelatednessRank(RelatednessMeasure rw, String itemID) {
        Node item1 = rw.getGraph().getNode(itemID);

        List<Node> facebookNodes = new ArrayList<>();
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q36215"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q370217"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q328929"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q185888"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q705525"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q40629"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q163820"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q13371"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q219523"));
        //facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q78850"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q3066455"));
        //facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q5190099"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q43432"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q239672"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q8811"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q169889"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q1934568"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q22905"));
        facebookNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q16538"));



        List<Node> googleNodes = new ArrayList<>();
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q4934"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q92764"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q94"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q65344"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q486860"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q182496"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q41506"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q37093"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q92743"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q388"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q312"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q52618"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q10134"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q585329"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q956568"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q4911497"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q121194"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q60"));
        googleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q37230"));



        List<Node> appleNodes = new ArrayList<>();
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q19837"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q483382"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q164750"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q308869"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q369783"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q6994646"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q163820"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q308993"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q35773"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q1982831"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q507469"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q2283"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q56005"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q935"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q7251"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q460173"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q81307"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q746052"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q6499202"));
        appleNodes.add(rw.getGraph().getNode("http://www.wikidata.org/entity/Q44294"));

        HashSet<Node> fullNodes = new HashSet<>();
        fullNodes.addAll(googleNodes);
        fullNodes.addAll(appleNodes);
        fullNodes.addAll(facebookNodes);

        Map<Node, Double> rank =
                appleNodes.parallelStream()
                        .filter(e -> !e.equals(item1))
                        .collect(Collectors.toMap(
                                n -> n,
                                n -> rw.computeRelatedness(item1, n)
                        ));

        return Utilities.sortByValues(rank);
    }

}
