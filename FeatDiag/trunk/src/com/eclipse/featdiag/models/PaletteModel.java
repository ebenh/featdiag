package com.eclipse.featdiag.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.gef.EditPart;

import com.eclipse.featdiag.parser.Edge;


/**
 * Palette Model class that contains the models of 
 * all the members in the flyout palette.
 * @author nic
 *
 */
public class PaletteModel extends BaseModel {
	private static final long serialVersionUID = 1L;
	
	/*
	 * The models of the fields and methods that are
	 * currently displayed in the palette.
	 */
	private Map<String, FieldModel> fieldModels;
	private Map<String, MethodModel> methodModels;
	
	/*
	 * The edges that should be added to the diagram, but
	 * are dependent on certain members being added to the
	 * diagram first.
	 * An edge is associated with both it's source and target
	 * nodes while either node is displayed in the palette. 
	 * When both nodes are removed from the palette, the edge
	 * is disassociated with both it's source and target.
	 */
	private Map<String, List<Edge>> fieldEdges;
	private Map<String, List<Edge>> methodEdges;

	/**
	 * Create a new palette model
	 */
	public PaletteModel() {
		this.methodModels = new HashMap<String, MethodModel>();
		this.fieldModels = new HashMap<String, FieldModel>();
		this.fieldEdges = new HashMap<String, List<Edge>>();
		this.methodEdges = new HashMap<String, List<Edge>>();
	}
	
	/**
	 * Return the methods in the palette.
	 * @return
	 */
	public Collection<MethodModel> getMethods() {
		return methodModels.values();
	}
	
	/**
	 * Return the fields in the palette.
	 * @return
	 */
	public Collection<FieldModel> getFields() {
		return fieldModels.values();
	}
	
	/**
	 * Returns a set of all edges between a member in the
	 * palette and either another member in the palette or
	 * a member in the diagram.
	 * @return
	 */
	public Collection<Edge> getEdges() {
		Set<Edge> retval = new HashSet<Edge>();
		
		for (List<Edge> edges : fieldEdges.values()) {
			retval.addAll(edges);
		}
		for (List<Edge> edges : methodEdges.values()) {
			retval.addAll(edges);
		}
		
		return retval;
	}
	
	/**
	 * Add a new method to the palette
	 * @param method
	 */
	public void addNewMethod(MethodModel method) {
		if (!methodModels.containsKey(method.getName())) {
			methodModels.put(method.getName(), method);
			firePropertyChange(PALETTE_METHOD, null, method);
		}
	}
	
	/**
	 * Add a new field to the palette
	 * @param field
	 */
	public void addNewField(FieldModel field) {
		if (!fieldModels.containsKey(field.getName())) {
			fieldModels.put(field.getName(), field);
			firePropertyChange(PALETTE_FIELD, null, field);
		}
	}
	
