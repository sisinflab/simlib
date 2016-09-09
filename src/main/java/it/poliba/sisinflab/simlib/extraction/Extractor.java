package it.poliba.sisinflab.simlib.extraction;

import it.poliba.sisinflab.simlib.Utilities;
import org.apache.jena.query.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A simple knowledge graph extractor from a SPARQL endpoint.
 *
 * The Extractor class is able to extract some RDF triples, taking as input a list of items and a list of predicates.
 * Given two sets I = {i1, i2}, P = {p1, p2}, the extraction task will take all the RDF triples having as subject
 * an item from I and as predicate an element of P. i.e.
 * i1 p1 x1
 * i1 p1 x2
 * i2 p1 x3
 * i1 p2 x2
 *
 * @author Paolo Tomeo
 * @author Giorgio Basile
 * @author Corrado Magarelli
 * @since 1.0
 */
public class Extractor {

	private static final String QUERY_TEMPLATE = "SELECT ?o WHERE {{<it_em> <pr_op>  ?o .}}";
	private static final String QUERY_TEMPLATE_INV = "SELECT ?o WHERE {{?o <pr_op>  <it_em> .}}";

	private static final String QUERY_TEMPLATE_DIST = "SELECT ?p ?o WHERE {{<it_em> ?p  ?o .}}";
	private static final String QUERY_TEMPLATE_DIST_NOLITERAL = "SELECT ?p ?o WHERE {{<it_em> ?p  ?o} . FILTER(!isLiteral(?o))}";
	private static final String QUERY_TEMPLATE_SUBPROP_WIKIDATA = "select ?s ?o where{ <it_em> ?s ?o . ?p <http://www.wikidata.org/prop/direct/P1647>* <pr_op> . " +
			"?p <http://wikiba.se/ontology#directClaim> ?s .}";

	private String endpoint;
	private HashSet<String> cacheQueries;

	public Extractor(String endpoint){
		this.endpoint = endpoint;
		this.cacheQueries = new HashSet<>();
	}

	public List<String> startExtraction(List<String> items, List<LinkedList<String>> paths) throws IOException{
		return extractionTask(items, paths);
	}

	public List<String> startExtraction(List<String> items, int distance) throws IOException{
		return extractionTask(items, distance);
	}

	private List<String> extractionTask(List<String> items, List<LinkedList<String>> paths) {
		List<String> triples = items.parallelStream()
				.flatMap(i -> extract(i, paths).stream())
				.collect(Collectors.toList());
		return triples;
	}

	private Set<String> extract(String subject, List<LinkedList<String>> paths) {
		System.out.println(subject);
		Set<String> triples = new HashSet<>();

		for (LinkedList<String> path : paths) {
			Set<String> pathTriples = extract(subject, path);
			/*if(pathTriples.size() == 0) {
				return new HashSet<>();
			}else{*/
			triples.addAll(pathTriples);
			//}
		}
		return triples;
	}

	private Set<String> extract(String subject, LinkedList<String> path){

		HashSet<String> triples = new HashSet<>();
		String q = "";
		String prop;
		if(path.size() != 0) {
			prop = path.getFirst();
			try {
				if(prop.startsWith("http://www.wikidata.org/entity/")){
					q = QUERY_TEMPLATE_SUBPROP_WIKIDATA.replaceAll("it_em", Utilities.uriEncoding(subject)).replaceAll("pr_op", Utilities.uriEncoding(prop));
				}else {
					q = QUERY_TEMPLATE.replaceAll("it_em", Utilities.uriEncoding(subject)).replaceAll("pr_op", Utilities.uriEncoding(prop));
				}
				if(!cacheQueries.contains(q)){

					Query query = QueryFactory.create(q);
					QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
					ResultSet results = qexec.execSelect();

					while (results.hasNext()) {
						QuerySolution qs = results.next();

						String object = URLDecoder.decode(qs.get("o").toString(), "UTF-8");
						processObject(subject, prop, object, triples);
						cacheQueries.add(q);
						//System.out.println(triple);
						LinkedList<String> subPath = new LinkedList<>(path);
						subPath.removeFirst();
						triples.addAll(extract(object, subPath));
					}
					qexec.close();
				}
			} catch (Exception exp) {
				exp.printStackTrace();
				System.out.println(q);
			}
		}
		return triples;
	}

	private List<String> extractionTask(List<String> items, int distance) {
		List<String> extracted = new ArrayList<>(); //contains items for which we have already extracted the properties
		List<String> out = new ArrayList<>();
		List<String> last_triples; //last extracted triples
		if(distance < 1)
			return out;

		last_triples = extractAllProps(items);
		out.addAll(last_triples);
		extracted.addAll(items);
		for (int i = 1; i < distance; i++){
			List<String> last_objects = last_triples.stream()
					.map(t -> t.split("\t")[2])
					.filter(o -> !extracted.contains(o))
					.collect(Collectors.toList());
			last_triples = extractAllProps(last_objects);
			out.addAll(last_triples);
			extracted.contains(last_objects);
		}
		return out;
	}
	private List<String> extractAllProps(List<String> items){
		return items.parallelStream()
				.flatMap(i -> extractAllProps(i).stream())
				.collect(Collectors.toList());
	}

	private Set<String> extractAllProps(String subject){
		System.out.println("query for " + subject);
		HashSet<String> triples = new HashSet<>();
		//String q = QUERY_TEMPLATE_DIST.replaceAll("it_em", Utilities.uriEncoding(subject));

		if(!Utilities.isURI(subject))
			return  triples;

		String q = QUERY_TEMPLATE_DIST_NOLITERAL.replace("it_em", Utilities.uriEncoding(subject));

		if(!cacheQueries.contains(q)){

			Query query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
			ResultSet results;
			try {
				results = qexec.execSelect();
			}catch(Exception ex){
				System.out.println("Error with URI: " + subject);
				return triples;
			}
			cacheQueries.add(q);
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				String prop = "", object = "";
				try {
					prop = URLDecoder.decode(qs.get("p").toString(), "UTF-8");
					object = URLDecoder.decode(qs.get("o").toString(), "UTF-8");
					if(!object.contains(".jpg") && !object.contains("/entity/statement/"))
						processObject(subject, prop, object, triples);
//                    if(distance - 1 > 0 && Utilities.isURI(object))
//					    triples.addAll(extract(object, distance - 1));
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("Error parsing a triple with subject: " + subject + " and predicate: " + prop);
				}

			}
			qexec.close();
		}

		return triples;
	}

	private void processObject(String subject, String prop, String object, HashSet<String> triples){
		// Some objects contain * in DBpedia
		if(object.contains("*"))
		{
			String[] objectc = object.split("@");
			String obj = objectc[0].replaceAll("\n", "");
			String[] objstar = obj.split("\\* ");
			//String[] objdef =  objectstar[1].split(" ");
			for(int i=1;i<objstar.length;i++)
			{
				String uriobject = "http://dbpedia.org/resource/"+objstar[i].replaceAll(" ", "_");
				String triple = subject + "\t" + prop + "\t" + uriobject;
				triples.add(triple);
			}
		}
		//Some objects contain \n or \t in DBpedia
		else if(object.contains("\n"))
		{
			String obj = object.replaceAll("\n", " ");
			String triple = subject + "\t" + prop + "\t" + obj;
			triples.add(triple);
		}else
		{
			String triple = subject + "\t" + prop + "\t" + object;
			triples.add(triple);
		}
	}
}