package com.eclipse.featdiag.parser.overlap.removal;

import static com.eclipse.featdiag.parser.overlap.removal.NodeOverlapRemoval.X;
import static com.eclipse.featdiag.parser.overlap.removal.NodeOverlapRemoval.Y;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import com.eclipse.featdiag.parser.meyers.ISOMLayout.Vertex;


/**
 * Constraints to solve, stored as a directed graph.
 * Nodes from the original graph represents nodes in this graph.
 * Constraints between 2 nodes represent edges in this graph.
 * @author nic
 *
 */
public class ConstraintGraph {
	// The vertices in this graph
	private List<Vertex> vertices;
	// Maps a vertex to all it's outward edges (constraints)
	private Map<Vertex, List<Constraint>> outConstraints;
	// Maps a vertex to all it's incoming edges (constraints)
	private Map<Vertex, List<Constraint>> inConstraints;
	// Maps a vertex to the block it is assigned to
	private Map<Vertex, Block> blocks;
	// Maps a vertex to the distance from the reference point of it's block
	private Map<Vertex, Double> offset;
	// Either 0 or 1, (X or Y respectively)
	private int dimension;

	
	/**
	 * Creates a new Constraint Graph with the given nodes, to be solved
	 * in the given dimension.
	 * @param vertices
	 * @param dimension
	 */
	public ConstraintGraph(Collection<Vertex> vertices, int dimension) {
		this.outConstraints = new HashMap<Vertex, List<Constraint>>();
		this.inConstraints = new HashMap<Vertex, List<Constraint>>();
		this.vertices = new Vector<Vertex>(vertices);
		this.blocks = new HashMap<Vertex, Block>();
		this.offset = new HashMap<Vertex, Double>();
		this.dimension = dimension;
	}
	
	/**
	 * Adds a new edge to this graph from left to right.
	 * @param left
	 * @param right
	 */
	public void addConstraint(Vertex left, Vertex right) {
		// Creates a new constraint from left to right.
		// Adds it to the maps.
		Constraint c = new Constraint(left, right, dimension);
		
		List<Constraint> leftConstraints = outConstraints.get(left);
		if (leftConstraints == null) {
			leftConstraints = new Vector<Constraint>();
		}
		leftConstraints.add(c);
		outConstraints.put(left, leftConstraints);
		
		List<Constraint> rightConstraints = inConstraints.get(right);
		if (rightConstraints == null) {
			rightConstraints = new Vector<Constraint>();
		}
		rightConstraints.add(c);
		inConstraints.put(right, rightConstraints);
	}
	
	/**
	 * Goes through each vertex and solves it's constraints
	 * by assigning vertices to blocks.
	 * @return
	 */
	public Map<Vertex, Double> satisfy() {
		totalOrder();
		for (Vertex v : vertices) {
			Block b = block(v);
			b = mergeLeft(b);
			blocks.put(v, b);
		}
		return shift();
	}
	
	/**
	 * Shifts all the nodes to the top left hand corner of the
	 * screen after all overlap has been removed.
	 * @return
	 */
	private Map<Vertex, Double> shift() {		
		double shiftDist = Integer.MIN_VALUE;
		
		// Determine the node closest to 0
		Map<Vertex, Double> retval = new HashMap<Vertex, Double>();
		for (Vertex v : vertices) {
			Block b = blocks.get(v);
			Double o = offset.get(v);
			if (b == null || o == null) {
				continue;
			}
			double vPos = o + b.pos;
			retval.put(v, vPos);
			if (-vPos > shiftDist) {
				shiftDist = -vPos + 10;
			}			
		}
		
		// Shift all nodes by the amount as to make the closest node
		// to 0 sit at 10 exactly.
		for (Vertex v : vertices) {
			Double d = retval.get(v);
			retval.put(v, d + shiftDist);
		}
		
		return retval;
	}
	
