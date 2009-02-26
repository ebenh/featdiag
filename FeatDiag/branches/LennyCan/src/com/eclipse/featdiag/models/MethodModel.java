package com.eclipse.featdiag.models;

import java.security.InvalidParameterException;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;

import com.eclipse.featdiag.commands.MemberAddCommand;
import com.eclipse.featdiag.commands.MethodAddCommand;
import com.eclipse.featdiag.models.modifiers.IconMap;
import com.eclipse.featdiag.parts.MethodPart;


/**
 * Model class that represents a model. Contains
 * all persistent data about this field.
 * @author nic
 *
 */
public class MethodModel extends MemberModel {

    private static final long serialVersionUID = -7662632863314325089L;

    /**
	 * Create a new method model with the given name, 
	 * and the given modifiers.
	 * @param name
	 * @param modifiers
	 * @param fieldType
	 */
	public MethodModel(String name, int modifiers, String[] argtypenames, String className) throws InvalidParameterException {
		super(name, modifiers, argtypenames, className);
	}
	
	/**
	 * Creates a new MethodPart.
	 */
	
	public EditPart createEditPart() {
		return new MethodPart();
	}
	
	/**
	 * Creates and returns a command to add this method to the given
	 * diagram at the given location.
	 */
	public MemberAddCommand getAddCommand(DiagramModel diagram, Point location) {
		return new MethodAddCommand(diagram, location, this);
	}

	/**
	 * Returns a string representation of this method:
	 * ex: "setMessage(String message)"
	 */
	
	public String toString() {
		return name + "(...)";
	}
	
	/**
	 * Returns the name of the image file for the icon
	 * for this method
	 */
	
	protected String getImageFileName() {
		return IconMap.getMethodIconName(modifiers);
	}
}
