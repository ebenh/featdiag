package com.eclipse.featdiag.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.eclipse.featdiag.editors.DiagramEditor;
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

	/**
	 * Called when option to open new feature diagram is selected
	 * from the context menu.
	 */
	public void run(IAction action) {
		try {
			IJavaElement element = (IJavaElement) ((StructuredSelection) selection).getFirstElement();
			
			IJavaProject javaProj = element.getJavaProject();
			IProject proj = javaProj.getProject();
			IPath path = element.getPath();
			IFile featFile = createFile(path, javaProj.getProject());

			walkProject2(javaProj); // note eben
			
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
				DiagramEditor editor = (DiagramEditor) window.getActivePage()
						.openEditor(input,
								"featdiag.editors.FeatureDiagramEditor");
				
				if (classFile != null && !getDiagExists()) {
					String classFilePath = classFile.getLocation().toString();
					System.out.println(classFilePath);
					System.out.println(javaFile.getFullPath().toString());
					editor.addMembers(classFilePath, javaFile.getFullPath().toString());
				}
			}
		} catch (PartInitException e) {
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
	
//	protected void walkProject(IJavaProject javaProject){
//		try {
//			for(IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()){
//				if(root.getKind() == IPackageFragmentRoot.K_SOURCE)
//				{	
//					for(IJavaElement elem : root.getChildren())
//					{
//						if(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT)
//						{
//							IPackageFragment fragment = (IPackageFragment)elem;
//							//System.out.println("Package fragment : " + fragment.getElementName());
//							
//							for(ICompilationUnit cu : fragment.getCompilationUnits())
//							{
//								//System.out.println("cu " + cu.getElementName() + " p: " + cu.getPath());
//								IFile file = (IFile)cu.getCorrespondingResource();
//								IFile classFile = FileUtils.getClassFile(file);
//								assert(classFile != null);		
//							} // end for
//						} // end if
//					} // end for
//				} // end if
//			}
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // end for	
//	}
	
	protected void walkProject2(IJavaProject javaProject){
		try {
			for(IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()){
				if(root.getKind() == IPackageFragmentRoot.K_SOURCE)
				{	
					for(IJavaElement elem : root.getChildren())
					{						
						if(elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT)
						{
							IPackageFragment fragment = (IPackageFragment)elem;
														
							for(IJavaElement elem2 : fragment.getChildren())
							{
								if(elem2.getElementType() == IJavaElement.COMPILATION_UNIT)
								{
									ICompilationUnit compilationUnit = (ICompilationUnit)elem2;
									
									for(IJavaElement elem3 : compilationUnit.getChildren()){
										
										if(elem3.getElementType() == IJavaElement.TYPE)
										{
											IType type = (IType)elem3;
											
											for(IJavaElement elem4 : type.getChildren()){
												
												if(elem4.getElementType() == IJavaElement.METHOD){
													IMethod method = (IMethod)elem4;
													System.out.println("--> " + method.toString());

												}
												else if(elem4.getElementType() == IJavaElement.FIELD){
													IField field = (IField)elem4;
													System.out.println("--> " + field.toString());	
												}		
												
												findReferences(elem4);
											}
										}										
									}
								}
							}
						} // end if
					} // end for
				} // end if
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // end for	
	}
	
	void findReferences(IJavaElement javaElement){
		assert javaElement instanceof IField;
		assert javaElement instanceof IMethod;
		
		SearchPattern searchPattern = SearchPattern.createPattern(javaElement, IJavaSearchConstants.REFERENCES);
		IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
		SearchEngine searchEngine = new SearchEngine();
		SearchRequestor searchRequestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				System.out.println("----> "	+ (IMethod)match.getElement());
			}
		};
		
		try {
			searchEngine.search(searchPattern, new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, searchScope, searchRequestor, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println();
	}
}
