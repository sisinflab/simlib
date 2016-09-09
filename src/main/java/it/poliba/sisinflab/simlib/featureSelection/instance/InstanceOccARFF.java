package it.poliba.sisinflab.simlib.featureSelection.instance;

import java.util.HashMap;


/**
 * Defines a single value of occurrence for each propertie of an instance
 *
 * @author Corrado Magarelli
 * 
 */
public class InstanceOccARFF extends InstanceARFF {

    private HashMap<String,Integer> propoccurrence;

    public InstanceOccARFF(Integer item, HashMap<String,Integer> propoccurrence) {
        super(item);
        this.propoccurrence = propoccurrence;
    }

    public HashMap<String,Integer> getPropoccurrence() {
        return propoccurrence;
    }

    public void setPropoccurrence(HashMap<String,Integer>  propoccurrence) {
        this.propoccurrence = propoccurrence;
    }

}
