package com.eclipse.featdiag.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.eclipse.featdiag.models.ConnectionModel;


/**
 * Command to move a bendpoint in a connection.
 * @author nic
 *
 */
public class BendpointMoveCommand extends Command {
	
	private ConnectionModel connection;
	private Point oldLocation;
	private Point newLocation;
	private int index;
	
	/**
	 * Move a bendpoint from one location to another.
	 */
	public void execute() {
		oldLocation = connection.getBendpoints().get(index);
		connection.removeBendpoint(index);
		connection.addBendpoint(index, newLocation);
	}
	
	/**
	 * Undo the last move of a bendpoint.
	 */
	public void undo() {
		newLocation = oldLocation;
		connection.removeBendpoint(index);
		connection.addBendpoint(index, newLocation);
	}

	/**
	 * Specify the connection of the bendpoint to
	 * be moved.
	 * @param model
	 */
	public void setConnection(Object model) {
		connection = (ConnectionModel) model;
	}

	/**
	 * Sets the new location to move the bendpoint
	 * to.
	 * @param point
	 */
	public void setLocation(Point point) {
		newLocation = point;
	}
	
	/**
	 * Set the index into the array of bendpoints where
	 * the bendpoint being moved exists.
	 * (The bendpoints of a connection are listed in 
	 * order from the source node to the target node.)
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
