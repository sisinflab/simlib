package it.poliba.sisinflab.simlib.input.triplefile;

import it.poliba.sisinflab.simlib.input.Statement;

/**
 * Defines a single predicate relation between two entities
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class Triple extends Statement {

    private String predicate;

    public Triple(String subject, String predicate, String object) {
        super(subject, object);
        this.predicate = predicate;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

}
