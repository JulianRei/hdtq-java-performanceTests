package main;

import java.io.IOException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.shared.JenaException;
import org.apache.jena.tdb.TDBFactory;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;

import util.ComponentQuad;
import util.SparqlGenerator;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class TestVirtuosoJenaHDTResults {
	
	private static HDT hdt;
	private static VirtGraph set;
	private static Dataset dataset;

	public static void main(String[] args) throws Exception {
		int offset = Integer.parseInt(args[0]);
		hdt = HDTManager.mapIndexedHDT("/Users/julian/Dropbox/WU/Master/Masterarbeit/Julian/performanceTests/datasets/LUBM5_1/LUBM1_5AT.hdt", null);
		dataset = TDBFactory.createDataset("/Users/julian/Dropbox/WU/Master/Masterarbeit/Julian/performanceTests/datasets/LUBM5_1/LUBM1_5.tdb");
		set = new VirtGraph ("jdbc:virtuoso://localhost:1111/charset=UTF-8", "dba", "dba");
		
		ComponentQuad componentQuad = new ComponentQuad();
		ComponentQuad queryQuad = new ComponentQuad();
		queryQuad.setAllFromVirtuoso("", "", "", "");
		//String queryString = "SELECT * WHERE { GRAPH ?g {?s ?p ?o}} OFFSET "+offset+" LIMIT 30000"; // wenn alles gut ab 280,000 weiter
		String queryString = SparqlGenerator.getSparql(queryQuad);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (queryString, set);
		ResultSet result = vqe.execSelect();
		while(result.hasNext()) {
			QuerySolution sol = result.next();
			componentQuad.setAllFromVirtuoso(
					sol.get("s").toString(), 
					sol.get("p").toString(), 
					sol.get("o").toString(), 
					sol.get("g").toString()
			);
			testHDT(componentQuad);
			testJena(componentQuad);
			testVirtuoso(componentQuad);
		}
	
	}
	
	private static void testHDT(ComponentQuad componentQuad) throws IOException, NotFoundException {
		String subject = componentQuad.getSubject().getForHDT();
		String predicate = componentQuad.getPredicate().getForHDT();
		String object = componentQuad.getObject().getForHDT();
		String graph = componentQuad.getGraph().getForHDT();
		try {
			IteratorTripleString it = hdt.search(subject, predicate, object, graph);
			if(!it.hasNext()) {
				throw new RuntimeException("HDT has no next for this: " + subject + " || " + predicate + " || " + object + " || " + graph);
			}
		} catch (NotFoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Subject: " + subject);
			System.out.println("Predicate: " + predicate);
			System.out.println("Object: " + object);
			System.out.println("Graph: " + graph);
			System.out.println("i:" + i);
			throw e;
		}
	}

	private static long i = 0;
	private static void testVirtuoso(ComponentQuad componentQuad) {
		String queryString = SparqlGenerator.getSparql(componentQuad);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (queryString, set);
		ResultSet result = null;
		try {
			result = vqe.execSelect();
			if(!result.hasNext()) {
				System.out.println(queryString);
				throw new RuntimeException("Virtuoso has no next for this: \n" + componentQuad);
			}
			i++;
			if(i % 10000 == 0 ) {
				System.out.println(i);
			}
		} catch(JenaException e) {
			e.printStackTrace();
			System.out.println(queryString);
			System.out.println(componentQuad);
		}
	}
	
	private static void testJena(ComponentQuad componentQuad) {
		String queryString = SparqlGenerator.getSparql(componentQuad);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		ResultSet results = qexec.execSelect();
		int count = 0;
		while(results.hasNext() && count < 100) {
			results.nextSolution();
			count++;
		}
	}
}
