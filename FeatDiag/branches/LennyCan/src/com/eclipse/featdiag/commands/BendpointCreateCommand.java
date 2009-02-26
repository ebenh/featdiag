package com.eclipse.featdiag.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.eclipse.featdiag.models.ConnectionModel;


/**
 * Command to create a bendpoint in a connection.
 * @author nic
 *
 */
public class BendpointCreateCommand extends Command {

	private ConnectionModel connection;
	private Point location;	
	private int index;
	
	/**
	 * Add the bendpoint at the location in the proper
	 * section of the connection.
	 */
	public void execute() {
		connection.addBendpoint(index, location);
	}
	
	/**
	 * Remove the last created bendpoint.
	 */
	public void undo() {
		connection.removeBendpoint(index);
	}

	/**
	 * Set which connection the bendpoint is to be
	 * created on.
	 * @param model
	 */
	public void setConnection(Object model) {
		connection = (ConnectionModel) model;
	}

	/**
	 * Set the location of the new bendpoint to be
	 * made.
	 * @param point
	 */
	public void setLocation(Point point) {
		location = point;
	}
	
	/**
	 * Set the index into the array of bendpoints where
	 * the next bendpoint is to be created.
	 * (The bendpoints of a connection are listed in 
	 * order from the source node to the target node.)
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
