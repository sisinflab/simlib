/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.neighborhood.entitybaseditem;


import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.neighborhood.NeighborGraph;
import java.util.HashSet;


/**
 *
 * @author Corrado on 29/03/2016.
 */
public final class ReachableEntities {
    
     //nodo item del neighborhood graph
     protected Node item;
     //profondità massima graph
     protected Integer hop;
     //entità raggiungibili con massimo hop salti 
     protected HashSet<Node> maxhopnodes = new HashSet<>();
     //entità raggiungibili con esattamente hop salti 
     protected HashSet<Node> exacthopnodes = new HashSet<>();


    public ReachableEntities(NeighborGraph ng){
        
        this.hop = ng.getHop();
        this.item = ng.getItem();      
    } 
     
    public Node getItem() {
        return item;
    }

    public void setItem(Node item) {
        this.item = item;
    }

    public void setHop(Integer hop) {
        this.hop = hop;
    }
    
    public Integer getHop() {
        return hop;
    }
    
    public HashSet<Node> getReachableEntitiesMaxHop(Integer distance) {
        maxhopnodes.clear();
        calculateReachableEntitiesMaxHop(distance,item);
        return maxhopnodes;
    }
    
    public HashSet<Node> getReachableEntitiesExactHop(Integer distance) {
        exacthopnodes.clear();
        calculateReachableEntitiesExactHop(distance,item);
        return exacthopnodes;
    }
    
    
    public HashSet<Node> calculateReachableEntitiesMaxHop(int distance,Node nodeitem) {

        HashSet<Node> nodivicini1 = new HashSet<>();
        HashSet<Node> nodi2 = new HashSet<>();
        HashSet<Node> neighbors = new HashSet<>();
        
        if(distance>0)
        {      
            //ottengo tutti i nodi vicini all'item a distanza 1
            //nodi2.addAll(nodeitem.getInOutNeighbors(1));
            nodi2.addAll(nodeitem.getNeighbors(1,Arrow.DIR_UND));         

            int s = hop-distance;
            distance--;

            if(!nodi2.isEmpty() && distance>0)
            {   
                for (Node entry : nodi2)
                {
                        nodivicini1 = calculateReachableEntitiesMaxHop(distance,entry);
                        //maxhopnodes.addAll(nodivicini1);
                }
            }
        }
        maxhopnodes.addAll(nodi2);
        return nodi2;
    }
    
    public HashSet<Node> calculateReachableEntitiesExactHop(int distance,Node nodeitem) {

        HashSet<Node> nodivicini1 = new HashSet<>();
        HashSet<Node> nodi2 = new HashSet<>();
        
        if(distance>0)
        {      
            //ottengo tutti i nodi vicini all'item a distanza 1
            //nodi2.addAll(nodeitem.getInOutNeighbors(1));
            nodi2.addAll(nodeitem.getNeighbors(1,Arrow.DIR_UND)); 

            int s = hop-distance;
            distance--;

            if(!nodi2.isEmpty() && distance>0)
            {   
                for (Node entry : nodi2)
                {
                        nodivicini1 = calculateReachableEntitiesExactHop(distance,entry);
                        
                }
            }
        }
        if(distance==0)
                        {
                            exacthopnodes.addAll(nodi2);
                        }
        return nodi2;
    }
   
    
}
