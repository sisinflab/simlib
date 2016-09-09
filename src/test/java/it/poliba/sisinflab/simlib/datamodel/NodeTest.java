package it.poliba.sisinflab.simlib.datamodel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tomeo on 19/05/16.
 */
public class NodeTest {
    Node n;

    public NodeTest() {
        Graph g = new Graph();
        n = g.createNode("ciao");
    }

    @Test
    public void testLengthId() throws Exception {
        assertEquals("ciao", n.getId());
    }

}