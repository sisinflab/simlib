package it.poliba.sisinflab.simlib.recommendation;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract Recommender Systems
 *
 * @author Paolo Tomeo
 */
public abstract class Recommender {

    public void recommend(HashMap<String,List<String>> training, int howMany, File w)  {
        System.out.println("compute recommendations");
        List<String> lines = training.entrySet().parallelStream()
                .flatMap(e -> recs(e.getValue(), howMany).stream()
                        .map(r -> e.getKey() + "\t" + r.getId() + "\t" + r.getValue())
                        .collect(Collectors.toList())
                        .stream())
                .collect(Collectors.toList());
        System.out.println("write recommendations");
        try {
            Files.write(w.toPath(), lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    abstract List<Rec> recs(List<String> user_likes, int howMany);
}
