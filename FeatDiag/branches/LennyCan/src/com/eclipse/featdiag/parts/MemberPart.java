package com.eclipse.featdiag.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.widgets.Display;

import com.eclipse.featdiag.models.ConnectionModel;
import com.eclipse.featdiag.models.MemberModel;


/**
 * Controller class for class member objects.
 * Handles connections to other members, updating the images
 * with changes to the model objects, and listening for
 * actions performed on the images.
 * @author nic
 *
 */
public abstract class MemberPart extends BasePart implements NodeEditPart {
	
	/**
	 * Activates this edit part. Adds this figures constraint
	 * to the editor layout.
	 */
	public void activate() {
		super.activate();
		DiagramPart parent = getDiagramParent();
		LayoutManager manager = parent.getFigure().getLayoutManager();
		manager.setConstraint(getFigure(), getMemberModel().getBounds());
	}
	
	/**
	 * Defines the actions allowed to be performed on this image.
	 */
	
	protected void createEditPolicies() {
	}
	
	/**
	 * Returns the List of the connection model objects for 
	 * which the model associated with this part is the source.
	 */
	
	protected List<ConnectionModel> getModelSourceConnections()
	{
		return getMemberModel().getUses();
	}
	
	/**
	 * Returns the List of the connection model objects for 
	 * which the model associated with this part is the target.
	 */
	
	protected List<ConnectionModel> getModelTargetConnections()
	{
		return getMemberModel().getUsedBy();
	}
	
	/**
	 * Handles changes to the bounds of this class
	 * member.
	 */
	protected void handleBoundsChange(PropertyChangeEvent evt)
	{
		IFigure figure = getFigure();
		Rectangle constraint = (Rectangle) evt.getNewValue();
		DiagramPart parent = getDiagramParent();
		parent.setLayoutConstraint(this, figure, constraint);
	}
	
	@SuppressWarnings("unchecked")
	
	protected void handleSourceChange(PropertyChangeEvent evt) {
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (oldValue != null && newValue != null) {
			throw new IllegalStateException(
			"Exactly one of old or new values must be non-null for CONNECTION event");
		}

		// New Connection Added
		if (oldValue == null) {
			ConnectionEditPart editPart = createOrFindConnection(newValue);
			int index = getModelSourceConnections().indexOf(newValue);
			Display.getDefault().syncExec(new AddConnection(editPart, index, true));
		} 
		// Connection Removed
		else {
			//remove connection
			List<ConnectionEditPart> children = getSourceConnections();

			ConnectionEditPart toRemove = null;
			for (ConnectionEditPart part : children) {
				if (oldValue.equals(part.getModel())) {
					toRemove = part;
					break;
				}
			}

			if (toRemove != null) {
				Display.getDefault().asyncExec(new RemoveConnection(toRemove, true));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	
	protected void handleTargetChange(PropertyChangeEvent evt) {
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (oldValue != null && newValue != null) {
			throw new IllegalStateException(
			"Exactly one of old or new values must be non-null for CONNECTION event");
		}

		// New Connection Added
		if (oldValue == null) {
			ConnectionEditPart editPart = createOrFindConnection(newValue);
			int index = getModelTargetConnections().indexOf(newValue);
			Display.getDefault().syncExec(new AddConnection(editPart, index, false));
		} 
		// Connection Removed
		else {
			//remove connection
			List<ConnectionEditPart> children = getTargetConnections();

			ConnectionEditPart toRemove = null;
			for (ConnectionEditPart part : children) {
				if (oldValue.equals(part.getModel())) {
					toRemove = part;
					break;
				}
			}

			if (toRemove != null) {
				Display.getDefault().asyncExec(new RemoveConnection(toRemove, false));
			}
		}
	}
	
	/**
	 * Returns the ConnectionAnchor for the specified source connection.
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	/**
	 *  Returns the source ConnectionAnchor for the specified Request.
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	/**
	 * Returns the ConnectionAnchor for the specified target connection.
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart request) {
		return new ChopboxAnchor(getFigure());
	}

	/**
	 * Returns the target ConnectionAnchor for the specified Request.
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request connection) {
		return new ChopboxAnchor(getFigure());
	}
	
	/**
	 * Returns the MemberModel object associated with
	 * this class member.
	 * @return
	 */
	public MemberModel getMemberModel() {
		return (MemberModel) getModel();
	}
	
	/**
	 * Returns the DiagramPart associated with
	 * the parent of this class member.
	 * @return
	 */
	public DiagramPart getDiagramParent() {
		return (DiagramPart) getParent();
	}
	
	/**
	 * Runnable class that adds connections to the
	 * diagram.
	 * Must be run in UI-thread.
	 * @author nic
	 *
	 */
	private class AddConnection implements Runnable {
		private ConnectionEditPart connection;
		private int index;
		private boolean source;
		
		public AddConnection(ConnectionEditPart connection, int index, boolean source) {
			this.connection = connection;
			this.index = index;
			this.source = source;
		}
		
		
		public void run() {
			if (source) {
				addSourceConnection(connection, index);
			}
			else {
				addTargetConnection(connection, index);
			}
		}
	}
	
	/**
	 * Runnable class that removes connections from the
	 * diagram.
	 * Must be run in UI-thread.
	 * @author nic
	 *
	 */
	private class RemoveConnection implements Runnable {
		private ConnectionEditPart connection;
		private boolean source;
		
		public RemoveConnection(ConnectionEditPart connection, boolean source) {
			this.connection = connection;
			this.source = source;
		}
		
		
		public void run() {
			if (source) {
				removeSourceConnection(connection);
			}
			else {
				removeTargetConnection(connection);
			}
		}
	}
}