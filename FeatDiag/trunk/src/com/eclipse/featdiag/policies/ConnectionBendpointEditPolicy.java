package com.eclipse.featdiag.policies;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import com.eclipse.featdiag.commands.BendpointCreateCommand;
import com.eclipse.featdiag.commands.BendpointDeleteCommand;
import com.eclipse.featdiag.commands.BendpointMoveCommand;


/**
 * Class to create and return the appropriate commands when a
 * change is made to a bendpoint on a connection.
 * @author nic
 *
 */
public class ConnectionBendpointEditPolicy extends BendpointEditPolicy {

	/**
	 * Create a command to create a new bendpoint.
	 */
	
	protected Command getCreateBendpointCommand(BendpointRequest request) {
		Point point = request.getLocation();
	    getConnection().translateToRelative(point);
	    
	    BendpointCreateCommand command = new BendpointCreateCommand();
	    command.setLocation(point);
	    command.setIndex(request.getIndex());
	    command.setConnection(getHost().getModel());
	    
	    return command;
	}

	/**
	 * Create a command to delete a bendpoint.
	 */
	
	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		Point point = request.getLocation();
	    getConnection().translateToRelative(point);
	    
	    BendpointDeleteCommand command = new BendpointDeleteCommand();
	    command.setIndex(request.getIndex());
	    command.setConnection(getHost().getModel());
	    
	    return command;
	}

	/**
	 * Create a command to move a bendpoint.
	 */
	
	protected Command getMoveBendpointCommand(BendpointRequest request) {
		Point point = request.getLocation();
		getConnection().translateToRelative(point);
		
		BendpointMoveCommand command = new BendpointMoveCommand();
		command.setLocation(point);
	    command.setIndex(request.getIndex());
	    command.setConnection(getHost().getModel());
		return command;
	}

}
