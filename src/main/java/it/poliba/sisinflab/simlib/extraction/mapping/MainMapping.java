package it.poliba.sisinflab.simlib.extraction.mapping;

import org.apache.jena.vocabulary.OWL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Giorgio Basile
 * @author Paolo Tomeo
 */
public class MainMapping {

    public static void main(String[] args){
        try {
            String working_directory = "data/facebook/music/";
            EntityMapper em = new EntityMapper("http://iceman2.cloudapp.net:10028/sparql", working_directory + "mapping_reduced");
            em.findMappings(EntityMapper.WIKIDATA, URI.create(OWL.sameAs.toString()), false);
            File outputFile = new File( working_directory + "mapping_reduced_wikidata");
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();

            em.write(new FileOutputStream(outputFile, false), EntityMapper.TSV);

            //INVERSE MAPPING FROM DBPEDIA
            /*nm.readURIStrings(new ArrayList<>(Arrays.asList("http://www.wikidata.org/entity/Q312", "http://www.wikidata.org/entity/Q22", "http://www.wikidata.org/entity/Q22")));
            nm.findMappings(DBPEDIA, URI.create(OWL.sameAs.toString()), true);*/
            //createMapping(em);
            //nm.write(new FileOutputStream(f, false), CSV);


        } catch (MalformedURLException | URISyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
