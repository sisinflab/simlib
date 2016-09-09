package it.poliba.sisinflab.simlib.featureSelection.dataset;

import it.poliba.sisinflab.simlib.featureSelection.instance.creator.InstanceCombCreater;
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceARFF;
import it.poliba.sisinflab.simlib.featureSelection.instance.creator.InstanceCreater;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.exceptions.InvalidDatasetException;
import it.poliba.sisinflab.simlib.featureSelection.instance.creator.InstanceOccCreater;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates dataset.
 *
 * @author Corrado Magarelli 
 * @since 1.0
 */
public class DatasetFactory {

    public static final String COMBINATION_DATASET = "combination_dataset";
    public static final String OCCURRENCE_DATASET = "occurrence_dataset";
    public static final String FEATURE_DATASET = "feature_dataset";
    /**
     * Creates a dataset object that represents the structure described in the graph
     *
     * @param graph
     * @param datasetType specifies the proper data model
     * @param datasetName name of output file that represents the dataset
     * @return dataset the resulting dataset object
     * @throws IOException if the graph is null
     * @throws InvalidDatasetException if the datasetType is unknown
     */
    public static DatasetARFF create(Graph graph, String datasetType, String workingDirectory, String datasetName,  Map<String,Node> items) throws Exception {

        String workingDirectoryTEMP = workingDirectory + "temp/";
        File f = new File(workingDirectoryTEMP);
        f.mkdir();

        DatasetARFF dataset = null;

        if(datasetType.equals(OCCURRENCE_DATASET)){

            InstanceCreater incr = new InstanceOccCreater(graph);

            //building dataset for PCA,..
            dataset = new DatasetOccARFF(graph);
            HashSet<Node> listproperties = graph.getProperties();

            HashMap<String,Integer> inditem = incr.indicizzaItems(items);
            HashMap<String,Integer> indprop = incr.indicizzaProperties(listproperties);

            HashMap<String,LinkedHashMap<String,Integer>> totpropvalues = incr.getTotpropvalues(items);
            Set<String> props = totpropvalues.keySet();
            for(String n:props)
            {
                //dataset.addNominalAttribute(indprop.get(n).toString(),allPropValues.get(n));
                dataset.addNumericAttribute(indprop.get(n).toString());

            }
            /*for(Node n:listproperties)
            {
                dataset.addNumericAttribute(n.getId());
            }*/
            //Set<String> stringitems = items.keySet();
            Collection<Integer> stringitems = inditem.values();

            dataset.addNominalAttribute("class", stringitems);
            dataset.create(workingDirectory + datasetName);
            //Set<Map.Entry<String,Node>> l = items.entrySet();
            Set<Map.Entry<String,Integer>> l = inditem.entrySet();

            for(Map.Entry<String,Integer> s:l)
            {
                List<InstanceARFF> instances = incr.createInstance(s, listproperties);
                dataset.addIntegerInstance(instances);
            }
            /*for(Map.Entry<String,Node> s:l)
            {
                List<InstanceARFF> instances = incr.createInstance(s.getValue());
                dataset.addInstance(instances);
            }*/
            dataset.save(workingDirectory + datasetName);

        }else if(datasetType.equals(COMBINATION_DATASET)){

            InstanceCreater incr = new InstanceCombCreater(graph);
            //List<InstanceARFF> instances = incr.createInstance();
            //building dataset fro IG,GR,..
            dataset = new DatasetCombARFF(graph);
            Set<Node> listproperties = graph.getPropertiesStartingFrom(items);
            //InstanceCombCreater incrcomb  = (InstanceCombCreater) incr;
            HashMap<String,LinkedHashMap<String,Integer>> totpropvalues = incr.getTotpropvalues(items);

            HashMap<String,Integer> inditem = incr.indicizzaItems(items);

            List<String> listmappingitem = new ArrayList<>();
            listmappingitem.addAll(inditem.entrySet().parallelStream().map(e->e.getValue().toString() + " -- "+e.getKey()).collect(Collectors.toList()));
            //java.nio.file.Path file = Paths.get(workingDirectory + "mappingitem.txt");
            //Files.write(file, listmappingitem, Charset.forName("UTF-8"));

            HashMap<String,Integer> indprop = incr.indicizzaProperties(listproperties);

            List<String> listmappingprop = new ArrayList<>();
            listmappingprop.addAll(indprop.entrySet().parallelStream().map(e->e.getValue().toString() +"\t"+ e.getKey()).collect(Collectors.toList()));
            java.nio.file.Path file2 = Paths.get(workingDirectory + "mappingSelectedFeatures.txt");
            Files.write(file2, listmappingprop, Charset.forName("UTF-8"));

            Set<String> props = totpropvalues.keySet();
            for(String n:props)
            {
                //dataset.addNominalAttribute(indprop.get(n).toString(),allPropValues.get(n));
                dataset.addNumericAttribute(indprop.get(n).toString());

            }
            Collection<Integer> stringitems = inditem.values();
            Set<Map.Entry<String,Integer>> l = inditem.entrySet();
            dataset.addNominalAttribute("class", stringitems);
            dataset.create(workingDirectory + datasetName);
            int i=0;
            for(Map.Entry<String,Integer> s:l)
            {
                i++;
                List<InstanceARFF> instances = incr.createInstance(s, listproperties);
                if(instances.size()>0)
                {
                    //dataset.addInstance(instances);
                    dataset.addIntegerInstance(instances);
                    dataset.appendInstances( workingDirectoryTEMP + i + datasetName);
                }
            }
            concatenateFiles(workingDirectoryTEMP, new File(workingDirectory + datasetName + ".arff"));
        }
        else
            throw new InvalidDatasetException("Unknown dataset type");

        return dataset;
    }

    private static void concatenateFiles(String workingDirectoryTEMP, File output) {
        File dir = new File(workingDirectoryTEMP);

        File first = dir.listFiles()[0] ;
        first.renameTo(output);

        Arrays.stream(dir.listFiles()).filter(f -> f.getName().contains(".arff")).limit(500).forEach(f -> append(output, f));
    }

    private static void append(File output, File file) {
        try {
            Files.write(output.toPath(), readLinesARFF(file), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readLinesARFF(File file) {
        String line;
        List<String> records = new ArrayList<String>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            do {
                line = bufferedReader.readLine();
            } while (!line.startsWith("@data"));

            while (line != null) {

                line = bufferedReader.readLine();
                if (line != null) {
                    records.add(line);
                }
            }
            bufferedReader.close();
        }catch(Exception ex){
            System.out.println(file.toPath());
            ex.printStackTrace();
        }
        return records;
    }
}