	/**
	 * Removes a method from the palette, and removes
	 * any edges that now have neither source nor target
	 * node in the palette.
	 * @param method
	 */
	public boolean removeMethod(MethodModel method) {
		if (methodModels.remove(method.getName()) != null) {
			removeEdges(method, true);
			firePropertyChange(PALETTE_METHOD, method, null);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a field from the palette, and removes
	 * any edges that now have neither source nor target
	 * node in the palette.
	 * @param field
	 */
	public boolean removeField(FieldModel field) {
		if (fieldModels.remove(field.getName()) != null) {
			removeEdges(field, false);
			firePropertyChange(PALETTE_FIELD, field, null);
			return true;
		}
		return false;
	}
	
	/**
	 * If the method with the given name is in the palette,
	 * returns the method model. Otherwise returns null.
	 * @param methodName
	 * @return
	 */
	public MemberModel getMethod(String methodName) {
		return methodModels.get(methodName);
	}
	
	/**
	 * If the field with the given name is in the palette,
	 * return the field model. Otherwise returns null.
	 * @param fieldName
	 * @return
	 */
	public MemberModel getField(String fieldName) {
		return fieldModels.get(fieldName);
	}
	
	/**
	 * Associates the given edge with both it's source
	 * and target nodes if either is in the palette.
	 * @param edge
	 */
	public void addEdge(Edge edge) {
		// If either or both of the source and target of
		// the edge are in the palette, add this edge to
		// to list of edges to be added to the diagram.
		if (sourceInPalette(edge) || targetInPalette(edge)) {
			addEdge(edge.getSource(), edge, true);
			addEdge(edge.getTarget(), edge, edge.targetIsMethod());
		}
	}
	
	/**
	 * Removes the given edge from the list of
	 * edges to be added to the diagram.
	 * @param edge
	 */
	public void removeEdge(Edge edge) {
		removeEdge(edge.getSource(), edge);
		removeEdge(edge.getTarget(), edge);
	}
	
	/**
	 * Returns all edges to be added to the diagram
	 * that are connected to the given field, and whose
	 * other connection is not in the palette.
	 * @param field
	 * @return
	 */
	public List<Edge> getEdges(FieldModel field) {
		return getEdges(field, false);
	}
	
	/**
	 * Returns all edges to be added to the diagram
	 * that are connected to the given method, and whose
	 * other connection is not in the palette.
	 * @param method
	 * @return
	 */
	public List<Edge> getEdges(MethodModel method) {
		return getEdges(method, true);
	}
	
	/**
	 * Disassociates the given edge with any methods or
	 * fields that are associated with it.
	 * @param key
	 * @param edge
	 */
	private void removeEdge(String key, Edge edge) {
		List<Edge> edges = fieldEdges.get(key);
		if (edges != null) {
			edges.remove(edge);
		}
		edges = methodEdges.get(key);
		if (edges != null) {
			edges.remove(edge);
		}
	}
	
	/**
	 * Returns all edges associated with the given member whose
	 * other connection is not in the palette.
	 * Method is true if the given member is a MethodModel, and
	 * false if it is a FieldModel.
	 * @param member
	 * @param method
	 * @return
	 */
	private List<Edge> getEdges(MemberModel member, boolean method) {
		List<Edge> retval = new Vector<Edge>();
		
		List<Edge> memberEdges = (method) ? methodEdges.get(member.getName()) : fieldEdges.get(member.getName());
		if (memberEdges != null) {
			for (Edge edge : memberEdges) {
				if ((edge.getSource().equals(member.getName()) && method && !targetInPalette(edge)) ||
						(edge.getTarget().equals(member.getName()) && (method == edge.targetIsMethod()) && !sourceInPalette(edge))) {
					retval.add(edge);
				}
			}
		}
		
		return retval;
	}
	
	/**
	 * Removes all edges associated with the given member whose
	 * other connection is not in the palette.
	 * Method is true if the given member is a MethodModel, and
	 * false if it is a FieldModel.
	 * @param member
	 * @param method
	 */
	private void removeEdges(MemberModel member, boolean method) {
		List<Edge> memberEdges = (method) ? methodEdges.get(member.getName()) : fieldEdges.get(member.getName());
		if (memberEdges != null) {
			List<Edge> toRemove = new Vector<Edge>();
			for (Edge edge : memberEdges) {
				if (edge.getSource().equals(member.getName()) && method && !targetInPalette(edge)) {
					toRemove.add(edge);
					List<Edge> edges = (edge.targetIsMethod()) ? methodEdges.get(edge.getTarget()) : fieldEdges.get(edge.getTarget());
					if (edges != null) {
						edges.remove(edge);
					}
				}
				else if (edge.getTarget().equals(member.getName()) && (method == edge.targetIsMethod()) && !sourceInPalette(edge)) {
					toRemove.add(edge);
					List<Edge> edges = methodEdges.get(edge.getSource());
					if (edges != null) {
						edges.remove(edge);
					}
				}
			}
			memberEdges.removeAll(toRemove);
		}
	}
	
	/**
	 * Returns true if the source model of the given edge
	 * is in the palette, and false otherwise.
	 * @param edge
	 * @return
	 */
	private boolean sourceInPalette(Edge edge) {
		return (methodModels.get(edge.getSource()) != null);
	}
	
	/**
	 * Returns true if the target model of the given edge
	 * is in the palette, and false otherwise.
	 * @param edge
	 * @return
	 */
	private boolean targetInPalette(Edge edge) {
		return ((edge.targetIsMethod() && methodModels.get(edge.getTarget()) != null) ||
				(!edge.targetIsMethod() && fieldModels.get(edge.getTarget()) != null));
	}
	
	/**
	 * Associates the given edge with the member with the given name.
	 * Method is true if the member is a method and false if it is a field.
	 * @param key
	 * @param edge
	 * @param method
	 */
	private void addEdge(String key, Edge edge, boolean method) {
		List<Edge> edges = method ? methodEdges.get(key) : fieldEdges.get(key);
		
		if (edges == null) {
			edges = new Vector<Edge>();
		}
		edges.add(edge);
		
		if (method) {
			methodEdges.put(key, edges);
		}
		else {
			fieldEdges.put(key, edges);
		}
	}

	
	public EditPart createEditPart() {
		// No corresponding edit part.
		// Corresponding part is a FlyoutPalette
		return null;
	}
}
