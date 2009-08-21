package com.eclipse.featdiag.zoom;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.editors.DiagramEditor;

public class EditorActionZoom implements IEditorActionDelegate {
	private IEditorPart targetGraphicalViewer;
	
	public void setActiveEditor(IAction action, IEditorPart graphicalViewer) {
		targetGraphicalViewer = graphicalViewer;
	}

	public void run(IAction action) {
		if(action.getId().equals("150")){
			((DiagramEditor) targetGraphicalViewer).zoomByPercent(1.50);
		}else if(action.getId().equals("125")){
			((DiagramEditor) targetGraphicalViewer).zoomByPercent(1.25);
		}else if(action.getId().equals("100")){
			((DiagramEditor) targetGraphicalViewer).zoomByPercent(1.00);
		}else if(action.getId().equals("75")){
			((DiagramEditor) targetGraphicalViewer).zoomByPercent(0.75);
		}else if(action.getId().equals("50")){
			((DiagramEditor) targetGraphicalViewer).zoomByPercent(0.50);
		}else if(action.getId().equals("fitToPage")){
			((DiagramEditor) targetGraphicalViewer).zoomByPercent(0.0);
		}else{
			assert false;
		}
	}

	public void selectionChanged( final IAction action, 
			final ISelection selection ) {
		// do nothing
	}

}