	/**
	 * Returns a list of the vertices in this graph ordered as [v1...vn]
	 * such that for all j>i there is no directed path in this constraint
	 * graph from vj to vi.
	 * (Root nodes with no in edges come first in the ordering and leaf
	 * nodes with no out edges come last in the ordering.)
	 * 
	 * @return
	 */
	private void totalOrder() {
		List<Vertex> roots = new Vector<Vertex>();
		for (Vertex v : vertices) {
			if (inConstraints.get(v) == null) {
				roots.add(v);
			}
		}
		
		if (roots.isEmpty()) {
			// Problem.. there should be at least one node with no
			// edges. Graph is not acyclic.
			return;
		}
		
		// Each vertex is mapped to a number to later sort vertices.
		// Roots are stored with 1's, and for each node with numbering n,
		// it's children are stored with numbers >= n+1.
		Map<Vertex, Integer> retval = new HashMap<Vertex, Integer>();
		for (Vertex v : vertices) {
			retval.put(v, new Integer(0));
		}
		
		List<Vertex> toVisit = new Vector<Vertex>();
		for (Vertex v : roots) {
			retval.put(v, new Integer(1));
			toVisit.add(v);
		}
		
		retval = orderVertices(retval, toVisit);
		Collections.sort(vertices, new VertexComparator(retval));
	}
	
	/**
	 * Create a new block and add v to it.
	 * @param v
	 * @return
	 */
	private Block block(Vertex v) {
		Block b = new Block();
		b.vars.add(v);
		b.pos = (dimension == X) ? v.x : v.y;
		b.in.addAll(inConstraints.get(v));
		blocks.put(v, b);
		offset.put(v, 0.0);
		return b;
	}
	
	/**
	 * Merges block b with the blocks of nodes to the left of b
	 * with constraints with nodes in block b. Creates a new block
	 * in the middle of the 2, and adds all nodes from both blocks
	 * to it. Updates the offsets appropriately.
	 * @param b
	 * @return
	 */
	private Block mergeLeft(Block b) {
		while (!b.in.isEmpty()) {
			Constraint c = b.in.remove();
			if (c.getViolation() > 0) {	
				Block cBlock = blocks.get(c.source);
				Double cLeftOffset = offset.get(c.source);
				Double cRightOffset = offset.get(c.target);
				
				if (cBlock == null || cLeftOffset == null || cRightOffset == null) {
					return null;
				}
				
				double dist = Math.abs(cLeftOffset + c.gap - cRightOffset);
				if (b.vars.size() > cBlock.vars.size()) {
					mergeBlock(b, c, cBlock, dist);
				}
				else {
					mergeBlock(cBlock, c, b, -dist);
					b = cBlock;
				}
			}
		}
		return b;
	}
	
	/**
	 * Merges blocks p and b. Constraint c is between a node in
	 * block p and a node in block b. Dist is the added distances
	 * between the nodes and their respective block's reference points.
	 * @param p
	 * @param c
	 * @param b
	 * @param dist
	 */
	private void mergeBlock(Block p, Constraint c, Block b, double dist) {
		p.pos += dist/2;
//		p.pos += b.pos - dist;
		for (Vertex v : b.vars) {
			blocks.put(v, p);
			Double vOffset = offset.get(v);
			if (vOffset == null) {
				return;
			}
			offset.put(v, vOffset - dist);
		}
		p.in.addAll(b.in);
		p.vars.addAll(b.vars);
	}
	
	/**
	 * Assigns each vertex a number as described in totalOrder for sorting purposes.
	 * @param vertices
	 * @param toVisit
	 * @return
	 */
	private Map<Vertex, Integer> orderVertices(Map<Vertex, Integer> vertices, List<Vertex> toVisit) {
		while (!toVisit.isEmpty()) {
			Vertex v = toVisit.remove(0);
			int n = vertices.get(v);
			List<Constraint> edges = outConstraints.get(v);
			if (edges != null) {
				for (Constraint c : edges) {
					Vertex child = c.target;
					if (!toVisit.contains(child)) {
						toVisit.add(child);
					}
					Integer existing = vertices.get(child);
					if (existing == null) {
						existing = -1;
					}
					vertices.put(child, Math.max(existing, new Integer(n + 1)));
				}
			}
		}
		return vertices;
	}

	/**
	 * A comparator to sort vertices based on the number assigned to
	 * them in by totalOrder.
	 * @author nic
	 *
	 */
	private class VertexComparator implements Comparator<Vertex> {
		private Map<Vertex, Integer> vertices;
		
		/**
		 * Takes in a map of each vertex to it's number as given in
		 * totalOrder. Used to determine the ordering of vertices.
		 * @param vertices
		 */
		public VertexComparator(Map<Vertex, Integer> vertices) {
			this.vertices = vertices; 
		}

		
		public int compare(Vertex v1, Vertex v2) {
			Integer i1 = vertices.get(v1);
			Integer i2 = vertices.get(v2);
			if (i1 == null && i2 == null) {
				return 0;
			}
			else if (i1 == null) {
				return -1;
			}
			else if (i2 == null) {
				return 1;
			}
			return (i1 > i2) ? 1 : (i1 == i2) ? 0 : -1;
		}
		
	}
	
