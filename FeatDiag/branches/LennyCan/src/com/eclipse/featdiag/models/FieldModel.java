package com.eclipse.featdiag.models;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;

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
