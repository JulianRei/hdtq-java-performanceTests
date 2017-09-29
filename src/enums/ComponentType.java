package enums;

public enum ComponentType {
	SUBJECT(0, "?s"),
	PREDICATE(1, "?p"),
	OBJECT(2, "?o"),
	GRAPH(3, "?g");
	
	private int value;
	private String sparqlVariable;
	
	private ComponentType(int value, String sparqlVariable) {
		this.value = value;
		this.sparqlVariable = sparqlVariable;
	}
	
	public static ComponentType getComponent(int i) {
		switch(i) {
		case 0: return SUBJECT;
		case 1: return PREDICATE;
		case 2: return OBJECT;
		case 3: return GRAPH;
		}
		throw new RuntimeException("Invalid parameter: " + i);
	}
	
	public int getValue() {
		return value;
	}
	
	public String getSparqlVariable() {
		return sparqlVariable; 
	}
}