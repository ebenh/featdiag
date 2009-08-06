package com.eclipse.featdiag.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.eclipse.featdiag.commands.MemberAddCommand;
import com.eclipse.featdiag.commands.MethodAddCommand;
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
	
    String toStringName;
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
		
		String pkg = method.getDeclaringType().getPackageFragment().getElementName();
		pkg = (pkg.equals("")?pkg:pkg+".");
		
		String methodSignature = "";
		String methodName = pkg + method.getDeclaringType().getElementName() + "." + method.getElementName();
		String[] parameterNames = {};
		
		try {
			methodSignature = method.getSignature();
			parameterNames = method.getParameterNames();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		toStringName = Signature.toString(methodSignature, methodName, parameterNames, true, true);
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
//			String methodSignature = "";
//			String methodName = method.getDeclaringType().getElementName() + "." + method.getElementName();
//			String[] parameterNames = {};
//			
//			try {
//				methodSignature = method.getSignature();
//				parameterNames = method.getParameterNames();
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			return Signature.toString(methodSignature, methodName, parameterNames, true, true);
		return toStringName;
	}
	
	public String[] getArgTypeNames() {
		String[] ret = {};
		
		try {
			ret = method.getParameterNames();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public String getName() {
		//return method.getElementName();
		return toString();
	}
	
	public int getModifiers() {
		int flags = 0;
		try {
			flags = method.getFlags();
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
	
	public boolean exists(){
		return method.exists();
	}
}
