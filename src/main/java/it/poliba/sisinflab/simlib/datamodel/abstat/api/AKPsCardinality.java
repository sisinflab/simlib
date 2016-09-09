package it.poliba.sisinflab.simlib.datamodel.abstat.api;

import java.util.HashMap;

/**
 * Created by giorgio on 26/05/16.
 */
public class AKPsCardinality {

    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String DATATYPE = "datatype";

    private HashMap<String, String> AKPsCardinality;

    public int getCardinalityValue(){
        return Integer.valueOf(AKPsCardinality.get(VALUE));
    }
}
