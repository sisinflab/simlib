package it.poliba.sisinflab.simlib;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.AbstatService;
import it.poliba.sisinflab.simlib.relatedness.KatzRelatedness;
import it.poliba.sisinflab.simlib.relatedness.Reword;

import java.util.HashSet;

/**
 * Created by giorgio on 12/05/16.
 */
public class MainAbstat {

    public static void main(String[] args){
        Graph graph = GraphFactory.createAbstatGraph(AbstatService.DBPEDIA_INFO_2015_10);

        Reword rw = new Reword(graph);
        KatzRelatedness kr = new KatzRelatedness(graph);
        long startTime = System.currentTimeMillis();

        System.out.println(rw.computeRelatedness("http://ld-summaries.org/resource/dbpedia-2015-10-infobox/dbpedia.org/ontology/Software",
                "http://ld-summaries.org/resource/dbpedia-2015-10-infobox/dbpedia.org/ontology/Person"));

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Execution time item: " + elapsedTime / 1000D);
    }

}
