package com.eclipse.featdiag.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.gef.EditPart;

import com.eclipse.featdiag.parser.Edge;
import com.eclipse.featdiag.parts.DiagramPart;


/**
 * Diagram Model class that represents the root model
 * which contains the models of all the class members.
 * @author nic
 *
 */
public class DiagramModel extends BaseModel {
	
    private static final long serialVersionUID = -3494446264060426555L;
    
    private String associatedJavaFile;

    private PaletteModel palette;
    private Map<String, FieldModel> fieldModels;
	private Map<String, MethodModel> methodModels;
	private List<ConnectionModel> connectionModels;
	
	/**
	 * Creates a new diagram object and inserts the class
	 * members in a diagram.
	 */
	public DiagramModel() {
		palette = new PaletteModel();
		fieldModels = new HashMap<String, FieldModel>();
		methodModels = new HashMap<String, MethodModel>();
		connectionModels = new Vector<ConnectionModel>();
	}
	
	/**
	 * Store the name of the java file associated with
	 * this diagram in path from project form.
	 * @param javaFile
	 */
	public void setAssociatedJavaFile(String javaFile) {
		this.associatedJavaFile = javaFile;
	}
	
	/**
	 * Returns the name of the java file associated
	 * with this diagram in path from project form.
	 * @return
	 */
	public String getAssociatedJavaFile() {
		return associatedJavaFile;
	}
	
	/**
	 * Returns the palette model in this diagram.
	 * @return
	 */
	public PaletteModel getPaletteModel() {
		if (palette == null) {
			palette = new PaletteModel();
		}
		return palette;
	}

	/**
	 * Adds a field to the diagram.
	 * @param memberModel
	 */
	public void addFieldModel(FieldModel field) {
		if (!fieldModels.containsKey(field.getName())) {
			fieldModels.put(field.getName(), field);
			firePropertyChange(CHILD, null, field);
		}
	}
	
	/**
	 * Adds a method to the diagram.
	 * @param method
	 */
	public void addMethodModel(MethodModel method) {
		if (!methodModels.containsKey(method.getName())) {
			methodModels.put(method.getName(), method);	
			firePropertyChange(CHILD, null, method);
		}
	}
	
	/**
	 * Tries to removes a field and all connecting edges
	 * from the diagram.
	 * If field is not found in the diagram, tries
	 * to remove it from the palette.
	 * Returns true if the method was removed from
	 * either of the two, false otherwise.
	 * @param field
	 * @return
	 */
	public boolean removeFieldModel(FieldModel field) {
		if (fieldModels.containsKey(field.getName())) {
			removeConnections(field);
			fieldModels.remove(field.getName());
			firePropertyChange(CHILD, field, null);
			return true;
		}
		else {
			return getPaletteModel().removeField(field);
		}
	}
	
	/**
	 * Tries to remove a method and all connecting edges
	 * from the diagram.
	 * If method is not found in the diagram, tries
	 * to remove it from the palette.
	 * Returns true if the method was removed from
	 * either of the two, false otherwise.
	 * @param method
	 * @return
	 */
	public boolean removeMethodModel(MethodModel method) {
		if (methodModels.containsKey(method.getName())) {
			removeConnections(method);
			methodModels.remove(method.getName());
			firePropertyChange(CHILD, method, null);
			return true;
		}
		else {
			return getPaletteModel().removeMethod(method);
		}
	}
	
	/**
	 * Adds a method to the palette
	 * @param method
	 */
	public void addPaletteMethod(MethodModel method) {
		getPaletteModel().addNewMethod(method);
	}
	
	/**
	 * Adds a field to the palette
	 * @param field
	 */
	public void addPaletteField(FieldModel field) {
		getPaletteModel().addNewField(field);
	}
	
	/**
	 * Removes a method from the palette
	 * @param method
	 */
	public boolean removePaletteMethod(MethodModel method) {
		return getPaletteModel().removeMethod(method);
	}
	
	/**
	 * Removes a field from the palette
	 * @param field
	 */
	public boolean removePaletteField(FieldModel field) {
		return getPaletteModel().removeField(field);
	}
	
	/**
	 * Gets the list of class members in the diagram.
	 * @return
	 */
	public List<MemberModel> getMemberModels() {
		List<MemberModel> members = new Vector<MemberModel>();
		members.addAll(fieldModels.values());
		members.addAll(methodModels.values());
		return members;
	}
	
