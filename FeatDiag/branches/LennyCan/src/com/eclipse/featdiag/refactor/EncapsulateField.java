package com.eclipse.featdiag.refactor;

import org.eclipse.jdt.ui.actions.SelfEncapsulateFieldAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.models.FieldModel;
import com.eclipse.featdiag.parts.FieldPart;

public class EncapsulateField implements IEditorActionDelegate {
	private ISelection selection;
	private IEditorPart graphicalViewer;
	
	public void setActiveEditor(IAction action, IEditorPart graphicalViewer) {
		this.graphicalViewer = graphicalViewer;
	}

	public void run(IAction action) {
		try {
			FieldPart element = (FieldPart) ((StructuredSelection) selection).getFirstElement();
			FieldModel model = (FieldModel) element.getMemberModel();

			SelfEncapsulateFieldAction ra = new SelfEncapsulateFieldAction(graphicalViewer.getSite());
			ra.run(new StructuredSelection(model.getField()));
		} catch (ClassCastException e) {
			MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
			"Operation Not Applicable", "This operation can be performed on fields only.");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			this.selection = selection;
		}
	}
}
