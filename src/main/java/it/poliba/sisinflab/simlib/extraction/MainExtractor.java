package it.poliba.sisinflab.simlib.extraction;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Knowledge Graph extractor test application
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class MainExtractor {

    public static final String DBPEDIA_DEFAULT_ENDPOINT_AZURE = "http://iceman2.cloudapp.net:10028/sparql";   //dbpedia
    public static final String WIKIDATA_DEFAULT_ENDPOINT_AZURE = "http://colossus2.cloudapp.net:10026/ds/sparql";   //wikidata
    public static final String BASEKB_ENDPOINT_AZURE = "http://archangel2.cloudapp.net:10026/ds/sparql";   //basekb


    public static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";
    public static final String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";

    public static final String ENDPOINT = DBPEDIA_DEFAULT_ENDPOINT_AZURE;

    public static String working_folder = "./data/facebook/music/dbpedia/";

    public static void main(String[] args) throws IOException {
        working_folder = args[0];
        File itemsFile = new File(working_folder + "mapping_reduced");

        File outputFile = new File(working_folder + "graph");

        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();

        Extractor ext = new Extractor(ENDPOINT);

//          File propsFile = new File(working_folder + "props_wikiproject");
//          List<String> triples = ext.startExtraction(readItems(itemsFile), readPaths(propsFile));

        List<String> triples = ext.startExtraction(readItems(itemsFile), 2);
//          System.out.println(triples);

        write(outputFile, triples);

        System.out.println("Extraction finished");

    }

    private static List<LinkedList<String>> readPaths(File inputFile) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.forName("UTF-8"))
                .map(l -> l.split("\t")[0])
                .map(l -> new LinkedList<>(Arrays.asList(l.split("--"))))
                .collect(toList());
    }

    private static List<String> readItems(File inputFile) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.forName("UTF-8"))
                .map(l -> l.split("\t"))
                .map(f -> f[1])
                .collect(toList());
    }

    private static void write(File file, List<String> triples) throws IOException {

        HashMap<String, HashMap<String, HashSet<String>>> struct = new HashMap<>();

        for(String triple : triples){
            String fields[] = triple.split("\t");
            String subject = fields[0];
            String predicate = fields[1];
            String object = fields[2];
            if(!struct.containsKey(subject)){
                struct.put(subject, new HashMap<>());
            }
            if(!struct.get(subject).containsKey(predicate)){
                struct.get(subject).put(predicate, new HashSet<>());
            }
            struct.get(subject).get(predicate).add(object);

        }

        BufferedWriter w = new BufferedWriter(new FileWriter(file));
        for(Map.Entry<String, HashMap<String, HashSet<String>>> e : struct.entrySet()){
            String subject = e.getKey();
            for(Map.Entry<String, HashSet<String>> e1 : e.getValue().entrySet()){
                String predicate = e1.getKey();
                String line = subject + "\t" + predicate + "\t" + "[";
                for(String object : e1.getValue()){
                    line += object + ", ";
                }
                line = line.substring(0, line.length() - 2) + "]";
                w.write(line);
                w.newLine();
            }

        }
        w.close();
    }
}
