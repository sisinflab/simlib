package it.poliba.sisinflab.simlib.extraction.mapping;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Creates a mapping of a URI from a knowledge graph to another one, based on a given mapping property (i.e. owl:sameAs)
 *
 * @author Giorgio Basile
 * @author Paolo Tomeo
 * @since 1.0
 */
public class EntityMapper {

    private HashMap<String, String> inpuMapping;
    private Model model;
    private Property mappingProperty;
    private URL sparqlEndpoint;
    private URI graphURI;
    private URI targetNamespace;

    public static final URI DBPEDIA = URI.create("http://dbpedia.org");
    public static final URI FREEBASE = URI.create("http://rdf.freebase.com");
    public static final URI BASEKB = URI.create("http://rdf.basekb.com");
    public static final URI WIKIDATA = URI.create("http://www.wikidata.org");
    private static final URI WIKIDATA_ALTERNATIVE = URI.create("http://wikidata.org");

    public static final String CSV = "csv";
    public static final String TSV = "tsv";

    private List<Resource> resources;

    public EntityMapper(String sparqlEndpointString, String inputFile) throws MalformedURLException{

        model = ModelFactory.createDefaultModel();
        sparqlEndpoint = new URL(sparqlEndpointString);
        resources = new ArrayList<>();
        try {
            this.inpuMapping = readMapping( new File(inputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.inpuMapping.keySet().forEach(uri -> resources.add(model.createResource(uri)));
    }

//    private void readURIStrings(List<String> uriStrings) throws IllegalArgumentException{
//        model.removeAll();
//        resources.clear();
//        for(String uriString : uriStrings) {
//            Resource resource = model.createResource(uriString);
//            resources.add(resource);
//        }
//    }

    public void findMappings(URI targetNamespaceString, URI mappingPropertyString, boolean inverse)
            throws MalformedURLException, URISyntaxException {

        findMappings(URI.create(""), targetNamespaceString, mappingPropertyString, inverse);

    }

    public void findMappings(URI sparqlGraphURI, URI targetNamespaceString, URI mappingPropertyString, boolean inverse)
            throws MalformedURLException, URISyntaxException {

        mappingProperty = model.createProperty(mappingPropertyString.toString());
        graphURI = sparqlGraphURI;
        targetNamespace = targetNamespaceString;

        resources.parallelStream().forEach(r -> findMapping(r, inverse));

//        for(Resource resource : resources){
//            findMapping(resource, inverse);
//        }

    }

    private void findMapping(Resource subject, boolean inverse){
        try {
            boolean found = false;
            String variable = "v";
            String pattern = "?" + variable;
            SelectBuilder builder = new SelectBuilder();
            if (!inverse) {
                builder.addVar(pattern)
                        .addWhere(subject, mappingProperty, pattern);
            } else {
                builder.addVar(pattern)
                        .addWhere(pattern, mappingProperty, subject);
            }

            Query query = builder.build();
            QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint.toString(), query, graphURI.toString());
            ResultSet results = qexec.execSelect();
            while (results.hasNext() && !found) {
                QuerySolution sln = results.nextSolution();
                Resource object = sln.getResource(variable);
                if (object.getNameSpace().startsWith(FREEBASE.toString()) && targetNamespace.toString().startsWith(BASEKB.toString())) {
                    String entityID = object.toString().substring(FREEBASE.toString().length());
                    object = model.createResource(BASEKB + entityID);
                }else if (object.getNameSpace().startsWith(WIKIDATA_ALTERNATIVE.toString()) && targetNamespace.toString().startsWith(WIKIDATA.toString())) {
                    String entityID = object.toString().substring(WIKIDATA_ALTERNATIVE.toString().length());
                    object = model.createResource(WIKIDATA + entityID);
                }

                if (object.getNameSpace().startsWith(targetNamespace.toString())) {
                    Statement s;
                    if (!inverse) {
                        s = model.createStatement(subject, mappingProperty, object);
                    } else {
                        s = model.createStatement(object, mappingProperty, subject);
                    }

                    //System.out.println(s.toString());
                    model.add(s);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("Mapping not found for URI: " + subject.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("ERROR finding mapping for URI: " + subject.toString());

        }
    }

    public void write(OutputStream out, String customSerialization) throws IOException {
        StmtIterator iter = model.listStatements();
        if(customSerialization.equals(CSV)){
            while(iter.hasNext()){
                Statement s = iter.nextStatement();
                String line = this.inpuMapping.get(s.getSubject().toString()) /*+ "," + s.getPredicate()*/ + "," + s.getObject() + "\n";
                out.write(line.getBytes());
            }
        }else if(customSerialization.equals(TSV)){
            while(iter.hasNext()){
                Statement s = iter.nextStatement();
                System.out.println(s.getSubject());
                String line = this.inpuMapping.get(s.getSubject().toString()) /*+ "\t" + s.getPredicate()*/ + "\t" + s.getObject() + "\n";
                out.write(line.getBytes());
            }
        }
    }

    public void write(OutputStream out, RDFFormat serialization){
        RDFDataMgr.write(out, model, serialization);
    }

    private static HashMap<String, String> readMapping(File file) throws IOException {
        HashMap<String, String> mapping = new HashMap<>();
        Files.lines(Paths.get(file.getPath()), Charset.forName("UTF-8"))
                .forEach(l -> mapping.put(l.split("\t")[1], l.split("\t")[0]));
        return mapping;
    }
}
