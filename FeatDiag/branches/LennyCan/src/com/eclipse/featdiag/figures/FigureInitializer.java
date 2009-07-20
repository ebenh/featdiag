package com.eclipse.featdiag.figures;


import java.io.File;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.eclipse.featdiag.editors.FeatDiagPlugin;
import com.eclipse.featdiag.models.MemberModel;
import com.eclipse.featdiag.models.modifiers.IconMap;
import com.sun.org.apache.bcel.internal.Constants;


/**
 * Static helper class to initialize a Figure.
 * @author nic
 *
 */
public class FigureInitializer {
	public static Color classColor = new Color(null, 255, 255, 206);
	public static Font defaultFont = new Font(null, "Arial", 12, SWT.NONE);

	/**
	 * Set the color, icon and label of the figure.
	 * @param figure
	 * @param model
	 */
	public static void initialize(Figure figure, MemberModel model) {
		BorderLayout layout = new BorderLayout();
		figure.setLayoutManager(layout);
		figure.setBackgroundColor(classColor);
		figure.setOpaque(true);
		figure.add(makeLabel(model), BorderLayout.CENTER);
		figure.validate();
	}
	
	/**
	 * Creates the label for the class member.
	 * Label consists of an icon representing the members
	 * modifiers and the toString representation of the
	 * members name.
	 * @param model
	 * @return
	 */
	private static Label makeLabel(MemberModel model) { 
		String s = "icons" + File.separator + IconMap.getMethodIconName(model.getModifiers());
		
		ImageDescriptor id = FeatDiagPlugin.getImageDescriptor(s);
		Image im = id.createImage();
		
		int modifiers = getConvertedModifier(model.getModifiers());

		//add final, abstract, static, synchronized images to base image
		if(modifiers != 0) {
			ImageDescriptor base_im = ImageDescriptor.createFromImage(im);
			Point pnt = new Point(im.getBounds().width,im.getBounds().height);
			im = (new JavaElementImageDescriptor(base_im, modifiers, pnt)).createImage();
		}
		
		Label label = new Label(model.toString(), im);
		label.setFont(defaultFont);
		return label;
	}
	
	/**
	 * Converts the BCEL modifiers flag to JavaElementImageDescriptor modifiers flag
	 * @param modifiers, BCEL flag
	 * @return JavaElementImageDescriptor flag
	 */
	private static int getConvertedModifier(int modifiers){
		int stat = 0;
		int fina = 0;
		int sync = 0;
		int abst = 0;
		if((modifiers & Constants.ACC_STATIC)!=0) {
			stat = JavaElementImageDescriptor.STATIC;
		}
		if((modifiers & Constants.ACC_FINAL)!=0) {
			fina = JavaElementImageDescriptor.FINAL;
		}
		if((modifiers & Constants.ACC_SYNCHRONIZED)!=0) {
			sync = JavaElementImageDescriptor.SYNCHRONIZED;
		}
		if((modifiers & Constants.ACC_ABSTRACT)!=0) {
			abst = JavaElementImageDescriptor.ABSTRACT;
		}
		return stat|fina|sync|abst;
	}
}
