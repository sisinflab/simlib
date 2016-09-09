package it.poliba.sisinflab.simlib.recommendation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Item-based K-nearest neighbors Recommender Systems
 *
 * @author Paolo Tomeo
 */
public class INN extends Recommender{

    //number of most similar neighbors
    int K;

    Map<String, List<Neighbor>> neighs = new HashMap<>();

    Map<String, List<String>> invNeighs = new HashMap<>();

    public INN(File simFile) {
        this(simFile, 20);
    }
    public INN(File simFile, int k) {
        K = k;
        readValues(simFile);
        sortAndFilter();
        saveInvNeighs();

    }


    List<Rec> recs(List<String> user_likes, int howMany){

        Set<String> candidates = user_likes.stream()
                .filter(invNeighs::containsKey)
                .flatMap(l -> invNeighs.get(l).stream())
                //remove liked item from candidates
                .filter(c -> !user_likes.contains(c))
                .collect(Collectors.toSet());

//        Set<String> candidates = user_likes.stream()
//                .filter(neighs::containsKey)
//                .flatMap(l -> neighs.get(l).stream())
//                //remove liked item from candidates
//                .filter(c -> !user_likes.contains(c))
//                .map(c -> c.getId())
//                .collect(Collectors.toSet());


        return candidates.stream()
                //for each candidate sum the similarity values with the liked item in the neighborhood
                .map(c -> new Rec(c, neighs.get(c).stream()
                        .filter(n -> user_likes.contains(n.getId()))
                        .mapToDouble(Neighbor::getSim)
                        .sum()))
                .sorted(Comparator.reverseOrder())
                .limit(howMany)
                .collect(Collectors.toList());

    }

    private void saveInvNeighs() {
        neighs.entrySet().forEach(e ->
                e.getValue().stream().forEach(n -> {
                            invNeighs.putIfAbsent(n.getId(), new ArrayList<>());
                            invNeighs.get(n.getId()).add(e.getKey());
                        }
                )
        );
    }

    private void sortAndFilter() {
        neighs.values().stream()
                .forEach(e -> {Collections.sort(e); Collections.reverse(e);  });
        neighs = neighs.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().subList(0, Math.min(K, e.getValue().size()))));
    }

    private void readValues(File simFile) {
        try {
            Files.lines(simFile.toPath()).forEach( l -> {
                String[] split = l.split("\t");

                neighs.putIfAbsent(split[0], new ArrayList<>());
                neighs.get(split[0]).add(new Neighbor(split[1], new Double(split[2])));

                neighs.putIfAbsent(split[1], new ArrayList<>());
                neighs.get(split[1]).add(new Neighbor(split[0], new Double(split[2])));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
class Neighbor implements Comparable<Neighbor> {
    String id;
    double sim;

    public Neighbor(String id, Double sim) {
        this.id = id;
        this.sim = sim;
    }

    public String getId() {
        return id;
    }

    public double getSim() {
        return sim;
    }

    @Override
    public int compareTo(Neighbor o) {
        return Double.compare(this.sim, o.sim);
    }

    @Override
    public String toString() {
        return this.getId() + "\t" + this.getSim();
    }
}

class Rec implements Comparable<Rec> {
    String id;
    double value;

    public Rec(String id, Double value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Rec o) {
        return Double.compare(this.value, o.value);
    }

    @Override
    public String toString() {
        return this.getId() + "\t" + this.getValue();
    }
}