package com.eclipse.featdiag.models;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.eclipse.featdiag.commands.MemberAddCommand;
import com.eclipse.featdiag.commands.MethodAddCommand;
import com.eclipse.featdiag.parts.MethodPart;
import com.eclipse.featdiag.utils.MemberWrapper;


/**
 * Model class that represents a model. Contains
 * all persistent data about this field.
 * @author nic
 *
 */
public class MethodModel extends MemberModel {

    private static final long serialVersionUID = -7662632863314325089L;

    private MemberWrapper<IMethod> method;
    private String methodSignature;
    private int flags;
    
    /**
	 * Create a new method model with the given name, 
	 * and the given modifiers.
	 * @param name
	 * @param modifiers
	 * @param fieldType
	 */	
	public MethodModel(IMethod method){
		super();
		this.method = new MemberWrapper<IMethod>(method);
				
		/************************/
		/* Get method signature */
		/************************/
		
		/* Get package name */
		String packageName = method.getDeclaringType().getPackageFragment().getElementName();
		packageName = (packageName.equals("")?packageName:packageName+".");
		
		/* Get fully qualified method name */
		String methodName = packageName + method.getDeclaringType().getElementName() + "." + method.getElementName();
		
		/* Get complete human readable method signature as a string */
		String[] parameterNames = {};
		try {
			methodSignature = method.getSignature();
			parameterNames = method.getParameterNames();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		methodSignature = Signature.toString(methodSignature, methodName, parameterNames, true, true);
		
		/********************/
		/* Get access flags */
		/********************/
		
		try {
			flags = method.getFlags();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		return methodSignature;
	}
	
	public String getName() {
		return toString();
	}
	
	public int getModifiers() {
		return flags;
	}
		
	public IMethod getMethod(){
		return method.getMember();
	}
	// end serialization stuff
	
	public boolean exists(){
		return method.getMember().exists();
	}
}
