/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see 
* http://jung.sourceforge.net/license.txt for a description.
*/
package com.eclipse.featdiag.parser.meyers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.draw2d.geometry.Rectangle;

import com.eclipse.featdiag.models.ConnectionModel;
import com.eclipse.featdiag.models.DiagramModel;
import com.eclipse.featdiag.models.MemberModel;
import com.eclipse.featdiag.parser.overlap.removal.NodeOverlapRemoval;


/**
 * Implements a self-organizing map layout algorithm, based on Meyer's
 * self-organizing graph methods.
 * 
 * @author Yan Biao Boey
 * @author nic
 */
public class ISOMLayout {
	
	private int maxIterations;
	private int iteration;

	private int radiusConstantTime;
	private int radius;
	private int minRadius;

	private double adaption;
	private double initialAdaption;
	private double minAdaption;
    
	private double coolingFactor;
	
	private List<Vertex> vertices;
	private int width, height;
	private Map<MemberModel, Vertex> models;

	public ISOMLayout(DiagramModel diagram) {
		calcSize(diagram.getMemberModels());
		initVertices(diagram.getMemberModels());
	}
	
	/**
	 * Arrange the nodes given to this layout.
	 */
	public void autoArrange() {
		init();
		while (iteration < maxIterations) {
			adjust();
			updateParameters();
		}
		shiftDiagram();
		new NodeOverlapRemoval().execute(vertices);
		setModelPositions();
	}
	
	/**
	 * Calculates the size of the diagram.
	 * Size is square and created based on the
	 * number of and the size of nodes.
	 * @param nodes
	 */
	private void calcSize(List<MemberModel> nodes) {
		int numNodes = nodes.size();
		double averageWidth = 0;
		double sqrt = Math.floor(Math.sqrt(numNodes));
		
		for (MemberModel node : nodes) {
			averageWidth += node.getWidth();// * 2.5;
		}
		averageWidth /= numNodes;
		
		width = new Double(averageWidth * 1.75 * sqrt).intValue();
		height = width;
	}
	
	/**
	 * Create vertex objects from list of class members.
	 * Initialize vertex positions to random positions.
	 * @param models
	 */
	private void initVertices(List<MemberModel> models) {
		this.vertices = new ArrayList<Vertex>();
		this.models = new HashMap<MemberModel, Vertex>();
		
		for (MemberModel model : models) {
			Rectangle rect = model.getBounds();
			
			double x,y;
			if (rect.x <= 0 || rect.y <= 0) {
				x = (Math.random() * width);
				y = (Math.random() * height);
			}
			else {
				x = rect.x;
				y = rect.y;
			}
			
			int width = model.getWidth();
			int height = model.getHeight();
			Vertex v = new Vertex(model.toString(), x + width/2, y + height/2, width, height);
			
			v.model = model;
			this.vertices.add(v);
			this.models.put(model, v);
		}
	}


	/**
	 * Initialize values for calculations
	 */
	private void init() {
		maxIterations = 2000;
		iteration = 0;

		radiusConstantTime = 100;
		radius = 5;
		minRadius = 1;

		initialAdaption = 90.0D / 100.0D;
		adaption = initialAdaption;
		minAdaption = 0;

		coolingFactor = 2;
	}
	
	/**
	 * After the diagram is positioned,
	 * shifts all the nodes towards the
	 * top left corner.
	 */
	private void shiftDiagram() {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		for (Vertex v : vertices) {
			minX = Math.min(minX, v.x);
			minY = Math.min(minY, v.y);
		}
		for (Vertex v : vertices) {
			v.x -= (minX - 10);
			v.y -= (minY - 10);
		}
	}
	
	/**
	 * Set the positions of the models to the newly
	 * calculated positions.
	 */
	private void setModelPositions() {
		for (Entry<MemberModel, Vertex> entry : models.entrySet()) {
			MemberModel model = entry.getKey();
			Vertex v = entry.getValue();
			int x = Math.round((float)v.x);
			int y = Math.round((float)v.y);
			Rectangle rect = new Rectangle(x, y, -1, -1);
			model.modifyBounds(rect);
		}
	}

	/**
	 * Adjust the layout by 1 iteration.
	 */
	private synchronized void adjust() {		
		double [] tempCoordinates = {10 + Math.random() * width, 10 + Math.random() * height};

		//Get closest vertex to random position
		Vertex closest = GraphElementAccessor.getVertex(vertices, tempCoordinates[0], tempCoordinates[1]);

		for (Vertex v : vertices) {
            v.distance = 0;
            v.visited = false;
		}
		adjustVertex(closest, tempCoordinates);
	}

	/**
	 * Update values after an iteration.
	 */
	private synchronized void updateParameters() {
		iteration++;
		double factor = Math.exp(-1 * coolingFactor * (1.0 * iteration / maxIterations));
		adaption = Math.max(minAdaption, factor * initialAdaption);
		if ((radius > minRadius) && (iteration % radiusConstantTime == 0)) {
			radius--;
		}
	}

	/**
	 * Adjust the given vertex and it's neighbors.
	 * @param v
	 * @param coordinates
	 */
	private synchronized void adjustVertex(Vertex v, double[] coordinates) {
		LinkedList<Vertex> queue = new LinkedList<Vertex>();
		v.distance = 0;
		v.visited = true;
		queue.add(v);
		Vertex current;

		while (!queue.isEmpty()) {
			current = queue.removeFirst();

			double dx = coordinates[0] - current.x;
			double dy = coordinates[1] - current.y;
			double factor = adaption / Math.pow(2, current.distance);
			
			current.x += factor * dx;
			current.y += factor * dy;

			if (current.distance < radius) {
			    Set<Vertex> neighbors = current.getNeighbors();
			    for (Vertex neighbor : neighbors) {
	                if (!neighbor.visited) {
	                	neighbor.visited = true;
	                	neighbor.distance = current.distance + 1;
	                    queue.add(neighbor);
	                }
	            }
			}
		}
	}
	
	/**
	 * A vertex in the graph.
	 * Has a name and position.
	 * @author nic
	 *
	 */
	public class Vertex {
		public String name;
		public double x,y;
		public int height;
		public int width;
		
		private int distance;
		private boolean visited;
		
		private MemberModel model;		
		private Set<Vertex> neighbors;
		
		public Vertex(String name, double x, double y, int width, int height) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.distance = 0;
			this.visited = false;
		}
		
		/**
		 * Get the nodes connected to this node by
		 * 1 edge.
		 * @return
		 */
		public Set<Vertex> getNeighbors() {
			if (neighbors == null) {
				neighbors = new HashSet<Vertex>();
				for (ConnectionModel edge : model.getUses()) {
					MemberModel target = edge.getTarget();
					neighbors.add(models.get(target));
				}
				for (ConnectionModel edge : model.getUsedBy()) {
					MemberModel target = edge.getSource();
					neighbors.add(models.get(target));
				}
			}
			return neighbors;
		}
		
		
		public String toString() {
			return name + " (" + x + "," + y + ")";
		}
		
		
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Vertex)) {
				return false;
			}
			Vertex v = (Vertex) o;
			return (name.equals(v.name) && x == v.x && y == v.y);
		}
	}
}