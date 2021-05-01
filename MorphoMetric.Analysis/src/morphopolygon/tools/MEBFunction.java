
// Morphometric Analysis of Polygons plugin for OpenJUMP - Java version
//
// Copyright (C) 2021 Burak Beyhan, Cüneyt Güler and Hidayet Tağa
// 
// This plugin is designed to analyse morphological properties of polygon features. For further
// explanations please refer to the following paper if you use this plugin in your studies;
//
// Güler, C., Beyhan, B. & Tağa, H. (2021) PolyMorph-2D: An open-source GIS plug-in for morphometric 
// analysis of vector-based 2D polygon features. Geomorphology. https://doi.org/10.1016/j.geomorph.2021.107755
//
// This program is free software; you can redistribute it and/or modify it under the terms of 
// the GNU General Public License as published by the Free Software Foundation; either version 2 
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
// without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
// See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, see http://www.gnu.org/licenses 


package morphopolygon.tools;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;


public class MEBFunction { 

	  public static Geometry getMEB(Polygon poly) { // MIC function with default input parameters ...
	      Geometry pcnvxhull = poly.convexHull(); // calculation of convex hull
		  Geometry result = MEBFunction.getMEB(pcnvxhull);
		  return result;
	  }
	  
	  public static Geometry getMEB(Geometry pcnvxhull) { // if envelope and convex hull of the polygon is already calculated ...
	        Geometry CH = pcnvxhull;
	        Geometry MER = null; // initialize MER geometry
	        double MERArea = 0; // initialize area of MER
	        double MinArea = Double.MAX_VALUE; // initialize minimum area
	        LineSegment bedge; // to store the edge before rotation
	        LineSegment redge = null; // to store the edge for re-rotation
	        Coordinate p0; // to store starting point of the edge
	        Coordinate p1; // to store ending point of the edge
	        Geometry RMER; // to store the rotated convex hull
	        Envelope RBB; // to store the bounding box of the rotated convex hull
	        Envelope SBB = null; // to store the smallest bounding box of the rotated convex hulls
	        AffineTransformation afin; // to construct the required affine transformation for rotating the CH according to the edge concerned
	        Coordinate noktas[] = CH.getCoordinates(); // getting coordinates of CH to process each edge
	        for (int i = 0; i < noktas.length-1; i++) { // iteration over the edges of the polygon
	           p0 = new Coordinate(noktas[i].x, noktas[i].y); // starting point of the edge
	           p1 = new Coordinate(noktas[i+1].x, noktas[i+1].y); // ending point of the edge
	           bedge = new LineSegment(p0, p1); // edge before rotation
	           afin = new AffineTransformation(); // new affine transformation
	           afin.translate(p0.x, p0.y).rotate(-bedge.angle()); // defining rotation parameters
	           RMER = (Geometry) CH.clone(); // creating a clone of the CH for rotation 
	           RMER.apply(afin); // rotating the clone according to parameters defined for affine transformation
	           RBB = RMER.getEnvelopeInternal(); // getting the bounding box of the rotated CH
	           MERArea = RBB.getArea(); // area of the RBB concerned
	           if (MERArea < MinArea) { // if the area of RBB concerned is less than the initial minimum area
	               MinArea = MERArea; // minimum area is set to the area of RBB concerned
	               redge = bedge; // edge for re-rotation
	               SBB = RBB; // smallest bounding box of the rotated convex hulls
	           }
	        }
	        afin = new AffineTransformation(); // new affine transformation
	        afin.rotate(redge.angle()).translate(-redge.p0.x, -redge.p0.y); // defining re-rotation parameters
	        GeometryFactory gf = new GeometryFactory(); // geometry factory ...
	        MER = gf.toGeometry(SBB); // re-rotating the SBB to its original state
	        MER.apply(afin);
	        return MER;
	  }
	  
	  
  	  public static double[] calMEB(Geometry MER) { // if envelope and convex hull of the polygon is already calculated ...
	        double uzunken;
	        double darken;
	        double fullAnglem;
	      
	        double recarea = MER.getArea(); // aslinda bu MERarea
	        double recperim = MER.getLength();
	        Coordinate[] as = MER.getCoordinates();
	        double edge1 = Math.sqrt((as[0].x-as[1].x)*(as[0].x-as[1].x)+(as[0].y-as[1].y)*(as[0].y-as[1].y));
	        double edge2 = Math.sqrt((as[1].x-as[2].x)*(as[1].x-as[2].x)+(as[1].y-as[2].y)*(as[1].y-as[2].y));

	        Coordinate coord5;
	        double radial;
	        int sol = 1;
	  	    CGAlgorithms cga = new CGAlgorithms();
	  	    if (edge1 > edge2) {
	           uzunken = edge1;
	           darken = edge2;
	           coord5 = new Coordinate(as[0].x, as[1].y);
	           radial = Angle.angleBetween(as[0], as[1], coord5);
	           sol = cga.computeOrientation(as[0], as[1], coord5);
	  	    }
	   	    else {
	  	       uzunken = edge2;
	   	       darken = edge1;
	   	       coord5 = new Coordinate(as[1].x, as[2].y);
	   	       radial = Angle.angleBetween(as[1], as[2], coord5);
	 	       sol = cga.computeOrientation(as[1], as[2], coord5);
	   	    }
	  	    fullAnglem = Angle.toDegrees(radial);
	   	    if (sol == -1) {
	  	       fullAnglem = 90 + fullAnglem;
	  	    }
	   	    if (sol == 1) {
	   	       fullAnglem = 90 - fullAnglem;
	   	    }
	   	    if (sol == 0) {
	   	   	   fullAnglem = 90 - fullAnglem;
	    	}
	   	    
		  double[] MEB = new double[5];
		  MEB[0] = recperim;
		  MEB[1] = recarea;
		  MEB[2] = uzunken;
		  MEB[3] = darken;
		  MEB[4] = fullAnglem;
		  return MEB;
	  }
}

		  

 	       
	     