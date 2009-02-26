package com.eclipse.featdiag.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import org.eclipse.gef.EditPart;

/**
 * Base class for all model classes.
 * Keeps model listeners and alerts them of all changes
 * made to models.
 * @author nic
 *
 */
public abstract class BaseModel implements Serializable {

	private static final long serialVersionUID = 3506274100939038669L;
	
	public static final String BOUNDS = "BOUNDS";
	public static final String BENDPOINT = "BENDPOINT";
	public static final String CHILD = "CHILD";
	public static final String CONNECTION_SOURCE = "CONNECTION_SOURCE";
	public static final String CONNECTION_TARGET = "CONNECTION_TARGET";
	public static final String PALETTE_METHOD = "METHOD";
	public static final String PALETTE_FIELD = "FIELD";
	public static final String SAVE = "SAVE";
	
	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	/**
	 * Add new listener
	 * @param l
	 */
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
	    if(listeners == null) {
	        listeners = new PropertyChangeSupport(this);
	    }
		listeners.addPropertyChangeListener(l);
	}

	/**
	 * Remove given listener
	 * @param l
	 */
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
	    if(listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
		listeners.removePropertyChangeListener(l);
	}
	
	/**
	 * Alert listeners of a change to the given object.
	 * @param prop
	 * @param old
	 * @param newValue
	 */
	protected void firePropertyChange(String prop, Object old, Object newValue)
	{
	    if(listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
		listeners.firePropertyChange(prop, old, newValue);
	}	

	/**
	 * Creates an edit part of the proper type depending
	 * on the type of this model.
	 */
	public abstract EditPart createEditPart();
}
