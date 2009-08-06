package com.eclipse.featdiag.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.utils.FileUtils;


/**
 * Listener for changes made to resources in the workbench.
 * @author nic
 *
 */
public class FileSaveListener implements IResourceChangeListener, IResourceDeltaVisitor {
	//private String associatedClassFile;
	//private IFile javaFile;
	private DiagramModel diagram;
	private DiagramEditor diagramEditor;
	
	/**
	 * Creates a new listener for changes made to the given
	 * java file.
	 * Changes will be reflected in the given diagram.
	 * @param diagram
	 * @param javaFile
	 * @param associateJavaFile
	 */
	public FileSaveListener(DiagramEditor diagramEditor, DiagramModel diagram, IFile javaFile, String associateJavaFile) {
		this.diagram = diagram;
		this.diagramEditor = diagramEditor;
		//this.javaFile = javaFile;
		//this.associatedClassFile = associateJavaFile;
	}

	
	public void resourceChanged(IResourceChangeEvent event) {
	      if (event.getType() == IResourceChangeEvent.POST_BUILD) {
			try {
				event.getDelta().accept(this);
			} 
			catch (CoreException e) {
			}
		}
	}

	
	public boolean visit(IResourceDelta delta) throws CoreException {
		//delta = delta.findMember(new Path(associatedClassFile));
		//delta = delta.findMember(new Path(diagram.getAssociatedJavaFile()));
		if (delta != null) {
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE &&
				((delta.getFlags() & IResourceDelta.CONTENT) > 0 ||
				 (delta.getFlags() & IResourceDelta.ENCODING) > 0)) {
				
				//FileUtils.updateDiagram((IFile) resource, diagram);
				//diagram.update();
				//diagramEditor.update();
				//diagram.doSave();
				//diagramEditor.doSave(new NullProgressMonitor());
			}
		}
		
		return false;		
	}
	
	/**
	 * Removes this listener from the associated file.
	 */
	public void dispose() {
		//javaFile.getWorkspace().removeResourceChangeListener(this);
	}
}