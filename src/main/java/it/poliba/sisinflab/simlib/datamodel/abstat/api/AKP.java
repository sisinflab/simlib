package it.poliba.sisinflab.simlib.datamodel.abstat.api;

import java.util.Map;

/**
 * Created by giorgio on 12/05/16.
 */
public class AKP {

    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String DATATYPE = "datatype";

    private Map<String, String> akp;
    private Map<String, String> subj;
    private Map<String, String> pred;
    private Map<String, String> obj;
    private Map<String, String> akp_frequency;
    private Map<String, String> subj_frequency;
    private Map<String, String> pred_frequency;
    private Map<String, String> obj_frequency;

    public String getAkpValue(){
        return akp.get(VALUE);
    }

    public String getSubjValue(){
        return subj.get(VALUE);
    }

    public String getPredValue(){
        return pred.get(VALUE);
    }

    public String getObjValue(){
        return obj.get(VALUE);
    }

    public int getAkpFrequencyValue(){
        return Integer.parseInt(akp_frequency.get(VALUE));
    }

    public int getSubjFrequencyValue(){
        if(subj_frequency == null){
            //special case for owl:Thing
            return 0;
        }
        return Integer.parseInt(subj_frequency.get(VALUE));
    }

    public int getPredFrequencyValue(){
        return Integer.parseInt(pred_frequency.get(VALUE));
    }

    public int getObjFrequencyValue(){
        if(obj_frequency == null){
            //special case for owl:Thing
            return 0;
        }
        return Integer.parseInt(obj_frequency.get(VALUE));
    }

}
