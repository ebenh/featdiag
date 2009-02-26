package com.eclipse.featdiag.figures;


import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;

import com.eclipse.featdiag.models.MemberModel;


/**
 * An rectangle figure representing a method in a class.
 * @author nic
 *
 */
public class MethodFigure extends RectangleFigure {
	private String name;

	public MethodFigure(MemberModel model) {
		this.name = model.toString();
		FigureInitializer.initialize(this, model);
		Dimension size = getPreferredSize();
		setPreferredSize(size.width + 5, size.height + 5);
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
