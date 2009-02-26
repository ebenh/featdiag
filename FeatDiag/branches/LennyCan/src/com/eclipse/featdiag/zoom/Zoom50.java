package com.eclipse.featdiag.zoom;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.editors.DiagramEditor;

public class Zoom50 implements IEditorActionDelegate {

	@SuppressWarnings("unused")
	private ISelection selection;
	private IEditorPart targetGraphicalViewer;
	
	public void setActiveEditor(IAction action, IEditorPart graphicalViewer) {
		targetGraphicalViewer = graphicalViewer;
	}

	public void run(IAction action) {
		((DiagramEditor) targetGraphicalViewer).zoomByPercent(0.5);
	}

	public void selectionChanged( final IAction action, 
			final ISelection selection ) {
		this.selection = selection;
	}

}
