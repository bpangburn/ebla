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
import com.nqadmin.Utils.DBConnector;
import com.greatmindsworking.EDISON.segm.SpeedUpLevel;



/**
 * ParameterData.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Manages video processing parameters for EBLA.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class ParameterData {
	/**
	 * ID of parameter_data record
	 */
	private long parameterID = 0;

	/**
	 * text description of video processing parameters
	 */
	private String description;

	/**
	 * temporary path for processing movies (experiences)
	 */
	private String tmpPath = "./ebla/";

	/**
	 * integer indicating which port of EDISON to use (0=04-25-2003; 1=04-14-2003)
	 */
	private int edisonPortVersion = 0;

	/**
	 * float containing the color radius for mean-shift analysis image segmentation
	 *
	 * The color radius is the number of pixels that constitute a "significant" color.
	 */
	private float segColorRadius = (float)6.5;

	/**
	 * integer containing the spatial radius for mean-shift analysis image segmentation
	 *
	 * The spatial radius is the pixel radius of the search window.  The smaller the value, the
	 * higher the segmentation resolution.
	 */
	private int segSpatialRadius = 7;

	/**
	 * integer containing the minimum number of pixel that constitute a region for
	 * mean-shift analysis image segmentation
	 *
	 * The minimum region is the smallest number of contiguous pixels required for a
	 * "significant" image region.
	 */
	private int segMinRegion = 20;

	/**
	 * integer containing the speed-up level for mean-shift analysis image segmentation
	 *
	 * 0=no speedup, 1=medium speedup, 2=high speedup
	 */
	private int segSpeedUp = 1;

	/**
	 * float containing the speed-up level factor for high speed up segmentation
	 *
	 * 0.0 = highest quality, 1.0 = highest speedup
	 */
	private float segSpeedUpFactor = (float)0.5;

	/**
	 * string containing file prefix for temp frames extracted from each movie/experience
	 */
	private String framePrefix = "/frame";

	/**
	 * string containing file prefix for temp segmented images created for each frame
	 */
	private String segPrefix = "/seg";

	/**
	 * string containing file prefix for temp polygon images created for each frame
	 */
	private String polyPrefix = "/poly";

	/**
	 * float containing the percentage of total pixels that an object must contain to
	 * be considered part of the background rather than a significant object (0 - 100)
	 */
	private float backgroundPixels = (float)20.0;

	/**
	 * integer containing the minimum number of pixels that constitute a "significant"
	 * object
	 */
	private int minPixelCount = 500;

	/**
	 * integer containing the minimum number of consecutive frames that an object must
	 * appear in to be considered a significant object (helps to eliminate
	 * noise / shadows).
	 */
	private int minFrameCount = 7;

	/**
	 * boolean flag indicating whether to reduce the color depth of any segmented regions
	 */
	private boolean reduceColor = false;

	/**
	 * string containing notes about current set of vision system parameters
	 */
	private String notes = "";



	/**
	 * Class constructor that creates an object containing vision processing parameters
	 * for EBLA based on a record in the parameter_data database table.
	 *
	 * @param _dbc			connection to database containing parameter table
	 * @param _parameterID	ID of parameter record to lookup
	 */
    public ParameterData(DBConnector _dbc, long _parameterID) {

		try {
			// SET PARAMETER ID
				parameterID = _parameterID;

			// LOOKUP PARAMETERS FROM DATABASE
				lookupParams(_dbc);

		} catch (Exception e) {
			System.out.println("\n--- ParameterData Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end ParameterData constructor



	/**
	 * Private method to lookup vision system parameters from the database
	 *
	 * @param _dbc			connection to database containing parameter table
	 */
	private void lookupParams(DBConnector _dbc) {

		// DECLARATIONS
			Statement paramState;	// STATEMENT USED TO CREATE PARAMETER RECORDSET
			ResultSet paramRS; 		// RESULTSET FOR RUNTIME PARAMETERS
			String sql;				// USED TO BUILD QUERY AGAINST parameter_data TABLE
			String tmpString;		// USED TO HOLD STRING RESULTS FROM DB (because 2nd ref. to a resultset field returns null)

		try {

			// BUILD PARAMETER_DATA QUERY STRING
				sql = "SELECT * FROM parameter_data WHERE parameter_id = " + parameterID + ";";

			// CREATE STATEMENT
				paramState = _dbc.getStatement();

			// EXECUTE QUERY
				paramRS = paramState.executeQuery(sql);

			// IF A RECORD IS RETURNED, EXTRACT PARAMETERS, OTHERWISE WARN USER
				if (paramRS.next()) {
					// EXTRACT DESCRIPTION
						tmpString = paramRS.getString("description");
						if (tmpString != "") {
							description = tmpString;
						}

					// EXTRACT TEMP PROCESSING PATH
						tmpString = paramRS.getString("tmp_path");
						if (tmpString != "") {
							tmpPath = tmpString;
						}

					// EXTRACT MEAN-SHIFT ANALYSIS IMAGE SEGMENTATION PARAMETERS
						edisonPortVersion = paramRS.getInt("edison_port_version");
						segColorRadius = paramRS.getFloat("seg_color_radius");
						segSpatialRadius = paramRS.getInt("seg_spatial_radius");
						segMinRegion = paramRS.getInt("seg_min_region");
						segSpeedUp = paramRS.getInt("seg_speed_up_code");
						segSpeedUpFactor = paramRS.getFloat("seg_speed_up_factor");
						if (segSpeedUpFactor < 0.0) {
							segSpeedUpFactor = (float)0.0;
						} else if (segSpeedUpFactor > 1.0) {
							segSpeedUpFactor = (float)1.0;
						}

					// EXTRACT SEGMENTATION FRAME IMAGE FILE PREFIXES
						tmpString = paramRS.getString("frame_prefix");
						if (tmpString != "") {
							framePrefix = tmpString;
						}

						tmpString = paramRS.getString("seg_prefix");
						if (tmpString != "") {
							segPrefix = tmpString;
						}

						tmpString = paramRS.getString("poly_prefix");
						if (tmpString != "") {
							polyPrefix = tmpString;
						}

					// EXTRACT MAX/MIN VALUES FOR DETERMINING BACKGROUND AND SIGNIFICANT OBJECTS
						// OBJECTS WITH pixelCount > (backgroundPixels * totalPixels) ARE ELIMINATED
						// AS BACKGROUND
							backgroundPixels = paramRS.getFloat("background_pixels");
						// OBJECTS MUST CONTAIN AT LEAST minPixelCount TO BE CONSIDERED SIGNIFICANT
							minPixelCount = paramRS.getInt("min_pixel_count");
						// OBJECTS MUST BE IN AT LEAST minFrameCount CONSECUTIVE FRAMES TO BE
						// CONSIDERED SIGNIFICANT
							minFrameCount = paramRS.getInt("min_frame_count");

					// EXTRACT COLOR REDUCTION FLAG
						if (paramRS.getInt("reduce_color_code")==0) {
							reduceColor = false;
						} else {
							reduceColor = true;
						}

					// EXTRACT NOTES
						tmpString = paramRS.getString("notes");
						if (tmpString != "") {
							notes = tmpString;
						}

				} else {
					// WARN USER
						System.out.println("No record was found for the specified parameter ID - using defaults.");
				}

			// CLOSE RESULTSET
				paramRS.close();

			// CLOSE STATEMENT
				paramState.close();

		} catch (Exception e) {
			System.out.println("\n--- ParameterData.lookupParams() Exception ---\n");
			e.printStackTrace();
		}

	} // end lookupParams()



	/**
	 * Returns the database ID for the current vision parameters.
	 *
	 * @return database ID for vision parameters
	 */
	public long getParameterID() {
		return parameterID;
	} // end getParameterID()



	/**
	 * Returns the current vision parameters description.
	 *
	 * @return description for current vision parameters
	 */
	public String getDescription() {
		return description;
	} // end getDescription()



	/**
	 * Returns the temporary path to use during processing.
	 *
	 * @return temporary processing path
	 */
	public String getTmpPath() {
		return tmpPath;
	} // end getTmpPath()



	/**
	 * Returns an integer indicating which port of EDISON to use.
	 *
	 * @return integer indicating which port of EDISON to use
	 */
	public int getEdisonPortVersion() {
		return edisonPortVersion;
	} // end getEdisonPortVersion()



	/**
	 * Returns the color radius for mean-shift analysis image segmentation which is
	 * the number of pixels that constitute a "significant" color.
	 *
	 * @return color radius for mean-shift analysis image segmentation
	 */
	public float getSegColorRadius() {
		return segColorRadius;
	} // end getSegColorRadius()



	/**
	 * Returns the spatial radius for mean-shift analysis image segmentation which is
	 * the pixel radius of the search window.  The smaller the value, the higher the
	 * segmentation resolution.
	 *
	 * @return spatial radius for mean-shift analysis image segmentation
	 */
	public int getSegSpatialRadius() {
		return segSpatialRadius;
	} // end getSegSpatialRadius()



	/**
	 * Returns the minimum region for mean-shift analysis image segmentation which is
	 * the minimum number of pixel that constitute a region.
	 *
	 * @return minimum region for mean-shift analysis image segmentation
	 */
	public int getSegMinRegion() {
		return segMinRegion;
	} // end getSegMinRegion()



	/**
	 * Returns the speedup level to use for mean-shift analysis image segmentation.
	 *
	 * @return speedup level for mean-shift analysis image segmentation
	 */
	public SpeedUpLevel getSegSpeedUp() {
		if (segSpeedUp == 0) {
			return SpeedUpLevel.NO_SPEEDUP;
		} else if (segSpeedUp ==1) {
			return SpeedUpLevel.MED_SPEEDUP;
		} else {
			return SpeedUpLevel.HIGH_SPEEDUP;
		}
	} // end getSegSpeedUp()



	/**
	 * Returns the speedup factor to use for mean-shift analysis image segmentation.
	 *
	 * @return speedup factor for high speedup option of mean-shift analysis image segmentation
	 */
	public float getSegSpeedUpFactor() {
		return segSpeedUpFactor;
	} // end getSegSpeedUpFactor()



	/**
	 * Returns the file prefix for temp frames extracted from each movie/experience.
	 *
	 * @return file prefix for temp frames extracted from each movie/experience
	 */
	public String getFramePrefix() {
		return framePrefix;
	} // end getFramePrefix()



	/**
	 * Sets the file prefix for temp frames extracted from each movie/experience.
	 *
	 * @param _framePrefix		file prefix to use for temp frames extracted from each movie/experience
	 */
	public void setFramePrefix(String _framePrefix) {
		if (_framePrefix != "") {
			framePrefix = _framePrefix;
		}
	} // end setFramePrefix()



	/**
	 * Returns the file prefix for temp segmented images created for each frame.
	 *
	 * @return file prefix for temp segmented images created for each frame
	 */
	public String getSegPrefix() {
		return segPrefix;
	} // end getSegPrefix()



	/**
	 * Sets the file prefix for temp segmented images created for each frame.
	 *
	 * @param _segPrefix		file prefix to use for temp segmented images created for each frame
	 */
	public void setSegPrefix(String _segPrefix) {
		if (_segPrefix != "") {
			segPrefix = _segPrefix;
		}
	} // end setSegPrefix()



	/**
	 * Returns the file prefix for temp polygon images created for each frame.
	 *
	 * @return file prefix for temp polygon images created for each frame
	 */
	public String getPolyPrefix() {
		return polyPrefix;
	} // end getPolyPrefix()



	/**
	 * Sets the file prefix for temp polygon images created for each frame.
	 *
	 * @param _polyPrefix		file prefix to use for temp polygon images created for each frame
	 */
	public void setPolyPrefix(String _polyPrefix) {
		if (_polyPrefix != "") {
			polyPrefix = _polyPrefix;
		}
	} // end setPolyPrefix()



	/**
	 * Returns the the percentage of total pixels that an object must contain to
	 * be considered part of the background rather than a significant object (0 - 100).
	 *
	 * @return percentage of total pixels that identify a region as background
	 */
	public float getBackgroundPixels() {
		return backgroundPixels;
	} // end getBackgroundPixels()



	/**
	 * Returns the minimum number of pixels that constitute a "significant"
	 * object.
	 *
	 * @return minimum pixel count for a significant object
	 */
	public int getMinPixelCount() {
		return minPixelCount;
	} // end getMinPixelCount()



	/**
	 * Returns the minimum number of consecutive frames that an object must
	 * appear in to be considered a significant object (helps to eliminate
	 * noise / shadows).
	 *
	 * @return minimum consecutive frame count for a significant object
	 */
	public int getMinFrameCount() {
		return minFrameCount;
	} // end getMinFrameCount()



	/**
	 * Returns a boolean flag indicating whether to reduce the color depth of of any segmented regions.
	 *
	 * @return reduce color flag for current session
	 */
	public boolean getReduceColor() {
		return reduceColor;
	} // end getReduceColor()



	/**
	 * Returns any notes in the database about the "current" experience.
	 *
	 * @return notes on the "current" experience
	 */
	public String getNotes() {
		return notes;
	} // end getNotes()


} // end Params class



/*
 * Revision history:
 * 	08-01-2001 	- 0.01 - initial coding
 *  08-02-2001  - 0.02 - added frame image paths
 *  09-05-2001  - 0.03 - added documentation for spatial radius
 *                       added get() methods for parameters
 *  09-19-2001  - 0.04 - added tmpString to lookupParams() because 2nd lookup of a
 *                       resultSet field returns null
 *  02-04-2002  - 1.1  - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.19  2004/02/25 21:24:45  yoda2
 * Added options for chosing which EDISON port to use and hiding intermediate segmentation results.
 *
 * Revision 1.18  2003/12/31 15:45:51  yoda2
 * Added speed up factor for high speedup segmentation option in latest release of jEDISON.
 *
 * Revision 1.17  2003/12/26 20:27:08  yoda2
 * Misc fixes required for renaming of Params.java to ParameterData.java and Session.java to SessionData.java.
 *
 * Revision 1.16  2003/12/26 19:51:42  yoda2
 * Renamed Params.java to ParameterData.java
 *
 * Revision 1.15  2003/08/08 13:36:03  yoda2
 * Rewritten for use with new database structure.
 * Some new fields were added and many were migrated to the Session class.
 * This allows for execution & storage of multiple calculation sessions for a single set of parameter values.
 *
 * Revision 1.14  2003/05/15 21:19:14  yoda2
 * Removed null constructor.
 *
 * Revision 1.13  2003/05/15 21:04:17  yoda2
 * Removed include_code as part of DB restructuring.
 *
 * Revision 1.12  2002/12/11 22:55:37  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.11  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.10  2002/10/21 15:23:08  bpangburn
 * Parameter cleanup and documentation.
 *
 * Revision 1.9  2002/10/02 20:50:52  bpangburn
 * Added parameters for min SD step size and locking min SD value.
 *
 * Revision 1.8  2002/09/27 23:11:06  bpangburn
 * Moved information for execution mode and # passed to parameters.
 *
 * Revision 1.7  2002/08/20 22:42:15  jayo
 * Patched to use "save_to_file_code" in parameter_data rather than "log_to_file_code".  Need to restore following next database recreation.
 *
 * Revision 1.6  2002/08/20 02:48:40  bpangburn
 * Added experience_index to track processing order and description for language generation to experience_data.  Added resolution_index to experience_lexeme_data for tracking speed of lexical resolution.  Revamped parameter_data table to handle experience selection, reduction of color depth, which stages of EBLA are processed, log file generation, EDISON-based segmentation parameters, background selection, minimum object size, mimimum number of frames for sig. objects, case-sensitivity, and notes.
 *
 * Revision 1.5  2002/08/06 02:50:32  bpangburn
 * Added parameters to determine which movies to process (include_code), where to write intermediate results, how to identify background based on pixel count, minimum number of significant pixels for a significnant object, and minimum number of consecutive frames for a significant object.  Also added notes to database extraction, prefixed segmentation parameters with "seg" and changed segColorRadius to float.
 *
 * Revision 1.4  2002/06/26 13:28:00  bpangburn
 * Changed default parameters for testing by Jon Ayo.
 *
 * Revision 1.3  2002/05/31 22:32:57  bpangburn
 * Tweaked segmentation settings for digital video.
 *
 * Revision 1.2  2002/02/05 20:28:03  bpangburn
 * Added additional CVS keywords for id and log to first comment block and added author, revision, & date to second comment block for integration with JavaDoc.
 *
 */