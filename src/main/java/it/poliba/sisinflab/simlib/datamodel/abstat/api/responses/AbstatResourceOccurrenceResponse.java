package it.poliba.sisinflab.simlib.datamodel.abstat.api.responses;

import it.poliba.sisinflab.simlib.datamodel.abstat.api.ResourceOccurrence;

import java.util.List;
import java.util.Map;

/**
 * Created by giorgio on 26/05/16.
 */
public class AbstatResourceOccurrenceResponse {
    private static final String BINDINGS = "bindings";

    Map<String, List<String>> head;

    Map<String, List<ResourceOccurrence>> results;


    public Map<String, List<String>> getHead() {
        return head;
    }

    public void setHead(Map<String, List<String>> head) {
        this.head = head;
    }

    public Map<String, List<ResourceOccurrence>> getResults() {
        return results;
    }

    public void setResults(Map<String, List<ResourceOccurrence>> results) {
        this.results = results;
    }

    public int getResourceOccurrence(){
        if(results.get(BINDINGS).size() == 0){
            //special case for owl:Thing
            return 0;
        }
        return results.get(BINDINGS).get(0).getFreqValue();
    }
}
