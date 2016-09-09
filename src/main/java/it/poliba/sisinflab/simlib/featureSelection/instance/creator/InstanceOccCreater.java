package it.poliba.sisinflab.simlib.featureSelection.instance.creator;

import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceARFF;
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceOccARFF;

import java.util.*;
import java.util.stream.Collectors;



/**
 * Create instances with occurrence structure
 *
 * @author Corrado Magarelli
 * 
 */
public class InstanceOccCreater extends InstanceCreater {

    public InstanceOccCreater(Graph graph){
        super(graph);
    }

    @Override
    public List createInstance(Map.Entry<String,Integer> item, Set<Node> listproperties ){
        
        HashMap<String,Node> listitems = graph.getItems();
            
        List<InstanceARFF> instanceList = new ArrayList<>();
        instanceList.addAll(processInstance(item,listproperties));

        //System.out.println("Instances: " + instanceList.size());

        return instanceList;

    }

    private List<InstanceOccARFF> processInstance(Map.Entry<String,Integer> item, Set<Node> listproperties){
        
        List<InstanceOccARFF> instanceList = new ArrayList<>();
        //Set<Map.Entry<String,Node>> l = listitems.entrySet();

        //for(Map.Entry<String,Node> s:l)
        //{ 
              Map<String,Integer> propocc = new HashMap<>();
              propocc = listproperties.parallelStream().collect(Collectors.toMap(
                                prop -> prop.getId(),
                                prop -> graph.createNode(item.getKey()).getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_OUT)).size()+graph.createNode(item.getKey()).getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_IN)).size())
                        );
                      //.filter(e -> !e.getValue().equals(item1)).forEach(prop->{
              //propocc.put(prop.getId(), s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_OUT)).size()+s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_IN)).size());
              //});
              
              /*for(Node prop:listproperties)
               {
                    //HashSet<Node> arrowsout = s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_OUT));
                    //HashSet<Node> arrowsin = s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_IN));
                    propocc.put(prop.getId(), s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_OUT)).size()+s.getValue().getArrowObjects(graph.createArrow(graph.createNode(prop.getId()), Arrow.DIR_IN)).size());
          
              }*/             
              InstanceOccARFF inocc = new InstanceOccARFF(item.getValue(), (HashMap)propocc);
              instanceList.add(inocc);
        //}
        return instanceList;
    }
}

