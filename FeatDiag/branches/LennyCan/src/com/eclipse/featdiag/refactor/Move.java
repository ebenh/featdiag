package com.eclipse.featdiag.refactor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.jdt.internal.ui.refactoring.actions.MoveInstanceMethodAction;
import org.eclipse.jdt.internal.ui.refactoring.actions.MoveStaticMembersAction;
import org.eclipse.jdt.internal.ui.refactoring.reorg.ReorgMoveAction;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;

import com.eclipse.featdiag.editors.DiagramEditor;
//import com.eclipse.featdiag.editors.FileSaveListener;
import com.eclipse.featdiag.parts.*;
//import com.eclipse.featdiag.utils.SelectionWrapper;

public class Move implements IEditorActionDelegate {
	
	private ISelection selection;
	private IEditorPart targetGraphicalViewer;
	private MoveInstanceMethodAction fMoveInstanceMethodAction;
	private MoveStaticMembersAction fMoveStaticMembersAction;
	private ReorgMoveAction fReorgMoveAction;
	
	public void setActiveEditor(IAction action, IEditorPart graphicalViewer) {
		targetGraphicalViewer = graphicalViewer;
	}

	public void run(IAction action) {    
//		IPath path = null;
//		try {
//			path = ((DiagramEditor) targetGraphicalViewer).getFilePath();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
//        FileSaveListener listener = ((DiagramEditor) targetGraphicalViewer).setListener(file);
//        file.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
//        IWorkbenchSite site = ((DiagramEditor) targetGraphicalViewer).getSite();
//        fMoveStaticMembersAction= new MoveStaticMembersAction(site);
//        fMoveInstanceMethodAction= new MoveInstanceMethodAction(site);
//        fReorgMoveAction= new ReorgMoveAction(site);
//        try {
//        	if (fMoveInstanceMethodAction.isEnabled() && tryMoveInstanceMethod((TreeSelection) selection)) 
//        		return;
//        	
//        	if (fMoveStaticMembersAction.isEnabled() && tryMoveStaticMembers((TreeSelection) selection)) 
//        		return;
//        	
//        	if (fReorgMoveAction.isEnabled())
//        		fReorgMoveAction.run((TreeSelection) selection);
//        		
//        } catch (JavaModelException e) {
//        	ExceptionHandler.handle(e, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.OpenRefactoringWizardAction_exception); 
//        }
	}

	public void selectionChanged( final IAction action, 
			final ISelection selection ) {
//		String flag;
//		Object part = ((IStructuredSelection) selection).getFirstElement();
//		if(part instanceof FieldPart)
//			flag = "field";
//		else if(part instanceof MethodPart)
//			flag = "method";
//		else if(part instanceof DiagramPart)
//			flag = "diagram";
//		else {
//			this.selection = selection;
//			return;
//		}
//		SelectionWrapper sw = new SelectionWrapper(selection, targetGraphicalViewer);
//		try {
//			this.selection = sw.wrap(flag);
//		}
//		catch (IOException e) {this.selection = selection;} 
	}

	
//	private boolean tryMoveStaticMembers(IStructuredSelection selection) throws JavaModelException {
//		IMember[] array= getSelectedMembers(selection);
//		if (!RefactoringAvailabilityTester.isMoveStaticMembersAvailable(array))
//			return false;
//		fMoveStaticMembersAction.run(selection);
//		return true;
//	}
//	
//	private boolean tryMoveInstanceMethod(IStructuredSelection selection) throws JavaModelException {
//		IMethod method= getSingleSelectedMethod(selection);
//		if (method == null)
//			return false;
//		if (!RefactoringAvailabilityTester.isMoveMethodAvailable(method))
//			return false;
//		fMoveInstanceMethodAction.run(selection);
//		return true;
//	}
//	
//	private static IMethod getSingleSelectedMethod(IStructuredSelection selection) {
//		if (selection.isEmpty() || selection.size() != 1) 
//			return null;
//		
//		Object first= selection.getFirstElement();
//		if (! (first instanceof IMethod))
//			return null;
//		return (IMethod) first;
//	}
//	
//	private static IMember[] getSelectedMembers(IStructuredSelection selection){
//		if (selection.isEmpty())
//			return null;
//		
//		for  (Iterator iter= selection.iterator(); iter.hasNext(); ) {
//			if (! (iter.next() instanceof IMember))
//				return null;
//		}
//		return convertToMemberArray(selection.toArray());
//	}
//	
//	private static IMember[] convertToMemberArray(Object[] obj) {
//		if (obj == null)
//			return null;
//		Set memberSet= new HashSet();
//		memberSet.addAll(Arrays.asList(obj));
//		return (IMember[]) memberSet.toArray(new IMember[memberSet.size()]);
//	}
}
