package com.eclipse.featdiag.commands;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.eclipse.featdiag.models.ConnectionModel;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.MethodModel;
import com.eclipse.featdiag.models.PaletteModel;
import com.eclipse.featdiag.parser.Edge;


/**
 * A command to add a new method to a diagram.
 * @author nic
 *
 */
public class MethodAddCommand extends MemberAddCommand {
	private MethodModel method;

	/**
	 * Creates a new command to add the given method to the
	 * given diagram at the given location.
	 * @param diagram
	 * @param location
	 * @param method
	 */
	public MethodAddCommand(DiagramModel diagram, Point location, MethodModel method) {
		super(diagram, location);
		this.method = method;
	}

	
	public void execute() {
		List<Edge> edges = diagram.getPaletteModel().getEdges(method);
		if (diagram.removePaletteMethod(method)) {
			method.modifyBounds(new Rectangle(location, new Dimension(-1, -1)));
			diagram.addMethodModel(method);
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
		List<ConnectionModel> connections = new Vector<ConnectionModel>(method.getUses());
		connections.addAll(method.getUsedBy());
		if (diagram.removeMethodModel(method)) {
			diagram.addPaletteMethod(method);
			PaletteModel palette = diagram.getPaletteModel();
			for (ConnectionModel connection : connections) {
				palette.addEdge(new Edge(connection.getSource().getName(), connection.getTarget().getName(), connection.targetIsMethod()));
			}
		}
	}
	
	
	public boolean canUndo() {
		List<MethodModel> methods = diagram.getMethodModels();
		return super.canUndo() && methods.contains(method); 
	}
	
	
	public boolean canExecute() {
		Collection<MethodModel> methods = diagram.getPaletteModel().getMethods();
		return super.canExecute() && methods.contains(method);
	}
}
