package it.poliba.sisinflab.simlib.datamodel.abstat.api.responses;

import it.poliba.sisinflab.simlib.datamodel.abstat.api.AKP;

import java.util.List;
import java.util.Map;

/**
 * Created by giorgio on 12/05/16.
 */
public class AbstatAKPResponse {

    private static final String BINDINGS = "bindings";

    Map<String, List<String>> head;

    Map<String, List<AKP>> results;


    public Map<String, List<String>> getHead() {
        return head;
    }

    public void setHead(Map<String, List<String>> head) {
        this.head = head;
    }

    public Map<String, List<AKP>> getResults() {
        return results;
    }

    public void setResults(Map<String, List<AKP>> results) {
        this.results = results;
    }

    public List<AKP> listAKPs(){
        return results.get(BINDINGS);
    }
}
