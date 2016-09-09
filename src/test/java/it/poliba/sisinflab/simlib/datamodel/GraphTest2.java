package it.poliba.sisinflab.simlib.datamodel;

import it.poliba.sisinflab.simlib.input.Statement;
import it.poliba.sisinflab.simlib.input.triplefile.Triple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by tomeo on 19/05/16.
 */
public class GraphTest2 {
    Graph g ;

    public static void main(String[] args) throws Exception {
        GraphTest2 graphTest2 = new GraphTest2();

        System.out.println(graphTest2.g.getNode("2").getArrows(Arrow.DIR_OUT));
        System.out.println(graphTest2.g.getNode("2").getArrows(Arrow.DIR_IN));
    }

    public GraphTest2() {
        g = new Graph();
        List<Statement> triples = new ArrayList<>();
        triples.add(new Triple("1", "p1", "2") );
        triples.add(new Triple("1", "p1", "3") );
        triples.add(new Triple("3", "p2", "4") );
        triples.add(new Triple("4", "p3", "2") );


        triples.add(new Triple("1", "p1", "6") );
        triples.add(new Triple("6", "p2", "4") );

        g.build(triples);


        List<String> itemsIDs = new ArrayList<>();
        itemsIDs.add("1");
        itemsIDs.add("2");
        g.markItems(itemsIDs);
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
        Node n3 = g.getNode("2");
        List<Path> shortest = n1.findTopKShortestPaths( n3, 5, 3);
        System.out.println(shortest);

    }
}