/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.neighborhood.entitybaseditem;

import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author Corrado on 31/03/2016.
 */
public class EntityWeightFeature {
    
   //item del neighborhood graph
    protected Node item;   
    //profondità massima graph
    protected Integer hop;
    //entità raggiungibili con esattamente 1,2,3,..hop salti 
    protected HashMap<Integer,HashSet<Node>> entityreachableexact= new HashMap<>();;
     //entità raggiungibili con massimo 1,2,3,..hop salti 
    protected HashMap<Integer,HashSet<Node>> entityreachablemax= new HashMap<>();;
    //oggetto reachableentities
    protected ReachableEntities rcent;
    
    protected LinkedHashMap<String,Double> nodesweight = new LinkedHashMap<>();
    
    protected Set<String> nodes;
    
    
    public EntityWeightFeature(ReachableEntities rc,Set<String> nodes){    
        this.hop = rc.getHop();
        this.rcent = rc;
        
        for(int i=1;i<=hop;i++)
        {
            this.entityreachableexact.put(i, (HashSet<Node>) rc.getReachableEntitiesExactHop(i).clone());
        }
        for(int i=1;i<=hop;i++)
        {
            this.entityreachablemax.put(i, (HashSet<Node>) rc.getReachableEntitiesMaxHop(i).clone());
        }
        this.nodes = nodes;
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
    
    public LinkedHashMap<String,Double> getWeights()
    {
        return nodesweight;
    }
    
    public void setWeights(LinkedHashMap<String,Double> nodesweight)
    {
        this.nodesweight = nodesweight;
    }
   
    //calcola il vettore risultante dei pesi delle feature
    public void calculateWeight()
    {     
        HashSet<Node> entitymaxhop = new HashSet<>();
        //nodes1.remove(rcent.getItem());
        entitymaxhop = entityreachablemax.get(hop);
        entitymaxhop.remove(rcent.getItem());
        
        nodes.forEach(e-> nodesweight.put(e, 0.0));
        
        for (Node entry : entitymaxhop) {  
            Double totwightnode = 0.0;      
            for(Integer i=1;i<=hop;i++)
            {
                Double wighthopnode = 0.0; 
                Double factor = calculateDecayFactor(i);
                Integer n = calculateNumTriple(entry,i);
                wighthopnode = n*factor;         
                totwightnode = totwightnode + wighthopnode;
            } 
             /*System.out.print("\033[0m node :" + entry.getId()+" ");
             System.out.print("\033[32m weight :" + totwightnode+" ");
             System.out.println();*/
            //listweight.put(entry,totwightnode);
            //inserisco nella chiave del nodo il valore del peso
            nodesweight.put(entry.getId(), totwightnode);
        }
        
    }
    
    public Double calculateDecayFactor(Integer hop)
    {
        Double factor= 0.0;
        
        factor = 1/(1+Math.log(hop));
        
        return factor;
    }
    
    
    //calculate Cl,m : l:profondità m:numero entità
    public Integer calculateNumTriple(Node entitym,Integer distance)
    {
        Integer c=0;
        HashSet<Node> entityexacthop = new HashSet<>();
        HashSet<Node> entityexhopminusone = new HashSet<>();
        HashSet<Node> nodivicini = new HashSet<>();
        
        //nodivicini.addAll(entitym.getInOutNeighbors(1));
        nodivicini.addAll(entitym.getNeighbors(1,Arrow.DIR_UND));
                
        //entityexacthop = (HashSet<Node>) rcent.getReachableEntitiesExactHop(distance).clone();
        entityexacthop = (HashSet<Node>) entityreachableexact.get(distance).clone();
        
        //entityexacthop2.addAll(entityexacthop);
        if(distance>1)
        {
            //entityexhopminusone = (HashSet<Node>) rcent.getReachableEntitiesExactHop(distance-1).clone();
            entityexhopminusone = (HashSet<Node>) entityreachableexact.get(distance-1).clone();

            //entityexhopminusone2.addAll(entityexhopminusone);
        }else
        {
            entityexhopminusone.add(rcent.getItem());
        }
        if(entityexacthop.contains(entitym))
        {
            for(Node n: nodivicini)
            {
                if(entityexhopminusone.contains(n))
                {
                    c++;
                }
            }
        }
        return c;
    }
       
}
