/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.neighborhood.pathbaseditem;

import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.neighborhood.NeighborGraph;

import java.util.HashSet;
import java.util.LinkedList;


/**
 *
 * @author Corrado on 04/04/2016.
 */
public final class ReachablePaths {

    //nodo item del neighborhood graph
    protected Node item;
    //profondità massima graph
    protected Integer hop;
    //paths percorribili dall'item i
    protected HashSet<EntityPath> entitypaths = new HashSet<>();
    //nodi from root to create paths
    protected LinkedList<Node> nodesfromroot = new LinkedList<>();
    //sub-paths di ogni path percorribile dall'item i 
    protected LinkedList<EntityPath> entitysubpaths = new LinkedList<>();

    public ReachablePaths(NeighborGraph ng){
        
        this.hop = ng.getHop();
        //this.graph = ng.getGraph();
        this.item = ng.getItem();
        calculateReachablePaths(hop,item);
        System.out.println("For item:"+ item.getId()+" number of mainpaths :"+entitypaths.size());
        calculateReachableSubPaths();
        System.out.println("For item:"+ item.getId()+" number of all subpaths :"+entitysubpaths.size());
        System.out.println();
               
    }
    
    public HashSet<EntityPath>  getEntitypaths() {
        return entitypaths;
    }

    public void setEntitypaths(HashSet<EntityPath> entitypaths) {
        this.entitypaths = entitypaths;
    }
    
    public LinkedList<EntityPath>  getEntitysubpaths() {
        return entitysubpaths;
    }

    public void setEntitysubpaths(LinkedList<EntityPath> entitysubpaths) {
        this.entitysubpaths = entitysubpaths;
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
    
    public void calculateReachableSubPaths(){
        
        entitysubpaths.clear();
        //verifica inserimento parallel stream?
        entitypaths.forEach((EntityPath path)->{entitysubpaths.addAll(path.getSubPathList());});
    }
    
    public HashSet<Node> calculateReachablePaths(int distance,Node nodeitem){
                    
        HashSet<Node> nodivicini1 = new HashSet<>();
        HashSet<Node> nodi2 = new HashSet<>();
        if(distance>0)
        {      
            //ottengo tutti i nodi vicini all'item a distanza 1
            //nodi2.addAll(nodeitem.getInOutNeighbors(1));  
            nodi2.clear();
            nodi2.addAll(nodeitem.getNeighbors(1,Arrow.DIR_UND));
            int s = hop-distance;
            //verifico la lunghezza del percorso dal root
            if(nodesfromroot.size()== s)
            {
                nodesfromroot.addLast(nodeitem);
            }else
            {
                //elimino i nodi in nodesfromroot in base alla profondità in cui mi trovo per aggiungere il successivo(es. 486-90-1,486-90-2,...per
                //passare al nodo 34 per eseguire 486-34-5,486-34-7, e così via)
                do{
                nodesfromroot.removeLast();
                }while(s!= nodesfromroot.size());
                nodesfromroot.addLast(nodeitem);
            }
            distance--;
            //s>0 stiamo esaminando dal secondo passo in poi..
            if(s>0)
            {
                //rimuovo dai nodi vicini il nodo già visitato(es. 486-187 se calcolo
                //i vicini di 187 riavrò 486 ma non devo considerarlo poichè sono arrivato a 187 da 486
                if(nodi2.contains(nodesfromroot.get(s-1)))
                {
                    nodi2.remove(nodesfromroot.get(s-1));
                }       
            }
            if(!nodi2.isEmpty() && distance>0)
            {   
                for (Node entry : nodi2)
                {
                    //verifico se il nodo è stato già visitato(ulteriore verifica)
                    if(!nodesfromroot.contains(entry))
                    {
                        nodivicini1 = calculateReachablePaths(distance,entry);
                        //nodesfromroot.remove(nodeitem); 
                    } 
                }
            }else
            {
                //creo path entity
                //caso in cui la distanza è 0(si è raggiunta la profondità richiesta dai paths)
                LinkedList<Node> list = new LinkedList<>();
                if(!nodi2.isEmpty()) 
                {
                    for(Node n : nodi2 )
                    {    
                        list.clear();
                        EntityPath ep = new EntityPath();
                        list.addAll(nodesfromroot);
                        //per non inserire nodi già visitati
                        if(!list.contains(n))
                        {
                            list.addLast(n);
                            //list.add(nodeitem);
                            ep.setPathList(list);
                            EntityPath w = new EntityPath(ep.getPathList(),ep.calculateReachableSubPaths());
                            entitypaths.add(w);
                            //writeOnOutput(ep,w.getSubPathList());
                        }
                    }
                }else if(nodi2.isEmpty())
                {   //caso in cui la distanza>0 ma non ci sono vicini per il nodoitem
                    EntityPath ep = new EntityPath();
                    //LinkedList<Node> list = new LinkedList<>();
                    list.clear();
                    list.addAll(nodesfromroot);
                    ep.setPathList(list);
                    //calculate subpaths
                    EntityPath w = new EntityPath(ep.getPathList(),ep.calculateReachableSubPaths());
                    //salvo il path corredato dei subpaths
                    entitypaths.add(w);
                    //writeOnOutput(ep,w.getSubPathList());
                             
                }
                 nodesfromroot.remove(nodeitem);      
            }
        }
        return nodivicini1;
    }
    
    public void writeOnOutput(EntityPath ep,LinkedList<EntityPath> listsubpath)
    {
        System.out.println("\033[31m  MAINPATH:");
        for(int i=0;i<ep.getPathList().size();i++)
            {                                
                System.out.print("node :" + ep.getPathList().get(i).getId()+" ");
            }
        System.out.println();
        System.out.println("\033[34m  SUBPATHS:");
        for(EntityPath e:listsubpath)
        {    
            e.getPathList().stream().forEach((n) -> {
            System.out.print("\033[0m node :" + n.getId()+" ");
            });
            System.out.println();  
        }
        System.out.println();
    }
   
    
}
