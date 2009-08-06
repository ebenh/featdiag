package com.eclipse.featdiag.parts;

import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Display;

import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.MemberModel;
import com.eclipse.featdiag.policies.DiagramXYLayoutEditPolicy;


/**
 * Controller class for root class diagram object.
 * Handles layout of the nodes representing
 * class members.
 * @author nic
 *
 */
public class DiagramPart extends BasePart {
	private IFile featFile;
	
	/**
	 * Saves the .feat file to save the diagram to.
	 * @param file
	 */
	public void setFeatFile(IFile file) {
		this.featFile = file;
	}
	
	public IFile getFeatFile() {
		return featFile;
	}

	/**
	 * Creates the figure used as this member's visual representation.
	 * In this case, an empty figure with a layout manager to contain
	 * the figures of the class members.
	 */
	
	protected IFigure createFigure() {
		Figure f = new Figure();
		f.setOpaque(true);
		f.setLayoutManager(new FreeformLayout());
		return f;
	}

	/**
	 * Defines the actions allowed to be performed on this image.
	 */
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new DiagramXYLayoutEditPolicy());
	}

	/**
	 * Returns the list of model objects representing the class members
	 * in this diagram.
	 */
	
	protected List<MemberModel> getModelChildren() {
		return getDiagramModel().getMemberModels();
	}

	@SuppressWarnings("unchecked")
	protected void handleChildChange(PropertyChangeEvent evt) {
		Object newValue = evt.getNewValue();
		Object oldValue = evt.getOldValue();

		if (oldValue != null && newValue != null) {
			throw new IllegalStateException(
			"Exactly one of old or new values must be non-null for CHILD event");
		}

		// New Member Added
		if (oldValue == null) {
			EditPart editPart = createChild(newValue);
			int modelIndex = getModelChildren().indexOf(newValue);
			Display.getDefault().syncExec(new AddToDiagram(editPart, modelIndex));
		} 
		// MemberRemoved
		else {
			List<EditPart> children = getChildren();
			
			EditPart toRemove = null;
			for (EditPart part : children) {
				if (part.getModel().equals(oldValue)) {
					toRemove = part;
					break;
				}
			}

			if (toRemove != null) {
				Display.getDefault().syncExec(new RemoveFromDiagram(toRemove));
			}
		}
	}

	/**
	 * Returns the DiagramModel object associated with
	 * this controller.
	 * @return
	 */
	public DiagramModel getDiagramModel() {
		return (DiagramModel) getModel();
	}
	
	/**
	 * Runnable class that adds nodes to the
	 * diagram.
	 * Must be run in UI-thread.
	 * @author nic
	 *
	 */
	private class AddToDiagram implements Runnable {
		private EditPart child;
		private int index;
		
		public AddToDiagram(EditPart child, int index) {
			this.child = child;
			this.index = index;
		}
		
		
		public void run() {
			addChild(child, index);
		}
	}
	
	/**
	 * Runnable class that removes nodes from
	 * the diagram.
	 * Must be run in UI-thread.
	 * @author nic
	 *
	 */
	private class RemoveFromDiagram implements Runnable {
		private EditPart child;
		
		public RemoveFromDiagram(EditPart child) {
			this.child = child;
		}
		
		
		public void run() {
			removeChild(child);
		}
	}
	
	
	public void doSave(IProgressMonitor monitor, boolean flush) {
		try {
			DiagramModel model = getDiagramModel();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream objectOut = new ObjectOutputStream(out);
			objectOut.writeObject(model);
			objectOut.close();
			
			if (featFile != null && featFile.exists()) {			
				featFile.setContents(new ByteArrayInputStream(out.toByteArray()), true, 
						false, monitor);
			}
			
			out.close();
			CommandStack stack = getViewer().getEditDomain().getCommandStack();
			if (flush) {
				stack.flush();
			}
			stack.markSaveLocation();
	    //TODO Should show error message? In pop-up? In editor?
		} 
		catch (IOException e) {System.err.println(e.toString());} 
		catch (CoreException e) {System.err.println(e.toString());}
	}

}
