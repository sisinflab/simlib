package it.poliba.sisinflab.simlib.recommendation;

import it.poliba.sisinflab.simlib.datamodel.GraphFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tomeo on 28/06/16.
 */
public class MainRec {

    public static void main(String[] args) throws IOException {
        //String simFile = "data/facebook/movie/wikidata/all_sim_katz";

        String simFolder = "data/facebook/movie/wikidata/sim/";
        String recFolder = "data/facebook/movie/recs/";

        String training = "data/facebook/movie/likes/training";
        String test = "data/facebook/movie/likes/test";
        String mapping = "data/facebook/movie/mapping_reduced";
//        long start = System.currentTimeMillis();
//        System.out.println((System.currentTimeMillis() - start) / 1000);

        //replaceURI(simFolder, mapping);
        runComputation(simFolder, recFolder, training);
        runEvaluation(recFolder, test);

    }

    private static void replaceURI(String simFolder, String mapping) throws IOException {
        Map<String, String> itemsMapping = GraphFactory.readInverseMappingItems(new File(mapping));
        File s = new File(simFolder);
        if(s.isDirectory()){
            Arrays.asList(s.listFiles()).forEach(simFile -> replaceURIoneFile(simFile, itemsMapping));
        }
    }

    private static void replaceURIoneFile(File simFile, Map<String, String> itemsMapping) {
        try {
            List<String> lines = Files.lines(simFile.toPath())
                    .map(l -> l.split("\t"))
                    .map(split -> itemsMapping.get(split[0]) + "\t" + itemsMapping.get(split[1]) + "\t" + split[2])
                    .collect(Collectors.toList());
            File temp = new File(simFile.getAbsolutePath() + "_temp");
            Files.write(temp.toPath(), lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW );
            temp.renameTo(simFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runComputation(String simFolder, String outputFolder, String training) {
        File s = new File(simFolder);
        if(s.isDirectory()){
            Arrays.asList(s.listFiles()).forEach(simFile -> {
                INN inn = new INN(simFile, 3000);
                inn.recommend(readLikes(training), 10, new File(outputFolder + simFile.getName()));
            });
        }
    }

    private static void runEvaluation(String recFolder, String test) {
        Evaluation ev = new Evaluation(test);
        System.out.println("\t" + ev.header());
        File s = new File(recFolder);
        if(s.isDirectory()){
            Arrays.asList(s.listFiles()).forEach(recFile -> {
                ev.loadRecs(recFile.getAbsolutePath());
                System.out.println(recFile.getName() + "\t" + ev.getResultsAsString());
            });
        }
    }


    private static HashMap<String,List<String>> readLikes(String f)  {
        HashMap<String,List<String>> likes = new HashMap<>();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(f));
            r.lines().forEach(l -> {
                String[] vals = l.split("\t") ;
                likes.putIfAbsent(vals[0], new ArrayList<>());
                likes.get(vals[0]).add(vals[1]);
            });
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return likes;
    }
}
