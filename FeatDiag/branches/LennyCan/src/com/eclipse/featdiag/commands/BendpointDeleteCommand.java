package com.eclipse.featdiag.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.eclipse.featdiag.models.ConnectionModel;


/**
 * Command to delete a bendpoint in a connection.
 * Bendpoints are not deleted using the delete key.
 * They are deleted by moving the bendpoint to be
 * deleted so that it lies on a straight line between
 * the 2 surrounding bendpoints.
 * @author nic
 *
 */
public class BendpointDeleteCommand extends Command {
	
	private ConnectionModel connection;
	private Point location;	
	private int index;
	
	/**
	 * Remove the bendpoint from the specified section
	 * of the the connection.
	 */
	public void execute() {
		location = connection.getBendpoints().get(index);
		connection.removeBendpoint(index);
	}
	
	/**
	 * Re add the last bendpoint to be deleted.
	 */
	public void undo() {
		connection.addBendpoint(index, location);
	}
	
	/**
	 * Specify which connection the bendpoint is to
	 * be deleted from.
	 * @param model
	 */
	public void setConnection(Object model) {
		connection = (ConnectionModel) model;
	}
	
	/**
	 * Set the index into the array of bendpoints where
	 * the bendpoint is to be deleted from.
	 * (The bendpoints of a connection are listed in 
	 * order from the source node to the target node.)
	 * @param index
	 */
	public void setIndex(int i) {
		index = i;
	}
}
