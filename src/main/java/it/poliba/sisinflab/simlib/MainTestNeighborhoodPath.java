package it.poliba.sisinflab.simlib;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.Path;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.input.StatementFileReader;
import it.poliba.sisinflab.simlib.neighborhood.KernelMetric;
import it.poliba.sisinflab.simlib.neighborhood.pathbaseditem.NeighborhoodPathKernelMetric;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author Corrado on 05/04/2016.
 */
public class MainTestNeighborhoodPath {
    
    //public static final String METADATA = "data/movies/metadata.txt";
    public static final String METADATA2 = "data/graph_wikidata.txt";
    public static final String ITEMS2 = "data/mapping_wikidata.txt";
    //public static final String ITEMS = "data/movies/mapping_completo";
    //public static final String FEATURED_PATHS = "data/movies/featuredPaths.txt";
    
    public static final String METADATA = "./data/movies/graph_dbpedia_film_ABSTAT50featuresOP_utf8.txt";
    public static final String ITEMS = "./data/movies/mapping_completo";
    public static final String FEATURED_PATHS = "./data/movies/featuredPaths.txt";

    public static void main(String[] args) {
        try {
            // Creating a File object that represents the disk file.
            //PrintStream o = new PrintStream(new File("Output.txt"));
            //System.setOut(o);
            
            int distance = 3;

            File graphFile = new File(METADATA);
            File itemsFile = new File(ITEMS);
            
            List<String> itemsIDs = readItems(itemsFile);
            Graph graph = GraphFactory.create(Arrays.asList(METADATA), itemsIDs, GraphFactory.TRIPLE_GRAPH);
            HashSet<Path> featuredPaths = new HashSet<>(readPaths(new File(FEATURED_PATHS), graph));
            
            System.out.println();

            long startTime = System.currentTimeMillis();
  
            KernelMetric neighborhoodpathKernel = new NeighborhoodPathKernelMetric(graph,distance, StatementFileReader.TAB_TRIPLES);
            
            Map<Node, Double> map = neighborhoodpathKernel.computeKernelRank("http://dbpedia.org/resource/Saw_II");
            List<String> ranking = new ArrayList<>();
            
            int n = 20;
            System.out.println("First " + n);

            int i = 0;
            Iterator<Map.Entry<Node, Double>> iter = map.entrySet().iterator();
            while(iter.hasNext() && i < n){
                Map.Entry<Node, Double> e = iter.next();
                String[] name = e.getKey().getId().split("/");
                int l = name.length;
                System.out.println((i + 1) + ". " + name[l-1] + " " + e.getValue());
                ranking.add((i + 1) + ". " + name[l-1] + " " + e.getValue());
                i++;
            }
            
            java.nio.file.Path file2 = Paths.get("data/GraphPathRankingDist3.txt");
            Files.write(file2, ranking, Charset.forName("UTF-8"));
            
            //System.out.println("Kernel(Saw_II,Saw_IV) = "+ neighborhoodpathKernel.computeKernel("http://dbpedia.org/resource/Saw_II", "http://dbpedia.org/resource/Saw_IV"));
            //System.out.println("Kernel(Saw_3D,Saw_II) = "+ neighborhoodpathKernel.computeKernel("http://dbpedia.org/resource/Saw_3D", "http://dbpedia.org/resource/Saw_II"));
            //System.out.println("Kernel(Saw_3D,Saw_VI) = "+ neighborhoodpathKernel.computeKernel("http://dbpedia.org/resource/Saw_3D", "http://dbpedia.org/resource/Saw_VI"));
            //System.out.println("Kernel(Saw_3D,Major_League_(film)) = "+ neighborhoodpathKernel.computeKernel("http://dbpedia.org/resource/Saw_3D","http://dbpedia.org/resource/Major_League_(film)"));
            //System.out.println("Kernel(The Sting,The Program) = "+ neighborhoodpathKernel.computeKernel("http://dbpedia.org/resource/The_Sting","http://dbpedia.org/resource/The_Program"));
            //System.out.println("Kernel(The Sting,Butch_Cassidy) = "+ neighborhoodpathKernel.computeKernel("http://dbpedia.org/resource/The_Sting","http://dbpedia.org/resource/Butch_Cassidy_and_the_Sundance_Kid"));
            //System.out.println("Kernel(FaF 5,FaF) = "+ neighborhoodpathKernel.computeKernel("http://dbpedia.org/resource/Fast_Five","http://dbpedia.org/resource/Fast_&_Furious_(2009_film)"));
           
            
            
            /*System.out.println("Kernel(Apple Inc.,Steve jobs) = "+ neighborhoodpathKernel.computeKernel("http://www.wikidata.org/entity/Q312","http://www.wikidata.org/entity/Q19837"));
            System.out.println("Kernel(Steve_Wozniak,Steve jobs) = "+ neighborhoodpathKernel.computeKernel("http://www.wikidata.org/entity/Q483382","http://www.wikidata.org/entity/Q19837"));
            System.out.println("Kernel(Apple Inc.,Ford Motor Company) = "+ neighborhoodpathKernel.computeKernel("http://www.wikidata.org/entity/Q312", "http://www.wikidata.org/entity/Q44294"));
           
            System.out.println("Kernel(Facebook,Mark Zuckerberg) = "+ neighborhoodpathKernel.computeKernel("http://www.wikidata.org/entity/Q380","http://www.wikidata.org/entity/Q36215"));
            System.out.println("Kernel(Facebook,South Park) = "+ neighborhoodpathKernel.computeKernel("http://www.wikidata.org/entity/Q380", "http://www.wikidata.org/entity/Q16538"));
           
            System.out.println("Kernel(Angelina Jolie,Brad Pitt) = "+ neighborhoodpathKernel.computeKernel("http://www.wikidata.org/entity/Q13909","http://www.wikidata.org/entity/Q35332"));
           */
            
            
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Execution time item: " + elapsedTime / 1000D);

      
        }catch (IOException | InvalidGraphException e){
            e.printStackTrace();
        }
    }
    
    private static Set<Path> readPaths(File inputFile, Graph graph) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.defaultCharset())
                .map(l -> graph.createPath(l.split("--")))
                .collect(toSet());
    }

    private static List<String> readItems(File inputFile) throws IOException {
        return Files.lines(Paths.get(inputFile.getPath()), Charset.forName("UTF-8"))
                .map(l -> l.split("\t"))
                .filter(f -> f[1].equals("movie"))
                .map(f -> f[2])
                .collect(toList());
    }
    
    //con wiki data :
    //.filter no
    //.map()f -> f[1])
}
