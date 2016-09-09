package it.poliba.sisinflab.simlib.input.pathfile;

import it.poliba.sisinflab.simlib.input.Statement;

import java.util.LinkedList;

/**
 * Extends the Statement class defining a path relation between two entities as a list of predicates
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class PathTriple extends Statement {

    private LinkedList<String> predicates;

    public PathTriple(String subject, LinkedList<String> properties, String object){
        super(subject, object);
        this.predicates = properties;
    }

    public LinkedList<String> getPredicates() {
        return predicates;
    }

    public void setPath(LinkedList<String> predicates) {
        this.predicates = predicates;
    }

}
