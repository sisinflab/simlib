package it.poliba.sisinflab.simlib.datamodel.abstat.api;

import java.util.HashMap;

/**
 * Created by giorgio on 26/05/16.
 */
public class ResourceOccurrence {

    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String DATATYPE = "datatype";

    private HashMap<String, String> freq;

    public int getFreqValue(){
        return Integer.valueOf(freq.get(VALUE));
    }
}
