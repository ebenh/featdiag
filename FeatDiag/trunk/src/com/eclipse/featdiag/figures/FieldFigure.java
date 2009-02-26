package com.eclipse.featdiag.figures;


import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.geometry.Dimension;

import com.eclipse.featdiag.models.MemberModel;


/**
 * An ellipse figure representing a field in a class.
 * @author nic
 *
 */
public class FieldFigure extends Ellipse {
	private String name;

	public FieldFigure(MemberModel model) {
		this.name = model.toString();
		FigureInitializer.initialize(this, model);
		Dimension size = getPreferredSize();
		setPreferredSize(size.width + 20, size.height + 10);
		setBounds(model.getBounds());
	}
	
	/**
	 * Returns the name of this class member image.
	 * The name is the string representation of
	 * the member.
	 * @return
	 */
	public String toString() {
		return name;
	}
}
