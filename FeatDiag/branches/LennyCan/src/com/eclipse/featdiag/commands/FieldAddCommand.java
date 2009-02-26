package com.eclipse.featdiag.commands;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.eclipse.featdiag.models.ConnectionModel;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.FieldModel;
import com.eclipse.featdiag.models.PaletteModel;
import com.eclipse.featdiag.parser.Edge;


/**
 * A command to add a new field to a diagram.
 * @author nic
 *
 */
public class FieldAddCommand extends MemberAddCommand {
	private FieldModel field;

	/**
	 * Creates a new command to add the given field to the given
	 * diagram at the given location.
	 * @param diagram
	 * @param location
	 * @param field
	 */
	public FieldAddCommand(DiagramModel diagram, Point location, FieldModel field) {
		super(diagram, location);
		this.field = field;
	}

	
	public void execute() {
		List<Edge> edges = diagram.getPaletteModel().getEdges(field);
		if (diagram.removePaletteField(field)) {
			field.modifyBounds(new Rectangle(location, new Dimension(-1, -1)));
			diagram.addFieldModel(field);
			for (Edge edge : edges) {
				if (edge.targetIsMethod()) {
					diagram.addMethodToMethodConnection(edge.getSource(), edge.getTarget());
				}
				else {
					diagram.addMethodToFieldConnection(edge.getSource(), edge.getTarget());
				}
			}
		}
	}
	
	
	public void undo() {
		List<ConnectionModel> connections = new Vector<ConnectionModel>(field.getUses());
		connections.addAll(field.getUsedBy());
		if (diagram.removeFieldModel(field)) {
			diagram.addPaletteField(field);
			PaletteModel palette = diagram.getPaletteModel();
			for (ConnectionModel connection : connections) {
				palette.addEdge(new Edge(connection.getSource().getName(), connection.getTarget().getName(), connection.targetIsMethod()));
			}
		}
	}
	
	
	public boolean canUndo() {
		List<FieldModel> fields = diagram.getFieldModels();
		return super.canExecute() && fields.contains(field);
	}
	
	
	public boolean canExecute() {
		Collection<FieldModel> fields = diagram.getPaletteModel().getFields();
		return super.canExecute() && fields.contains(field);
	}
}
