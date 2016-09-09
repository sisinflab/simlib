package it.poliba.sisinflab.simlib.recommendation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

import org.apache.commons.collections4.bag.HashBag;


public class Evaluation {

    public final static String[] ordered_metrics = {"Precision",
            //"MAP",
            "MRR",
            "itemCov", "aggrEntropy"};

    HashMap<String, ArrayList<String>> recs;

    HashMap<String, ArrayList<String>> test = new HashMap<>();

    HashBag recFreq;

    int N = 10;

    HashMap<String, Double> results = new HashMap<>();

    public Evaluation(String test) {
        this.readTest(test);
    }

    public void loadRecs(String f)  {
        readRecs(f);
        System.out.println(recs.size());
        results.put("Precision", precision());
        // results.put("MAP", MAP());
        results.put("MRR", MRR());
        results.put("itemCov", itemCov());
        results.put("aggrEntropy", aggrEntropy());
    }

    public HashMap<String, Double> getResults() {
        return results;
    }
    public String getResultsAsString() {
        StringBuilder b = new StringBuilder();
        for (String m : ordered_metrics) {
            b.append(results.get(m) +"\t");
        }
        return b.toString();
    }

    public double precision(){
        return test.entrySet().stream().mapToDouble(u -> precision(recs.get(u.getKey()), N, test.get(u.getKey()))).average().orElse(0);
    }
    private void readTest(String f)  {
        BufferedReader r;
        try {
            r = new BufferedReader(new FileReader(f));
            r.lines().forEach(l -> {
                String[] vals = l.split("\t") ;
                test.putIfAbsent(vals[0], new ArrayList<>());
                test.get(vals[0]).add(vals[1]);
            });
            r.close();
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void readRecs(String f)  {
        recFreq = new HashBag();
        recs = new HashMap<>();
        BufferedReader r;
        try {
            r = new BufferedReader(new FileReader(f));
            r.lines().forEach(l -> {

                if(l.contains("[")){
                    String[] vals = l.split("\t") ;

                    Arrays.asList(vals[1].replace("[", "").replace("]", "").split("[,:]")).stream().forEach(rec ->{
                        if(!rec.contains(".")){
                            addRec(vals[0], rec);
                        }
                    });
                }else{
                    String[] vals = l.replace(",", "\t").replace("\"", "").split("\t") ;
                    addRec(vals[0], vals[1]);
                }
            });
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRec(String user, String item) {
        recs.putIfAbsent(user, new ArrayList<>());
        recs.get(user).add(item);
    }

    public double MAP (){
        return  recs.entrySet().stream().mapToDouble(u -> AP(u.getValue(), N, test.get(u.getKey()))).average().getAsDouble();
    }
    public double MRR (){

        return  recs.entrySet().stream().mapToDouble(u -> RR(u.getValue(), N, test.get(u.getKey()))).average().getAsDouble();
    }

    private static double precision(List<String> list, int N, List<String> test) {
        return (double) list.stream().limit(N).filter(r -> test.contains(r)).count() / (double) N  ;
    }
    private static double AP(List<String> list, int N, List<String> test) {
        if(list.size() == 0 )
            return 0;

        return IntStream.range(0, N)
                .filter(i -> test.contains(list.get(i)))
                .mapToDouble(i -> precision(list, i+1 , test) )
                .sum() / Math.min(list.size(), N);
    }
    private static double RR(List<String> list, int N, List<String> test) {
        int n = Math.min(list.size(), N);
        return 1.0 / (1 + IntStream.range(0, n)
                .filter(i -> test.contains(list.get(i)))
                .findFirst().orElse(n));
    }

    private double itemCov(){
        return this.recs.values().stream().flatMap(l -> l.stream().limit(N) ).distinct().count();
    }

    private double aggrEntropy(){
        double ln2 = Math.log(2.0);
        this.recs.values().stream().forEach(l -> l.stream().limit(N).forEach(r -> recFreq.add(r)));
        double totalRecs = recFreq.size();
        return - this.recFreq.uniqueSet().stream().mapToDouble(i -> recFreq.getCount(i) / totalRecs  ).map(f -> f * Math.log(f) / ln2 ).sum();

    }

    public static String header() {
        StringBuilder b = new StringBuilder();
        for (String m : ordered_metrics) {
            b.append(m +"\t");
        }
        return b.toString();
    }

    public static void main(String[] args) throws IOException {
        Evaluation e = new Evaluation("data/facebook/movie/likes/test");
        e.loadRecs("data/facebook/movie/recs/p");
        System.out.println("\t" + e.header());
        System.out.println("\t" + e.getResultsAsString());
    }
}
