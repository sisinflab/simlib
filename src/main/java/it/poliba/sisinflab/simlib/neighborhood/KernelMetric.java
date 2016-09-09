package it.poliba.sisinflab.simlib.neighborhood;


import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;

import java.util.Map;

/**
 * Created by Corrado on 15/04/2016.
 */
public abstract class KernelMetric {

    protected Graph graph;

    public KernelMetric(Graph graph){
        this.graph = graph;
    }

    public KernelMetric() {

    }

    public abstract double computeKernel(Node item1, Node item2);
    public abstract Map<Node, Double> computeKernelRank(String itemID);
    
    public abstract Map<Node, Double> computeFeatureMap(Node n);

    public abstract double computeKernel(String idItem1, String idItem2);
    //public abstract Map<Node, Double> computeKernelRank(String nodeId);

}
