package tests;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.tdb.TDBFactory;

import enums.RDFSystem;
import util.SparqlGenerator;

public class JenaTest extends BaseTest {

	private Dataset dataset;
	private Query query;
	
	@Override
	public void loadData() {
		dataset = TDBFactory.createDataset(dataFile);
	}

	@Override
	protected void doWarmup() {
		String queryString = "SELECT ?s ?p ?o ?g WHERE { "
				+ "GRAPH ?g { ?s ?p ?o} "
				+ "}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		ResultSet results = qexec.execSelect();
		int count = 0;
		while(results.hasNext() && count < 100) {
			results.nextSolution();
			count++;
		}
	}
	
	@Override
	public void prepareQuery() {
		String queryString = SparqlGenerator.getSparql(componentQuad);
		query = QueryFactory.create(queryString);
	}

	@Override
	public int doSearch() {
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		ResultSet results = qexec.execSelect();
		int numberOfResults = 0;
		while(results.hasNext()) {
			results.nextSolution();
			numberOfResults++;
		}
		return numberOfResults;
	}

	@Override
	public String getSystem() {
		return "JENA";
	}

}
