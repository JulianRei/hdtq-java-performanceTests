package util;

import enums.ComponentType;

public class ComponentString {
	private String string = "";  // this is set by virtuoso, e.g. 1^^http://www.w3.org/2001/XMLSchema#nonNegativeInteger
	private ComponentType type;
	
	public ComponentString(ComponentType type) {
		this.type = type;
	}
	
	public boolean isEmpty() {
		return "".equals(string);
	}
	
	public ComponentType getType() {
		return type;
	}
	
	public String getString() {
		return string;
	}
	
	public void setFromVirtuoso(String string) {
		this.string = string;
	}
	
	public String getForHDT() {
		if(isEmpty()) {
			return string;
		}
		String returnValue = string;
		
		if(returnValue.contains("^^")) {
			String parts[] = returnValue.split("\\^\\^");
			return "\"" + parts[0] + "\"^^" + parts[1]; 
		}
		
		if(isValidEmailAddress(string)) {
			return "\""+returnValue+"\"";
		}
		
		if(returnValue.contains("@")) {
			String parts[] = returnValue.split("@");
			return "\"" + parts[0] + "\"@" + parts[1];
		}
		
		if(returnValue.startsWith("http")) {
			return returnValue;
		}
		
		return "\""+returnValue+"\"";
	}
	
	public String getForSparql() {
		String returnValue = string;
		if(returnValue.equals("")) {
			return type.getSparqlVariable() + " ";
		}
		returnValue = returnValue.replace("\\", "\\\\"); // basically, backslashes must be escaped with a backslash
		returnValue = returnValue.replace("\\\\\"", "\\\""); // but if the backslash escapes a quote, we need to remove the extra backslash again
		returnValue = returnValue.replace("\n", "\\n");
		
		if(returnValue.contains("^^")) {
			String parts[] = returnValue.split("\\^\\^");
			return "\"" + parts[0] + "\"^^<" + parts[1] + ">"; 
		}
		if(returnValue.startsWith("http")) {
			return "<" + returnValue + ">";
		}
		return "\""+returnValue+"\"";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) 
			return true;
		if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    ComponentString other = (ComponentString) obj;
		return string.equals(other.string);
	}
	
	public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
	}
}
