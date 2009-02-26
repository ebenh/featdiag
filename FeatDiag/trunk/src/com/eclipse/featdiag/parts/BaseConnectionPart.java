package com.eclipse.featdiag.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractConnectionEditPart;

import com.eclipse.featdiag.models.BaseModel;


/**
 * A base controller class for connections.
 * Acts as a listener for connection bendpoint changes.
 * @author nic
 *
 */
public abstract class BaseConnectionPart extends AbstractConnectionEditPart implements PropertyChangeListener {

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

		if (BaseModel.BENDPOINT.equals(property)) {
			handleBendpointChange();
		}
	}

	/**
	 * Handles changes to a connection bendpoint.
	 * To be overridden by subclasses which allow
	 * movement.
	 */
	protected void handleBendpointChange() {
	}

}
