package com.eclipse.featdiag.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.parser.Parser;


public class FileUtils {
	
	/**
	 * Updates the given diagram based on changes made in given
	 * .class file.
	 * File should be full path to .class file
	 * ie: L/ProjectName/OutputDir/Package/.../ClassName.class
	 * @param classFile
	 * @param diagram
	 * @throws CoreException 
	 */
	public static void updateDiagram(IFile classFile, DiagramModel diagram) throws CoreException {
		IPath path = classFile.getLocation();
		String classFilePath = path.toString();
		
		Parser parser = new Parser(classFilePath);
		parser.updateMembers(diagram);
	}
	
	/**
	 * Gets the .class file associated with the java file selected.
	 * @param path
	 * @param proj
	 * @return
	 */
	public static IFile getClassFile(IFile javaFile) {
		try {
			IPath javaPath = javaFile.getFullPath();
			IProject proj = javaFile.getProject();
			IJavaProject javaProj = JavaCore.create(proj);
			IPath sourceFolder = null;
			
			// Find the path to the source folder that contains
			// the given java file.
			IClasspathEntry[] entries = javaProj.getRawClasspath();
			for (IClasspathEntry entry : entries) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE &&
						entry.getPath().isPrefixOf(javaPath)) {
					sourceFolder = entry.getPath();
					break;
				}
			}
			
			if (sourceFolder != null) {
				// Remove the path to the source folder from the java file path.
				// Now path contains only packages and .java file.
				int matchingSegs = javaPath.matchingFirstSegments(sourceFolder);
				javaPath = javaPath.removeFirstSegments(matchingSegs);
			
				// Get the path to the projects build directory.
				// Append to it the packages and .java file.
				// Change the .java extension to .class.
				IPath path = javaProj.getOutputLocation();
				path = path.append(javaPath);
				path = path.removeFileExtension();
				path = path.addFileExtension("class");
				if (proj.getFullPath().isPrefixOf(path)) {
					int i = path.matchingFirstSegments(proj.getFullPath());
					path = path.removeFirstSegments(i);
				}
				return (IFile) proj.findMember(path);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}		
		return null;
	}
}
