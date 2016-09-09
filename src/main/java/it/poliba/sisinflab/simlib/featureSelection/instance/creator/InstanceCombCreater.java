package it.poliba.sisinflab.simlib.featureSelection.instance.creator;

import com.google.common.collect.Sets;
import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceARFF;
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceCombARFF;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.stream.Collectors;

/**
 * Create instances with combination structure
 *
 * @author Corrado Magarelli
 *
 */
public class InstanceCombCreater extends InstanceCreater {

    public Map<Integer,List<String>> propvalues2 = new LinkedHashMap<>();
    //public static int LIMIT_MAX_INSTANCES = 200000;
    public static int LIMIT_MAX_INSTANCES = 1000000;
    public int i=0;
    public int j=0;

    public InstanceCombCreater(Graph graph){
        super(graph);
    }


    @Override
    public List createInstance(Entry<String,Integer> item, Set<Node> listproperties ) {

        HashMap<String,Node> listitems = graph.getItems();

        List<InstanceARFF> instanceList = new ArrayList<>();
        //try {
        instanceList.addAll(processInstance(item,new LinkedHashSet(listproperties)));
        /*} catch (Exception ex) {
            Logger.getLogger(InstanceCombCreater.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        //System.out.println("Instances: " + instanceList.size());

        return instanceList;

    }

    //private List<InstanceCombARFF> 
    private List<InstanceCombARFF>  processInstance(Entry<String,Integer> item,LinkedHashSet<Node> listproperties){

        List<InstanceCombARFF> instanceList = new ArrayList<>();
        List<InstanceCombARFF> instanceList2 = new ArrayList<>();


        //Set<Map.Entry<String,Node>> l = listitems.entrySet();
        List<String> listpropstring = new ArrayList<>();
        listpropstring.addAll(listproperties.parallelStream().map(e->e.getId()).collect(Collectors.toList()));
        //int i=0;
        //for(Map.Entry<String,Node> s:l)
        //{
        List<LinkedHashSet<Integer>> propvalues= new ArrayList<>();
        List<InstanceCombARFF> valuesinstance = new ArrayList<>();

              /*propvalues = listproperties.stream().filter(prop->prop!=null)
                      .map(prop -> getObjects(s.getValue(),prop,Arrow.DIR_UND)).collect(Collectors.toList());*/

        //effettuo l'indicizzazione

        listproperties.parallelStream()
                .forEachOrdered(prop->propvalues.add(getObjects(graph.createNode(item.getKey()),prop,Arrow.DIR_UND)));

        //.filter(prop->!prop.getId().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
              /*propvalues3 = listproperties.parallelStream().collect(Collectors.toMap(
                                prop -> prop.getId(),
                                prop -> getObjects(s.getValue(),prop,Arrow.DIR_UND)
              ));*/
        Set<List<Integer>> listpropobject2 = new HashSet<>();
              /*if(s.getValue().getId().equals("http://dbpedia.org/resource/A_Good_Year"))
              {
                    System.out.println("FOUND");
              }*/
        try{
            listpropobject2 = Sets.cartesianProduct(new ArrayList(propvalues));

        } catch (Exception ex) {
            Logger.getLogger(InstanceCombCreater.class.getName()).log(Level.SEVERE, null, ex);
            j++;
            //System.out.println("\033[31m DISCARED CARTESIAN PRODUCT OVER item "+item.getId());
            //System.out.println(j);
            //i++;
        }
                /*valuesinstance = listpropobject2.stream().filter(list->list!=null)
                .map(list -> createInstanceComb(s.getValue(),list)).collect(Collectors.toList());*/

        if(listpropobject2.size()>LIMIT_MAX_INSTANCES)
        {
            i++;
            //System.out.println("\033[34m DISCARED MAX INSTANCES item "+item.getId()+" number of combinations: "+listpropobject2.size());
            //System.out.println(i);

        }else
        {
            //System.out.println("Combinations for item "+item.getId()+" are: "+listpropobject2.size());
            listpropobject2.parallelStream().forEachOrdered(
                    list -> valuesinstance.add(new InstanceCombARFF(item.getValue(),new ArrayList<>(list)))
            );

        }

        //instanceList.addAll(valuesinstance);

        //}

        //System.out.println("Number of items discarded: "+i);  
        //indicizzaProp();

        //return instanceList;
        return valuesinstance;
    }

    public LinkedHashSet<Integer> getObjects(Node item,Node prop,String direction)
    {
        //impostare la direzione
        LinkedHashSet<Integer> indlistobject = new LinkedHashSet<>();
        LinkedHashSet<String> listobject = new LinkedHashSet<>();
        listobject.addAll(item.getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_OUT)).parallelStream().map(e->e.getId()).collect(Collectors.toList()));
        //listobject.addAll(item.getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_IN)).parallelStream().map(e->e.getId()).collect(Collectors.toList()));
        if(listobject.isEmpty())
        {
            listobject.add("NULL");
        }

        listobject.forEach(s->{
            indlistobject.add(totpropvalind.get(prop.getId()).get(s));
            //System.out.println();
        });
        
        /*if(allPropValues.get(prop.getId())!=null)
        {
            LinkedHashSet<String> objects = allPropValues.get(prop.getId());
            objects.addAll(listobject);
            allPropValues.put(prop.getId(), objects);
        }else
        {
            allPropValues.put(prop.getId(), listobject);
        }*/

        return indlistobject;
    }

}

