package it.poliba.sisinflab.simlib.featureSelection;

import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.GraphFactory;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.exceptions.InvalidGraphException;
import it.poliba.sisinflab.simlib.featureSelection.dataset.DatasetFactory;
import it.poliba.sisinflab.simlib.featureSelection.methods.IG;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Paolo Tomeo
 */
public class FSManager {
    // workingDirectory
    String workingDirectory;
    Graph graph;
    List<String> nodesAlreadyTraversed = new ArrayList<>();

    SelectedFeature root  = new SelectedFeature("root", null);
    ArrayDeque<SelectedFeature> deque = new ArrayDeque<>();

    public FSManager(String workingDirectory, Graph graph) {
        this.workingDirectory = workingDirectory;
        this.graph = graph;
    }

    public void execute(){
        //start from the items
        Set<SelectedFeature> rankedFeatures = rankFeatures(graph.getItems());

        root.setSuccessiveFeatures(rankedFeatures);
        nodesAlreadyTraversed.addAll(graph.getItems().keySet());

        deque.addAll(root.getSuccessiveFeatures());
        //continue the feature selection throughout the graph
        execute2();
        System.out.println(root);
    }


    private void execute2() {
        if (deque.isEmpty())
            return;
        if(graph.getNodes().size() <= nodesAlreadyTraversed.size())
            return;

        //expand the graph following one of the features selected before
        SelectedFeature feature = deque.pop();

        Map<String, Node> items = graph.getNodesFrom(feature.getStartNodes(), feature.getName());
        nodesAlreadyTraversed.stream().forEach(n -> items.remove(n)); //not consider the nodes already visited
        if(items.isEmpty())
            execute2();

        Set<SelectedFeature> rankedFeatures = rankFeatures(items);  //rank the features
        feature.setSuccessiveFeatures(rankedFeatures);
        deque.addAll(rankedFeatures);

        nodesAlreadyTraversed.addAll(graph.getItems().keySet());
        execute2();
    }


    public Set<SelectedFeature> rankFeatures(Map<String,Node> items) {
        // FIRST PHASE
        // pre-filtering phase: remove features with too many missing or distinct values
        FeatureStatistics p = new FeatureStatistics();
        p.computeStatistics(graph, items);

        Set<String> missing = p.getMissing().entrySet().stream()
                .filter(e -> e.getValue() > 0.95)
                .map(e -> e.getKey())
                .collect(Collectors.toSet());

        Set<String> distinct =  p.getDistinct().entrySet().stream()
                .filter(e -> e.getValue() > 0.95)
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
        System.out.println(missing.size());
        System.out.println(distinct.size());
        graph.removeProperties(missing, items);
        graph.removeProperties(distinct, items);
        System.out.println(graph.getPropertiesStartingFrom(items).size());
        // SECOND PHASE
        try {
            DatasetFactory.create(graph, DatasetFactory.COMBINATION_DATASET, workingDirectory , "ig", items);
            IG igfs = new IG();
            List<String> rankedFeatures = igfs.execute(workingDirectory, "ig.arff");
            return remap(workingDirectory + "mappingSelectedFeatures.txt", rankedFeatures).stream()
                    .map(rf -> new SelectedFeature(rf, items.keySet()))
                    .collect(Collectors.toSet());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    private static List<String> remap(String s, List<String> rankedFeatures) throws IOException {
        File f = new File(s);
        Map<String, String> map = Files.lines(f.toPath())
                .map(l -> l.split("\t"))
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
        return rankedFeatures.stream()
                .map(feat -> map.get(feat))
                .collect(Collectors.toList());
    }

    public static final String METADATA = "data/facebook/book/wikidata/graph";
    public static final String ITEMS = "data/facebook/book/wikidata/mapping_reduced";
    public static void main(String[] args) throws IOException, InvalidGraphException {
        File itemsFile = new File(ITEMS);

        // Feature selection stars from items
        List<String> itemsIDs = GraphFactory.readItems(itemsFile);
        Graph graph = GraphFactory.create(METADATA, itemsIDs, GraphFactory.TRIPLE_GRAPH);

        HashMap<String, Node> items = graph.getItems();

        FSManager fs = new FSManager("data/facebook/book/wikidata/", graph);
        fs.execute();
    }
}

/**
 * Recursive feature definition
 */
class SelectedFeature {
    String name;
    Set<SelectedFeature> successiveFeatures = new HashSet<>();
    Set<String> startNodes; //the nodes from which the features have been extracted

    public SelectedFeature(String name, Set<String> startNodes) {
        this.name = name;
        this.startNodes = startNodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getStartNodes() {
        return startNodes;
    }

    public Set<SelectedFeature> getSuccessiveFeatures() {
        return successiveFeatures;
    }

    public void setSuccessiveFeatures(Set<SelectedFeature> successiveFeatures) {
        this.successiveFeatures = successiveFeatures;
    }

    @Override
    public String toString() {
        return this.getName() + " " + successiveFeatures.toString();
    }
}
