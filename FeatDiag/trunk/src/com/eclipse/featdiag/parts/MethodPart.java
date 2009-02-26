package com.eclipse.featdiag.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

import com.eclipse.featdiag.figures.MethodFigure;
import com.eclipse.featdiag.models.MethodModel;


/**
 * Controller class for method objects.
 * Most handles done by parent class MemberPart.
 * @author nic
 *
 */
public class MethodPart extends MemberPart {

	/**
	 * Create a figure that represents a method.
	 */
	
	protected IFigure createFigure() {
		MethodModel model = (MethodModel) getModel();
		MethodFigure result = new MethodFigure(model);
		Dimension size = result.getPreferredSize();
		model.setSize(size.width, size.height);
		return result;
	}

}
