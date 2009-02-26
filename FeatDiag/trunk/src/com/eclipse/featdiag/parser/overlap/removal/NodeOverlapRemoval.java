package com.eclipse.featdiag.parser.overlap.removal;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.eclipse.featdiag.parser.meyers.ISOMLayout.Vertex;


/**
 * Creates a constraint representation of the given graph
 * to remove any overlap between nodes of the graph. 
 * 
 * Used the method described by Tim Dwyer, Kim Marriott, 
 * and Peter J. Stuckey. The paper can be found at
 * http://www.cs.mu.oz.au/~pjs/papers/gd2005b.pdf
 * @author nic
 *
 */
public class NodeOverlapRemoval {
	// Dimensions to check for and remove overlap
	static final int X = 0;
	static final int Y = 1;

	// Vertices of the graph. Sorted by location of 1 dimension.
	private SortedSet<Vertex> vertices;
	// Maps a vertex to all of it's neighbours with which it should
	// have constraints with. (Overlapping neighbours).
	// One map for each direction, either left/up or right/down.
	private Map<Vertex, Set<Vertex>> left;
	private Map<Vertex, Set<Vertex>> right;
	// Either 0 or 1, (X or Y respectively)
	private int dimension = -1;
	
	/**
	 * Takes in a list of vertices with cooridnates to the node's
	 * bottom left corner, and the width and height of the node.
	 * Creates constraints between all pairs of nodes with overlap.
	 * Satisfies those constraints and updates the location of the
	 * nodes.
	 * @param vertices
	 */
	public void execute(final List<Vertex> vertices) {
		// Creates constraints for horizontal overlaps.
//		init(vertices, X);
//		ConstraintGraph constraints = generateContraints();
//		constraints.rearrange();
//		Map<Vertex, Double> xPos = constraints.satisfy();
		
		// Creates constraints for vertical overlaps.
		init(vertices, Y);
		ConstraintGraph constraints = generateContraints();
		Map<Vertex, Double> yPos = constraints.satisfy();
		
		// Set the new positions of the vertices.
		for (Vertex v : vertices) {
//			v.x = xPos.get(v);
			v.y = yPos.get(v);
		}
	}
	
	/**
	 * Initializes the global variables.
	 * Takes in a list of vertices and the dimension on which to
	 * solve overlaps.
	 * @param vertices
	 * @param dimension
	 */
	private void init(Collection<Vertex> vertices, int dimension) {
		this.vertices = new TreeSet<Vertex>(new VertexComparator(dimension));
		this.vertices.addAll(vertices);
		this.left = new HashMap<Vertex, Set<Vertex>>();
		this.right = new HashMap<Vertex, Set<Vertex>>();
		this.dimension = dimension;
	}
	
	/**
	 * Generates a list of constraints between all pairs of overlapping
	 * nodes.
	 * @return
	 */
	private ConstraintGraph generateContraints() {
		// Constraints are stored as a graph
		ConstraintGraph constraints = new ConstraintGraph(vertices, dimension);
		// Stores the left/above and right/below neighbours for all of the nodes
		getAllNeighbors();
		// Removes neighbours that are implied though other neighbour relationships.
		removeRedundantNeighbors();
		
		// Add constraints for each of the immediate neighbours for each node.
		for (Vertex v : vertices) {
			for (Vertex u : left.get(v)) {
				constraints.addConstraint(u, v);
				right.get(u).remove(v);
			}
			for (Vertex u : right.get(v)) {
				constraints.addConstraint(v, u);
				left.get(u).remove(v);
			}
		}
		
		return constraints; 
	}
	
	/**
	 * Stores neighbours for each node in both the left/upwards direction
	 * and the right/downwards direction.
	 */
	private void getAllNeighbors() {
		for (Vertex v : vertices) {		
			// Get all neighbours of v to the left/top. Store them
			// in the map.
			Set<Vertex> leftV = getNeighbors(v, true);
			left.put(v, leftV);
			
			// Add v as a right/downward neighbour to each of it's 
			// left/upwards neighbours.
			for (Vertex u : leftV) {
				Set<Vertex> rightU = right.get(u);
				if (rightU == null) {
					rightU = new HashSet<Vertex>();
				}
				rightU.add(v);
			}
			
			// Get all neighbours of v to the right/bottom. Store them
			// in the map.
			Set<Vertex> rightV = getNeighbors( v, false);
			right.put(v, rightV);
			// Add u as a left/upwards neighbour to each of it's 
			// right/downwards neighbours.
			for (Vertex u : rightV) {
				Set<Vertex> leftU = left.get(u);
				if (leftU == null) {
					leftU = new HashSet<Vertex>();
				}
				leftU.add(v);
			}
		}
	}
	
