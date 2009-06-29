package com.eclipse.featdiag.refactor;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.actions.PushDownAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.editors.DiagramEditor;
import com.eclipse.featdiag.editors.FileSaveListener;
import com.eclipse.featdiag.parts.DiagramPart;
import com.eclipse.featdiag.parts.FieldPart;
import com.eclipse.featdiag.parts.MethodPart;
import com.eclipse.featdiag.utils.SelectionWrapper;

public class PushDown implements IEditorActionDelegate {
	private ISelection selection;
	private IEditorPart targetGraphicalViewer;
	
	public void setActiveEditor(IAction action, IEditorPart graphicalViewer) {
		targetGraphicalViewer = graphicalViewer;
	}

	public void run(IAction action) {
		IPath path = null;
		try {
			path = ((DiagramEditor) targetGraphicalViewer).getFilePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
        FileSaveListener listener = ((DiagramEditor) targetGraphicalViewer).setListener(file);
        file.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
        PushDownAction ra = new PushDownAction(((DiagramEditor) targetGraphicalViewer).getSite());
        ra.run((TreeSelection) selection);
	}

	public void selectionChanged( final IAction action, 
			final ISelection selection ) {
		String flag;
		Object part = ((IStructuredSelection) selection).getFirstElement();
		if(part instanceof FieldPart)
			flag = "field";
		else if(part instanceof MethodPart)
			flag = "method";
		else if(part instanceof DiagramPart)
			flag = "diagram";
		else {
			this.selection = selection;
			return;
		}
		SelectionWrapper sw = new SelectionWrapper(selection, targetGraphicalViewer);
		try {
			this.selection = sw.wrap(flag);
		} catch (IOException e) {
			this.selection = selection;
		}
	}

}