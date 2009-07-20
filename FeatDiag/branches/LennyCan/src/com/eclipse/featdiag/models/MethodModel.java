package com.eclipse.featdiag.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

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

    transient IMethod method;
    private String iTypeHandleIdentifier; // used for serialization
	
    /**
	 * Create a new method model with the given name, 
	 * and the given modifiers.
	 * @param name
	 * @param modifiers
	 * @param fieldType
	 */	
	public MethodModel(IMethod method){
		super();
		this.method = method;
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
		return method.getElementName() + "(...)";
	}
	
	/**
	 * Returns the name of the image file for the icon
	 * for this method
	 */
	
	protected String getImageFileName() {
		String ret = "";
		try {
			ret = IconMap.getMethodIconName(getModifiers(method.getFlags()));
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public String[] getArgTypeNames() {
		//return argtypenames;
		String[] ret = new String[1];
		try {
			ret = method.getParameterNames();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public String getName() {
		return method.getElementName();
	}
	
	public int getModifiers() {
		int flags = 0;
		try {
			flags = getModifiers(method.getFlags());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flags;
	}
	
	public String getClassName() {
		return method.getDeclaringType().getElementName();
	}
	
	// serialization stuff
	private void writeObject(ObjectOutputStream out) throws IOException {
		iTypeHandleIdentifier = method.getHandleIdentifier();
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// our "pseudo-constructor"
		in.defaultReadObject();
		method = (IMethod) JavaCore.create(iTypeHandleIdentifier);
	}
	
	public IMethod getMethod(){
		return method;
	}
	// end serialization stuff
}
