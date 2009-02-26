package com.eclipse.featdiag.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.eclipse.featdiag.models.MemberModel;


/**
 * Command to move a class member image on the diagram.
 * @author nic
 *
 */
public class MemberMoveCommand extends Command {
	
	private MemberModel memberModel;
	private Rectangle oldBounds;
	private Rectangle newBounds;

	/**
	 * Creates a new command to move the image associated with the given
	 * model from the oldBounds location to the newBounds location.
	 * @param memberModel
	 * @param oldBounds
	 * @param newBounds
	 */
	public MemberMoveCommand(MemberModel memberModel, Rectangle oldBounds,
			Rectangle newBounds) {
		super();
		
		this.memberModel = memberModel;
		this.oldBounds = oldBounds;
		this.newBounds = newBounds;
	}
	
	/**
	 * Executes the command to move from the oldBounds to the
	 * newBounds.
	 */
	public void execute()
	{
		memberModel.modifyBounds(newBounds);
	}

	/**
	 * Undoes the move from the oldBounds to the newBounds.
	 */
	public void undo()
	{
		memberModel.modifyBounds(oldBounds);
	}
}
