package it.poliba.sisinflab.simlib.featureSelection.instance.creator;

import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceARFF;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Create instance reading graph
 *
 * @author Corrado Magarelli
 * 
 */
public abstract class InstanceCreater {
    
    protected Graph graph;
    
    protected HashMap<String,LinkedHashSet<String>> totpropvalues = new HashMap<>();
    
    protected HashMap<String,LinkedHashMap<String,Integer>> totpropvalind = new HashMap<>();
    
    protected HashMap<String,Integer> itemsind = new HashMap<>();
    
    protected HashMap<String,Integer> propertiesind = new HashMap<>();
    

    public InstanceCreater(Graph graph){
        this.graph = graph;
    }

    /**
     * Reads graph anche create instances
     *
     * @return a list of instances
     *
     */
    public abstract List<InstanceARFF> createInstance(Entry<String,Integer> item, Set<Node> listproperties );
    
    public HashMap<String,LinkedHashMap<String,Integer>> getTotpropvalues(Map<String, Node> items)
    {
        Set<Entry<String,Node>> l = items.entrySet();
        Set<Node> listproperties = graph.getPropertiesStartingFrom(items);
        for(Entry<String,Node> s:l)
        {
          for(Node prop:listproperties)
          {
                LinkedHashSet<String> listobject = new LinkedHashSet<>();
                listobject.addAll(s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_OUT)).parallelStream()
                      .map(e->e.getId())
                      .collect(Collectors.toList()));
              //listobject.addAll(s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_IN)).parallelStream().map(e->e.getId()).collect(Collectors.toList()));
                if(listobject.isEmpty())
                {
                    listobject.add("NULL");
                }
                if(totpropvalues.get(prop.getId())!=null)
                {
                    LinkedHashSet<String> objects = totpropvalues.get(prop.getId());
                    objects.addAll(listobject);
                    totpropvalues.put(prop.getId(), objects);
                }else
                {
                    totpropvalues.put(prop.getId(), listobject);
                }
                }
        }
        
        return indicizzaValues();
    }

    
    public HashMap<String,Integer> indicizzaItems(Map<String,Node> listitems)
    {

        //HashMap<String,LinkedHashMap<String,Integer>> totpropvalind = new HashMap<>();
        int index = 0;
        for(Entry<String, Node> item:listitems.entrySet())
        {
            LinkedHashMap<String,Integer> valind = new LinkedHashMap<>();
            index++;
            itemsind.put(item.getKey(), index);
        }
        
        return itemsind;
    
    }
    
    public HashMap<String,Integer> indicizzaProperties(Set<Node> listproperties)
    {

        //HashMap<String,LinkedHashMap<String,Integer>> totpropvalind = new HashMap<>();
        int index = 0;
        for(Node propertie:listproperties)
        {
            LinkedHashMap<String,Integer> valind = new LinkedHashMap<>();
            index++;
            propertiesind.put(propertie.getId(), index);
        }
        
        return propertiesind;
    
    }
    
    
    public HashMap<String,LinkedHashMap<String,Integer>> indicizzaValues()
    {

        //HashMap<String,LinkedHashMap<String,Integer>> totpropvalind = new HashMap<>();
        int index = 0;
        for(Entry<String, LinkedHashSet<String>> entry : totpropvalues.entrySet())
        {
            LinkedHashMap<String,Integer> valind = new LinkedHashMap<>();
            String key=entry.getKey();
            LinkedHashSet<String> val =entry.getValue();
            for(String s:val)
            {
                index++;
                valind.put(s,index);
            }
            totpropvalind.put(key, valind);
        }
        
        return totpropvalind;
    
    }
    

}
