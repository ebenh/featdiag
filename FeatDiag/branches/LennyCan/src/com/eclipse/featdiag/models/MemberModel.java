package com.eclipse.featdiag.models;

import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.eclipse.featdiag.commands.MemberAddCommand;

/**
 * Model class that represents either a field or method.
 * @author nic
 *
 */
public abstract class MemberModel extends BaseModel implements Comparable<MemberModel> {
	
	private static final long serialVersionUID = 5488986110945292031L;
	
	private List<ConnectionModel> uses;
	private List<ConnectionModel> usedBy;
	private Rectangle bounds;
	private int width;
	private int height;
			
	/**
	 * Creates a new field or method object
	 * with the given name and modifiers.
	 * Modifiers should be created using MemberModifierConstants.
	 * ex: MOD_PUBLIC | MOD_FINAL
	 * Note: Exactly one of MOD_PUBLIC, MOD_PRIVATE and MOD_PROTECTED must be used.
	 * @param name
	 */
	public MemberModel() {
		this.uses = new Vector<ConnectionModel>();
		this.usedBy = new Vector<ConnectionModel>();
	}
		
	/**
	 * Changes the stored location of the image of this
	 * model to a new location.
	 * @param bounds
	 */
	public void modifyBounds(Rectangle bounds) {
		Rectangle oldBounds = this.bounds;
		if (!bounds.equals(oldBounds)) {
			this.bounds = bounds;
			firePropertyChange(BOUNDS, oldBounds, bounds);
		}
	}
	
	/**
	 * Sets the location of the image of this model
	 * without updating the diagram.
	 * @param bounds
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	/**
	 * Returns the location of the image of this model.
	 * @return
	 */
	public Rectangle getBounds() {
		if (bounds == null) {
			bounds = new Rectangle(0, 0, -1, -1);
		}
		return bounds;
	}
	
	/**
	 * Set the width and height of the figure
	 * representation of this member.
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;		
	}
	
	/**
	 * Get the width of the figure representation
	 * of this member.
	 * @return
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the figure presentation
	 * of this member.
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Adds a class member as a usage of this member.
	 * Intended to be called only from the constructor
	 * of a connection model.
	 * @param model
	 */
	public void addUses(ConnectionModel model) {
		uses.add(model);
		firePropertyChange(CONNECTION_SOURCE, null, model);
	}
	
	/**
	 * Removes a class member as a usage of this member.
	 * @param model
	 */
	public void removeUses(ConnectionModel model) {
		uses.remove(model);
		firePropertyChange(CONNECTION_SOURCE, model, null);
	}
	
	/**
	 * Gets a list of all the class members that this
	 * member uses.
	 * @return
	 */
	public List<ConnectionModel> getUses() {
		return uses;
	}
	
	/**
	 * Adds a class member that uses this member.
	 * Intended to be called only from the constructor
	 * of a connection model.
	 * @param model
	 */
	public void addUsedBy(ConnectionModel model) {
		usedBy.add(model);
		firePropertyChange(CONNECTION_TARGET, null, model);
	}
	
	/**
	 * Removes a class member from using this member.
	 * @param model
	 */
	public void removeUsedBy(ConnectionModel model) {
		usedBy.remove(model);
		firePropertyChange(CONNECTION_TARGET, model, null);
	}
	
	/**
	 * Gets a list of all the class members that
	 * use this member.
	 * @return
	 */
	public List<ConnectionModel> getUsedBy() {
		return usedBy;
	}
	
	/**
	 * Returns true if the given object is a method or field
	 * with the same name as this method or field.
	 * Returns false otherwise. 
	 */
	public boolean equals(Object o) {
		return (o instanceof MemberModel &&
				toString().equals((o.toString())));
	}
	
	/**
	 * Compares the String representations of this and the
	 * given MemberModel.
	 */
	public int compareTo(MemberModel member) {
		if (member == null) {
			return 1;
		}
		return toString().compareTo(member.toString());
	}

	public abstract MemberAddCommand getAddCommand(DiagramModel diagram, Point location);
	
	public abstract String toString();
	
	public abstract String getName();
	
	public abstract int getModifiers();
	
	public abstract String getClassName();
}
