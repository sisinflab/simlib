package it.poliba.sisinflab.simlib.datamodel.abstat.api;

import it.poliba.sisinflab.simlib.datamodel.abstat.api.responses.AbstatAKPResponse;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.responses.AbstatAKPsCardResponse;
import it.poliba.sisinflab.simlib.datamodel.abstat.api.responses.AbstatResourceOccurrenceResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.concurrent.TimeUnit;

/**
 * Created by giorgio on 12/05/16.
 */
public interface AbstatService {

    String SYSTEM_TEST = "system-test";
    String DBPEDIA_2015_10 = "dbpedia-2015-10";
    String DBPEDIA_INFO_2015_10 = "dbpedia-2015-10-infobox";

    String HOST_CLOUDAPP = "http://abstat.cloudapp.net";
    String HOST_UNIMIB = "http://abstat.disco.unimib.it";
    String API_PATH = "/api/v1/";

    String DATASET_PARAM = "dataset";
    String SUBJECT_PARAM = "subjectType";
    String PREDICATE_PARAM = "predicate";
    String OBJECT_PARAM = "objectType";
    String RANKING_PARAM = "rankingFunction";
    String LIMIT_PARAM = "limit";
    String FORMAT_PARAM = "format";

    String URI_PARAM = "URI";

    @GET("queryWithParams")
    Call<AbstatAKPResponse> minimalPattern(@Query(DATASET_PARAM) String dataset,
                                           @Query(value = SUBJECT_PARAM, encoded = true) String subject,
                                           @Query(value = PREDICATE_PARAM, encoded = true) String predicate,
                                           @Query(value =  OBJECT_PARAM, encoded = true) String object,
                                           @Query(RANKING_PARAM) String ranking,
                                           @Query(LIMIT_PARAM) String limit,
                                           @Query(FORMAT_PARAM) String format);

    @GET("AKPsCardinality")
    Call<AbstatAKPsCardResponse> akpsCardinality(@Query(DATASET_PARAM) String dataset);

    @GET("resourceOccurrence")
    Call<AbstatResourceOccurrenceResponse> resourceOccurrence(@Query(URI_PARAM) String URI);


    static AbstatService serviceFactory(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST_UNIMIB + API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.newBuilder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).writeTimeout(100, TimeUnit.SECONDS).build())
                .build();

        return retrofit.create(AbstatService.class);

    }

}
