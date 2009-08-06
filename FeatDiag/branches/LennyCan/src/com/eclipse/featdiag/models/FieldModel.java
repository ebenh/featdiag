package com.eclipse.featdiag.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.eclipse.featdiag.commands.FieldAddCommand;
import com.eclipse.featdiag.commands.MemberAddCommand;
import com.eclipse.featdiag.parts.FieldPart;


/**
 * Model class that represents a field. Contains
 * all persistent data about this field.
 * @author nic
 *
 */
public class FieldModel extends MemberModel {

    private static final long serialVersionUID = -7570555419537725680L;

    /**
     * The string representation of the field's type
     */
    transient IField field;
    private String iTypeHandleIdentifier; // used for serialization
	
    String toStringName;
    /**
	 * Create a new field model with the given name, of
	 * the given class type, with the given modifiers.
	 * @param name
	 * @param modifiers
	 * @param fieldType
	 */	
    public FieldModel(IField field) {
		super();
		this.field = field;
		
		try {
			toStringName = Signature.toString(field.getTypeSignature()) + " " + field.getDeclaringType().getElementName() + "." + field.getElementName();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Creates a new FieldPart.
	 */
	
	public EditPart createEditPart() {
		return new FieldPart();
	}
	
	/**
	 * Creates and returns a command to add this field to the given
	 * diagram at the given point.
	 */
	public MemberAddCommand getAddCommand(DiagramModel diagram, Point location) {
		return new FieldAddCommand(diagram, location, this);
	}

	/**
	 * Returns a string representation of this field:
	 * ex: "String message"
	 */
	
	public String toString() {
//		String ret = "";
//		
//		try {
//			ret = Signature.toString(field.getTypeSignature()) + " " + field.getElementName();
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		
//		return ret;
		return toStringName;
	}
	
	public String getName() {
		//return field.getElementName();
		return toString();
	}
	
	public int getModifiers() {
		int flags = 0;
		try {
			flags = field.getFlags();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flags;
	}
	
	public String getClassName() {
		return field.getDeclaringType().getElementName();
	}
	
	// serialization stuff
	private void writeObject(ObjectOutputStream out) throws IOException {
		iTypeHandleIdentifier = field.getHandleIdentifier();
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();
		field = (IField) JavaCore.create(iTypeHandleIdentifier);
	}
	// end serialization stuff
	
	public IField getField(){
		return field;
	}
	
	public boolean exists(){
		return field.exists();
	}
}
