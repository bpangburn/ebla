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



import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.sql.*;
import com.nqadmin.Utils.DBConnector;
import com.greatmindsworking.EDISON.segm.*;
import com.greatmindsworking.EBLA.Interfaces.StatusScreen;



/**
 * <pre>
 * FrameProcessor.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This class performs all of the video preprocessing on frames extracted
 * from the video (experiences) processed by EBLA.  It generates all of the
 * data for the frame_analysis_data table which is later used by the
 * EntityExtractor class.
 *<p>
 * This class calls the mean shift image segmentation routines contained in
 * the com.greatmindsworking.EDISON.segm package.
 *<p>
 * <pre>
 * ==============================================================
 * Notes:
 *
 *	TO CONVERT 2-D IMAGE COORDINATES TO 1-D ARRAY COORDINATES:
 *
 *		myArray[width * y + x] == myImage[x, y]
 *
 * ==============================================================
 * </pre>
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class FrameProcessor {
	/**
	 * database connection info
	 */
	private DBConnector dbc = null;

	/**
	 * long containing the database record ID for the parameter-experience
	 * combination begin processed
	 */
	private long paramExpID = -1;

	/**
	 * String containing the path for storing results when processing the current experience
	 */
	private String expPath = "./tmp/";

	/**
	 * integer containing the width of the current frame image
	 */
	private int width;

	/**
	 * integer contaiing the height of the current frame image
	 */
	private int height;

	/**
	 * integer containing the pixel count of the current frame image
	 * (width * height)
	 */
	private int pixelCount;

	/**
	 * total number of significant objects found while processing experience
	 * (if the same object is found in multiple frames, it is only counted once)
	 */
	private int objectCount = 0;

	/**
	 * maximum score that will be considered for two objects to have a correlation across frames
	 * (lower score = higher correlation)
	 */
	private double maxCorrelationScore = 1.0;

	/**
	 * integer containing the index of the first frame to process
	 */
	private int firstFrameIndex;

	/**
	 * integer containing the index of the last frame to process
	 */
	private int lastFrameIndex;

	/**
	 * ParameterData object containing vision system parameters
	 */
	private ParameterData pd = null;

	/**
	 * SessionData object containing calc session settings
	 */
	private SessionData sd = null;

	/**
	 * boolean flag indicating whether or not to update the frame_analysis_data table
	 */
	private boolean updateFAD = false;

	/**
	 * ArrayList of FrameObjects containing properties for a each "significant"
	 * object in the "current" frame
	 */
	private ArrayList cfoArrayList = null;

	/**
	 * ArrayList of FrameObjects containing properties for a each "significant"
	 * object in the "prior" frame
	 */
	private ArrayList pfoArrayList = null;

	/**
	 * Boolean that allows FrameProcessor to be run in a stand-alone mode without
	 * the ebla_data database
	 */
	private boolean useDataBase = true;

	/**
	 * Integer containing the number of pixels an object must contain to be
	 * considered part a frame's background
	 */
	private int maxArea = 320 * 240;


	/**
	 * EBLA status screen where intermediate images should be displayed as they are processed
	 */
	private StatusScreen statusScreen = null;



	/**
	 * Class constructor that sets parameters when a FrameProcess is created
	 * from another class
	 *
	 * @param _firstFrameIndex		index of first frame image to process
	 * @param _lastFrameIndex		index of last frame image to process
	 * @param _paramExpID			unique id of parameter_experience_data record
	 * @param _expPath				processing path for experiences
	 * @param _dbc					connection to EBLA database
	 * @param _pd					vision system parameters
	 * @param _sd					calculation session settings
	 * @param _updateFAD			boolean indicating whether or not to update frame_analysis_data
	 * @param _statusScreen			EBLA status window where intermediate images should be displayed (if applicable)
	 */
	public FrameProcessor(int _firstFrameIndex, int _lastFrameIndex,
		long _paramExpID, String _expPath, DBConnector _dbc, ParameterData _pd,
		SessionData _sd, boolean _updateFAD, StatusScreen _statusScreen) {

		try {

			// SET INDEX OF FIRST AND LAST FRAME IMAGES TO PROCESS
				firstFrameIndex = _firstFrameIndex;
				lastFrameIndex = _lastFrameIndex;

			// SET PARAMETER-EXPERIENCE ID FOR DATABASE
				paramExpID = _paramExpID;

			// SET PROCESSING PATH FOR EXPERIENCE
				expPath = _expPath;

			// SET DATABASE CONNECTION
				dbc = _dbc;

			// SET VISION PARAMETERS
				pd = _pd;

			// SET CALCULATION SESSION SETTINGS
				sd = _sd;

			// SET frame_analysis_update FLAG
				updateFAD = _updateFAD;

			// SET STATUS WINDOW
				statusScreen = _statusScreen;

		} catch (Exception e) {
			System.out.println("\n--- FrameProcessor Constructor Exception ---\n");
			e.printStackTrace();
		}

  	} // end FrameProcessor()



  	/**
	 * Loops through each frame in an experience, extracting significant object data from each.
	 */
	public boolean processFrames() {

		// DECLARATIONS
			String framePath = expPath + pd.getFramePrefix();	// SOURCE PATH PREFIX FOR EXPERIENCE FRAMES
			String frameFile = "";								// PATH AND FILE NAME FOR EXPERIENCE FRAMES
			BufferedImage frameImage = null;					// EXPERIENCE FRAME IMAGE

			String segPath = expPath + pd.getSegPrefix();		// TARGET PATH PREFIX FOR SEGMENTED IMAGES
			String segFile = "";								// PATH AND FILE NAME FOR SEGMENTED IMAGES
			BufferedImage segImage = null;						// SEGMENTED IMAGE

			String polyPath = expPath + pd.getPolyPrefix();		// TARGET PATH PREFIX FOR POLYGON IMAGES
			String polyFile = "";								// PATH AND FILE NAME FOR POLYGON IMAGES
			BufferedImage polyImage = null;						// POLYGON IMAGE

			String fileExt = "0000.png";						// DEFAULT FILE EXTENSION

			ArrayList polyList = null;							// ARRAYLIST CONTAINING POLYGONS FOUND IN SEGMENTED IMAGE

			int sleepInterval = 1;
			boolean processorResult = false;


		try {

			// INITIALIZE COUNTER TO ESTABLISH NUMBER OF FRAMES ACTUALLY PROCESSED
				int frameCounter = 0;

			// LOOP THROUGH ALL IMAGES
				for (int i=firstFrameIndex; i<=lastFrameIndex; i++) {
					// CHECK TO SEE IF CANCEL BUTTON HAS BEEN PRESSED
						if (statusScreen.getEBLACanceled()) {
							return processorResult;
						}

					// INITIALIZE PROGRESS BAR
						statusScreen.setBarMax(3, lastFrameIndex);

					// BUILD FILE EXTENSION
						if (i<10) {
							fileExt = "000" + i + ".png";
						} else if (i<100) {
							fileExt = "00" + i + ".png";
						} else if (i<1000) {
							fileExt = "0" + i + ".png";
						} else {
							fileExt = i + ".png";
						}

					// BUILD SOURCE IMAGE FILE NAME
						frameFile = framePath + fileExt;

					// INDICATE TO USER THE CURRENT IMAGE BEING PROCESSED
						System.out.println("Processing image: " + frameFile);

					// LOAD SOURCE IMAGE USING LOADER CLASS
						frameImage = ImageLoad.loadImage(frameFile);

					// DETERMINE WIDTH AND HEIGHT
						width = frameImage.getWidth();
						height = frameImage.getHeight();

					// CROP IMAGE
						frameImage = frameImage.getSubimage(5, 5, width-5, height-5);

					// RECALCULATE WIDTH AND HEIGHT
						width = frameImage.getWidth();
						height = frameImage.getHeight();

					// CALCULATE TOTAL NUMBER OF PIXELS
						pixelCount = width * height;

					// RECALCULATE MAX PIXELS (FOR SIGNIFICANT OBJECT) BASED ON PIXEL COUNT
						maxArea = (int)((double)pixelCount * pd.getBackgroundPixels() / 100);

					// STARTING INCORPORATION OF PORTED EDISION CODE....
						// INITIALIZE ARRAYS FOR RGB PIXEL VALUES
							int framePixels[] = new int[pixelCount];
							frameImage.getRGB(0, 0, width, height, framePixels, 0, width);

						// CREATE MSImageProcessor OBJECT
							MSImageProcessor mySegm = new MSImageProcessor();

						// SET IMAGE
							mySegm.DefineImage(framePixels, ImageType.COLOR, height, width);

						// SET SPEEDUP FACTOR FOR HIGH SPEEDUP OPTION
							if (pd.getSegSpeedUp() == SpeedUpLevel.HIGH_SPEEDUP) {
								mySegm.SetSpeedThreshold(pd.getSegSpeedUpFactor());
							}

						// SEGMENT IMAGE
						// (NO_SPEEDUP, MED_SPEEDUP, HIGH_SPEEDUP)
							mySegm.Segment(pd.getEdisonPortVersion(), sd.getDisplayText(),
								pd.getSegSpatialRadius(), pd.getSegColorRadius(), pd.getSegMinRegion(), pd.getSegSpeedUp());

						// GET RESULTING SEGMENTED IMAGE (RGB) PIXELS
							int segpixels[] = new int[pixelCount];
							mySegm.GetResults(segpixels);

						// BUILD BUFFERED IMAGE FROM RGB PIXEL DATA
							segImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
							segImage.setRGB(0, 0, width, height, segpixels, 0, width);

						// BUILD ArrayList OF POLYGONS FROM SEGMENTATION RESULTING BOUNDRIES
							polyList = RegionTracer.bugWalk(mySegm.GetRegions(), width, height, pd.getMinPixelCount(), maxArea);

						// IF ANY OBJECT TRACES FAIL, polyList WILL RETURN NULL - SKIP FRAME...
							if (polyList != null) {
								// INCREMENT NUMBER OF FRAMES ACTUALLY PROCESSED
									frameCounter++;

								// ANALYZE POLYGONS IN CURRENT FRAME AND WRITE TO DB IF UPDATING
									analyzeFrame(polyList, segImage, frameCounter, updateFAD);

								// DRAW POLYGONS ON BUFFERED IMAGE
									polyImage = drawPolys(polyList, segImage, width, height);

								// BUILD TARGET IMAGE FILE NAME(S)
									segFile = segPath + fileExt;
									polyFile = polyPath + fileExt;

								// SAVE BUFFERED IMAGE(S) AS PNG
									ImageIO.write(segImage, "png", new File(segFile));
									ImageIO.write(polyImage, "png", new File(polyFile));
							}


						// UPDATE STATUS SCREEN
							statusScreen.updateStatus(3, "Analyzed Frame " + i
										+ " of " + lastFrameIndex);

						// UPDATE PROGRESS BAR
							statusScreen.updateBar(3, i);

						// ADD CURRENT FRAME ON STATUS SCREEN
							statusScreen.updateImage(1, frameImage);
							statusScreen.updateImage(2, segImage);
							statusScreen.updateImage(3, polyImage);

						// SET STATUS SCREEN REFRESH
							Thread.sleep(sleepInterval);

				} // end for

			// INDICATE SUCCESS
				processorResult = true;


		} catch (Exception e) {
			System.out.println("\n--- FrameProcessor.processFrames() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN
			return processorResult;


  	} // end processFrames()



	/**
	 * Analyze the ArrayList of polygons for the current frame and write results to database
	 *
	 * @param _polyList		ArrayList containing polygons found in current frame
	 * @param _segImage		BufferedImage containing frame segmentation into significant regions
	 * @param _frameNumber	index of the current frame
	 */
	protected void analyzeFrame(ArrayList _polyList, BufferedImage _segImage,
		int _frameNumber, boolean _updateFAD) {

		// DECLARATIONS
			Statement tmpState = null;	// USED TO EXECUTE DELETE AND INSERT STATEMENTS AGAINT frame_analysis_data
			int rgb = 0;				// RGB COLOR OF "CURRENT" REGION
			String sql;					// USED TO BUILD QUERY AGAINST frame_analysis_data TABLE
			double score = 0.0;			// CORRELATION SCORE COMPARING AN OBJECT IN CURRENT FRAME TO OBJECT IN PRIOR FRAME
			boolean firstFrame = false; // INDICATES IF FIRST FRAME WITH TRACIBLE OBJECTS IS BEING PROCESSED  (FIRST
										// TRACIBLE FRAME MAY NOT BE FIRST FRAME IF RegionTracer HAS PROBLEMS


		try {

			// CREATE STATEMENT
				if (_updateFAD) {
					tmpState = dbc.getStatement();
				}

			// DETERMINE IF FIRST FRAME IS BEING PROCESSED
				if (cfoArrayList == null) {
					firstFrame = true;
				}

			// INITIALIZE ARRAY LIST OF PRIOR FRAME OBJECTS
				if (! firstFrame) {
					pfoArrayList = new ArrayList(cfoArrayList);
				}

			// INITIALIZE ARRAY LIST OF CURRENT FRAME OBJECTS
				cfoArrayList = new ArrayList();

			// LOOP THROUGH ALL POLYGONS IN ArrayList, CALCULATE OBJECT PROPERTIES FOR EACH
			//  1. NUMBER OF POINTS
			//  2. COORDINATES OF POINTS IN POLYGON
			//  3. RGB COLOR OF OBJECT
			//  4. BOUNDING RECTANGLE POINTS
			//  5. EXPERIENCE ID
			//  6. FRAME NUMBER
			//  7. CENTROID / CENTER OF GRAVITY
			//  8. AREA
				for (int i=0; i < _polyList.size(); i++) {

					// EXTRACT "CURRENT" POLYGON
						Polygon poly = (Polygon)_polyList.get(i);

					// INITIALIZE CURRENT FRAME OBJECT
						FrameObject fo = new FrameObject(poly, _frameNumber, 0);

					// DETERMINE RGB COLOR FROM FIRST POINT IN POLYGON
						rgb = _segImage.getRGB(poly.xpoints[0], poly.ypoints[0]);

					// SET RGB COLOR
						fo.rgb = rgb;

					// IF PROCESSING FIRST FRAME, SET CORRELATION INDEX
						if (firstFrame) {
							objectCount++;
							fo.correlationIndex = objectCount;
						}

					// ADD NEW OBJECT TO ARRAY LIST OF CURRENT FRAME OBJECTS
						cfoArrayList.add(fo);

				} // end for loop

			// CORRELATE OBJECTS IN CURRENT FRAME TO THOSE IN PRIOR FRAME
			// 	-- LOGIC ASSUMES THAT OBJECTS DON'T DISAPPEAR AND THEN REAPPEAR
			//	-- I.E. LOGIC CAN'T CORRELATE OVER NON-CONSECUTIVE FRAMES
				if (! firstFrame) {
				// INITIALIZE ARRAY LIST OF MATCH SCORE OBJECTS
					ArrayList msArrayList = new ArrayList();

				// LOOP THROUGH PRIOR FRAME OBJECTS
					for (int i=0; i < pfoArrayList.size(); i++) {
					// INITIALIZE PRIOR FRAME OBJECT
						FrameObject pfo = (FrameObject)pfoArrayList.get(i);

					// LOOP THROUGH CURRENT FRAME OBJECTS
						for (int j=0; j < cfoArrayList.size(); j++) {
						// INITIALIZE CURRENT FRAME OBJECT
							FrameObject cfo = (FrameObject)cfoArrayList.get(j);

						// CALCULATE SCORE
							score = Math.abs(cfo.area-pfo.area);
							score += height * Math.abs(cfo.cg.x-pfo.cg.x);
							score += width * Math.abs(cfo.cg.y-pfo.cg.y);
							score = score / pixelCount;

						// IF SCORE IS UNDER THRESHOLD, THEN CREATE MatchScore OBJECT
						// 	-- THRESHOLD PREVENTS SWITCHING (I.E. o1 & o2 IN FRAME 1 WITH o2 & o3 IN FRAME 2)
							if (score < maxCorrelationScore) {
								// CREATE MATCH SCORE OBJECT
									MatchScore ms = new MatchScore();

								// SET CORRELATION INDEX
									ms.correlationIndex = pfo.correlationIndex;

								// SET CURRENT FRAME OBJECT INDEX
									ms.arrayIndex = j;

								// SET SCORE
									ms.score = score;

								// ADD TO ARRAY LIST
									msArrayList.add(ms);
							} // end if
						} // end for j
					} // end for i

				// SORT msArrayList IN ASCENDING ORDER BY SCORE
					Collections.sort(msArrayList);

				// SCAN LIST TILL EMPTY:
				//		A. EACH PASS, TAKE LOWEST SCORE
				//		B. SET CORRELATION INDEX FOR CORRESPONDING CURRENT OBJECT
				//		C. REMOVE ANY OTHER ENTRIES IN LIST WITH SAME CORRELATION INDEX (CURRENT OR PRIOR)
					while (! msArrayList.isEmpty()) {
						// GET OBJECT WITH LOWEST SCORE
							MatchScore ms = (MatchScore)msArrayList.get(0);

						// SET CORRELATION INDEX
							FrameObject cfo = (FrameObject)cfoArrayList.get(ms.arrayIndex);
							cfo.correlationIndex = ms.correlationIndex;
							cfoArrayList.set(ms.arrayIndex, cfo);

						// REMOVE CURRENT MS OBJECT FROM ARRAYLIST
							msArrayList.remove(0);

						// ITERATE THROUGH REMAINING ITEMS & REMOVE ANY CORRELATED OBJECTS
							Iterator itr = msArrayList.iterator();

							while (itr.hasNext()) {
								MatchScore tmpMS = (MatchScore)itr.next();
								if ((tmpMS.correlationIndex == ms.correlationIndex) || (tmpMS.arrayIndex == ms.arrayIndex)) {
									//msArrayList.remove(tmpMS);
									itr.remove();
								}
							}
					}

				// MAKE FINAL PASS THROUGH cfoArrayList AND CHECK FOR UNMATCHED OBJECTS (correlationIndex = 0)
				//		-- IF FOUND, INCREMENT objectCount & SET correlationIndex = objectCount
					for (int i=0; i < cfoArrayList.size(); i++) {
					// INITIALIZE CURRENT FRAME OBJECT
						FrameObject cfo = (FrameObject)cfoArrayList.get(i);

					// CHECK FOR CORRELATION INDEX = 0
						if (cfo.correlationIndex == 0) {
							objectCount++;
							cfo.correlationIndex = objectCount;
							cfoArrayList.set(i, cfo);
						}
					}


				} // end correlation


			// WRITE ALL OBJECTS IN cfoArrayList TO DATABASE
				if (_updateFAD) {

					Iterator itr = cfoArrayList.iterator();

					while (itr.hasNext()) {

						// EXTRACT CURRENT FRAME OBJECT
							FrameObject fo = (FrameObject)itr.next();

						// WRITE TO DATABASE
							fo.writeToDB(tmpState, paramExpID);
					}
				}

			// CLOSE STATEMENT
				if (_updateFAD) {
					tmpState.close();
				}

		} catch (Exception e) {
			System.out.println("\n--- FrameProcessor.analyzeFrame() Exception ---\n");
			e.printStackTrace();
		}

	} // end analyzeFrame()



	/**
	 * Draws polygons on to a BufferedImage.
	 *
	 * @param _polyList		ArrayList containing polygons found in current frame
	 * @param _segImage		BufferedImage containing frame segmentation into significant regions
	 * @param _width		width of BufferedImage
	 * @param _height		height of BufferedImage
	 *
	 * @return image with polygon outlines of "significant" objects
	 */

// CHANGE THIS TO USE cfoArrayList RATHER THAN _polyList
	protected BufferedImage drawPolys(ArrayList _polyList, BufferedImage _segImage, int _width, int _height) {

		// DECLARATIONS
			BufferedImage polyImage = null;		// IMAGE TO BE RETURNED BY drawPolys()

		try {

			// INITILIZE FILTERED IMAGE
				polyImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);

			// EXTRACT GRAPHICS CONTEXT FOR DRAWING
				Graphics2D g = (Graphics2D)polyImage.getGraphics();

			// FILL BACKGROUND IN WHITE
				g.setColor(Color.white);
				g.fillRect(0, 0, _width, _height);

			// SET STROKE WIDTH
				g.setStroke(new BasicStroke(3));

			// LOOP THROUGH ALL POLYGONS IN ARRAYLIST OF CURRENT FRAME OBJECTS, EXTRACTING AND DRAWING EACH
				Iterator itr = cfoArrayList.iterator();

				while (itr.hasNext()) {

					// EXTRACT CURRENT FRAME OBJECT
						FrameObject fo = (FrameObject)itr.next();

					// SET COLOR FOR CURRENT POLYGON
						Color c = new Color(fo.rgb);
						g.setColor(c);

					// DRAW POLYGON ONTO BUFFEREDIMAGE
						g.drawPolyline(fo.poly.xpoints,fo.poly.ypoints,fo.numPoints);

					// DRAW BOUNDING RECTANGLE
						g.setColor(Color.red);
						g.drawRect(fo.boundRect.x , fo.boundRect.y , fo.boundRect.width , fo.boundRect.height);

					// DRAW STARTING POINT AND CENTER OF GRAVITY (CENTROID)
						g.setColor(Color.black);
						g.drawRect(fo.poly.xpoints[0], fo.poly.ypoints[0], 1, 1);
						g.drawRect(fo.cg.x, fo.cg.y, 1, 1);
				}

		} catch (Exception e) {
			System.out.println("\n--- FrameProcessor.drawPolys() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN CONVERTED IMAGE
			return polyImage;

	} // end drawPolys()

} // end FrameProcessor



/*
 * Revision history:
 * 	03-22-2001 	- 0.01 - new class based on FrameProcessor1 created
 *  03-25-2001  - 0.02 - tied in color image segmentation code
 *  04-21-2001  - 1.00 - cleaned up code and fleshed out JavaDOC comments
 *  07-26-2001  - 1.01 - added DBConnection to constructor and added logic to
 *						 write basic frame analysis results to database
 *  01-20-2002  - 1.02 - added reduceColorDepth code to limit palette to 27 colors
 *  02-04-2002  - 1.10 - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.33  2004/01/13 17:11:44  yoda2
 * Added logic to reset calc_status_code in parameter_experience_data to zero if user cancels processing.
 *
 * Revision 1.32  2003/12/31 19:38:24  yoda2
 * Fixed various thread synchronization issues.
 *
 * Revision 1.31  2003/12/31 15:45:51  yoda2
 * Added speed up factor for high speedup segmentation option in latest release of jEDISON.
 *
 * Revision 1.30  2003/12/30 23:19:41  yoda2
 * Modified for more detailed updating of EBLA Status Screen.
 *
 * Revision 1.29  2003/12/26 20:25:53  yoda2
 * Misc fixes required for renaming of Params.java to ParameterData.java and Session.java to SessionData.java.
 *
 * Revision 1.28  2003/08/08 17:25:03  yoda2
 * Modified to accomidate the new database structure (e.g. parameter_experience_data).
 * Added logic to display frames to EBLA GUI during rip if applicable.
 * Removed main() method for standalone testing (no longer applicable).
 *
 * Revision 1.27  2002/12/11 22:52:54  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.26  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.25  2002/10/21 15:23:08  bpangburn
 * Parameter cleanup and documentation.
 *
 * Revision 1.24  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.23  2002/08/20 22:13:31  bpangburn
 * Added code to set frame index in frame_analysis_data based on actual # frames processed.
 *
 * Revision 1.22  2002/08/20 02:48:40  bpangburn
 * Added experience_index to track processing order and description for language generation to experience_data.  Added resolution_index to experience_lexeme_data for tracking speed of lexical resolution.  Revamped parameter_data table to handle experience selection, reduction of color depth, which stages of EBLA are processed, log file generation, EDISON-based segmentation parameters, background selection, minimum object size, mimimum number of frames for sig. objects, case-sensitivity, and notes.
 *
 * Revision 1.21  2002/08/19 22:06:49  bpangburn
 * Modified drawPolys() routine to use a white background and a 3 pixel thick brush stroke.  Required substitution of Graphics with Graphics2D.
 *
 * Revision 1.20  2002/08/15 17:27:34  bpangburn
 * Fixed object correlation code to work based on actual number of frames successfully processed by RegionTracer rather than unprocessed frame count.
 *
 * Revision 1.19  2002/08/09 21:46:44  bpangburn
 * Added code to skip frames where RegionTracer fails to trace some object.
 *
 * Revision 1.18  2002/08/07 13:51:57  bpangburn
 * Removed unused code for LUV conversion, bugwalk, and old segmentation routines.  Moved code to delete records from frame_analysis_data to the EBLA class.
 *
 * Revision 1.17  2002/08/01 17:38:49  bpangburn
 * Fixed version number.
 *
 * Revision 1.16  2002/08/01 17:36:49  bpangburn
 * Tweaked segmentation parameters.
 *
 * Revision 1.15  2002/07/05 22:58:48  bpangburn
 * Renamed FrameProcessor2 to FrameProcessor and evaluated various segmentation settings.
 *
 * Revision 1.14  2002/07/03 22:54:09  bpangburn
 * Moved bug walk code to RegionTracer.java.
 *
 * Revision 1.13  2002/07/01 22:20:14  bpangburn
 * Added setting to allow dump of screen output to redirect.out file.
 *
 * Revision 1.12  2002/06/28 22:34:00  bpangburn
 * Cleaned up bugwalk code.  Modified RGB color depth reduction routine to split each color into 0-85=0, 86-170=128, & 171-255=255.
 *
 * Revision 1.11  2002/06/28 19:18:29  bpangburn
 * Incorporated new segmentation code based on EDISON.
 *
 * Revision 1.10  2002/06/26 13:30:39  bpangburn
 * Modified for compatiblity with JDK 1.4 (removed ImageIOWorkaround code).
 *
 * Revision 1.9  2002/04/24 22:34:25  bpangburn
 * Finished code to dump significant regions to grayscale image and found bug in bugwalk routine - now end point only has to be 3 pixels from starting point (rather than 2).
 *
 * Revision 1.8  2002/04/23 22:50:17  bpangburn
 * Searching for segmentation / bug walk glitch - added logic to dump significant regions to PNG files.
 *
 * Revision 1.7  2002/04/22 21:06:17  bpangburn
 * Moved EBLA timestamps to main EBLA class and modified color extraction to use pixel at centroid rather than first point in polygon.
 *
 * Revision 1.6  2002/04/08 22:32:01  bpangburn
 * Debugging of object correlation code.
 *
 * Revision 1.5  2002/04/03 23:18:13  bpangburn
 * Finished object correlation code.
 *
 * Revision 1.4  2002/03/31 04:17:54  bpangburn
 * Added MatchScore class to hold data members used during the object correlation process.
 * Started object numbering scheme.
 *
 * Revision 1.3  2002/03/29 23:13:17  bpangburn
 * Created FrameObject class to handle significant object properties and started  correlation code.
 *
 * Revision 1.2  2002/02/05 20:28:03  bpangburn
 * Added additional CVS keywords for id and log to first comment block and added author, revision, & date to second comment block for integration with JavaDoc.
 *
 */