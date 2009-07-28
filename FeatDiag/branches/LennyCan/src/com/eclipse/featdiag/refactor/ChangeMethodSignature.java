package com.eclipse.featdiag.refactor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.jdt.internal.ui.refactoring.actions.InlineConstantAction;
import org.eclipse.jdt.internal.ui.refactoring.actions.InlineMethodAction;
import org.eclipse.jdt.ui.actions.ModifyParametersAction;
import org.eclipse.jdt.ui.actions.RenameAction;
import org.eclipse.jdt.ui.actions.SelfEncapsulateFieldAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.editors.DiagramEditor;
import com.eclipse.featdiag.models.FieldModel;
import com.eclipse.featdiag.models.MemberModel;
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
			if(action.getId().equals("changemethodsignature")){
				changeMethodSignature();
			}else if(action.getId().equals("encapfield")){
				encapsulateField();
			}else if(action.getId().equals("inline")){
				inline();
			}else if(action.getId().equals("rename")){
				rename();
			}else{
				assert false;
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// this should never happen
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			this.selection = selection;
		}
	}
	
	private void changeMethodSignature() throws JavaModelException{
		try {
			MethodPart element = (MethodPart) ((StructuredSelection) selection).getFirstElement();
			MethodModel model = (MethodModel) element.getMemberModel();

			ModifyParametersAction ra = new ModifyParametersAction(graphicalViewer.getSite());
			StructuredSelection selection = new StructuredSelection(model.getMethod());
			if(RefactoringAvailabilityTester.isChangeSignatureAvailable(selection)){
				ra.run(selection);
				((DiagramEditor)graphicalViewer).update();
				((DiagramEditor)graphicalViewer).doSave(new NullProgressMonitor());
			}else{
	    		MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
	    				"Operation Not Applicable", "This operation can be performed on this method.");
			}
		} catch (ClassCastException e) {
			MessageDialog.openInformation(graphicalViewer.getSite().getShell(), "Operation Not Applicable",
					"The operation can only be performed on methods.");
		}		
	}
	
	private void encapsulateField() throws JavaModelException{
		try {
			FieldPart element = (FieldPart) ((StructuredSelection) selection).getFirstElement();
			FieldModel model = (FieldModel) element.getMemberModel();

			SelfEncapsulateFieldAction ra = new SelfEncapsulateFieldAction(graphicalViewer.getSite());
			StructuredSelection selection = new StructuredSelection(model.getField());
			if(RefactoringAvailabilityTester.isSelfEncapsulateAvailable(selection)){
				ra.run(selection);
				((DiagramEditor)graphicalViewer).update();
				((DiagramEditor)graphicalViewer).doSave(new NullProgressMonitor());
			}else{
	    		MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
	    				"Operation Not Applicable", "This operation can be performed on this field.");
			}
		} catch (ClassCastException e) {
			MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
			"Operation Not Applicable", "This operation can be performed on fields only.");
		}
	}
	
	private void inline() throws JavaModelException{
		MemberPart element = (MemberPart) ((StructuredSelection) selection).getFirstElement();
		MemberModel model = element.getMemberModel();
		
		if(model instanceof FieldModel){
			InlineConstantAction ra = new InlineConstantAction(graphicalViewer.getSite());
			StructuredSelection selection = new StructuredSelection(((FieldModel)model).getField());
			if(RefactoringAvailabilityTester.isInlineConstantAvailable(selection)){
				ra.run(selection);
				((DiagramEditor)graphicalViewer).update();
				((DiagramEditor)graphicalViewer).doSave(new NullProgressMonitor());
			}else{
	    		MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
	    				"Operation Not Applicable", "This operation can be performed on this field.");
			}
		}else if(model instanceof MethodModel){
	    	InlineMethodAction ra = new InlineMethodAction(graphicalViewer.getSite());
	    	StructuredSelection selection = new StructuredSelection(((MethodModel)model).getMethod());
	    	if(RefactoringAvailabilityTester.isInlineMethodAvailable(selection)){
	    		ra.run(selection);
	    		((DiagramEditor)graphicalViewer).update();
	    		((DiagramEditor)graphicalViewer).doSave(new NullProgressMonitor());
	    	}else{
	    		MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
	    				"Operation Not Applicable", "This operation can be performed on this method.");
	    	}
		}else{
			assert false;
		}
	}
	
	private void rename() throws CoreException{
		MemberPart element = (MemberPart) ((StructuredSelection) selection).getFirstElement();
		MemberModel model = element.getMemberModel();
		
		if(model instanceof FieldModel){
	        RenameAction ra = new RenameAction(graphicalViewer.getSite());
	        IField field = ((FieldModel)model).getField();
	        if(RefactoringAvailabilityTester.isRenameFieldAvailable(field)){
	        	ra.run(new StructuredSelection(field));
	        	((DiagramEditor)graphicalViewer).update();
    			((DiagramEditor)graphicalViewer).doSave(new NullProgressMonitor());
	        }else{
	    		MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
	    				"Operation Not Applicable", "This operation can be performed on this field.");
	        }
		}else if(model instanceof MethodModel){
	        RenameAction ra = new RenameAction(graphicalViewer.getSite());
	        IMethod method = ((MethodModel)model).getMethod();
	        if(RefactoringAvailabilityTester.isRenameAvailable(method)){
	        	ra.run(new StructuredSelection(method));
	        	((DiagramEditor)graphicalViewer).update();
    			((DiagramEditor)graphicalViewer).doSave(new NullProgressMonitor());
	        }else{
	    		MessageDialog.openInformation(graphicalViewer.getSite().getShell(), 
	    				"Operation Not Applicable", "This operation can be performed on this method.");
	        }
		}else{
			assert false;
		}
	}
}
