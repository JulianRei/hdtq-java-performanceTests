package tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.rdfhdt.hdt.quads.QuadString;

import enums.ComponentType;
import util.ComponentQuad;
import util.ComponentString;
import util.SparqlGenerator;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class VirtuosoTest extends BaseTest {

	VirtGraph set;
	Query query;
	
	@Override
	public void loadData() {
		set = new VirtGraph ("jdbc:virtuoso://localhost:1111/charset=UTF-8", "dba", "dba");
	}

	@Override
	protected void doWarmup() throws Exception {
		String queryString = "SELECT ?s ?p ?o ?g WHERE { "
				+ "GRAPH ?g { ?s ?p ?o} "
				+ "}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (queryString, set);
		ResultSet results = vqe.execSelect();
		int count = 0;
		while(results.hasNext() && count < 100) {
			results.next();
			count++;
		}
	}
	
	/**
	 * Needed by query generation
     * Returns the total number of records in the dataset
	 * @return
	 */
	public long getTotalNumberOfRecords() {
		String queryString = "SELECT (count(?s) as ?c) WHERE { "
				+ "GRAPH ?g { ?s ?p ?o} "
				+ "}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (queryString, set);
		ResultSet results = vqe.execSelect();
		QuerySolution result = results.next();
		return result.get("c").asLiteral().getLong();
	}
	
	/**
	 * Needed by query generation

	 * Returns the number of distinct records for the given pattern
	 * @return
	 */
	public long getNumberOfDistinctRecords(String pattern) {
		if(pattern.equals("SPOG")) {
			return getTotalNumberOfRecords();
		}
		if(pattern.equals("????")) {
			throw new RuntimeException("This pattern should be handeled elsewhere: " + pattern);
		}
		String patternWithoutVariables = pattern.replace("?", "");
		String[] parts = patternWithoutVariables.split("");
		List<String> list = Arrays.asList(parts);
		list.replaceAll(x -> "?" + x.toLowerCase());
		String queryString;
		if(list.size() == 1) {
			queryString = "SELECT "
					+ "(COUNT(DISTINCT " + list.get(0) + ") as ?c) "
					+ "WHERE { GRAPH ?g { ?s ?p ?o} }";
		} else {
			queryString = "SELECT "
				+ "(COUNT(DISTINCT concat(" + String.join(",", list) + ")) as ?c) "
				+ "WHERE { GRAPH ?g { ?s ?p ?o} }";
		}
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (queryString, set);
		ResultSet results = vqe.execSelect();
		QuerySolution result = results.next();
		return result.get("c").asLiteral().getLong();
	}
	
	/**
	 * Needed by query generation
	 * Returns the quad at the given position (starting with 0) for the given pattern
	 * @return
	 */
	public QuadString getRecord(String pattern, long position) {
		String patternWithoutVariables = pattern.replace("?", "");
		String[] parts = patternWithoutVariables.split("");
		List<String> list = Arrays.asList(parts);
		list.replaceAll(x -> "?" + x.toLowerCase());
		String queryString;
		if(patternWithoutVariables.equals("SPOG")) {
			queryString = "SELECT * WHERE { "
					+ "GRAPH ?g { ?s ?p ?o} "
					+ "} OFFSET " + position + " LIMIT 1";
		} else {
		queryString = "SELECT DISTINCT " + String.join(" ", list) + " WHERE { "
				+ "GRAPH ?g { ?s ?p ?o} "
				+ "} OFFSET " + position + " LIMIT 1";
		}
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (QueryFactory.create(queryString), set);
		ResultSet results = vqe.execSelect();
		QuerySolution result = results.next();
		return new QuadString(
				pattern.contains("S") ? result.get("s").toString() : "", 
				pattern.contains("P") ? result.get("p").toString() : "",
				pattern.contains("O") ? result.get("o").toString() : "",
				pattern.contains("G") ? result.get("g").toString() : "");
	}
	
	/**
	 * Needed by query generation.
	 * @param askedComponent The component one asks for
	 * @param position The asked for position in the list of records retrieved
	 * @param subject This is given in the query
	 * @param predicate This is given in the query
	 * @param object This is given in the query
	 * @param graph This is given in the query
	 * @return
	 */
	public ComponentString getRecord(ComponentType askedComponent, long position, ComponentQuad componentQuad) {
		String queryString = "SELECT distinct "+askedComponent.getSparqlVariable()+" "
				+"WHERE { GRAPH "+componentQuad.getGraph().getForSparql()+" {"
				+componentQuad.getSubject().getForSparql()+" "
				+componentQuad.getPredicate().getForSparql()+" "
				+componentQuad.getObject().getForSparql()+"}} "
				+"OFFSET "+position+" LIMIT 1";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (QueryFactory.create(queryString), set);
		ResultSet results = vqe.execSelect();
		QuerySolution result = results.next();
		ComponentString componentStringResult = new ComponentString(askedComponent);
		componentStringResult.setFromVirtuoso(result.get(askedComponent.getSparqlVariable().replaceAll("\\?", "")).toString());
		return componentStringResult;
	}
	
	@Override
	public void prepareQuery() {
		String queryString = SparqlGenerator.getSparql(componentQuad);
		query = QueryFactory.create(queryString);
	}

	@Override
	public int doSearch() {
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, set);
		ResultSet results = vqe.execSelect();
		int numberOfResults = 0;
		while(results.hasNext()) {
			results.next();
			numberOfResults++;
		}
		return numberOfResults;
	}

	@Override
	public String getSystem() {
		String[] fileNameParts = dataFile.split("\\.");
		if(fileNameParts[0].endsWith("+")) {
			return "VIRTUOSO+";
		}
		return "VIRTUOSO";
	}

	public long getNumberOfDistinctElements(ComponentType askedComponent, ComponentQuad componentQuad) {
		
		String queryString = "SELECT count(distinct "+askedComponent.getSparqlVariable()+") as ?c "
				+"WHERE { GRAPH "+componentQuad.getGraph().getForSparql()+" {"
				+componentQuad.getSubject().getForSparql()+" "
				+componentQuad.getPredicate().getForSparql()+" "
				+componentQuad.getObject().getForSparql()+"}} ";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (queryString, set);
		System.out.println(queryString);
		ResultSet results = vqe.execSelect();
		QuerySolution result = results.next();
		return result.get("c").asLiteral().getLong();
	}
	
	/**
	 * Returns all entries for given componenttype
	 * @param askedComponent
	 * @return
	 */
	public HashSet<ComponentQuad> getAllElements(ComponentType askedComponent) {
		HashSet<ComponentQuad> resultSet = new HashSet<ComponentQuad>();
		String queryString = "SELECT distinct "+askedComponent.getSparqlVariable()+" as ?v "
				+"WHERE { GRAPH ?g { ?s ?p ?o }}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (queryString, set);
		ResultSet results = vqe.execSelect();
		while(results.hasNext()) {
			QuerySolution result = results.next();
			ComponentQuad quad = new ComponentQuad();
			ComponentString compString = new ComponentString(askedComponent);
			compString.setFromVirtuoso(result.get("v").toString());
			quad.setComponent(compString);
			resultSet.add(quad);
		}
		return resultSet;
	}

	public HashSet<ComponentQuad> getAllElements(String pattern) {
		String patternWithoutVariables = pattern.replace("?", "");
		String[] parts = patternWithoutVariables.split("");
		List<String> list = Arrays.asList(parts);
		list.replaceAll(x -> "?" + x.toLowerCase());
		String queryString;
		if(patternWithoutVariables.equals("SPOG")) {
			queryString = "SELECT * WHERE { GRAPH ?g { ?s ?p ?o} }";
		} else {
			queryString = "SELECT DISTINCT " + String.join(" ", list) + " WHERE { GRAPH ?g { ?s ?p ?o} }";
		}
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (QueryFactory.create(queryString), set);
		ResultSet results = vqe.execSelect();
		HashSet<ComponentQuad> resultSet = new HashSet<ComponentQuad>();
		while(results.hasNext()) {
			QuerySolution result = results.next();
			ComponentQuad quad = new ComponentQuad();
			quad.setAllFromVirtuoso(
					pattern.contains("S") ? result.get("s").toString() : "", 
					pattern.contains("P") ? result.get("p").toString() : "", 
					pattern.contains("O") ? result.get("o").toString() : "", 
					pattern.contains("G") ? result.get("g").toString() : ""
			);
			resultSet.add(quad);
		}
		return resultSet;
	}

	public boolean moreRecordsThan(String pattern, int minNumberOfQueries) {
		String patternWithoutVariables = pattern.replace("?", "");
		String[] parts = patternWithoutVariables.split("");
		List<String> list = Arrays.asList(parts);
		list.replaceAll(x -> "?" + x.toLowerCase());
		String queryString;
		if(patternWithoutVariables.equals("SPOG")) {
			queryString = "SELECT * WHERE { GRAPH ?g { ?s ?p ?o} }";
		} else {
			queryString = "SELECT DISTINCT " + String.join(" ", list) + " WHERE { GRAPH ?g { ?s ?p ?o} }";
		}
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (QueryFactory.create(queryString), set);
		ResultSet results = vqe.execSelect();
		HashSet<ComponentQuad> resultSet = new HashSet<ComponentQuad>();
		int numberOfResults = 0;
		while(results.hasNext()) {
			numberOfResults++;
			if(numberOfResults > minNumberOfQueries) {
				return true;
			}
			results.next();
		}
		return false;
	}

}
