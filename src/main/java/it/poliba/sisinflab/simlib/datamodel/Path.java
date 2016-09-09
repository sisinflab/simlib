package it.poliba.sisinflab.simlib.datamodel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Defines a path relation as a sequence of {@link Arrow} objects, each one with its given direction
 * The overall path direction (or type) will directly depend on the singles arrows direction (see {@link #getType()}
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class Path {

    public static final String DIR_OUT = "out";
    public static final String DIR_IN = "in";
    public static final String DIR_UND = "und";
    public static final String DIR_MIX = "mix";

    private LinkedList<Arrow> arrowsList;

    protected Path(){
        this.arrowsList = new LinkedList<>();
    }
    protected Path(Path path, Arrow arrow){
        this(path.getArrowsList());
        this.add(arrow);
    }
    protected Path(Arrow arrow){
        this.arrowsList = new LinkedList<>();
        this.add(arrow);
    }

    protected Path(LinkedList<Arrow> arrowsList){
        this.arrowsList = new LinkedList<>(arrowsList);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Path){
            Path p = (Path) o;
            return arrowsList.equals(p.getArrowsList());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return arrowsList.hashCode();
    }

    public LinkedList<Arrow> getArrowsList() {
        return arrowsList;
    }

    public void setArrowsList(LinkedList<Arrow> pathList) {
        this.arrowsList = pathList;
    }

    public void removeFirst() {
        arrowsList.removeFirst();
    }

    public void add(Arrow arrow){
        this.arrowsList.add(arrow);
    }

    public Integer length(){
        return this.arrowsList.size();
    }

    /**
     * Returns the path type. It'll be equal to:
     * <ul>
     *     <li>{@link #DIR_OUT} if all the arrows {@link Arrow#direction} attribute are equal to {@link Arrow#DIR_OUT}</li>
     *     <li>{@link #DIR_IN} if all the arrows {@link Arrow#direction} attribute are equal to {@link Arrow#DIR_IN}</li>
     *     <li>{@link #DIR_MIX} if the arrows have different {@link Arrow#direction} attribute</li>
     * </ul>
     *
     * @return the path type
     */
    public String getType(){
        String type = arrowsList.getFirst().getDirection();
        for(Arrow a : getArrowsList()){
            if(!a.getDirection().equals(type)){
                type = DIR_MIX;
                break;
            }
        }
        return type;
    }

    public Path getInvertedPath(){
        LinkedList<Arrow> arrowsInverted = new LinkedList<>();
        for(Arrow a : arrowsList){
            arrowsInverted.add(a.getInvertedArrow());
        }
        return new Path(arrowsInverted);
    }


    @Override
    public String toString() {
        return arrowsList.toString();
    }
}
