package it.poliba.sisinflab.simlib.datamodel.abstat;

import it.poliba.sisinflab.simlib.datamodel.Arrow;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.datamodel.Node;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.AKP;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.ResourceOccurrence;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.responses.AbstatAKPResponse;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.AbstatService;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.responses.AbstatAKPsCardResponse;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.responses.AbstatResourceOccurrenceResponse;
import retrofit2.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giorgio on 12/05/16.
 */
public class AbstatGraph extends Graph {

    private AbstatService abstatService;
    private int statementsCount = -1;
    private String graphURI;

    public AbstatGraph(String graphURI){
        super();
        this.graphURI = graphURI;
        this.abstatService = AbstatService.serviceFactory();
        this.statementsCount = getAPKsCardinality(graphURI);
    }

    @Override
    public Node createNode(String id){
        if(!nodes.containsKey(id)){
            nodes.put(id, new AbstatNode(id, this));
        }
        return nodes.get(id);
    }

    public Node createNode(String id, int frequency){
        if(!nodes.containsKey(id)){
            nodes.put(id, new AbstatNode(id, this, frequency));
        }
        return nodes.get(id);
    }

    public Arrow createArrow(AbstatNode p, String direction, int frequency){
        return new AbstatArrow(p, direction, frequency);
    }

    @Override
    public Node getNode(String id){
        return createNode(id);
    }

    @Override
    public int getArrowOccurrences(Arrow arrow){
        return getAkps(graphURI, null, arrow.getProperty().getId(), null,
                null, String.valueOf(Integer.MAX_VALUE), null).size();
    }

    @Override
    public int getStatementsCount(){
        if(statementsCount == -1){
            statementsCount = getAPKsCardinality(graphURI);
        }
        return statementsCount;
    }

    public AbstatService getAbstatService() {
        return abstatService;
    }

    public void setAbstatService(AbstatService abstatService) {
        this.abstatService = abstatService;
    }

    public List<AKP> getAkps(String dataset, String subject, String predicate, String object,
                              String ranking, String limit, String format){

        //needed until ABSTAT encoding issues are solved
        String subjAPI = subject;
        if(subjAPI != null){
            subjAPI = subjAPI.replace("#", "%23");
        }
        String predAPI = predicate;
        if(predAPI != null){
            predAPI = predAPI.replace("#", "%23");
        }
        String objAPI = object;
        if(objAPI != null){
            objAPI = objAPI.replace("#", "%23");
        }

        Call<AbstatAKPResponse> abstatResponse =
                getAbstatService().minimalPattern(dataset, subjAPI, predAPI, objAPI, ranking, limit, format);

        try {

            AbstatAKPResponse resp = abstatResponse.execute().body();
            List<AKP> akps = resp.listAKPs();
            return akps;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int getAPKsCardinality(String dataset){

        Call<AbstatAKPsCardResponse> abstatResponse =
                getAbstatService().akpsCardinality(dataset);

        try {

            AbstatAKPsCardResponse resp = abstatResponse.execute().body();
            int akps = resp.getAKPsCardinality();
            return akps;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getResourceOccurrence(String resourceURI){
        if(resourceURI.endsWith("www.w3.org/2002/07/owl#Thing")){
            return 0;
        }

        Call<AbstatResourceOccurrenceResponse> abstatResponse =
                getAbstatService().resourceOccurrence(resourceURI);

        try {

            AbstatResourceOccurrenceResponse resp = abstatResponse.execute().body();
            int occ = resp.getResourceOccurrence();
            return occ;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getGraphURI() {
        return graphURI;
    }

    public void setGraphURI(String graphURI) {
        this.graphURI = graphURI;
    }
}
