package util;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import enums.ComponentType;

public class ComponentQuad {
	private ComponentString subject, predicate, object, graph;
	
	public ComponentQuad() {
		subject = new ComponentString(ComponentType.SUBJECT);
		predicate = new ComponentString(ComponentType.PREDICATE);
		object = new ComponentString(ComponentType.OBJECT);
		graph = new ComponentString(ComponentType.GRAPH);
	}
	
	public ComponentQuad(ComponentString subject, ComponentString predicate, ComponentString object, ComponentString graph) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.graph = graph;
	}
	
	public void setComponent(ComponentString component) {
		switch(component.getType()) {
		case SUBJECT:
			subject = component;
			break;
		case PREDICATE:
			predicate = component;
			break;
		case OBJECT:
			object = component;
			break;
		case GRAPH:
			graph = component;
			break;
		}
	}

	public ComponentString getSubject() {
		return subject;
	}

	public ComponentString getPredicate() {
		return predicate;
	}

	public ComponentString getObject() {
		return object;
	}

	public ComponentString getGraph() {
		return graph;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,31)
				.append(subject.getForHDT())
				.append(predicate.getForHDT())
				.append(object.getForHDT())
				.append(graph.getForHDT())
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    ComponentQuad other = (ComponentQuad) obj;
	    if (!subject.getForHDT().equals(other.subject.getForHDT()))
	        return false;
	    if (!predicate.getForHDT().equals(other.predicate.getForHDT()))
	        return false;
	    if (!object.getForHDT().equals(other.object.getForHDT()))
	    	return false;
	    if (!graph.getForHDT().equals(other.graph.getForHDT()))
	    	return false;
	    return true;
	}

	public boolean allAreSet() {
		return ! (subject.getString().equals("") || predicate.getString().equals("") || object.getString().equals("") || graph.getString().equals(""));
	}
	
	@Override
	public String toString() {
		return "Subject:\t" + subject.getString() + "\nPredicate:\t" + predicate.getString() + "\nObject:\t\t" + object.getString() + "\nGraph:\t\t" + graph.getString();
	}
	
	public void setAllFromVirtuoso(String subject, String predicate, String object, String graph) {
		this.subject.setFromVirtuoso(subject);
		this.predicate.setFromVirtuoso(predicate);
		this.object.setFromVirtuoso(object);
		this.graph.setFromVirtuoso(graph);
	}

	public static String getJSONString(Collection<ComponentQuad> queries) {
		JSONArray jsonArray = new JSONArray();
		for(ComponentQuad query : queries) {
			jsonArray.add(query.getJSONObject());
		}
		return jsonArray.toJSONString();
	}
	
	public static JSONArray getJSONArray(Collection<ComponentQuad> queries) {
		JSONArray jsonArray = new JSONArray();
		for(ComponentQuad query : queries) {
			jsonArray.add(query.getJSONObject());
		}
		return jsonArray;
	}

	private JSONObject getJSONObject() {
		JSONObject json = new JSONObject();
		json.put("subject", subject.getString());
		json.put("predicate", predicate.getString());
		json.put("object", object.getString());
		json.put("graph", graph.getString());
		return json;
	}
}
