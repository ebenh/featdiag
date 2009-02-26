package com.eclipse.featdiag.utils;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;

import com.eclipse.featdiag.editors.DiagramEditor;
import com.eclipse.featdiag.models.MemberModel;
import com.eclipse.featdiag.parts.FieldPart;
import com.eclipse.featdiag.parts.MethodPart;

public class SelectionWrapper {

	private ISelection selection;
	private IEditorPart targetGraphicalViewer;
	
	public SelectionWrapper(ISelection selection, IEditorPart targetGraphicalViewer) {
		this.selection = selection;
		this.targetGraphicalViewer = targetGraphicalViewer;
	}
	
	public TreeSelection wrap(String flag) throws IOException {
		String elementName = "";
		String[] argtypenames = null;
		String className = "";
		Object part = ((IStructuredSelection) selection).getFirstElement();
		if(flag == "field") {
			MemberModel m = (MemberModel)((FieldPart) part).getModel();
			elementName = m.getName();
			className = m.getClassName();
		}
		else if(flag == "method") {
			MemberModel m = (MemberModel)((MethodPart)part).getModel();
			elementName = m.getName();
			argtypenames = m.getArgTypeNames();
			className = m.getClassName();
		}
		else
			className = ((DiagramEditor) targetGraphicalViewer).getFilePath().removeFileExtension().lastSegment();		
		IPath path = ((DiagramEditor) targetGraphicalViewer).getFilePath();
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
        ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
        IJavaModel model = cu.getJavaModel();
        IJavaProject project = cu.getJavaProject();
        IJavaElement pack =  cu.getParent();
        IJavaElement packroot = pack.getParent();
        IJavaElement element = null;
        IType type = cu.getType(className);
        if(flag == "field")
			element = type.getField(elementName);
		else if(flag == "method")
			element = type.getMethod(elementName, argtypenames);
		Object[] pathsegments = {model, project, packroot, pack, cu, type, element};
		Object[] pathsegments2 = {model, project, packroot, pack, cu, type};
		TreePath treepath;
		if(flag == "diagram")
			treepath = new TreePath(pathsegments2);
		else
			treepath = new TreePath(pathsegments);
        TreeSelection treeSelection = new TreeSelection(treepath);
        return treeSelection;
	}
}