	/**
	 * Removes neighbours that can be implied by other relationships.
	 * ie: if A has right neighbours B and C, and B has right neighbour C, 
	 * C can be removed from the right neighbours for A, as that
	 * relationship can be implied from A-B and B-C.
	 */
	private void removeRedundantNeighbors() {
		for (Vertex v : vertices) {
			Set<Vertex> leftV = left.get(v);
			Set<Vertex> rightV = right.get(v);
			
			for (Vertex u : leftV) {
				Set<Vertex> rightU = right.get(u);
				rightU.removeAll(rightV);
			}
			
			for (Vertex u : rightV) {
				Set<Vertex> leftU = left.get(u);
				leftU.removeAll(leftV);
			}
		}
	}
	
	/**
	 * Returns neighbours in one direction for the given node.
	 * @param v
	 * @param left
	 * @return
	 */
	private Set<Vertex> getNeighbors(Vertex v, boolean left) {
		Set<Vertex> existingNeighbors = (left) ? this.left.get(v) : this.right.get(v);
		if (existingNeighbors == null) {
			existingNeighbors = new HashSet<Vertex>();
		}
		
		// Vertices are stored in sorted order based on location.
		SortedSet<Vertex> vNeighbors = new TreeSet<Vertex>(left ? vertices.headSet(v) : vertices.tailSet(v));
		// If getting nodes greater than current node (v),
		// vNeighbours includes v in the returned list.
		// Remove it from the front of the list.
		if (!left && !vNeighbors.isEmpty()) {
			vNeighbors.remove(vNeighbors.first());
		}
		
		while (!vNeighbors.isEmpty()) {
			// Get the next closest node to v. Calculate the
			// vertical and horizontal overlap between the 2 nodes.
			Vertex u = left ? vNeighbors.last() : vNeighbors.first();
			vNeighbors.remove(u);
			
			double xOverlap = calcXOverlap(u, v);
			double yOverlap = calcYOverlap(u, v);
			
			// If the overlap in the dimension that is being fixed is
			// less than -10, (ie: there is no overlap and the nodes are
			// at least 10px apart),
			// can stop looking. All further neighbours are even
			// further away.
			if ((dimension == X && xOverlap < -10) ||
				(dimension == Y && yOverlap < -10)) {
				existingNeighbors.add(u);
				break;
			}
			// If searching in the X dimension and the horizontal overlap
			// is greater than the vertical overlap, don't solve this overlap
			// now. Solve it later in the Y dimension.
			// Either direction, be sure that the overlap is greater than 10
			// in both directions. (ie: the nodes actually overlap, or are
			// within 10 px of each other, and are not just on the same parallel.
			if ((dimension == X && xOverlap < yOverlap && yOverlap >= -10) ||
				(dimension == Y && xOverlap >= -10)) {
				existingNeighbors.add(u);
			}
		}
		
		if (left) {
			this.left.put(v, existingNeighbors);
		}
		else {
			this.right.put(v, existingNeighbors);
		}
		
		return existingNeighbors;
	}
	
	/**
	 * Calculates the horizontal overlap between the 2
	 * given nodes.
	 * @param u
	 * @param v
	 * @return
	 */
	private double calcXOverlap(Vertex u, Vertex v) {
		return ((u.width + v.width)/2) - Math.abs(u.x - v.x);
	}
	
	/**
	 * Calculates the vertical overlap between the 2
	 * given nodes.
	 * @param u
	 * @param v
	 * @return
	 */
	private double calcYOverlap(Vertex u, Vertex v) {
		return ((u.height + v.height)/2) - Math.abs(u.y - v.y);
	}
	
	/**
	 * A comparator to sort vertices by location, either from
	 * left to right or fromk top to bottom.
	 * @author nic
	 *
	 */
	private class VertexComparator implements Comparator<Vertex> {
		private int sortDirection;
		
		public VertexComparator(int sortDirection) {
			this.sortDirection = sortDirection;
		}

		
		public int compare(Vertex v1, Vertex v2) {
			int retval;
			switch (sortDirection) {
			case X:
				retval = Double.compare(v1.x, v2.x);
				break;
			case Y:
				retval = Double.compare(v1.y, v2.y);
				break;
			default:
				retval = 0;
				break;
			}
			if (retval == 0) {
				retval = v1.name.compareTo(v2.name);
			}
			return retval;
		}
	}
}
