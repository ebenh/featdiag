/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 *
 * Created on Apr 12, 2005
 */
package com.eclipse.featdiag.parser.meyers;

import java.util.List;

import com.eclipse.featdiag.parser.meyers.ISOMLayout.Vertex;



/**
 * Static class that returns the vertex or edge
 * that is closest to a specified location.
 * 
 * @author nic
 */
public class GraphElementAccessor {
    
    private static double maxDistance = Double.MAX_VALUE - 1000;
            
	/**
	 * Gets the vertex nearest to the location of the (x,y) location selected,
	 * within a distance of maxDistance. Iterates through all
	 * visible vertices and checks their distance from the click.
	 * @param x
	 * @param y
	 */
	public static Vertex getVertex(List<Vertex> vertices, double x, double y) {
		double minDistance = maxDistance * maxDistance;
        Vertex closest = null;
        for (Vertex v : vertices) {
        	double dx = v.x - x;
        	double dy = v.y - y;
            double dist = dx * dx + dy * dy;
            if (dist < minDistance) {
                minDistance = dist;
                closest = v;
            }
        }
		return closest;
	}
}
