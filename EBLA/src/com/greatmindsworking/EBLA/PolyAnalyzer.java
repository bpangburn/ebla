/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002, Brian E. Pangburn
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */



package com.greatmindsworking.EBLA;



import java.awt.*;



/**
 * PolyAnalyzer.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This class is used to perform various geometric computations on
 * Polygon objects including the area and centroid.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class PolyAnalyzer {
	/**
	 * area of polygon
	 */
	protected double area = 0.0;

	/**
	 * centroid (center of gravity) of polygon
	 */
	protected Point centroid = new Point();



	/**
	 * Constructor used to call protected calcAreaAndCentroid() method.
	 *
	 * @param _poly		polygon to analyze
	 */
	public PolyAnalyzer(Polygon _poly) {

		try {

			// CALC AREA AND CENTROID
				calcAreaAndCentroid(_poly);

		} catch (Exception e) {
			System.out.println("\n--- PolyAnalyzer Constructor Exception ---\n");
			e.printStackTrace();
		}

  	} // end PolyAnalyzer()



	/**
	 * Calculate area and centroid (center of gravity) for specified polygon.
	 * Computes the weighted sum of	each triangle's area times its centroid.
	 * Twice area and three times centroid is used to avoid division until the last moment.
	 *
	 * Based on centroid.c from Joseph O'Rourke (orourke@cs.smith.edu) available from
	 * ftp://cs.smith.edu/pub/code/centroid.c
	 *
	 * @param _poly		polygon to analyze
	 */
	protected void calcAreaAndCentroid(Polygon _poly) {

		// 	DECLARATIONS
			int i;								// LOOP COUNTER
			double triArea = 0;					// TWICE AREA OF CURRENT TRIANGLE
			double totalArea = 0;				// RUNNING TOTAL OF TWICE POLYGON AREA
			Point triCentroid = new Point(); 	// THRICE CENTROID OF CURRENT TRIANGLE
			int n = 0;							// NUMBER OF POINTS IN POLYGON
			Point a = new Point();				// FIRST POINT FOR "CURRENT" TRIANGLE
			Point b = new Point();				// SECOND POINT FOR "CURRENT" TRIANGLE
			Point c = new Point();				// THIRD POINT FOR "CURRENT" TRIANGLE

		try {

			// DETERMINE # POINTS IN POLYGON
				n = _poly.npoints;

			// EXTRACT 1st AND 2nd POINT IN POLYGON
				a.setLocation(_poly.xpoints[0], _poly.ypoints[0]);

			// LOOP THORUGH POINTS IN POLYGON AND CALC AREA AND CENTROID FOR TRIANGULAR COMPONENTS
				for (i = 1; i < n-1; i++) {

					// EXTRACT 2nd and 3rd POINTS FOR NEXT TRIANGLE
						b.setLocation(_poly.xpoints[i], _poly.ypoints[i]);
						c.setLocation(_poly.xpoints[i+1], _poly.ypoints[i+1]);

					// CALCULATE THREE TIMES CENTROID FOR CURRENT TRIANGLE
						triCentroid = tripleTriCentroid(a, b, c);

					// CALCULATE TWO TIMES AREA FOR CURRENT TRIANGLE
						triArea =  doubleTriArea(a, b, c);

					// UPDATE CENTROID AND AREA TOTALS
						centroid.x += triArea * triCentroid.x;
						centroid.y += triArea * triCentroid.y;
						totalArea += triArea;
				}

			// DETERMINE LOCATION OF POLYGON CENTROID
				centroid.x /= 3 * totalArea;
				centroid.y /= 3 * totalArea;

			// CALC TOTAL AREA - TAKE ABSOLUTE VALUE IN CASE POLYGON ORIENTATION IS CLOCK-WISE
				area = Math.abs(totalArea / 2);

	  	} catch (Exception e) {
			System.out.println("\n--- PolyAnalyzer.calcCentroid() Exception ---\n");
			e.printStackTrace();
		}

	} // end calcAreaAndCentroid()



	/**
	 * Returns twice the signed area of the triangle determined by a, b, c.
     * Result is positive if a, b, c are oriented ccw, and negative if cw.
	 *
	 * @param _a		first point on triangle
	 * @param _b		second point on triangle
	 * @param _c		third point on triangle
	 *
	 * @return twice the area of the triangle specified
	 */
	private int doubleTriArea(Point _a, Point _b, Point _c) {

		// 	DECLARATIONS
			int area = 0;						// TWICE AREA OF TRIANGLE

		try {

			area = (_b.x - _a.x) * (_c.y - _a.y) - (_c.x - _a.x) * (_b.y - _a.y);

	  	} catch (Exception e) {
			System.out.println("\n--- PolyAnalyzer.doubleTriArea() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN AREA
			return(area);

	} // end doubleTriArea()



	/**
	 * Returns three times the centroid of the triangle determined by a, b, c.
     * The factor of 3 is left in to permit division to be avoided until later.
	 *
	 * @param _a		first point on triangle
	 * @param _b		second point on triangle
	 * @param _c		third point on triangle
	 *
	 * @return thrice the centroid of triangle specified
	 */
	private Point tripleTriCentroid(Point _a, Point _b, Point _c) {

		// 	DECLARATIONS
			Point centroid = new Point();		// THRICE CENTROID OF TRIANGLE

		try {

			centroid.x = _a.x + _b.x + _c.x;
			centroid.y = _a.y + _b.y + _c.y;

	  	} catch (Exception e) {
			System.out.println("\n--- PolyAnalyzer.tripleTriCentroid() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN CENTROID
			return(centroid);

	} // end tripleTriCentroid()

} // end polyAnalyzer class



/*
 * Revision history:
 * 	07-18-2001 	- 0.01 - initial coding
 *  09-05-2001  - 0.02 - updated documentation for values returned by functions
 *  02-04-2002  - 1.1  - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.6  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.5  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.4  2002/08/21 14:34:43  bpangburn
 * Fixed CVS commit log notes to reflect modifications to area calc.
 *
 * Revision 1.3  2002/08/21 14:32:36  bpangburn
 * Forced area calc to return a positive value regardless of polygon orientation (CW or CCW)
 *
 * Revision 1.2  2002/02/05 20:28:03  bpangburn
 * Added additional CVS keywords for id and log to first comment block and added author, revision, & date to second comment block for integration with JavaDoc.
 *
 */