package com.eclipse.featdiag.editors;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import com.eclipse.featdiag.editors.palette.PaletteViewer;
import com.eclipse.featdiag.factories.BaseFactory;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.PaletteModel;
import com.eclipse.featdiag.parser.meyers.ISOMLayout;
import com.eclipse.featdiag.parts.DiagramPart;
import org.eclipse.gef.MouseWheelZoomHandler;


/**
 * Base editor containing a graphical viewer and a flyout palette.
 * This class will contain the whole feature diagram.
 * 
 * @author nic
 * 
 */

public class DiagramEditor extends GraphicalEditorWithFlyoutPalette implements IPartListener {
	private PaletteViewer paletteViwer;
	
	/**
	 * Creates a new editor.
	 */
	public DiagramEditor() {
		paletteViwer = new PaletteViewer();
		setEditDomain(new DefaultEditDomain(this));
	}
	
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().getPage().addPartListener(this);
	}
	
	
	public void dispose() {
		getSite().getPage().removePartListener(this);
		super.dispose();
	}
	
	/**
	 * Initializes the existing graphical viewer. Sets the factory for creating
	 * parts to our BaseFactory, and sets the contents to be the root feature
	 * diagram class.
	 */
	
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setEditPartFactory(new BaseFactory());

		DiagramModel contents = loadDiagramModel();
		PaletteModel palette = contents.getPaletteModel();
		palette.addPropertyChangeListener(paletteViwer);
		paletteViwer.addContents(palette);
		getGraphicalViewer().setContents(contents);
		getGraphicalViewer().setKeyHandler(getKeyHandler());
		getGraphicalViewer().setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);
		setFileName();

		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		getSite().registerContextMenu(menuMgr, getGraphicalViewer());
		
		Control control = getGraphicalViewer().getControl();
		Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);
	}
	
	
	protected PaletteRoot getPaletteRoot() {
		return paletteViwer.getPaletteRoot();
	}
	
	/**
	 * Saves the contents of this editor.
	 */
	
	public void doSave(IProgressMonitor monitor) {
		DiagramPart content = getContents();
		content.doSave(monitor, false);
	}
	
	public void zoomByPercent(double zoom) {
		if(zoom == 0)
			((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager().setZoomAsText(ZoomManager.FIT_ALL);
		else
			((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager().setZoom(zoom);
	}
	
	public void commandStackChanged(EventObject evt) {
		/* If this feature diagram is not the active editor,
		 * and a save is triggered by an update to the
		 * corresponding java editor, simply calling
		 * firePropertyChange(PROP_DIRTY) does not update
		 * the * in the tab of this editor.
		 * By calling firePropertyChange(PROP_DIRTY) from
		 * the UI thread, it is updated regardless of the
		 * active editor.
		 */
		
		Display.getDefault().syncExec(new Runnable() {
			
			public void run() {
				firePropertyChange(PROP_DIRTY);
			}			
		});
	}
	
	/**
	 * Loads the DiagramModel saved in the given input file.
	 * If the file is empty, corrupt, or does not exist,
	 * returns an empty DiagramModel.
	 * @return
	 */
	private DiagramModel loadDiagramModel() {
		DiagramModel content = new DiagramModel();
		IEditorInput input = getEditorInput();

		assert input instanceof IFileEditorInput;

		try {
			IFile featFile = ((IFileEditorInput) input).getFile();
			setPartName(featFile.getName());
			InputStream is = featFile.getContents(true);
			ObjectInputStream ois = new ObjectInputStream(is);
			content = (DiagramModel) ois.readObject();
			ois.close();
		} catch (CoreException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}

		return content;
	}

	/**
	 * Creates and returns a new key handler. Currently the key handler allows
	 * selecting and switching focus of nodes using the arrow keys.
	 * 
	 * @return
	 */
	private KeyHandler getKeyHandler() {
		KeyHandler handler = new GraphicalViewerKeyHandler(getGraphicalViewer());
		handler.setParent(getGraphicalViewer().getKeyHandler());

		handler.put(KeyStroke.getPressed((char) 26, 122, SWT.CTRL),
				getActionRegistry().getAction(ActionFactory.UNDO.getId()));
		handler.put(KeyStroke.getPressed((char) 25, 121, SWT.CTRL),
				getActionRegistry().getAction(ActionFactory.REDO.getId()));
		return handler;
	}
	
	/**
	 * Parses the given .class file and adds all the fields, methods,
	 * and connections to this editor.
	 * @param classFile
	 * @throws CoreException 
	 */
	public void addMembers(IType classType) {
		DiagramPart contents = getContents();
		DiagramModel model = contents.getDiagramModel();
		model.addMembers(classType);
	    new ISOMLayout(model).autoArrange();
	    autoArrange();
	    refresh();
	    setFileName();
	    doSave(new NullProgressMonitor());		
	}

	public void autoArrange(){
		DiagramPart contents = getContents();
		final DiagramModel model = contents.getDiagramModel();

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				new ISOMLayout(model).autoArrange();
			}			
		});		
	}
	
	public void update(){
		DiagramPart contents = getContents();
		DiagramModel model = contents.getDiagramModel();
		boolean ret = model.update();
		if(ret == false){
    		MessageDialog.openInformation(getSite().getShell(), 
    				"Error", "Class does not exist.");
		}
	    refresh();
	    doSave(new NullProgressMonitor());
	}
	
	// note eben
	public boolean isConsistent(){
		DiagramPart contents = getContents();
		DiagramModel model = contents.getDiagramModel();
		return model.isConsistent();
	}
	
	/**
	 * Refreshes all edit parts in this editor.
	 */
	public void refresh() {
		EditPart root = getGraphicalViewer().getRootEditPart();
		refresh (root.getChildren());
	}
	
	/**
	 * Refreshes the given list of children and all their
	 * children.
	 * @param children
	 */
	private void refresh(List<?> children) {
		if (children == null) {
			return;
		}
		for (Iterator<?> iter = children.iterator(); iter.hasNext(); ) {
			EditPart child = (EditPart) iter.next();
			child.refresh();
			refresh(child.getChildren());
		}
	}
	
	/**
	 * Get the DiagramPart shown in this editor
	 * @return
	 */
	private DiagramPart getContents() {
		GraphicalViewer viewer = getGraphicalViewer();
		if (viewer != null) {
			return (DiagramPart) viewer.getContents();
		}
		return null;
	}
	
	/**
	 * Sets the name of the .feat file in the
	 * diagram controller.
	 */
	private void setFileName() {
		DiagramPart contents = getContents();
		if (contents != null) {
			contents.setFeatFile(((IFileEditorInput) getEditorInput()).getFile());
		}
	}
		
	public void partOpened(IWorkbenchPart part) {

	} 
	
	public void partActivated(IWorkbenchPart part) {
		/* Check if this diagram is consistent, i.e. all the fields and methods
		 * it references still exist. If the diagram is inconsistent, regenerate
		 * the diagram. 
		 */
		if(part == this && !isConsistent()){
    		MessageDialog.openInformation(getSite().getShell(), 
    				"Diagram is out of date", "One or more of the elements in this diagram no longer exist. This diagram needs to be updated.");
    		update();
		}
	}
	
	
	public void partClosed(IWorkbenchPart part) {

	}

	
	public void partDeactivated(IWorkbenchPart part) {
		// Do Nothing
	}
	
	
	public void partBroughtToTop(IWorkbenchPart part) {
		// Do Nothing
	}
}
