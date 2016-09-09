package it.poliba.sisinflab.simlib.recommendation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Split a recommendation dataset in training and test sets
 *
 * @author Paolo Tomeo
 */
public class SplitRandom {
    HashMap<String, HashSet<String>> likes = new HashMap<>();
    HashMap<String, List<String>> training = new HashMap<>();
    HashMap<String, List<String>> test = new HashMap<>();

    public void split(String fileDataset, int percentage) throws IOException {

        readLikes(fileDataset, 20);
        likes.entrySet().stream().forEach(
                e -> splitUserLikes(e.getKey(), e.getValue().stream().collect(Collectors.toList()), e.getValue().size() * percentage / 100)
        );
        write(training, fileDataset + "_training");
        write(test, fileDataset + "_test");
    }

    private void write(HashMap<String, List<String>> likes, String w) throws IOException {
        List<String> lines = likes.entrySet().stream()
                .flatMap(u -> u.getValue().stream()
                        .map(l -> u.getKey() + "\t" + l)
                        .collect(Collectors.toList())
                        .stream())
                .collect(Collectors.toList());

        Files.write(new File(w).toPath(), lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
    }

    private void splitUserLikes(String user, List<String> likes, int trainingSize) {
       training.put(user, likes.subList(0, trainingSize));
        test.put(user, likes.subList(trainingSize, likes.size()));
    }

    private void readLikes(String f, int min_num_likes) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(f));
        r.lines().forEach(l -> {
            String[] vals = l.split("\t") ;
            likes.putIfAbsent(vals[0], new HashSet<>());
            likes.get(vals[0]).add(vals[1]);
        });
        r.close();
        HashMap<String, HashSet<String>> l = new HashMap<>();
        likes.entrySet().stream().filter(e -> e.getValue().size() >= min_num_likes).forEach(e -> l.put(e.getKey(), e.getValue()));
        likes = l;
    }

    public static void main(String[] args) throws IOException {
        SplitRandom split = new SplitRandom();
        split.split("data/facebook/movie/likes/all", 80);
    }
}
