package com.eclipse.featdiag.refactor;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.jdt.internal.ui.refactoring.actions.InlineConstantAction;
import org.eclipse.jdt.internal.ui.refactoring.actions.InlineMethodAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.editors.DiagramEditor;
import com.eclipse.featdiag.editors.FileSaveListener;
import com.eclipse.featdiag.parts.*;
import com.eclipse.featdiag.utils.SelectionWrapper;

public class Inline implements IEditorActionDelegate {

	private ISelection selection;
	private IEditorPart targetGraphicalViewer;
	private String flag;
	
	public void setActiveEditor(IAction action, IEditorPart graphicalViewer) {
		targetGraphicalViewer = graphicalViewer;
	}

	public void run(IAction action) {		
		IPath path = null;
		try {
			path = ((DiagramEditor) targetGraphicalViewer).getFilePath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
        FileSaveListener listener = ((DiagramEditor) targetGraphicalViewer).setListener(file);
        file.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
        if(flag == "field") {
        	try {
    			if (!RefactoringAvailabilityTester.isInlineConstantAvailable((IStructuredSelection) selection)) {
    				MessageDialog.openInformation(targetGraphicalViewer.getSite().getShell(), 
    						"Operation Not Applicable", "The selected field is not STATIC FINAL.");
    				return;
    			}
    		} catch (JavaModelException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
        	InlineConstantAction ra = new InlineConstantAction(((DiagramEditor) targetGraphicalViewer).getSite());
        	ra.run((TreeSelection) selection);
        }
        else if(flag == "method") {
        	InlineMethodAction ra = new InlineMethodAction(((DiagramEditor) targetGraphicalViewer).getSite());
        	ra.run((TreeSelection) selection);
        } else {
        	MessageDialog.openInformation(targetGraphicalViewer.getSite().getShell(), 
					"Operation Not Applicable", "The operation cannot be performed on class object.");
        }
	}

	public void selectionChanged( final IAction action, 
			final ISelection selection ) {
		Object part = ((IStructuredSelection) selection).getFirstElement();
		if(part instanceof FieldPart)
			flag = "field";
		else if(part instanceof MethodPart)
			flag = "method";
		else {
			flag = "diagram";
			this.selection = selection;
			return;
		}
		SelectionWrapper sw = new SelectionWrapper(selection, targetGraphicalViewer);
		try {
			this.selection = sw.wrap(flag);
		}
		catch (IOException e) {this.selection = selection;} 
	}

}
