package util;

public class SparqlGenerator {
	
	public static String getSparql(ComponentQuad componentQuad) {
		ComponentString subject = componentQuad.getSubject();
		ComponentString predicate = componentQuad.getPredicate();
		ComponentString object = componentQuad.getObject();
		ComponentString graph = componentQuad.getGraph();
		String select = "", where = "";
		if(componentQuad.allAreSet()) {
			select = "(\"a\" as ?a)"; // if all components are given, some dummy is returned
		} else {
			select = (subject.isEmpty()   ? subject.getForSparql()   : "" ) + 
					 (predicate.isEmpty() ? predicate.getForSparql() : "" ) +
					 (object.isEmpty()    ? object.getForSparql()    : "" ) +
					 (graph.isEmpty()     ? graph.getForSparql()     : "" );
		}
		if(graph.isEmpty()) {
			where = "GRAPH ?g { " + subject.getForSparql() + " " + predicate.getForSparql() + " " + object.getForSparql() + "}";
		} else {
			select += " FROM " + graph.getForSparql() + " ";
			where  += subject.getForSparql() + " " + predicate.getForSparql() + " " + object.getForSparql();
		}
		
		return "SELECT " + select + " WHERE {" + where + "}";
	}
	
	public static String prepareComponent(String component) {
		if("?s".equals(component) ||
		   "?p".equals(component) ||
		   "?o".equals(component) ||
		   "?g".equals(component)) return component;
		component = component.replaceAll("\n", "\\n");
		String[] parts = component.split("\\^\\^");
		if(parts.length == 1) {
			if(!isLiteral(component)) {
				return "<" + component + ">";
			}
		} else {
			return "\"" + parts[0] + "\"^^" + "<" + parts[1] + ">";
			
		}
		return component;
	}
	
	private static boolean isLiteral(String component) {
		return component.startsWith("\"");
	}
}
