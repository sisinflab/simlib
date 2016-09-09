package it.poliba.sisinflab.simlib.datamodel;

import it.poliba.sisinflab.simlib.input.Statement;
import it.poliba.sisinflab.simlib.input.triplefile.Triple;
import it.poliba.sisinflab.simlib.relatedness.KatzRelatedness;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by tomeo on 19/05/16.
 */
public class GraphTest {
    Graph g ;
    Graph g_withp4;

    public GraphTest() {
        g = new Graph();
        List<Statement> triples = new ArrayList<>();
        triples.add(new Triple("1", "p1", "2") );
        triples.add(new Triple("1", "p1", "3") );
        triples.add(new Triple("3", "p2", "4") );
        triples.add(new Triple("4", "p3", "2") );
        //building graph from triples list

        g.build(triples);

        List<String> itemsIDs = new ArrayList<>();
        itemsIDs.add("1");
        itemsIDs.add("2");
        g.markItems(itemsIDs);

        g_withp4 = new Graph();
        triples = new ArrayList<>();
        triples.add(new Triple("1", "p1", "2") );
        triples.add(new Triple("1", "p1", "3") );
        triples.add(new Triple("3", "p2", "4") );
        triples.add(new Triple("4", "p3", "2") );
        triples.add(new Triple("1", "p4", "2") );
        triples.add(new Triple("2", "p4", "3") );

        g_withp4.build(triples);
        g_withp4.markItems(itemsIDs);
    }

    @Test
    public void findTop0ShortestPaths() throws Exception {
        Node n1 = g.getNode("1");
        Node n2 = g.getNode("2");
        List<Path> shortest = n1.findTopKShortestPaths(n2, 0, 3);
        assertEquals( 0, shortest.size());
    }
    @Test
    public void findTop1ShortestPaths() throws Exception {
        Node n1 = g.getNode("1");
        Node n2 = g.getNode("2");
        List<Path> shortest = n1.findTopKShortestPaths(n2, 1, 3);
        assertEquals(1, shortest.size());
    }
    @Test
    public void findTop2ShortestPaths() throws Exception {
        Node n1 = g.getNode("1");
        Node n2 = g.getNode("2");
        List<Path> shortest = n1.findTopKShortestPaths(n2, 2, 3);
        assertEquals(2, shortest.size());
    }


    @Test
    public void findTopKShortestPaths_2hops() throws Exception {
        Node n1 = g.getNode("1");
        Node n3 = g.getNode("3");
        List<Path> shortest = n1.findTopKShortestPaths( n3, 5, 2);
        System.out.println(shortest);
        List<Path> shortest_test = new ArrayList<>();
        shortest_test.add(new Path(new Arrow(new Node("p1", g), Arrow.DIR_OUT)));
        assertEquals(shortest_test, shortest);
    }


    @Test
    public void findTopKShortestPaths_3hops() throws Exception {
        Node n1 = g.getNode("1");
        Node n3 = g.getNode("3");
        List<Path> shortest = n1.findTopKShortestPaths( n3, 5, 3);
        System.out.println(shortest);
        List<Path> shortest_test = new ArrayList<>();
        shortest_test.add(new Path(new Arrow(new Node("p1", g), Arrow.DIR_OUT)));
        assertEquals(shortest_test, shortest);
    }

    @Test
    public void findShortestPaths() throws Exception {
        Node n1 = g.getNode("1");
        Node n3 = g.getNode("3");

        System.out.println(g.getNeighborsDifferentDepths(n1, 4, Arrow.DIR_OUT));

        KatzRelatedness r = new KatzRelatedness(g,  3 , 4, 0.75);

        System.out.println(r.computeRelatednessRank(n1, g.getItems()));
    }
//    @Test
//    public void removeProps() throws Exception {
//
//        Set<String> props = new HashSet<>();
//        props.add("p4");
//        this.g_withp4.removeProperties(props, items);
//        assertEquals(g.getProperties(), g_withp4.getProperties());
//
//        assertEquals(g.getNodes(), g_withp4.getNodes());
//        assertEquals(g.getItems().size(), g_withp4.getItems().size());
//
//    }
//
//    public static void main(String[] args) throws Exception {
//        GraphTest test = new GraphTest();
//        test.removeProps();
//    }
}