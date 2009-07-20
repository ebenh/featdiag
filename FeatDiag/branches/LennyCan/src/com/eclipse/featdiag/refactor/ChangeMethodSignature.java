package com.eclipse.featdiag.refactor;

import org.eclipse.jdt.ui.actions.ModifyParametersAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.models.MethodModel;
import com.eclipse.featdiag.parts.*;

public class ChangeMethodSignature implements IEditorActionDelegate {
	private ISelection selection;
	private IEditorPart graphicalViewer;

	public void setActiveEditor(IAction action, IEditorPart graphicalViewer) {
		this.graphicalViewer = graphicalViewer;
	}

	public void run(IAction action) {
		try {
			MethodPart element = (MethodPart) ((StructuredSelection) selection).getFirstElement();
			MethodModel model = (MethodModel) element.getMemberModel();

			ModifyParametersAction ra = new ModifyParametersAction(graphicalViewer.getSite());
			ra.run(new StructuredSelection(model.getMethod()));
		} catch (ClassCastException e) {
			MessageDialog.openInformation(graphicalViewer.getSite().getShell(), "Operation Not Applicable",
					"The operation can only be performed on methods.");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			this.selection = selection;
		}
	}
}
