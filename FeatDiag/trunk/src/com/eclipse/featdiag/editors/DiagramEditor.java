package com.eclipse.featdiag.editors;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import com.eclipse.featdiag.editors.palette.PaletteViewer;
import com.eclipse.featdiag.factories.BaseFactory;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.PaletteModel;
import com.eclipse.featdiag.parser.InvalidClassName;
import com.eclipse.featdiag.parser.Parser;
import com.eclipse.featdiag.parser.meyers.ISOMLayout;
import com.eclipse.featdiag.parts.DiagramPart;
import com.eclipse.featdiag.utils.FileUtils;


/**
 * Base editor containing a graphical viewer and a flyout palette.
 * This class will contain the whole feature diagram.
 * 
 * @author nic
 * 
 */

public class DiagramEditor extends GraphicalEditorWithFlyoutPalette implements IPartListener {
	private boolean javaFileOpen = false;
	private FileSaveListener listener;
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
		if (listener != null) {
			listener.dispose();
		}
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
//	private DiagramModel loadDiagramModel() {
//		DiagramModel content = new DiagramModel();
//		IEditorInput input = getEditorInput();
//		
//		if (input instanceof IFileEditorInput) {
//	        try
//	        {
//	        	IFile featFile = ((IFileEditorInput) input).getFile();
//	            setPartName(featFile.getName());
//	            InputStream is = featFile.getContents(true);
//	            ObjectInputStream ois = new ObjectInputStream(is);
//	            content = (DiagramModel) ois.readObject();
//
//	            // Update diagram to reflect any changes made to
//	            // associated java file since diagram closed
////	            IProject proj = featFile.getProject();
////	            IPath javaPath = new Path(content.getAssociatedJavaFile());
////	            if (proj.getFullPath().isPrefixOf(javaPath)) {
////	            	int i = javaPath.matchingFirstSegments(proj.getFullPath());
////	            	javaPath = javaPath.removeFirstSegments(i);
////	            }
////	            IFile file = (IFile) proj.findMember(javaPath);
////	            
////	            IFile classFile = FileUtils.getClassFile(file);
////	            FileUtils.updateDiagram(classFile, content);
//	            ois.close();
//	        }
//	        // Exceptions could get thrown if saved file is empty,
//	        // corrupt, or does not exist.
//	        //TODO Should show error message? In pop-up? In editor?
//	        catch (ClassCastException e) {System.err.println(e.getMessage());}
//	        catch (CoreException e) {System.err.println(e.getMessage());} 
//	        catch (IOException e) {System.err.println(e.getMessage());} 
//	        catch (ClassNotFoundException e) {System.err.println(e.getMessage());}
//		}
//		return content;
//	}
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
	 */
	public void addMembers (String classFile, String associatedJavaFile) {
	    try {
	    	DiagramPart contents = getContents();
	    	DiagramModel model = contents.getDiagramModel();
	    	model.setAssociatedJavaFile(associatedJavaFile);
	    	
            new Parser(classFile).addMembersToDiagram(model);
            getGraphicalViewer().setContents(model);
            new ISOMLayout(model).autoArrange();
            refresh();
            setFileName();
            doSave(new NullProgressMonitor());
        }
	    catch(InvalidClassName e) {}
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
	
	/**
	 * Called when a workbench part changes. Checks if part is
	 * the editor for the associated java file. If it is, returns
	 * the java file input to the editor. Otherwise returns null.
	 * @param part
	 * @param associatedFile
	 * @return
	 */
	private IFile getFile(IWorkbenchPart part, String associatedFile)	{
		if (part instanceof EditorPart) {
			IEditorInput input = ((EditorPart) part).getEditorInput();
			if (input instanceof FileEditorInput) {
				IFile file = ((FileEditorInput) input).getFile();
				if (file != null) {
					if (file.getFullPath().toString().equals(associatedFile)) {
						return file;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Check if the recently opened/activated part is the
	 * java file associated with this diagram. If it is,
	 * add this as as listener for changes.
	 * @param part
	 */
	private void handleNewPart(IWorkbenchPart part) {
		DiagramPart contents = getContents();			
		if (contents != null) {
			DiagramModel model = contents.getDiagramModel();
			String associatedFile = model.getAssociatedJavaFile();
			IFile file = getFile(part, associatedFile);
			if (file != null) {
				IFile classFile = FileUtils.getClassFile(file);
				String classFileString = classFile.getFullPath().toString();
				listener = new FileSaveListener(model, file, classFileString);
				
				file.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
				javaFileOpen = true;
			}
		}
	}
	
	/**
	 * Check if the recently closed part is the java
	 * file associated with this diagram. If it is,
	 * remove this as a listener for changes.
	 * @param part
	 */
	private void handlePartClosed(IWorkbenchPart part) {
		DiagramPart contents = getContents();			
		if (contents != null) {
			String associatedFile = contents.getDiagramModel().getAssociatedJavaFile();
			IFile file = getFile(part, associatedFile);
			if (file != null) {
				file.getWorkspace().removeResourceChangeListener(listener);
				javaFileOpen = false;
			}
		}
	}
	
	
	public void partOpened(IWorkbenchPart part) {
		if (!javaFileOpen) {
			handleNewPart(part);
		}
	}

	
	public void partActivated(IWorkbenchPart part) {
		if (!javaFileOpen) {
			handleNewPart(part);
		}
	}
	
	
	public void partClosed(IWorkbenchPart part) {
		if (javaFileOpen) {
			handlePartClosed(part);
		}
	}

	
	public void partDeactivated(IWorkbenchPart part) {
		// Do Nothing
	}
	
	
	public void partBroughtToTop(IWorkbenchPart part) {
		// Do Nothing
	}


	public void addMembers(IType classType) {
		DiagramPart contents = getContents();
		DiagramModel model = contents.getDiagramModel();
		model.addMembers(classType);
		
	    getGraphicalViewer().setContents(model);
	    new ISOMLayout(model).autoArrange();
	    refresh();
	    setFileName();
	    doSave(new NullProgressMonitor());		
	}
}
