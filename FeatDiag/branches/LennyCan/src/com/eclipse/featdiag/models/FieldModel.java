package com.eclipse.featdiag.models;

import java.security.InvalidParameterException;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

import com.eclipse.featdiag.commands.FieldAddCommand;
import com.eclipse.featdiag.commands.MemberAddCommand;
import com.eclipse.featdiag.models.modifiers.IconMap;
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
    private String fieldType;

	/**
	 * Create a new field model with the given name, of
	 * the given class type, with the given modifiers.
	 * @param name
	 * @param modifiers
	 * @param fieldType
	 */
	public FieldModel(String name, int modifiers, String fieldType, String className) {
		super(name, modifiers, null, className);
		this.fieldType = fieldType;
	}
	
	public FieldModel(IField field) throws InvalidParameterException, JavaModelException {
		super(field.getElementName(), getModifiers(field.getFlags()), null, field.getDeclaringType().getElementName());
		this.fieldType = field.getTypeSignature();
	}
	
	private static int getModifiers(int flags){
		int ret = 0;
		
		if((flags & Flags.AccPublic) == Flags.AccPublic){
			ret |= 1;
		}
		else if((flags & Flags.AccPublic) == Flags.AccPrivate){
			ret |= 2;
		}
		else if((flags & Flags.AccPublic) == Flags.AccProtected){
			ret |= 4;
		}
		return ret;
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
		return fieldType + " " + name; 
	}

	/**
	 * Returns the name of the image file for the icon
	 * for this field
	 */
	protected String getImageFileName() {
		return IconMap.getFieldIconName(modifiers);
	}
}
