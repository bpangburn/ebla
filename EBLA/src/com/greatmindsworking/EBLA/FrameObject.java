/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2003, Brian E. Pangburn
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



import java.sql.*;
import java.awt.*;



/**
 * FrameObject.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This class stores the data members for the "significant" object in each video
 * frame processed by EBLA.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class FrameObject {
	/**
	 * polygon definition of object
	 */
	public Polygon poly = null;

	/**
	 * number of points in object polygon
	 */
	public int numPoints = 0;

	/**
	 * bounding rectangle for object
	 */
	public Rectangle boundRect = null;

	/**
	 * center of gravity/centroid of object
	 */
	public Point cg = null;

	/**
	 * area of object
	 */
	public double area = 0.0;

	/**
	 * RGB color of object
	 */
	public int rgb = 0;

	/**
	 * index of frame containing object
	 */
	public int frameIndex = 0;

	/**
	 * correlation index of object (a given object will have the same correlation index across all frames)
	 */
	public int correlationIndex = 0;



	/**
	 * Class constructor that takes a polygon and extracts useful information about it
	 *
	 * @param _poly Java polygon object
	 * @param _expID ID of experience record containing object
	 * @param _rgb RGB color of object
	 */
    public FrameObject(Polygon _poly, int _frameIndex, int _rgb) {

		try {

			// SET PARAMETERS
				poly = new Polygon(_poly.xpoints, _poly.ypoints, _poly.npoints);
				frameIndex = _frameIndex;
				rgb = _rgb;

			// CALCULATE OBJECT PROPERTIES
				calculateProperties();

		} catch (Exception e) {
			System.out.println("\n--- FrameObject Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end FrameObject()



	/**
	 * Based on the polygon for the current object, determine number of points, bounding rectangle,
	 * area, and center of gravity
	 */
	protected void calculateProperties() {

		try {

			// DETERMINE # POINTS IN POLYGON
				numPoints = poly.npoints;

			// DETERMINE BOUNDING RECTANGLE
				boundRect = new Rectangle(poly.getBounds());

			// CREATE PolyAnalyzer OBJECT TO DETERMINE CENTROID & AREA
				PolyAnalyzer pa = new PolyAnalyzer(poly);
				cg = pa.centroid;
				area = pa.area;

		} catch (Exception e) {
			System.out.println("\n--- FrameObject.calculateProperties() Exception ---\n");
			e.printStackTrace();
		}

	} // end calculateProperties()



	/**
	 * Public method to write data members to the database
	 *
	 * @param _tmpState		database statement for execution of any SQL commands
	 * @param _paramExpID	ID of parent parameter_experience_data record
	 *
	 * @return boolean indicating success (true) or failure (false)
	 */
	public boolean writeToDB(Statement _tmpState, long _paramExpID) {

		// DECLARATIONS
			boolean result = true;		// ASSUME DB WRITE IS OK UNTIL ERROR IS ENCOUNTERED
			String sql;					// USED TO BUILD QUERY AGAINST frame_analysis_data TABLE
			String polyPoints;			// CSV STRING CONTAINING POLYGON POINTS
			String boundPoints;			// CSV STRING CONTAINING BOUNDING RECTANGLE POINTS

		try {

			// PLACE FIRST POINTS IN STRING
				polyPoints = "'" + poly.xpoints[0] + "," + poly.ypoints[0];

			// ADD SUBSEQUENT POINTS TO STRING
				for (int j = 1; j < (numPoints-1); j++) {
					polyPoints += "," + poly.xpoints[j] + "," + poly.ypoints[j];
				}

			// ADD CLOSING SINGLE QUOTE
				polyPoints += "'";

			// BUILD STRING WITH BOUNDING RECTANGLE X, Y, WIDTH, & HEIGHT VALUES
				boundPoints = "'" + boundRect.x + "," + boundRect.y + ","
					+ boundRect.width + "," + boundRect.height + "'";

			// BUILD INSERT QUERY
				sql = "INSERT INTO frame_analysis_data (parameter_experience_id, frame_number, object_number,"
					+ " polygon_point_count, polygon_point_list, rgb_color, bound_rect_points, centroid_x,"
					+ " centroid_y, area)"
					+ " VALUES (" + _paramExpID + "," + frameIndex + "," + correlationIndex + "," + numPoints
					+ "," + polyPoints + "," + rgb + "," + boundPoints + "," + cg.x + "," + cg.y
					+ "," + area + ");";

			// WRITE VALUES TO DATABASE
				_tmpState.executeUpdate(sql);

		} catch (Exception e) {
			result = false;
			System.out.println("\n--- FrameObject.writeToDB() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN RESULT
			return result;

	} // end writeToDB()

} // end FrameObject class



/*
 * $Log$
 * Revision 1.7  2002/12/11 22:52:10  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.6  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.5  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.4  2002/04/17 23:05:07  bpangburn
 * Completed code to detect entities (and their attributes) from frame_analysis_data.  Still need to flesh out writeToDB() in EntityExtractor class.
 *
 * Revision 1.3  2002/04/16 20:17:02  bpangburn
 * Modified writeToDB method to add # of points in polygon, not just polygon itself.
 *
 * Revision 1.2  2002/04/08 22:32:01  bpangburn
 * Debugging of object correlation code.
 *
 * Revision 1.1  2002/03/29 23:13:17  bpangburn
 * Created FrameObject class to handle significant object properties and started  correlation code.
 *
 */