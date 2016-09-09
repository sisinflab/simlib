/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.neighborhood;


import it.poliba.sisinflab.simlib.datamodel.Node;



/**
 *
 * @author Corrado on 29/03/2016.
 */
public class NeighborGraph {

    //item del neighborhood graph
    protected Node item;
    //profondità massima graph
    protected Integer hop;

    public NeighborGraph(Node iditem,int distance){
        
        this.item = iditem;
        this.hop = distance;

    }
    
     public Node getItem() {
        return item;
    }

    public void setItem(Node item) {
        this.item = item;
    }

    public Integer getHop() {
        return hop;
    }

    public void setHop(int distance) {
        this.hop = distance;
    }
}
