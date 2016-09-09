package it.poliba.sisinflab.simlib.featureSelection;


import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;


import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import java.util.stream.Collectors;

/**
 *
 * @author Corrado Magarelli on 16/05/2016.
 */

class FeatureStatistics {

    HashMap<String, Float> missing = new HashMap<>();
    HashMap<String, Float> distinct = new HashMap<>();


    public void computeStatistics(Graph graph, Map<String, Node> items){

        HashMap<String,HashMap<String,Integer>> c = getTotpropvalues(graph, items);


        for(Entry<String,HashMap<String,Integer>> prop:c.entrySet())
        {
            float missingValues = Float.valueOf(prop.getValue().get("missing"));
            float totItems = Float.valueOf(items.size());
            missing.put(prop.getKey(), (missingValues/totItems));

            float nubDistinctValues = Float.valueOf(prop.getValue().entrySet().parallelStream().filter(e->!e.getKey().equals("missing")).filter(e->e.getValue()==1).count());
            float totValues = Float.valueOf(prop.getValue().entrySet().parallelStream().filter(e->!e.getKey().equals("missing")).count());

            distinct.put(prop.getKey(), (nubDistinctValues/totValues));
        }
    }

    public HashMap<String, Float> getMissing() {
        return missing;
    }

    public HashMap<String, Float> getDistinct() {
        return distinct;
    }

    private HashMap<String,HashMap<String,Integer>> getTotpropvalues(Graph graph,  Map<String, Node> items)
    {
        HashMap<String,HashMap<String,Integer>> allPropValues = new HashMap<>();
        Set<Entry<String,Node>> l = items.entrySet();
        HashSet<Node> props = graph.getProperties();
        for(Entry<String,Node> s:l)
        {
            for(Node prop:props)
            {
                LinkedHashSet<String> listobject = new LinkedHashSet<>();
                listobject.addAll(s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_OUT)).parallelStream()
                        .map(e->e.getId())
                        .collect(Collectors.toList()));
                //listobject.addAll(s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_IN)).parallelStream().map(e->e.getId()).collect(Collectors.toList()));
                if(listobject.isEmpty())
                {
                    listobject.add("missing");
                }
                if(allPropValues.get(prop.getId())!=null)
                {
                    HashMap<String,Integer> objects = allPropValues.get(prop.getId());
                    for(String c:listobject)
                    {
                        if(objects.containsKey(c))
                        {
                            int occ = objects.get(c)+1;
                            objects.put(c, occ);
                        }else
                        {
                            objects.put(c, 1);
                        }
                    }
                    allPropValues.put(prop.getId(), objects);
                }else
                {
                    HashMap<String,Integer> objects= new HashMap<>();
                    for(String c:listobject)
                    {
                        objects.put(c, 1);
                    }
                    allPropValues.put(prop.getId(), objects);
                }
            }
        }
        return allPropValues;
    }
}
