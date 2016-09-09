package it.poliba.sisinflab.simlib.neighborhood.pathbaseditem;

import it.poliba.sisinflab.simlib.Utilities;
import it.poliba.sisinflab.simlib.datamodel.Node;
import org.apache.commons.collections.bag.HashBag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;


/**
 *
 * @author Corrado on 05/04/2016.
 */
public class PathWeightFeature {
    
   //item del neighborhood graph
    protected Node item;   
    //profondità massima graph
    protected Integer hop;
    //lista sub-paths di ogni path percorribile dall'item i 
    protected LinkedList<EntityPath> listsubpaths = new LinkedList<>();
    //insieme dei paths percorribili dall'item i
    protected HashSet<EntityPath> setcompletepaths = new HashSet<>();
    //paths and weights
    protected LinkedHashMap<EntityPath,Double> pathsweights = new LinkedHashMap<>();
        
    public PathWeightFeature(int distance,ReachablePaths rc){
        
        this.setcompletepaths = (HashSet<EntityPath>) rc.getEntitypaths().clone();
        this.listsubpaths = (LinkedList<EntityPath>) rc.getEntitysubpaths().clone();
        this.hop = distance;
        this.item = rc.getItem();     
    }
    
    public Node getItem() {
        return item;
    }

    public void setItem(Node item) {
        this.item = item;
    }
    
    public Integer getHop()
    {
        return hop;
    }
    
    public void setHop(Integer hop)
    {
        this.hop = hop;
    }
    
    public LinkedHashMap<EntityPath,Double> getWeights()
    {
        return pathsweights;
    }
    
    public void setWeights(LinkedHashMap<EntityPath,Double> pathsweights)
    {
        this.pathsweights = pathsweights;
    }
    
    public LinkedList<EntityPath> getListSubPaths()
    {
        return listsubpaths;
    }
    
    public void setListSubpaths(LinkedList<EntityPath> listsubpaths)
    {
        this.listsubpaths = listsubpaths;
    }
   
    //calcola il vettore risultante dei pesi delle feature
    public void calculateWeight(String typemean,HashSet<EntityPath> allsubpaths)
    {     
        HashMap<EntityPath,LinkedList<Double>> listentityweight = new HashMap<>();

        allsubpaths.forEach(e-> pathsweights.put(e, 0.0));
        HashBag hsbag = new HashBag();
        hsbag.addAll(listsubpaths);
        for(EntityPath mainpath : setcompletepaths) {          
            LinkedList<EntityPath> subpath = mainpath.getSubPathList();
            int occ=0;
            for(EntityPath p : subpath)
            {   //is faster HashBag then Collections.frequency() and then stream().filter().count()
                long startTime3 = System.currentTimeMillis();
                occ =  hsbag.getCount(p);
                long stopTime3 = System.currentTimeMillis();
                long elapsedTime3 = stopTime3 - startTime3;
                //calculate single weight
                double totweightpath = calculatesingleWeight(mainpath,p,occ);
                if(listentityweight.containsKey(p))
                {
                    listentityweight.get(p).add(totweightpath);
                }else
                {
                    LinkedList<Double> list = new LinkedList<>();
                    list.add(totweightpath);
                    listentityweight.put(p, list);
                }
            }                
        }
        
         System.out.println("Creating vector weights...");
         createVectorWeights(listentityweight,typemean);
    }

    public Double calculatesingleWeight(EntityPath mainpath,EntityPath p,int occ)
    {
        double value=0.0;
        double discount = 0;
        
        discount = Math.abs(mainpath.getPathList().size()-1) - Math.abs(p.getPathList().size()-1);                
        value = (double)occ/discount;

        return value;
    }
   
    //controllo se il sub-path ha più di una occorrenza e fare la media aritmetica o geometrica dei pesi calcolati   
    public void createVectorWeights(HashMap<EntityPath,LinkedList<Double>> listentityweight,String typemean)
    {
        Set<Entry<EntityPath,LinkedList<Double>>> lew = listentityweight.entrySet();
        
        for(Entry<EntityPath, LinkedList<Double>> epw : lew)
        {
               if(typemean.equals(NeighborhoodPathKernelMetric.MEAN_ARITHMETIC))
               {//inserisco la media aritmetica
                   pathsweights.put(epw.getKey(),Utilities.meanArithmetic(epw.getValue()));
               }else if(typemean.equals(NeighborhoodPathKernelMetric.MEAN_GEOMETRIC))
               {//inserisco la media geometrcia
                   pathsweights.put(epw.getKey(), Utilities.meanGeometric(epw.getValue()));
               }
        }
    }
       
}
