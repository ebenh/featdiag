package com.eclipse.featdiag.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.eclipse.featdiag.editors.DiagramEditor;
import com.eclipse.featdiag.editors.FileSaveListener;
import com.eclipse.featdiag.utils.FileUtils;


/**
 * Action to open the feature diagram associated with the
 * selected class in a feature diagram editor.
 * @author nic
 *
 */
public class OpenNewEditorAction implements IActionDelegate {
	boolean diagExists = false;
	ISelection selection;
	DiagramEditor editor = null;
	/**
	 * Called when option to open new feature diagram is selected
	 * from the context menu.
	 */
	public void run(IAction action) {
		try {
			IJavaElement element = (IJavaElement) ((StructuredSelection) selection)
					.getFirstElement();
			IJavaProject javaProj = element.getJavaProject();
			IProject proj = javaProj.getProject();
			IPath path = element.getPath();
			IFile featFile = createFile(path, javaProj.getProject());
			
			if (proj.getFullPath().isPrefixOf(path)) {
				int i = path.matchingFirstSegments(proj.getFullPath());
				path = path.removeFirstSegments(i);
			}
			IFile javaFile = proj.getFile(path);
			IFile classFile = FileUtils.getClassFile(javaFile);

			if (featFile != null) {
				FileEditorInput input = new FileEditorInput(featFile);
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				editor = (DiagramEditor) window.getActivePage()
						.openEditor(input,
								"featdiag.editors.FeatureDiagramEditor");
				
				
		        
		        
				if (classFile != null && !getDiagExists()) {
					String classFilePath = classFile.getLocation().toString();
					editor.addMembers(classFilePath, javaFile.getFullPath().toString());
				}
			}
		} catch (PartInitException e) {
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Set up a listener for file changes.
		IPath filePath = null;
		try {
			filePath = editor.getFilePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (filePath != null) {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(filePath);
			FileSaveListener listener = editor.setListener(file);
			file.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
		}
		
	}
	
	/**
	 * Returns whether or not the feature diagram associated
	 * with the selected class already exists.
	 * @return
	 */
	private boolean getDiagExists() {
		return diagExists;
	}

	/**
	 * Selected class changed.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			this.selection = selection;
		}
	}

	/**
	 * Places the feature diagram in the project folder. If the
	 * file does not already exist, it is created.
	 * @param path
	 * @param proj
	 * @return
	 */
	private IFile createFile(IPath path, IProject proj) {
		IFile retval = null;
		try {
			// Get just the name of the new .feat file
			path = new Path(path.lastSegment());
			path = path.removeFileExtension().addFileExtension("feat");

			// Place the new file in the project directory.
			// Create the file if it does not exist.
			IPath filePath = proj.getLocation().append(path);
			File file = filePath.toFile();
			
			diagExists = (file == null || file.exists());
			file.createNewFile();

			// Notify the workspace that the file has changed
			retval = proj.getFile(path);
			proj.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (CoreException e) {
			System.out.println(e.getMessage());
		}
		return retval;
	}
}
