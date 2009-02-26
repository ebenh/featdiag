package com.eclipse.featdiag.parser;

import java.io.Serializable;

/**
 * Used to describe edges between methods and fields as well as methods and
 * other methods.
 * 
 * @author Leo
 * @author Michael
 */
public class Edge implements Comparable<Edge>, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String source;
    private String target;
    private boolean targetIsMethod;

    public Edge(String source, String destination, boolean targetIsMethod) {
        this.source = source;
        this.target = destination;
        this.targetIsMethod = targetIsMethod;
    }
    
    public String getSource() {
    	return source;
    }
    
    public String getTarget() {
    	return target;
    }
    
    public boolean targetIsMethod() {
        return targetIsMethod;
    }

    public boolean contains(String name) {
        return source.equals(name) || target.equals(name);
    }

    public boolean containsSubstring(String sub) {
        return source.indexOf(sub) != -1 || target.indexOf(sub) != -1;
    }

    public int hashCode() {
        return source.hashCode() ^ target.hashCode();
    }

    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass())
            return false;
        Edge otherEdge = (Edge) other;
        return (source.equals(otherEdge.getSource()) &&
        		target.equals(otherEdge.getTarget()) &&
        		targetIsMethod == otherEdge.targetIsMethod());
    }
    
    
    public String toString() {
    	String retval = source + " -> " + target;
    	if (targetIsMethod) {
    		retval += "()";
    	}
    	return retval;
    }
    
    
    public int compareTo(Edge e) {
    	if (e == null) {
    		return 1;
    	}
    	return toString().compareTo(e.toString());
    }
}