package com.eclipse.featdiag.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.eclipse.featdiag.models.BaseModel;


/**
 * A base controller class.
 * Acts as a listener for model changes.
 * @author nic
 *
 */
public abstract class BasePart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	/**
	 * Activate this controller.
	 * Add this as a listener to the associated model.
	 */
	public void activate() {
		super.activate();
		BaseModel model = (BaseModel) getModel();
		model.addPropertyChangeListener(this);
	}

	/**
	 * Deactivate this controller.
	 * Remove this as a listener to the associated
	 * model.
	 */
	public void deactivate()
	{
		super.deactivate();
		BaseModel model = (BaseModel) getModel();
		model.removePropertyChangeListener(this);
	}
	
	/**
	 * Called when a property of the associated
	 * model is changed.
	 * Handles the change appropriately.
	 */
	public void propertyChange(PropertyChangeEvent evt)
	{

		String property = evt.getPropertyName();

		if (BaseModel.BOUNDS.equals(property))
		{
			handleBoundsChange(evt);
		}
		else if (BaseModel.CHILD.equals(property)) {
			handleChildChange(evt);
		}
		else if (BaseModel.CONNECTION_SOURCE.equals(property)) {
			handleSourceChange(evt);
		}
		else if (BaseModel.CONNECTION_TARGET.equals(property)) {
			handleTargetChange(evt);
		}
		else if (BaseModel.SAVE.equals(property)) {
			doSave(new NullProgressMonitor(), true);
		}
	}
	
	/**
	 * Handles changes to the bounds.
	 * To be overridden by subclasses which allow
	 * movement.
	 */
	protected void handleBoundsChange(PropertyChangeEvent evt)
	{
	}
	
	/**
	 * Handles members being added or removed 
	 * from the diagram.
	 * To be overridden by diagram subclass.
	 */
	protected void handleChildChange(PropertyChangeEvent evt)
	{
	}
	
	/**
	 * Handles connection being added or removed 
	 * from the diagram.
	 * To be overridden by member subclass.
	 */
	protected void handleSourceChange(PropertyChangeEvent evt)
	{
	}
	
	/**
	 * Handles connection being added or removed 
	 * from the diagram.
	 * To be overridden by member subclass.
	 */
	protected void handleTargetChange(PropertyChangeEvent evt)
	{
	}
	
	/**
	 * Saves the diagram model to the .feat file.
	 * Flush is true if should flush the command stack,
	 * (so that undo/redo's are cleared).
	 * To be overridden by diagram subclass.
	 * @param monitor
	 * @param flush
	 */
	public void doSave(IProgressMonitor monitor, boolean flush)
	{
	}
}
