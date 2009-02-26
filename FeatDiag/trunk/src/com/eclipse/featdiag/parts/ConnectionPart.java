package com.eclipse.featdiag.parts;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;

import com.eclipse.featdiag.models.ConnectionModel;
import com.eclipse.featdiag.policies.ConnectionBendpointEditPolicy;


/**
 * Controller class for connection objects.
 * Handles connections between 2 members, and the
 * drawing, selecting, and movement of the lines
 * between the 2 members.
 * @author nic
 *
 */
public class ConnectionPart extends BaseConnectionPart {

	/**
	 * Defines the actions allowed to be performed on this line.
	 */
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new ConnectionBendpointEditPolicy());
	}
	
	/**
	 * Creates the figure used as this member's visual representation,
	 * in this case, a line with an arrow.
	 */
	protected IFigure createFigure()
	{
		PolylineConnection conn = (PolylineConnection) super.createFigure();
		conn.setConnectionRouter(new BendpointConnectionRouter());
		conn.setTargetDecoration(new PolygonDecoration());
		return conn;
	}

	/**
	 * Sets the width of the line when selected.
	 */
	public void setSelected(int value)
	{
		super.setSelected(value);
		
		int lineWidth = (value != EditPart.SELECTED_NONE) ? 2 : 1;
		((PolylineConnection) getFigure()).setLineWidth(lineWidth);
	}

	/**
	 * Called when the model for this connection has changed it's
	 * bendpoints, either when a bendpoint has been added, deleted,
	 * or moved.
	 * Redraws all bendpoints for this connection in order at the
	 * specified locations.
	 */
	
	protected void handleBendpointChange() {
		List<Point> bendpoints = getConnectionModel().getBendpoints();
		List<AbsoluteBendpoint> constraint = new Vector<AbsoluteBendpoint>();
		
		for (Iterator<Point> iter = bendpoints.iterator(); iter.hasNext(); ) {
			constraint.add(new AbsoluteBendpoint(iter.next()));
		}
		
		getConnectionFigure().setRoutingConstraint(constraint);
	}
	
	/**
	 * When a refresh is called for this connection, redraw all
	 * bendpoints.
	 */
	protected void refreshVisuals() {
		handleBendpointChange();
	}
	
	/**
	 * Return the ConnectionModel associated with this part.
	 * @return
	 */
	public ConnectionModel getConnectionModel() {
		return (ConnectionModel) getModel();
	}
}