	/**
	 * Gets the list of fields in the diagram.
	 * @return
	 */
	public List<FieldModel> getFieldModels() {
		return new Vector<FieldModel>(fieldModels.values());
	}
	
	/**
	 * Gets the list of methods in the diagram.
	 * @return
	 */
	public List<MethodModel> getMethodModels() {
		return new Vector<MethodModel>(methodModels.values());
	}
	
	/**
	 * Gets the list of connections in the diagram.
	 * @return
	 */
	public List<ConnectionModel> getConnectionModels() {
		return connectionModels;
	}
	
	/**
	 * Gets the list of names of fields in the diagram.
	 * @return
	 */
	public Set<String> getFields() {
		return fieldModels.keySet();
	}
	
	/**
	 * Gets the list of names of methods in the diagram.
	 * @return
	 */
	public Set<String> getMethods() {
		return methodModels.keySet();
	}

	/**
	 * Adds a connection between the source method and the
	 * target method.
	 * @param source
	 * @param target
	 */
	public void addMethodToMethodConnection(String source, String target) {
		MemberModel sourceModel = methodModels.get(source);
		MemberModel targetModel = methodModels.get(target);
		if (sourceModel != null && targetModel != null) {
			connectionModels.add(new ConnectionModel(sourceModel, targetModel, true));
		}
		else {
			palette.addEdge(new Edge(source, target, true));
		}
	}
	
	/**
	 * Adds a connection between the source method and the
	 * target field.
	 * @param source
	 * @param target
	 */
	public void addMethodToFieldConnection(String source, String target) {
		MemberModel sourceModel = methodModels.get(source);
		MemberModel targetModel = fieldModels.get(target);
		if (sourceModel != null && targetModel != null) {
			connectionModels.add(new ConnectionModel(sourceModel, targetModel, false));
		}
		else {
			palette.addEdge(new Edge(source, target, false));
		}
	}
	
	/**
	 * Remove a connection between the source method and target method
	 * @param source
	 * @param target
	 */
	public void removeMethodToMethodConnection(String source, String target) {
		MemberModel sourceModel = methodModels.get(source);
		MemberModel targetModel = methodModels.get(target);
		if (sourceModel != null && targetModel != null) {
			ConnectionModel toRemove = null;
			for (ConnectionModel connection : connectionModels) {
				if (sourceModel.equals(connection.getSource()) &&
						targetModel.equals(connection.getTarget()) &&
						connection.targetIsMethod()) {
					toRemove = connection;
					break;
				}
			}
			if (toRemove != null) {
				toRemove.dispose();
				connectionModels.remove(toRemove);
			}
		}
		else {
			palette.removeEdge(new Edge(source, target, true));
		}
	}

	/**
	 * Removes a connection between the source method and target field
	 * @param source
	 * @param target
	 */
	public void removeMethodToFieldConnection(String source, String target) {
		MemberModel sourceModel = methodModels.get(source);
		MemberModel targetModel = fieldModels.get(target);
		if (sourceModel != null && targetModel != null) {
			ConnectionModel toRemove = null;
			for (ConnectionModel connection : connectionModels) {
				if (sourceModel.equals(connection.getSource()) &&
						targetModel.equals(connection.getTarget()) &&
						!connection.targetIsMethod()) {
					toRemove = connection;
					break;
				}
			}
			if (toRemove != null) {
				toRemove.dispose();
				connectionModels.remove(toRemove);
			}
		}
		else {
			palette.removeEdge(new Edge(source, target, false));
		}
	}
	
	/**
	 * Removes all connections to and from the given member
	 * from the diagram.
	 * @param member
	 */
	private void removeConnections(MemberModel member) {
		List<ConnectionModel> connections = new Vector<ConnectionModel>(member.getUses());
		connections.addAll(member.getUsedBy());
		for (ConnectionModel connection : connections) {
			if (connection.targetIsMethod()) {
				removeMethodToMethodConnection(connection.getSource().getName(), connection.getTarget().getName());
			}
			else {
				removeMethodToFieldConnection(connection.getSource().getName(), connection.getTarget().getName());
			}
		}
	}
	
	/**
	 * Saves this diagram to the .feat file.
	 */
	public void doSave() {
		firePropertyChange(SAVE, null, null);
	}

	/**
	 * Creates a new DiagramPart.
	 */
	
	public EditPart createEditPart() {
		return new DiagramPart();
	}
}
