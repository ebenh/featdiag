package com.eclipse.featdiag.models;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.eclipse.featdiag.commands.FieldAddCommand;
import com.eclipse.featdiag.commands.MemberAddCommand;
import com.eclipse.featdiag.parts.FieldPart;
import com.eclipse.featdiag.utils.MemberWrapper;


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

    private MemberWrapper<IField> field;
    private String fieldName;
    private int flags;
    
    /**
	 * Create a new field model with the given name, of
	 * the given class type, with the given modifiers.
	 * @param name
	 * @param modifiers
	 * @param fieldType
	 */	
    public FieldModel(IField field) {
		super();
		this.field = new MemberWrapper<IField>(field);
		
		try {
			String packageName = field.getDeclaringType().getPackageFragment().getElementName();
			packageName = (packageName.equals("")?packageName:packageName+".");
			fieldName = Signature.toString(field.getTypeSignature()) + " " + packageName + field.getDeclaringType().getElementName() + "." + field.getElementName();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		try {
			flags = field.getFlags();
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
		return fieldName;
	}
	
	public String getName() {
		return toString();
	}
	
	public int getModifiers() {
		return flags;
	}
		
	public IField getField(){
		return field.getMember();
	}
	
	public boolean exists(){
		return field.getMember().exists();
	}
}