	/**
	 * Constraints between 2 overlapping nodes. Represents edges in
	 * this graph.
	 * @author nic
	 *
	 */
	public class Constraint {
		// Source node
		private Vertex source;
		// Target node
		private Vertex target;
		// position of source node
		private double left;
		// position of target node
		private double right;
		// the minimum required gap between the 2 nodes
		// in order to remove overlap
		private int gap;
		
		/**
		 * Creates a new constraint between left and right to be
		 * solved in the given dimension.
		 * @param left
		 * @param right
		 * @param dimension
		 */
		public Constraint(Vertex left, Vertex right, int dimension) {
			this.source = left;
			this.target = right;
			if (dimension == X) {
				this.left = left.x;
				this.right = right.x;
				this.gap = ((left.width + right.width)/2) + 10;
			}
			else if (dimension == Y) {
				this.left = left.y;
				this.right = right.y;
				this.gap = ((left.height + right.height)/2) + 10;
			}
		}
		
		/**
		 * The amount of overlap between the 2 nodes.
		 * @return
		 */
		public int getViolation() {
			double left = this.left;
			double right = this.right;
			
			Block b = blocks.get(source);
			Double o = offset.get(source);
			if (b != null && o != null) {
				left = b.pos + o;
			}
			
			b = blocks.get(target);
			o = offset.get(target);
			if (b != null && o != null) {
				right = b.pos + o;
			}
			
			return new Double(Math.ceil(left + gap - right)).intValue();
		}

		
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Constraint)) {
				return false;
			}
			
			Constraint c = (Constraint) o;
			return (source.equals(c.source) &&
					target.equals(c.target) &&
					left == c.left &&
					right == c.right &&
					gap == c.gap);
		}
		
		
		public String toString() {
			return source.name + " + " + gap + " <= " + target.name;
		}
	}
	
	/**
	 * A block containing nodes that have been solved for all overlap.
	 * @author nic
	 *
	 */
	private class Block {
		// The nodes in this block.
		List<Vertex> vars;
		// A priority queue of the constraints between
		// nodes in this block and nodes to the left of
		// this block.
		Queue<Constraint> in;
		// The reference position of this block
		double pos;
		
		/**
		 * Create a new block
		 */
		public Block() {
			vars = new Vector<Vertex>();
			in = new InQueue(1, new ConstraintComparator(), this);
			pos = -1;
		}
		
		
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Block)) {
				return false;
			}
			
			Block b = (Block) o;
			return (vars.equals(b.vars) &&
					in.equals(b.in) &&
					pos == b.pos);
		}
		
		/**
		 * A comparator to sort constraints by violation, where
		 * a greater violation means a larger contraint.
		 * @author nic
		 *
		 */
		private class ConstraintComparator implements Comparator<Constraint> {

			
			public int compare(Constraint c1, Constraint c2) {
				int c1Violation = c1.getViolation();
				int c2Violation = c2.getViolation();
				return (c1Violation > c2Violation) ? 1 : (c1Violation == c2Violation) ? 0 : -1;
			}
		}
		
		/**
		 * A priority queue to store constraints.
		 * @author nic
		 *
		 */
		private class InQueue extends PriorityQueue<Constraint> {
			private static final long serialVersionUID = 1L;
			private Block b;

			/**
			 * Creates this Priority Queue with the given initial capacity,
			 * the given comparator for the constraints in this queue, and
			 * the block of the nodes that are the targets of the constraints 
			 * in this queue
			 * are 
			 * @param i
			 * @param constraintComparator
			 * @param b
			 */
			public InQueue(int i, ConstraintComparator constraintComparator, Block b) {
				super(i, constraintComparator);
				this.b = b;
			}

			/**
			 * Adds all the given constraints. Silently deletes any constraints
			 * between 2 nodes in the same block.
			 */
			public boolean addAll(Collection<? extends Constraint> toAdd) {
				if (toAdd == null) {
					return true;
				}
				
				List<Constraint> toRemove = new Vector<Constraint>();
				for (Constraint c : toAdd) {
					if (blocks.get(c.source).equals(b)) {
						toRemove.add(c);
					}
				}
				toAdd.removeAll(toRemove);				
				return super.addAll(toAdd);
			}
		}
	}

}
