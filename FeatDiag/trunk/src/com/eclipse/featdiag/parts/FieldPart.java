package com.eclipse.featdiag.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

import com.eclipse.featdiag.figures.FieldFigure;
import com.eclipse.featdiag.models.FieldModel;


/**
 * Controller class for field objects.
 * Most handles done by parent class MemberPart.
 * @author nic
 *
 */
public class FieldPart extends MemberPart {

	/**
	 * Create a figure that represents a field.
	 */
	
	protected IFigure createFigure() {
		FieldModel model = (FieldModel) getModel();
		FieldFigure result = new FieldFigure(model);
		Dimension size = result.getPreferredSize();
		model.setSize(size.width, size.height);
		return result;
	}
}
