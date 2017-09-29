package tests;

import java.io.IOException;
import org.rdfhdt.hdt.exceptions.NotFoundException;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleString;
import org.rdfhdt.hdt.triples.TripleString;

import enums.ComponentType;

public class HDTTest extends BaseTest {

	public HDT hdt;
	
	@Override
	public void loadData() throws IOException {
		hdt = HDTManager.mapIndexedHDT(dataFile, null); // load HDT file
	}

	@Override
	public void doWarmup() throws NotFoundException {
		IteratorTripleString it = hdt.search("", "", "","");
		int count = 0;
		while(it.hasNext() && count < 100) {
			it.next();
			count++;
		}
	}

	@Override
	protected void prepareQuery() {
		// nothing to do here
	}
	
	@Override
	public int doSearch() throws NotFoundException {
		IteratorTripleString it;
		try {
			it = hdt.search(
					componentQuad.getSubject().getForHDT(), 
					componentQuad.getPredicate().getForHDT(), 
					componentQuad.getObject().getForHDT(), 
					componentQuad.getGraph().getForHDT());
		} catch (NotFoundException e) {
			System.out.println("Not found in dictionary:");
			System.out.println(componentQuad.getSubject().getForHDT());
			System.out.println(componentQuad.getPredicate().getForHDT());
			System.out.println(componentQuad.getObject().getForHDT());
			System.out.println(componentQuad.getGraph().getForHDT());
			throw e;
		}
		int numberOfResults = 0;
		while(it.hasNext()) {
			TripleString triple = it.next();
			if(verbose) {
				System.out.println(triple);
			}
			numberOfResults++;
		}
		return numberOfResults;
	}

	@Override
	public String getSystem() {
		String[] fileNameParts = dataFile.split("\\.");
		if(fileNameParts[0].endsWith("AT")) {
			return "HDTAT";
		}
		if(fileNameParts[0].endsWith("AG")) {
			return "HDTAG";
		}
		throw new RuntimeException("HDT file does not end with AT or AG: " + dataFile);
	}
	
	/**
	 * Needed by query generation
	 * @param i
	 */
	public long getNumberOfDistinctElements(ComponentType type) {
		switch(type) {
		case SUBJECT:
			return hdt.getDictionary().getNsubjects();
		case PREDICATE:
			return hdt.getDictionary().getNpredicates();
		case OBJECT:
			return hdt.getDictionary().getNobjects();
		case GRAPH:
			return hdt.getDictionary().getNgraphs();
		default:
			throw new RuntimeException("invalid argument: ");
		}
	}
}
