package it.poliba.sisinflab.simlib.datamodel;

import java.util.HashSet;
import java.util.Objects;

/**
 * A single relation between two nodes, with a given {@link #direction} attribute
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class Arrow {

    public static final String DIR_OUT = "out";
    public static final String DIR_IN = "in";
    public static final String DIR_UND = "und";

    private Node property;
    private String direction;

    /**
     * TODO: explain why property is instance of Node
     * @param property
     * @param direction
     */
    protected Arrow(Node property, String direction){
        this.property = property;
        if(isValidDirection(direction)){
            this.direction = direction;
        }else{
            throw new IllegalArgumentException("Wrong direction value: " + direction);
        }
    }

    public Node getProperty() {
        return property;
    }

    public void setProperty(Node property) {
        this.property = property;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isValidDirection(String direction){
        return direction.equals(DIR_IN) || direction.equals(DIR_OUT) || direction.equals(DIR_UND);
    }

    /**
     * Creates a new {@link Arrow} object with the opposite direction with respect to this arrow's direction
     *
     * @return the inverted arrow
     */
    public Arrow getInvertedArrow(){
        if(direction.equals(DIR_OUT)) {
            return new Arrow(property, DIR_IN);
        }else if(direction.equals(DIR_IN)) {
            return new Arrow(property, DIR_OUT);
        }else
            throw new IllegalArgumentException("You can't invert a undirected edge");
    }

    @Override
    public int hashCode(){
        return Objects.hash(property, direction);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Arrow){
            Arrow e = (Arrow) o;
            return e.getProperty().equals(property) && e.getDirection().equals(direction);
        }
        return false;
    }

    @Override
    public String toString() {
        return property.toString() + " - " + direction;
    }
}
