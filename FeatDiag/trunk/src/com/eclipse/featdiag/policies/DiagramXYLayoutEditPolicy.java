package com.eclipse.featdiag.policies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.eclipse.featdiag.commands.MemberAddCommand;
import com.eclipse.featdiag.commands.MemberMoveCommand;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.MemberModel;
import com.eclipse.featdiag.parts.DiagramPart;
import com.eclipse.featdiag.parts.MemberPart;


public class DiagramXYLayoutEditPolicy extends XYLayoutEditPolicy {

	/**
	 * Creates command to move class member. Does not allow nodes
	 * to be resized.
	 */
	
	protected Command createChangeConstraintCommand(EditPart child, Object constraint)
	{
		if (!(child instanceof MemberPart) || !(constraint instanceof Rectangle)) {
			return null;
		}

		MemberPart memberPart = (MemberPart) child;
		MemberModel memberModel = memberPart.getMemberModel();
		IFigure figure = memberPart.getFigure();
		
		Rectangle oldBounds = figure.getBounds();
		Rectangle newBounds = (Rectangle) constraint;

		if  ((oldBounds.width != newBounds.width && newBounds.width != -1) ||
			 (oldBounds.height != newBounds.height && newBounds.height != -1) ||
			 (newBounds.x < 0 || newBounds.y < newBounds.height)) {
				return null;
			}

		MemberMoveCommand command = new MemberMoveCommand(memberModel, oldBounds.getCopy(), newBounds.getCopy());
		return command;
	}

	/**
	 * Returns the current bounds as the constraint if none can be found in the
	 * figures Constraint object
	 */
	public Rectangle getCurrentConstraintFor(GraphicalEditPart child)
	{
		IFigure fig = child.getFigure();
		Rectangle rectangle = (Rectangle) fig.getParent().getLayoutManager().getConstraint(fig);
		if (rectangle == null)
		{
			rectangle = fig.getBounds();
		}
		return rectangle;
	}

	/**
	 * Creates a command to add a new image in the diagram.
	 */
	
	protected Command getCreateCommand(CreateRequest request) {
		Object newObject = request.getNewObject();
		if (!(newObject instanceof MemberModel))
		{
			return null;
		}
		
		Point location = request.getLocation();
		DiagramPart diagramPart = (DiagramPart) getHost();
		DiagramModel diagram = diagramPart.getDiagramModel();
		
		MemberAddCommand addCommand = ((MemberModel) newObject).getAddCommand(diagram, location);
		return addCommand;
	}

}
