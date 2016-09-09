package it.poliba.sisinflab.simlib.datamodel.abstat.api.responses;

import it.poliba.sisinflab.simlib.datamodel.abstat.api.AKPsCardinality;

import java.util.List;
import java.util.Map;

/**
 * Created by giorgio on 26/05/16.
 */
public class AbstatAKPsCardResponse {
    private static final String BINDINGS = "bindings";

    Map<String, List<String>> head;

    Map<String, List<AKPsCardinality>> results;


    public Map<String, List<String>> getHead() {
        return head;
    }

    public void setHead(Map<String, List<String>> head) {
        this.head = head;
    }

    public Map<String, List<AKPsCardinality>> getResults() {
        return results;
    }

    public void setResults(Map<String, List<AKPsCardinality>> results) {
        this.results = results;
    }

    public int getAKPsCardinality(){
        return results.get(BINDINGS).get(0).getCardinalityValue();
    }
}
