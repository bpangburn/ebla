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



import java.sql.*;
import com.greatmindsworking.EDISON.segm.SpeedUpLevel;



/**
 * Params.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Manages runtime parameters for EBLA, retrieving from the ebla_data database
 * if a parameter_data record ID is supplied and using defaults otherwise.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class Params {
	/**
	 * boolean flag indicating whether to process videos for current run
	 *
	 * If true, all video pre-processing will be performed up to the entity analysis stage.
	 */
	private boolean processVideos = true;

	/**
	 * boolean flag indicating whether to process entities for current run
	 *
	 * This stage cannot run unless there are records in the frame_analysis_data table.
	 */
	private boolean processEntities = true;

	/**
	 * boolean flag indicating whether to process lexemes for current run
	 *
	 * This stage cannot run unless there are records in the entity_data table.
	 */
	private boolean processLexemes = true;

	/**
	 * boolean flag indicating whether to write results to screen or log file
	 */
	private boolean logToFile = true;

	/**
	 * boolean flag indicating whether to randomize the experiences processed by EBLA
	 */
	private boolean randomizeExp = true;

	/**
	 * boolean flag indicating whether to generate descriptions for some experiences
	 */
	private boolean generateDesc = false;

	/**
	 * number of experiences to process before trying to generate descriptions
	 */
	private int descThreshold = 7;

	/**
	 * starting minimum standard deviation for matching entities
	 */
	private int minStdDevStart = 5;

	/**
	 * stopping minimum standard deviation for matching entities
	 */
	private int minStdDevStop = 5;

	/**
	 * minimum standard deviation step size
	 */
	private int minStdDevStep = 5;

	/**
	 * number of times to process all experiences for each minimum standard deviation
	 */
	private int eblaLoopCount = 5;

	/**
	 * boolean flag indicating whether to limit standard deviation to value specified
	 *
	 * When this is true, the standard deviation calculated for the current entity
	 * is ignored.
	 */
	private boolean fixedStdDev = false;

	/**
	 * temporary path for processing movies (experiences)
	 */
	private String tmpPath = "./ebla/";

	/**
	 * boolean flag indicating whether to display movies while extracting frames
	 */
	private boolean displayMovie = false;

	/**
	 * boolean flag indicating whether to show detailed intermediate results on the terminal
	 */
	private boolean displayText = false;

	/**
	 * boolean flag indicating whether to save movie frames after processing/analysis
	 */
	private boolean saveImages = false;

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
	 * boolean flag indicating whether lexemes are case sensitive
	 */
	private boolean caseSensitive = false;

	/**
	 * string containing notes about current set of runtime parameters
	 */
	private String notes = "";



	/**
	 * Class constructor that creates an object containing default runtime parameters for BULA
	 */
    public Params() {

		try {

			// NOTHING TO DO FOR CASE WHERE NO DATABASE LOOKUP OCCURS

		} catch (Exception e) {
			System.out.println("\n--- Params Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end Params()



	/**
	 * Class constructor that creates an object containing runtime parameters for BULA based
	 * on a record in the parameter_data database table
	 *
	 * @param _dbc			connection to database containing parameter table
	 * @param _parameterID	ID of parameter record to lookup (-1 if system should use first available)
	 */
    public Params(DBConnector _dbc, long _parameterID) {

		try {

			// LOOKUP PARAMETERS FROM DATABASE
				lookupParams(_dbc, _parameterID);

		} catch (Exception e) {
			System.out.println("\n--- Params Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end Params()



	/**
	 * Private method to lookup runtime parameters in a database
	 *
	 * @param _dbc			connection to database containing parameter table
	 * @param _parameterID	ID of parameter record to lookup (-1 if system should use first available)
	 */
	private void lookupParams(DBConnector _dbc, long _parameterID) {

		// DECLARATIONS
			Statement paramState;	// STATEMENT USED TO CREATE PARAMETER RECORDSET
			ResultSet paramRS; 		// RESULTSET FOR RUNTIME PARAMETERS
			String sql;				// USED TO BUILD QUERY AGAINST parameter_data TABLE
			String tmpString;		// USED TO HOLD STRING RESULTS FROM DB (because 2nd ref. to a resultset field returns null)

		try {

			// BUILD PARAMETER_DATA QUERY STRING
				if (_parameterID == -1) {
					sql = "SELECT * FROM parameter_data;";
				} else {
					sql = "SELECT * FROM parameter_data WHERE parameter_id = " + _parameterID + ";";
				}

			// CREATE STATEMENT
				paramState = _dbc.getStatement();

			// EXECUTE QUERY
				paramRS = paramState.executeQuery(sql);

			// IF A RECORD IS RETURNED, EXTRACT PARAMETERS, OTHERWISE WARN USER
				if (paramRS.next()) {
					// EXTRACT VIDEO PROCESSING FLAG
						if (paramRS.getInt("process_videos_code")==0) {
							processVideos = false;
						} else {
							processVideos = true;
						}

					// EXTRACT ENTITY PROCESSING FLAG
						if (paramRS.getInt("process_entities_code")==0) {
							processEntities = false;
						} else {
							processEntities = true;
						}

					// EXTRACT LEXEME PROCESSING FLAG
						if (paramRS.getInt("process_lexemes_code")==0) {
							processLexemes = false;
						} else {
							processLexemes = true;
						}

					// EXTRACT OUTPUT DESTINATION FLAG
						if (paramRS.getInt("log_to_file_code")==0) {
							logToFile = false;
						} else {
							logToFile = true;
						}

					// EXTRACT EXPERIENCE RANDOMIZATION FLAG
						if (paramRS.getInt("randomize_exp_code")==0) {
							randomizeExp = false;
						} else {
							randomizeExp = true;
						}

					// EXTRACT DESCRIPTION GENERATION FLAG
						if (paramRS.getInt("generate_desc_code")==0) {
							generateDesc = false;
						} else {
							generateDesc = true;
						}

					// EXTRACT DESCRIPTION GENERATION THRESHOLD
						descThreshold = paramRS.getInt("desc_threshold");

					// EXTRACT MINIMUM STANDARD DEVIATION VALUES
						minStdDevStart = paramRS.getInt("min_sd_start");
						minStdDevStop = paramRS.getInt("min_sd_stop");
						minStdDevStep = paramRS.getInt("min_sd_step");

					// EXTRACT # OF TIMES TO PROCESS EXPERIENCES FOR EACH
					// MINIMUM STANDARD DEVIATION VALUE
						eblaLoopCount = paramRS.getInt("loop_count");

					// EXTRACT FIXED MINIMUM STANDARD DEVIATION FLAG
						if (paramRS.getInt("fixed_sd_code")==0) {
							fixedStdDev = false;
						} else {
							fixedStdDev = true;
						}

					// EXTRACT TEMP PROCESSING PATH
						tmpString = paramRS.getString("tmp_path");
						if (tmpString != "") {
							tmpPath = tmpString;
						}

					// EXTRACT MOVIE DISPLAY FLAG
						if (paramRS.getInt("display_movie_code")==0) {
							displayMovie = false;
						} else {
							displayMovie = true;
						}

					// EXTRACT DETAILED MESSAGE DISPLAY FLAG
						if (paramRS.getInt("display_text_code")==0) {
							displayText = false;
						} else {
							displayText = true;
						}


					// EXTRACT SAVE EXTRACTED IMAGES FLAG
						if (paramRS.getInt("save_images_code")==0) {
							saveImages = false;
						} else {
							saveImages = true;
						}

					// EXTRACT MEAN-SHIFT ANALYSIS IMAGE SEGMENTATION PARAMETERS
						segColorRadius = paramRS.getFloat("seg_color_radius");
						segSpatialRadius = paramRS.getInt("seg_spatial_radius");
						segMinRegion = paramRS.getInt("seg_min_region");
						segSpeedUp = paramRS.getInt("seg_speed_up_code");

					// EXTRACT SEGMENTATINO FRAME IMAGE FILE PREFIXES
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

					// EXTRACT CASE-SENSITIVITY FLAG
						if (paramRS.getInt("case_sensitive_code")==0) {
							caseSensitive = false;
						} else {
							caseSensitive = true;
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
			System.out.println("\n--- Params.lookupParams() Exception ---\n");
			e.printStackTrace();
		}

	} // end lookupParams()



	/**
	 * Returns a boolean flag indicating whether to process videos for the current run.
	 *
	 * @return process videos flag for current session
	 */
	public boolean getProcessVideos() {
		return processVideos;
	} // end getProcessVideos()



	/**
	 * Returns a boolean flag indicating whether to process entities for the current run.
	 *
	 * @return process entities flag for current session
	 */
	public boolean getProcessEntities() {
		return processEntities;
	} // end getProcessEntities()



	/**
	 * Returns a boolean flag indicating whether to process lexemes for the current run.
	 *
	 * @return process lexemes flag for current session
	 */
	public boolean getProcessLexemes() {
		return processLexemes;
	} // end getProcessLexemes()



	/**
	 * Returns a boolean flag indicating whether to display intermediate results
	 * or write them to a log file (ebla_log.txt in the current path).
	 *
	 * @return log to file flag for current session
	 */
	public boolean getLogToFile() {
		return logToFile;
	} // end getLogToFile()



	/**
	 * Returns a boolean flag indicating whether to randomize the experiences
	 * processed by EBLA.
	 *
	 * @return randomize experiences flag for current session
	 */
	public boolean getRandomizeExp() {
		return randomizeExp;
	} // end getRandomizeExp()



	/**
	 * Returns a boolean flag indicating whether to generate descriptions for
	 * some of the experiences being processed by EBLA.
	 *
	 * @return generate descriptions flag for current session
	 */
	public boolean getGenerateDesc() {
		return generateDesc;
	} // end getGenerateDesc()



	/**
	 * Returns a number of experiences to process before attempting to generate
	 * descriptions.
	 *
	 * @return experience threshold for description generation
	 */
	public int getDescThreshold() {
		return descThreshold;
	} // end getDescThreshold()



	/**
	 * Returns the starting minimum standard deviation to use for entity
	 * comparisons.
	 *
	 * @return starting minimum standard deviation
	 */
	public int getMinStdDevStart() {
		return minStdDevStart;
	} // end getMinStdDevStart()



	/**
	 * Returns the stopping minimum standard deviation to use for entity
	 * comparisons.
	 *
	 * @return stopping minimum standard deviation
	 */
	public int getMinStdDevStop() {
		return minStdDevStop;
	} // end getMinStdDevStop()



	/**
	 * Returns the step size for incrementing the  minimum standard deviation.
	 *
	 * @return minimum standard deviation step size
	 */
	public int getMinStdDevStep() {
		return minStdDevStep;
	} // end getMinStdDevStep()



	/**
	 * Returns the number of times to process the experiences for each
	 * minimum standard deviation value.
	 *
	 * @return number of times to evaluate each minimum standard deviation
	 */
	public int getEBLALoopCount() {
		return eblaLoopCount;
	} // end getEBLALoopCount()



	/**
	 * Returns a boolean flag indicating whether to fix the standard
	 * deviation used for comparing entities to the specified minimum.
	 * Otherwise the calculated standard deviation for each attribute
	 * can be used if it is greater than the specified minimum value.
	 *
	 * @return fixed standard deviation flag for entity comparisons
	 */
	public boolean getFixedStdDev() {
		return fixedStdDev;
	} // end getFixedStdDev()



	/**
	 * Returns the temporary path to use during processing.
	 *
	 * @return temporary processing path
	 */
	public String getTmpPath() {
		return tmpPath;
	} // end getTmpPath()



	/**
	 * Returns a boolean flag indicating whether or not to display experience movies
	 * when ripping/extracting frames.
	 *
	 * @return display flag for experience movies
	 */
	public boolean getDisplayMovie() {
		return displayMovie;
	} // end getDisplayMovie()



	/**
	 * Returns a boolean flag indicating whether or not to display detailed intermediate
	 * results while processing experiences.
	 *
	 * @return display flag for detailed intermediate results
	 */
	public boolean getDisplayText() {
		return displayText;
	} // end getDisplayText()



	/**
	 * Returns a boolean flag indicating whether or not to save intermediate image files
	 * generated during frame processing.
	 *
	 * @return save flag for intermediate image files
	 */
	public boolean getSaveImages() {
		return saveImages;
	} // end getSaveImages()



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
	 * Returns a boolean flag indicating whether or not lexemes are case-sensitive.
	 *
	 * @return case-sensitivity flag for current session
	 */
	public boolean getCaseSensitive() {
		return caseSensitive;
	} // end getCaseSensitive()



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