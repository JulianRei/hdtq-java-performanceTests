package tests;

import enums.RDFSystem;

public class TestFactory {
	public static BaseTest getTest(RDFSystem system) throws Exception {
		switch(system) {
		case HDT:
			return new HDTTest();
		case JENA:
			return new JenaTest();
		case VIRTUOSO:
			return new VirtuosoTest();
		default:
			throw new Exception("Unknown system, cannot find Test.");
		}	
	}
}
