package it.poliba.sisinflab.simlib.input;

/**
 * Defines the abstract idea of statement as a relation between two entities. This relation may be a single property,
 * a path or any other relation used to describe a graph pattern
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public abstract class Statement {

    protected String subject;
    protected String object;

    public Statement(){}

    public Statement(String subject, String object){
        this.subject = subject;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

}
