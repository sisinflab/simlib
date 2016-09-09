package it.poliba.sisinflab.simlib.datamodel;

import it.poliba.sisinflab.simlib.datamodel.abstat.AbstatGraph;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.input.Statement;
import it.poliba.sisinflab.simlib.input.triplefile.TabTriplesFileReader;
import it.poliba.sisinflab.simlib.datamodel.pathmodel.PathGraph;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Creates {@link Graph} objects based on some given parameters. It contains only static methods.
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class GraphFactory {

    public static final String TRIPLE_GRAPH = "triple_graph";
    public static final String PATH_GRAPH = "path_graph";
    public static final String ABSTAT_GRAPH = "asbtat_graph";

    /**
     * Creates a Graph object that represents the structure described in the input file, using the proper
     * data representation through the graphType value
     *
     * @param graphFiles list of files containing the graph statements
     * @param graphType specifies the proper data model
     * @return the resulting {@link Graph} object
     * @throws IOException if the inputFile doesn't exist
     * @throws InvalidGraphException if the graphType is unknown
     */
    public static Graph create(List<String> graphFiles, String graphType) throws IOException, InvalidGraphException {

        Graph graph;
        if(graphType.equals(TRIPLE_GRAPH)){
            List<Statement> triples = new ArrayList<>();
            graphFiles.forEach(f -> triples.addAll(TabTriplesFileReader.readTriples(f)));

            //building graph from triples list
            graph = new Graph();
            graph.build(triples);

        }else if(graphType.equals(PATH_GRAPH)){
            List<Statement> triples = new ArrayList<>();
            graphFiles.forEach(f -> triples.addAll(TabTriplesFileReader.readTriples(f)));

            //building graph from path list
            graph = new PathGraph();
            graph.build(triples);

        }else
            throw new InvalidGraphException("Unknown graph type");

        return graph;
    }

    /**
     * Creates a Graph object that represents the structure described in the input file, using the proper
     * data representation through the graphType value
     *
     * @param graphFiles list of files containing the graph statements
     * @param itemsIDs list containing the items IDs
     * @param graphType specifies the proper data model
     * @return the resulting {@link Graph} object
     * @throws IOException if the inputFile doesn't exist
     * @throws InvalidGraphException if the graphType is unknown
     */
    public static Graph create(List<String> graphFiles, List<String> itemsIDs, String graphType) throws IOException, InvalidGraphException {
        Graph graph = create(graphFiles, graphType);
        graph.markItems(itemsIDs);
        return graph;
    }

    public static Graph create(String graphFile, List<String> itemsIDs, String graphType) throws IOException, InvalidGraphException {
        ArrayList<String> files = new ArrayList<>();
        files.add(graphFile);
        Graph graph = create(files, graphType);
        graph.markItems(itemsIDs);
        return graph;
    }

    /**
     * Creates an ABSTAT graph object
     *
     * @param graphURI URI of the graph to explore
     * @return a AbstatGraph object
     */
    public static AbstatGraph createAbstatGraph(String graphURI){
        AbstatGraph graph = new AbstatGraph(graphURI);
        return graph;
    }
    public static Set<Path> readPaths(File inputFile, Graph graph) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.defaultCharset())
                .map(l -> graph.createPath(l.split("--")))
                .collect(toSet());
    }

    public static List<String> readItems(File inputFile) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.forName("UTF-8"))
                .map(l -> l.split("\t"))
                .map(f -> f[1])
                .collect(toList());
    }

    /**
     * Returns inverse mapping: <URI, ID>
     */
    public static Map<String,String> readInverseMappingItems(File inputFile) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.forName("UTF-8"))
                .map(l -> l.split("\t"))
                .collect(Collectors.toMap(e -> e[1], e -> e[0]));
    }
    /**
     * Returns mapping: <ID, URI>
     */
    public static Map<String,String> readMappingItems(File inputFile) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.forName("UTF-8"))
                .map(l -> l.split("\t"))
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
    }
}
