/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2004, Brian E. Pangburn
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
import java.util.StringTokenizer;



/**
 * FrameAnalysisData.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This class stores the data members for data in the frame_analysis_data table.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class FrameAnalysisData {
	/**
	 * current frame number
	 */
	public int frameIndex = 0;

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
	 * Red RGB color component of object
	 */
	public int red = 0;

	/**
	 * Green RGB color component of object
	 */
	public int green = 0;

	/**
	 * Blue RGB color component of object
	 */
	public int blue = 0;

	/**
	 * Grayscale shade of object
	 */
	public int grayScale = 0;



	/**
	 * Class constructor that sets data members based on a record from the frame_analysis_data table
	 *
	 * @param _fadRS ResultSet with a current record for frame_analysis_data
	 */
    public FrameAnalysisData(ResultSet _fadRS) {

		// DECLARATIONS
			StringTokenizer st = null;		// USED TO TOKENIZE POINTS IN BOUNDING RECTANGLE & POLYGON

		try {
			// FRAME INDEX
				frameIndex = _fadRS.getInt("frame_number");

			// POLYGON
				// EXTRACT DATABASE FIELD
					String polyPoints = _fadRS.getString("polygon_point_list");

				// TOKENIZE
					st = new StringTokenizer(polyPoints, ",");

				// INITIALIZE POLYGON
					poly = new Polygon();

				// LOOP THROUGH TOKENS AND ADD TO POLYGON
					while (st.hasMoreTokens()) {
						int x = Integer.parseInt(st.nextToken());
						int y = Integer.parseInt(st.nextToken());
					}

			// # POINTS IN POLYGON
				numPoints = _fadRS.getInt("polygon_point_count");

			// BOUNDING RECTANGLE
				// EXTRACT DATABASE FIELD
					String rectPoints = _fadRS.getString("bound_rect_points");

				// TOKENIZE
					st = new StringTokenizer(rectPoints, ",");

				// EXTRACT X, Y, WIDTH, & HEIGHT
					int x = Integer.parseInt(st.nextToken());
					int y = Integer.parseInt(st.nextToken());
					int width = Integer.parseInt(st.nextToken());
					int height = Integer.parseInt(st.nextToken());

				// INITIALIZE RECTANGLE
					boundRect = new Rectangle(x, y, width, height);

				// GROW RECTANGLE BY PIXEL ON ALL SIDES
				//  - SEGMENTATION PROCESS CAN ADD SMALL SPACE BETWEEN OBJECTS THAT TOUCH
					boundRect.grow(1, 1);

			// CENTROID
				cg = new Point(_fadRS.getInt("centroid_x"), _fadRS.getInt("centroid_y"));

			// AREA
				area = _fadRS.getDouble("area");

			// RGB COLOR
				rgb = _fadRS.getInt("rgb_color");

			// SET COLOR COMPONENTS
				Color c = new Color(rgb);
				red = c.getRed();
				green = c.getGreen();
				blue = c.getBlue();

			// SET GRAYSCALE
			//	grayScale = (red + green + blue) / 3;
			// 06-25-2004 - GOING FROM 0-255 GRAYSCALE TO 216 COLORS (0-215)
			// DIVIDE EACH COLOR BY 51 AND THEN TAKE B*36 + G*6 + R
			// (see "Death of the Websafe Color Palette?" @ http://hotwired.lycos.com/webmonkey/00/37/index2a.html
			//	grayScale = ((blue/51)*36) + ((green/51)*6) + (red/51);
				grayScale = ((blue/85)*16) + ((green/85)*4) + (red/85);
		} catch (Exception e) {
			System.out.println("\n--- FrameAnalysisData Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end FrameAnalysisData()

} // end FrameAnalysisData class



/*
 * $Log$
 * Revision 1.11  2004/08/02 18:20:29  yoda2
 * Change "color" attribute calc from 256 grayscale to 16M -> 216 RGB color reduction.
 *
 * Revision 1.10  2004/06/26 02:56:56  yoda2
 * Started conversion of 256 grayscale calc to 216 color calc for "color" attribute.  Still need to update other references and attribute name in database.
 *
 * Revision 1.9  2004/02/25 21:58:10  yoda2
 * Updated copyright notice.
 *
 * Revision 1.8  2002/12/11 22:50:58  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.7  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.6  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.5  2002/08/21 14:32:57  bpangburn
 * Added fields for red, green, & blue pixel values as well as a grayscale value.
 *
 * Revision 1.4  2002/04/23 22:49:22  bpangburn
 * Debugging - found that x coordinate of centroid was being read from database than area.
 *
 * Revision 1.3  2002/04/22 21:07:27  bpangburn
 * Grew bounding rectangle by one pixel on all sides to improve calculation of overlap/contact.
 *
 * Revision 1.2  2002/04/19 22:52:20  bpangburn
 * Debugged EntityExtractor and added writeToDB method.
 *
 * Revision 1.1  2002/04/17 23:05:07  bpangburn
 * Completed code to detect entities (and their attributes) from frame_analysis_data.  Still need to flesh out writeToDB() in EntityExtractor class.
 *
 */