package com.eclipse.featdiag.factories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.eclipse.featdiag.models.BaseModel;


/**
 * A factory for creating new EditParts
 * @author nic
 *
 */
public class BaseFactory implements EditPartFactory {

	/**
	 * Creates and returns a new EditPart corresponding to the
	 * given model object.
	 */
	public EditPart createEditPart(EditPart content, Object model) {
		EditPart part = null;
		if (model instanceof BaseModel) {
			part = ((BaseModel) model).createEditPart();
		}
		if (part != null) {
			part.setModel(model);
		}
		return part;
	}

}
