package it.poliba.sisinflab.simlib.output;

import it.poliba.sisinflab.simlib.datamodel.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Paolo Tomeo
 * @since 1.0
 */
public class OutputWriter {
    BufferedWriter w ;

    public OutputWriter(String path, boolean append) {
        try {
            w = new BufferedWriter(new FileWriter(path, append));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeValue(String item1, String item2, Double value){
        try {
            w.write(item1 + "\t" + item2 + "\t" + value);
            w.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void writeValues(String item1, Map<Node, Double> values){
        values.entrySet().forEach(e -> writeValue(item1, e.getKey().toString(), e.getValue()));
    }

    public void close() {
        try {
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeValues(String item1, Map<Node, Double> values, Map<String, String> itemsMapping) {
        values.entrySet().forEach(e -> writeValue(item1, itemsMapping.get(e.getKey().getId()), e.getValue()));
    }
}
