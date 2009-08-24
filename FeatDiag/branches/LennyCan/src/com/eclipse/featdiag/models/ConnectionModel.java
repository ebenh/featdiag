package com.eclipse.featdiag.models;

import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;

import com.eclipse.featdiag.parts.ConnectionPart;


/**
 * Model class that represents a connection between
 * 2 class members.
 * @author nic
 *
 */
public class ConnectionModel extends BaseModel implements Comparable<ConnectionModel> {
	
    private static final long serialVersionUID = 3503933399973068113L;

    private boolean targetIsMethod;
    private MemberModel source;
	private MemberModel target;
	private List<Point> bendpoints;
	
	/**
	 * Create a new connection between 2 class members.
	 * @param source
	 * @param target
	 * @param destIsMethod - true if target is a method, false if target is a field
	 */
	public ConnectionModel(MemberModel source, MemberModel target, boolean destIsMethod) {
		this.source = source;
		this.target = target;
		this.targetIsMethod = destIsMethod;
		this.bendpoints = new Vector<Point>();
		this.source.addUses(this);
		this.target.addUsedBy(this);
	}
	
	/**
	 * Removes this connection from source and target.
	 */
	public void dispose() {
		this.source.removeUses(this);
		this.target.removeUsedBy(this);
	}
	
	/**
	 * Returns the source member of this connection.
	 * @return
	 */
	public MemberModel getSource() {
		return source;
	}
	
	/**
	 * Returns the target member of this connection.
	 * @return
	 */
	public MemberModel getTarget() {
		return target;
	}
	
	/**
	 * Returns true if the destination of this connection
	 * is a method, false if the destination is a field.
	 * @return
	 */
	public boolean targetIsMethod() {
		return targetIsMethod;
	}
	
	/**
	 * Add a new bendpoint at the given location at the
	 * specified section of the connection.
	 * @param index
	 * @param bendpoint
	 */
	public void addBendpoint(int index, Point bendpoint) {
		bendpoints.add(index, bendpoint);
		firePropertyChange(BENDPOINT, null, null);
	}
	
	/**
	 * Remove the bendpoint at the specified section
	 * of the connection.
	 * @param index
	 */
	public void removeBendpoint(int index) {
		bendpoints.remove(index);
		firePropertyChange(BENDPOINT, null, null);
	}
	
	/**
	 * Remove the bendpoint at the given location.
	 * @param bendpoint
	 */
	public void removeBendpoint(Point bendpoint) {
		bendpoints.remove(bendpoint);
		firePropertyChange(BENDPOINT, null, null);
	}
	
	/**
	 * Gets the list of bendpoints for this connection.
	 * The list returned is a list of locations of
	 * bendpoints in order from the source node to the
	 * target node.
	 * @return
	 */
	public List<Point> getBendpoints() {
		return bendpoints;
	}

	/**
	 * Creates a new ConnectionPart
	 */
	
	public EditPart createEditPart() {
		return new ConnectionPart();
	}
	
	
	public String toString() {
		return source.toString() + "->" + target.toString();
	}
	
	
	public int compareTo(ConnectionModel connection) {
		if (connection == null) {
			return 1;
		}
		return toString().compareTo(connection.toString());
	}
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		
		if(!(o instanceof ConnectionModel))
			return false;
		
		ConnectionModel model = (ConnectionModel)o;
		
		return (this.compareTo(model)==0) ? true : false;
	}
}
