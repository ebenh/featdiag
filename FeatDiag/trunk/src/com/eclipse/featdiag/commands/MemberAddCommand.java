package com.eclipse.featdiag.commands;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.eclipse.featdiag.models.DiagramModel;


/**
 * A command to add a new class member to a diagram.
 * @author nic
 *
 */
public abstract class MemberAddCommand extends Command {
	protected DiagramModel diagram;
	protected Point location;
	
	/**
	 * Creates a command to add a new class member to the given 
	 * diagram at the given location.
	 * @param diagram
	 * @param location
	 */
	public MemberAddCommand(DiagramModel diagram, Point location) {
		this.diagram = diagram;
		this.location = location;
	}
	
	
	public abstract void execute();
	
	public abstract void undo();
}
