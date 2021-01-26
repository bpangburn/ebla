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



package com.greatmindsworking.ebla;



import java.sql.*;
import java.io.*;

import javax.swing.JOptionPane;

import com.greatmindsworking.utils.DBConnector;
import com.greatmindsworking.ebla.ui.StatusScreen;



/**
 * EBLA.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This is the main executable class for the EBLA software system.
 *<p>
 * EBLA is an open computational framework for visual perception and
 * grounded language acquisition.  EBLA can watch a series of short
 * videos and acquire a simple language of nouns and verbs corresponding
 * to the objects and object-object relations in those videos.  Upon
 * acquiring this protolanguage, EBLA can perform basic scene analysis
 * to generate descriptions of novel videos.
 *<p>
 * The general architecture of EBLA is comprised of three stages: vision
 * processing, entity extraction, and lexical resolution.  In the vision
 * processing stage, EBLA processes the individual frames in short videos,
 * using a variation of the mean shift analysis image segmentation algorithm
 * to identify and store information about significant objects.  In the entity
 * extraction stage, EBLA abstracts information about the significant objects
 * in each video and the relationships among those objects into internal
 * representations called entities.  Finally, in the lexical acquisition
 * stage, EBLA extracts the individual lexemes (words) from simple
 * descriptions of each video and attempts to generate entity-lexeme mappings
 * using an inference technique called cross-situational learning.  EBLA is
 * not primed with a base lexicon, so it faces the task of bootstrapping its
 * lexicon from scratch.
 *<p>
 * While there have been several systems capable of learning object or event
 * labels for videos, EBLA is the first known system to acquire both nouns and
 * verbs using a grounded computer vision system.
 *<p>
 * EBLA was developed as part of Brian E. Pangburn's dissertation research
 * in the Department of Computer Science at Louisiana State University.
 *<p>
 * The full dissertation along with other information on EBLA is available from
 * http://www.greatmindsworking.com
 *<p>
 *<pre>
 * TO-DO:
 *  1. finish code for dynamic loading of attribute calculations using
 *     forName()
 *  2. revisit color attributes (R, G, B) and color reduction
 *  3. weight words based on # of prior occurrences when generating descriptions
 *  4. revise drawPolys method in FrameProcessor to use cfoArrayList rather
 *     than _polyList
 *</pre>
 *<p>
 * @author	$Author$
 * @version	$Revision$ $Date$
 */
public class EBLA extends Thread {
	/**
	 * database connection info
	 */
	private DBConnector dbc = null;

	/**
	 * calculation session options
	 */
	private SessionData sd = null;

	/**
	 * vision processing parameters
	 */
	private ParameterData pd = null;

	/**
	 * name of semi-colon separated text file for performance results
	 */
	//private final static String performanceFN = "performance.ssv";

	/**
	 * name of semi-colon separated text file for mapping results
	 */
	//private final static String mappingFN = "mapping.ssv";

	/**
	 * name of semi-colon separated text file for generated descriptions
	 */
	//private final static String descriptionFN = "description.ssv";

	/**
	 * maximum time (in milliseconds) allowed for a client to perform
	 * the vision processing for a given experience (try 10 minutes)
	 */
	private final static int maxCalcMS = 1000 * 60 * 10;

	/**
	 * EBLA status screen where intermediate images should be displayed as they are processed
	 */
	private StatusScreen statusScreen = null;



	/**
	 * Class constructor that initializes database connection and retrieves
	 * the vision processing parameters based on the supplied SessionData object.
	 *
	 * @param _sd			SessionData object with EBLA calculation session settings
	 * @param _dbc			connection to database containing parameter table
	 * @param _statusScreen	EBLA status window
	 */
    @SuppressWarnings("resource")
	public EBLA(SessionData _sd, DBConnector _dbc, StatusScreen _statusScreen) {
    	
    	PrintStream outputPS = null;

		try {
			// SET SESSION OBJECT
				sd = _sd;

			// SET DATABASE OBJECT
				dbc = _dbc;

			// SET STATUS WINDOW
				statusScreen = _statusScreen;

			// INITIALIZE VISION PROCESSING PARAMETERS OBJECT
				pd = new ParameterData(dbc, sd.getParameterID());

			// REDIRECT OUTPUT TO LOG FILE
				if (sd.getLogToFile()) {
					outputPS = new PrintStream (new FileOutputStream ("ebla_log_"
						+ sd.getSessionID() + ".txt"));
					System.setOut (outputPS);
					System.setErr (outputPS);
				}

		} catch (Exception e) {
			System.out.println("\n--- EBLA Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end EBLA()



	/**
	 * Performs video processing, entity extraction, and lexical resolution
	 * for a set of experiences.
	 *<p>
	 * The subset of experiences to be processed is specified in the
	 * parameter_experience_data table.  The experiences are processed a set
	 * number of times for a range of minimum standard deviation values.
	 * These minimum standard  deviation values determine how closely the
	 * attribute values for two objects or object-object relations must match
	 * for them to be considered instances of the same entity.  The starting
	 * and stopping minimum standard deviation values along with the step size
	 * and number of iterations are either passed to EBLA as commandline
	 * parameters or as a SessionData object to the EBLA constructor.
	 *<p>
	 * The video processing, entity extraction, and lexical resolution phases
	 * of EBLA are only run as needed or if the user specifies regeneration of
	 * the intermediate images created by the vision processing stage.  This
	 * prevents unnecessary runs of the computationally expensive video
	 * processing phase.
	 */
	@SuppressWarnings("resource")
	private void processExperiences() {

		// DECLARATIONS
			ResultSet tmpRS=null;			// USED TO HOLD RESULTS OF MISC QUERIES
			ResultSet experienceRS=null;	// RESULTSET FOR QUERIES AGAINST experience_data TABLE
			String sql;						// USED TO BUILD QUERIES AGAINST THE ebla_data DATABASE

			long runID=0;					// DATABASE RECORD ID FOR CURRENT CALCULATION RUN
			long experienceID=0;			// DATABASE RECORD ID OF "CURRENT" EXPERIENCE
			String moviePath;				// PATH TO "CURRENT" EXPERIENCE SOURCE MOVIE
			String expTmpPath; 				// SUBDIRECTORY TO STORE INTERMEDIATE IMAGES GENERATED FOR EACH EXPERIENCE
											// APPENDED TO tmp_path IN parameter_data
			int frameCount=0;					// NUMBER OF FRAMES IN "CURRENT" EXPERIENCE
			String lexemes;					// LEXEMES (WORDS) IN LANGUAGE ASSOCIATED WITH "CURRENT" EXPERIENCE

			int experienceIndex=0;			// COUNTER USED TO TRACK ORDER THAT EXPERIENCES ARE PROCESSES

			java.util.Date startTime;		// USED TO TIME DURATION OF PROCESSING FOR EACH SET OF EXPERIENCES

			int loopCount=0;				// CUMULATIVE # OF EXPERIENCES PROCESSED (i.e. NOT RESET FOR EACH BATCH)

			int calcStatusCode=0;			// INDICATES WHETHER OR NOT frame_analysis_data NEEDS TO BE RECALCUALTED
											// FOR THE CURRENT EXPERIENCE
			long calcElapsedTime;			// TIME ELAPSED SINCE LAST PERSON STARTED frame_analysis_data
											// CALCULATIONS FOR THE CURRENT EXPERIENCE
			boolean updateFAD=false;		// BOOLEAN FLAG INDICATING WHETHER frame_analysis_data SHOULD BE
											// UPDATED DURING VIDEO PROCESSING FOR THE CURRENT EXPERIENCE
			long parameterExperienceID=0;	// ID OF RECORD FROM parameter_experience_data MAPPING EXPERIENCE TO
											// PARAMETERS
			boolean visionDone=false;		// INDICATES WHETHER OR NOT frame_analysis_data RECORDS REMAIN TO BE
											// CALCULATED FOR THE EXPERIENCES IN THE CURRENT SESSION
			int experienceCount=0;			// # OF EXPERIENCES TO BE ANALYIZED IN CURRENT SESSION
			int progressBarValue = 0;		// USED FOR UPDATING PROGRESS BAR

			FileWriter fo1 = null;
			FileWriter fo2 = null;
			FileWriter fo3 = null;

			Date currentTimeStamp=null;
			Date priorTimeStamp=null;

			int sleepInterval = 1;

			boolean stopEBLA = false;
			boolean processorResult = false;


		try {
			// DETERMINE MAX # OF EXPERIENCES THAT WILL BE PROCESSED IN ORDER TO SET STATUS BAR
				sql = "SELECT COUNT(*) AS row_count FROM parameter_experience_data"
					+ " WHERE parameter_id = " + sd.getParameterID() + ";";
				tmpRS = dbc.getStatement().executeQuery(sql);
				tmpRS.next();
				experienceCount = tmpRS.getInt("row_count");
				tmpRS.close();

			// HIDE PROGRESS BAR #1 (NOT USED AT ALL AS OF 12-30-2003)
				statusScreen.hideBar(1);
				Thread.sleep(sleepInterval);

			// SEE IF USER WANTS TO REGENERATE ALL INTERMEDIATE IMAGES
			// GENERATED BY VISION PROCESSING SYSTEM
				if (sd.getRegenIntImages()) {
					// UPDATE STATUS TEXT #1
						statusScreen.updateStatus(1, "Regenerating Intermediate Images...");

					// HIDE PROGRESS BAR #1
						statusScreen.hideBar(1);

					// UPDATE STATUS TEXT #2
						statusScreen.updateStatus(2, "Querying Experiences");

					// INITIALIZE PROGRESS BAR #2
						statusScreen.setBarMax(2, experienceCount);
						progressBarValue = 0;
						statusScreen.updateBar(2, progressBarValue);

					// ALLOW STATUS SCREEN TO REFRESH
						Thread.sleep(sleepInterval);

					// QUERY EXPERIENCES IN experience_data BASED ON MAPPINGS IN parameter_experience_data
						sql = "SELECT * FROM experience_data"
							+ " WHERE experience_id IN (SELECT experience_id FROM parameter_experience_data"
							+ "		WHERE parameter_id=" + sd.getParameterID() + ");";

					// EXECUTE QUERY
						experienceRS = dbc.getStatement().executeQuery(sql);

					// CREATE TMP STATEMENT
					//	tmpState = dbc.getStatement();

					// RIP FRAMES FOR EACH EXPERIENCE AND PERFORM VISION PROCESSING ON EACH
					//
					// FOR EACH EXPERIENCE IN parameter_experience_data WHERE
					// calc_status_code=0:
					//   1. SET calc_status_code to 1
					//   2. DELETE ANY DATA IN frame_analysis_data BASED ON parameter_experience_id
					//   3. PROCESS FRAMES AND WRITE TO frame_analysis_data
					//   4. SET calc_status_code to 2
						while (experienceRS.next()) {
							// CHECK TO SEE IF CANCEL BUTTON HAS BEEN PRESSED
								if (statusScreen.getEBLACanceled()) {
									stopEBLA = true;
							    	throw new Exception("EBLA Execution Canceled.");
                				}

							// UPDATE STATUS SCREEN
								progressBarValue++;
                				statusScreen.updateStatus(2, "Processing Experience " + progressBarValue
                					+ " of " + experienceCount);
								statusScreen.updateBar(2, progressBarValue);
								Thread.sleep(sleepInterval);

							// EXTRACT EXPERIENCE ID
								experienceID = experienceRS.getLong("experience_id");

							// INCREMENT EXPERINCE INDEX
								experienceIndex++;

							// EXTRACT PATH TO MOVIE
								moviePath = experienceRS.getString("video_path");

							// EXTRACT SUBDIRECTORY FOR STORAGE OF INTERMEDIATE IMAGES
								expTmpPath = experienceRS.getString("tmp_path");

							// BUILD FULL PATH FOR STORAGE OF INTERMEDIATE IMAGES
								expTmpPath = pd.getTmpPath() + expTmpPath;

							// IF PATH/DIRECTORY EXISTS, DELETE ANY FILES - OTHERWISE CREATE IT
								File expTmpDir = new File(expTmpPath);
								if (expTmpDir.isDirectory()) {
									String fileList[] = expTmpDir.list();
									for (int k=0; k<fileList.length; k++) {
										File currentFile = new File(expTmpPath + "/" + fileList[k]);
										currentFile.delete();
									}
								} else {
									expTmpDir.mkdirs();
								}

							// INITIALIZE frame_analysis_data UPDATE FLAG
								updateFAD = false;

							// DETERMINE IF frame_analysis_data SHOULD BE MODIFIED FOR CURRENT EXPERIENCE
								sql = "SELECT parameter_experience_id, calc_status_code, now() as current_ts, calc_timestamp as prior_ts"
									+ " FROM parameter_experience_data"
									+ " WHERE parameter_id=" + sd.getParameterID()
									+ " AND experience_id=" + experienceID + ";";

								tmpRS = dbc.getStatement().executeQuery(sql);
								tmpRS.next();

								parameterExperienceID = tmpRS.getLong("parameter_experience_id");
								calcStatusCode = tmpRS.getInt("calc_status_code");
								currentTimeStamp = tmpRS.getDate("current_ts");
								priorTimeStamp = tmpRS.getDate("prior_ts");
								calcElapsedTime = currentTimeStamp.getTime() - priorTimeStamp.getTime();

								tmpRS.close();

								if ((calcStatusCode==0) || ((calcStatusCode==1) && (calcElapsedTime > maxCalcMS))) {
									// CHANGE frame_analysis_data UPDATE FLAG
										updateFAD = true;

									// UPDATE DATABASE
										sql = "UPDATE parameter_experience_data SET calc_status_code=1, calc_timestamp=now()"
											+ " WHERE parameter_experience_id=" + parameterExperienceID + ";";

										dbc.getStatement().executeUpdate(sql);
								}

							// IF UPDATING frame_analysis_data, DELETE ANY EXISTING DATA FOR CURRENT EXPERIENCE
								if (updateFAD) {
									sql = "DELETE FROM frame_analysis_data"
										+ " WHERE parameter_experience_id = " + parameterExperienceID + ";";

									dbc.getStatement().executeUpdate(sql);
								}

							// CREATE A FRAME GRABBER TO EXTRACT IMAGES
								FrameGrabber fg = new FrameGrabber(moviePath, (expTmpPath + pd.getFramePrefix()),
									statusScreen, sd.getDisplayMovie());

							// RIP FRAMES
								frameCount = fg.ripFrames();

							// DISPOSE OF FRAME GRABBER AND SET TO NULL
								fg.dispose();
								fg = null;

							// RECOMMEND GARBAGE COLLECTION
								System.gc();

							// CREATE A FRAME PROCESSOR TO PERFORM INITIAL ANALYSIS OF FRAMES
								FrameProcessor fp = new FrameProcessor(1, frameCount, parameterExperienceID,
									expTmpPath, dbc, pd, sd, updateFAD, statusScreen);

							// PROCESS FRAMES
								processorResult = fp.processFrames();

							// SET FRAME PROCESSOR TO NULL
								fp = null;

							// IF UPDATING frame_analysis_data, INDICATE THAT CALCS FOR CURRENT EXPERIENCE
							// HAVE BEEN COMPLETED IN parameter_experience_data
								if (updateFAD) {
									if (frameCount>-1 && processorResult) {
									// FRAME ANALYSIS DATA GENERATED
										sql = "UPDATE parameter_experience_data SET calc_status_code=2"
											+ " WHERE parameter_experience_id=" + parameterExperienceID + ";";
									} else {
									// SOMETHING WENT WRONG
										sql = "UPDATE parameter_experience_data SET calc_status_code=0"
											+ " WHERE parameter_experience_id=" + parameterExperienceID + ";";
									}

									dbc.getStatement().executeUpdate(sql);
								}

							// HIDE INTERMEDIATE IMAGES
								statusScreen.hideImage(1);
								statusScreen.hideImage(2);
								statusScreen.hideImage(3);

							// RECOMMEND GARBAGE COLLECTION
								System.gc();

						} // end while (experienceRS.next())

				} // end if (sd.getRegenIntImages())

			// PERFORM VISION PROCESSING FOR ALL EXPERIENCES ASSOCIATED WITH
			// CURRENT PARAMETER SET IF NOT ALREADY DONE

			// UPDATE STATUS SCREEN
			// SET STATUS SCREEN TEXT
				// UPDATE STATUS TEXT #1
					statusScreen.updateStatus(1, "Current Operation: Video Processing");

				// UPDATE STATUS TEXT #2
					statusScreen.updateStatus(2, "Querying Experiences");

				// INITIALIZE PROGRESS BAR #2
					statusScreen.setBarMax(2, experienceCount);
					progressBarValue = 0;
					statusScreen.updateBar(2, progressBarValue);

				// ALLOW STATUS SCREEN TO REFRESH
					Thread.sleep(sleepInterval);

			// INITIALIZE VISION PROCESSING FLAG
				visionDone = false;

			// LOOP THROUGH REMAINING UNPROCESSED VIDEOS
				while (!visionDone) {
					// QUERY FIRST EXPERIENCE WHERE VISION PROCESSING NEEDS TO BE DONE
					// BASED ON MAPPINGS AND CALC STATUS IN parameter_experience_data
						sql = "SELECT * FROM experience_data"
							+ " WHERE experience_id IN (SELECT experience_id FROM parameter_experience_data"
							+ "		WHERE parameter_id=" + sd.getParameterID()
// check next line...
							+ " 	AND (calc_status_code=0 OR (calc_status_code=1 AND (now()-calc_timestamp)>" + maxCalcMS + ")) LIMIT 1);";

					// EXECUTE QUERY
						experienceRS = dbc.getStatement().executeQuery(sql);

					// CREATE TMP STATEMENT
					//	tmpState = dbc.getStatement();

					// RIP FRAMES AND PERFORM VISION PROCESSING FOR CURRENT EXPERIENCE (IF ANY)
					//   1. SET calc_status_code to 1
					//   2. DELETE ANY DATA IN frame_analysis_data BASED ON parameter_experience_id
					//   3. PROCESS FRAMES AND WRITE TO frame_analysis_data
					//   4. SET calc_status_code to 2
						if (experienceRS.next()) {
							// CHECK TO SEE IF CANCEL BUTTON HAS BEEN PRESSED
								if (statusScreen.getEBLACanceled()) {
									stopEBLA = true;
							    	throw new Exception("EBLA Execution Canceled.");
                				}

							// UPDATE STATUS SCREEN
								progressBarValue++;
                				statusScreen.updateStatus(2, "Processing Experience " + progressBarValue
                					+ " of " + experienceCount);
								statusScreen.updateBar(2, progressBarValue);
								Thread.sleep(sleepInterval);

							// EXTRACT EXPERIENCE ID
								experienceID = experienceRS.getLong("experience_id");

							// INCREMENT EXPERINCE INDEX
								experienceIndex++;

							// EXTRACT PATH TO MOVIE
								moviePath = experienceRS.getString("video_path");

							// EXTRACT SUBDIRECTORY FOR STORAGE OF INTERMEDIATE IMAGES
								expTmpPath = experienceRS.getString("tmp_path");

							// BUILD FULL PATH FOR STORAGE OF INTERMEDIATE IMAGES
								expTmpPath = pd.getTmpPath() + expTmpPath;

							// IF PATH/DIRECTORY EXISTS, DELETE ANY FILES - OTHERWISE CREATE IT
								File expTmpDir = new File(expTmpPath);
								if (expTmpDir.isDirectory()) {
									String fileList[] = expTmpDir.list();
									for (int k=0; k<fileList.length; k++) {
										File currentFile = new File(expTmpPath + "/" + fileList[k]);
										currentFile.delete();
									}
								} else {
									expTmpDir.mkdirs();
								}

							// SET frame_analysis_data UPDATE FLAG
								updateFAD = true;

							// RETRIEVE parameter_experience_id
								sql = "SELECT parameter_experience_id FROM parameter_experience_data"
									+ " WHERE parameter_id = " + sd.getParameterID()
									+ " AND experience_id = " + experienceID + ";";

								tmpRS = dbc.getStatement().executeQuery(sql);
								tmpRS.next();
								parameterExperienceID = tmpRS.getLong("parameter_experience_id");
								tmpRS.close();

							// UPDATE CALCULATION STATUS IN parameter_experience_data TABLE
								sql = "UPDATE parameter_experience_data SET calc_status_code=1, calc_timestamp=now()"
									+ " WHERE parameter_experience_id=" + parameterExperienceID + ";";

								dbc.getStatement().executeUpdate(sql);

							// DELETE ANY EXISTING DATA FOR CURRENT EXPERIENCE
								sql = "DELETE FROM frame_analysis_data"
									+ " WHERE parameter_experience_id = " + parameterExperienceID + ";";

								dbc.getStatement().executeUpdate(sql);

							// CREATE A FRAME GRABBER TO EXTRACT IMAGES
								FrameGrabber fg = new FrameGrabber(moviePath, (expTmpPath + pd.getFramePrefix()),
									statusScreen, sd.getDisplayMovie());

							// RIP FRAMES
								frameCount = fg.ripFrames();

							// DISPOSE OF FRAME GRABBER AND SET TO NULL
								fg.dispose();
								fg = null;

							// RECOMMEND GARBAGE COLLECTION
								System.gc();

							// CREATE A FRAME PROCESSOR TO PERFORM INITIAL ANALYSIS OF FRAMES
								FrameProcessor fp = new FrameProcessor(1, frameCount, parameterExperienceID,
									expTmpPath, dbc, pd, sd, updateFAD, statusScreen);

							// PROCESS FRAMES
								processorResult = fp.processFrames();

							// SET FRAME PROCESSOR TO NULL
								fp = null;

							// INDICATE THAT CALCS FOR CURRENT EXPERIENCE
							// HAVE BEEN COMPLETED IN parameter_experience_data
								if (frameCount>-1 && processorResult) {
								// FRAME ANALYSIS DATA GENERATED
									sql = "UPDATE parameter_experience_data SET calc_status_code=2"
										+ " WHERE parameter_experience_id=" + parameterExperienceID + ";";
								} else {
								// SOMETHING WENT WRONG
									sql = "UPDATE parameter_experience_data SET calc_status_code=0"
										+ " WHERE parameter_experience_id=" + parameterExperienceID + ";";
								}

								dbc.getStatement().executeUpdate(sql);

							// HIDE INTERMEDIATE IMAGES
								statusScreen.hideImage(1);
								statusScreen.hideImage(2);
								statusScreen.hideImage(3);

							// RECOMMEND GARBAGE COLLECTION
								System.gc();

						} else {
							// VISION PROCESSING IS COMPLETE FOR ALL EXPERIENCES...
								visionDone = true;

						} // end if (experienceRS.next())

				} // end while (!visionDone)

// may want to add code to warn user of any experience where vision data is still being processes
// (e.g. by another user)


			// HIDE ENTIRE IMAGE PANEL
				statusScreen.hideImagePanel();

			// UPDATE STATUS TEXT #1
				statusScreen.updateStatus(1, "Extracting Entities and Resolving Lexemes...");

			// HIDE STATUS TEXT #2 & #3
				statusScreen.updateStatus(2, "");
				statusScreen.updateStatus(3, "");

			// HIDE PROGRESS BAR #2 & #3
				statusScreen.hideBar(2);
				statusScreen.hideBar(3);

			// DETERMINE # OF EXPERIENCES THAT WILL BE PROCESSED
				sql = "SELECT COUNT(*) AS row_count FROM parameter_experience_data"
					+ " WHERE parameter_id = " + sd.getParameterID()
					+ " AND calc_status_code = 2;";
				tmpRS = dbc.getStatement().executeQuery(sql);
				tmpRS.next();
				experienceCount = tmpRS.getInt("row_count");
				tmpRS.close();

			// RESET LENGTH OF PROGRESS BAR #2 TO TOTAL # OF RUNS
				statusScreen.setBarMax(2, (((sd.getMinStdDevStop()-sd.getMinStdDevStart())/sd.getMinStdDevStep()+1)*sd.getEBLALoopCount()));

			// INITIALIZE LOG FILES - STAMP NAMES WITH SESSION ID
				fo1 = new FileWriter("session_" + sd.getSessionID() + "_performance.ssv");
				fo1.write("loopCount;stdDev;runNumber;experienceIndex;totalSec;totalLex;totalUMLex;totalEnt;totalUMEnt\n");
				fo2 = new FileWriter("session_" + sd.getSessionID() + "_mappings.ssv");
				fo2.write("loopCount;experienceIndex;resolutionCount\n");
				fo3 = new FileWriter("session_" + sd.getSessionID() + "_descriptions.ssv");
				fo3.write("loopCount;stdDev;experienceIndex;generatedDescription;numCorrect;numWrong;numUnknown;origDescription\n");

			// INITIALIZE COUNTER FOR ALL EXPERINCES PROCESSED
				loopCount = 0;

			// BASED ON SESSION OPTIONS, LOOP THROUGH EXPERIENCES SPECIFIED
			// NUMBER OF TIMES (sd.loopCount), FOR ALL STANDARD DEVIATIONS BETWEEN
			// MIN (sd.minSDStart) AND MAX (sd.minSDStop) SESSION VALUES
			// USING SPECIFIED STEP SIZE (sd.minSDStep)
				for (int i=sd.getMinStdDevStart(); i<=sd.getMinStdDevStop(); i=i+sd.getMinStdDevStep()) {
					for (int j=1;j<=sd.getEBLALoopCount(); j++) {
						// INCREMENT LOOP COUNTER TO TRACK TOTAL EXPERIENCES PROCESSED
							loopCount++;

						// SET STATUS SCREEN TEXT
							statusScreen.updateStatus(2, "Min .Std. Dev=" + i + " - Run #" + j);

						// UPDATE PROGRESS BAR
							statusScreen.updateBar(2, loopCount);

						// ALLOW STATUS SCREEN TO REFRESH
							Thread.sleep(sleepInterval);

						// GET A NEW RUN ID & ADD RUN RECORD
							// GET NEXT run_id
								sql = "SELECT nextval('run_data_seq') AS next_index;";
								tmpRS = dbc.getStatement().executeQuery(sql);
								tmpRS.next();
								runID = tmpRS.getLong("next_index");
								tmpRS.close();

							// ADD RUN DATA RECORD
								sql = "INSERT INTO run_data (run_id, session_id, run_index, min_sd) VALUES ("
									+ runID + "," + sd.getSessionID() + "," + loopCount + "," + i + ");";

								dbc.getStatement().executeUpdate(sql);

						// DETERMINE START TIME FOR CURRENT RUN
							startTime = new java.util.Date();

						// QUERY EXPERIENCES BASED ON parameter_id AND parameter_experience_data
						// H2 version 1.3.155 (2011-05-27) added support for RANDOM() IN ADDITION TO RAND()
							if (sd.getRandomizeExp()) {
								sql = "SELECT * FROM parameter_experience_data, experience_data"
									+ " WHERE parameter_experience_data.parameter_id = " + sd.getParameterID()
									+ " AND parameter_experience_data.experience_id = experience_data.experience_id"
									+ " AND parameter_experience_data.calc_status_code = 2"
									+ " ORDER BY random();";
									//+ " ORDER BY rand();";
							} else {
								sql = "SELECT * FROM parameter_experience_data, experience_data"
									+ " WHERE parameter_experience_data.parameter_id = " + sd.getParameterID()
									+ " AND parameter_experience_data.experience_id = experience_data.experience_id"
									+ " AND parameter_experience_data.calc_status_code = 2"
									+ " ORDER BY experience_data.experience_id ASC;";
							}

						// EXECUTE QUERY
							experienceRS = dbc.getStatement().executeQuery(sql);

						// INITIALIZE experienceIndex;
							experienceIndex = 0;

						// RESET LENGTH OF PROGRESS BAR #3 TO TOTAL # OF EXPERIENCES
							statusScreen.setBarMax(3, experienceCount);


						// IF A RECORD IS RETURNED, EXTRACT PARAMETERS, OTHERWISE WARN USER
							while (experienceRS.next()) {
								// CHECK TO SEE IF CANCEL BUTTON HAS BEEN PRESSED
									if (statusScreen.getEBLACanceled()) {
										stopEBLA = true;
										throw new Exception("EBLA Execution Canceled.");
									}

								// EXTRACT EXPERIENCE ID
									experienceID = experienceRS.getLong("experience_id");

								// EXTRACT EXPERIENCE-PARAMETER ID
									parameterExperienceID = experienceRS.getLong("parameter_experience_id");

								// INCREMENT EXPERINCE INDEX
									experienceIndex++;

								// SET STATUS SCREEN TEXT
									statusScreen.updateStatus(3, "Experience " + experienceIndex + " of " + experienceCount);

								// UPDATE PROGRESS BAR
									statusScreen.updateBar(3, experienceIndex);

								// ALLOW STATUS SCREEN TO REFRESH
									Thread.sleep(sleepInterval);

								// ADD RECORD TO experience_run_data AND SET experience_index
									sql = "INSERT INTO experience_run_data (experience_id, run_id,"
										+ " experience_index) VALUES (" + experienceID + "," + runID
										+ "," + experienceIndex + ");";

									dbc.getStatement().executeUpdate(sql);

								// PROCESS ENTITIES
									// CREATE AN ENTITY EXTRACTOR TO PERFORM MORE DETAILED ANALYSIS
									// OF OBJECTS AND RELATIONSHIPS BASED ON BASIC FRAME ANALYSIS
										EntityExtractor ee = new EntityExtractor(experienceID, parameterExperienceID,
											runID, dbc, sd, pd, (i/100.0));

									// EXTRACT ENTITIES & WRITE RESULTS TO DATABASE
										ee.extractEntities();
										ee.writeToDB();

									// SET ENTITY EXTRACTOR TO NULL
										ee = null;

								// PERFORM LEXICAL RESOLUTION
									// EXTRACT LEXEMES DESCRIBING CURRENT EXPERIENCE
										lexemes = experienceRS.getString("experience_lexemes");

									// DROP CASE ON ALL LEXEMES IF NOT CASE-SENSITIVE
										if (! sd.getCaseSensitive()) {
											lexemes = lexemes.toLowerCase();
										}

									// CREATE A LEXEME RESOLVER TO PERFORM LEXICAL RESOLUTION
									// OR GENENRATE DESCRIPTIONS
										LexemeResolver lr = new LexemeResolver(lexemes, experienceID,
											runID, experienceIndex, dbc);

									// RESOLVE LEXEMES OR GENERATE DESCRIPTIONS
										if (experienceIndex > (experienceCount - sd.getDescToGenerate())) {
											lr.generateDescriptions(fo3, loopCount, i);
											fo3.flush();
										} else {
											lr.resolveLexemes();
										}

									// SET LEXEME RESOLVER TO NULL
										lr = null;

							} // end while

						// CLOSE RESULTSET
							experienceRS.close();

						// UPDATE ENDING TIMESTAMP FOR CURRENT RUN
							sql = "UPDATE run_data SET run_stop=now() WHERE run_id=" + runID + ";";

							dbc.getStatement().executeUpdate(sql);

						// PRINT START AND STOP TIME
							java.util.Date stopTime = new java.util.Date();
							System.out.println("EBLA Started: " + startTime + " ... finished: " + stopTime);
							long totalSec = (stopTime.getTime()-startTime.getTime())/1000;
							long secCopy = totalSec;
							long hours = totalSec / 3600;
							totalSec = totalSec % 3600;
							long minutes = totalSec / 60;
							totalSec = totalSec % 60;
							System.out.println("Total elapsed time: " + hours  + " hours, " + minutes + " minutes, and " + totalSec + " seconds.");


						// WRITE PERFORMANCE LOG INFO
						// NEED: loopCount, stdDev, experienceIndex, totalSec, totalLex, totalEnt, unmappedLex, unmappedEnt
						//	tmpState = dbc.getStatement();

							tmpRS = dbc.getStatement().executeQuery("SELECT COUNT(*) AS lex_count FROM experience_run_data, experience_lexeme_data"
								  + " WHERE experience_run_data.run_id = " + runID
								  + " AND experience_lexeme_data.run_id = " + runID
								  + " AND experience_run_data.experience_id=experience_lexeme_data.experience_id;");
							tmpRS.next();
							int totalLex = tmpRS.getInt("lex_count");
							tmpRS.close();

							tmpRS = dbc.getStatement().executeQuery("SELECT COUNT(*) AS ent_count FROM experience_run_data, experience_entity_data"
								  + " WHERE experience_run_data.run_id = " + runID
								  + " AND experience_entity_data.run_id = " + runID
								  + " AND experience_run_data.experience_id=experience_entity_data.experience_id;");
							tmpRS.next();
							int totalEnt = tmpRS.getInt("ent_count");
							tmpRS.close();

							tmpRS = dbc.getStatement().executeQuery("SELECT COUNT(*) AS um_lex_count FROM experience_run_data, experience_lexeme_data"
								  + " WHERE experience_run_data.run_id = " + runID
								  + " AND experience_lexeme_data.run_id = " + runID
								  + " AND experience_run_data.experience_id=experience_lexeme_data.experience_id"
								  + " AND experience_lexeme_data.resolution_code=0;");
							tmpRS.next();
							int totalUMLex = tmpRS.getInt("um_lex_count");
							tmpRS.close();

							tmpRS = dbc.getStatement().executeQuery("SELECT COUNT(*) AS um_ent_count FROM experience_run_data, experience_entity_data"
								  + " WHERE experience_run_data.run_id = " + runID
								  + " AND experience_entity_data.run_id = " + runID
								  + " AND experience_run_data.experience_id=experience_entity_data.experience_id"
								  + " AND experience_entity_data.resolution_code=0;");
							tmpRS.next();
							int totalUMEnt = tmpRS.getInt("um_ent_count");
							tmpRS.close();

							fo1.write(loopCount + ";" + i + ";" + j + ";" + experienceIndex + ";" + secCopy + ";" + totalLex + ";"
								+ totalUMLex + ";" + totalEnt + ";" + totalUMEnt + "\n");
							fo1.flush();

						// WRITE MAPPING LOG INFO
						// NEED: loopCount, experienceIndex, (resolutionIndex-experienceIndex)
							tmpRS = dbc.getStatement().executeQuery("SELECT * FROM experience_run_data, experience_lexeme_data"
								  + " WHERE experience_run_data.run_id = " + runID
								  + " AND experience_lexeme_data.run_id = " + runID
								  + " AND experience_run_data.experience_id = experience_lexeme_data.experience_id"
								  + " AND experience_lexeme_data.resolution_code = 1"
								  + " ORDER BY experience_run_data.experience_index;");

							while (tmpRS.next()) {
								int expIndex = tmpRS.getInt("experience_index");
								int resIndex = tmpRS.getInt("resolution_index");
								fo2.write(loopCount + ";" + expIndex + ";" + (resIndex-expIndex) + "\n");
							}

							fo2.flush();

						// CLOSE TMP RESULTSET
							tmpRS.close();

					} // end j loop

				} // end i loop

			// UPDATE STATUS SCREEN
				statusScreen.updateStatus(1, "EBLA Session Completed!");
				statusScreen.updateStatus(2, "");
				statusScreen.updateStatus(3, "");
				statusScreen.hideBar(1);
				statusScreen.hideBar(2);
				statusScreen.hideBar(3);
				Thread.sleep(sleepInterval);

			// UPDATE ENDING TIMESTAMP FOR CURRENT SESSION
				sd.updateSessionStop(dbc);

			// INDICATE COMPLETION OF EBLA ON STATUS SCREEN
				statusScreen.indicateEBLACompletion();

			// RECOMMEND GARBAGE COLLECTION
				System.gc();

		} catch (Exception e) {
		// CHECK FOR CANCEL BUTTON & UPDATE STATUS
			if (stopEBLA) {
				statusScreen.updateStatus(1, "EBLA Session Canceled!");
			} else {
				statusScreen.indicateEBLACompletion();
				JOptionPane.showInternalConfirmDialog
					(statusScreen,"EBLA Processor Exception. Please check log file for details.",
					"Processor Exception",JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);	
				statusScreen.updateStatus(1, "");
			}

		// DISPLAY EXCEPTION MESSAGE
			System.out.println("\n--- EBLA.processExperiences() Exception ---\n");
			e.printStackTrace();
			
		} finally {
		// CLOSE OPEN FILES/RESULTSETS
			try {
				if (experienceRS!=null) experienceRS.close();
				if (tmpRS!=null) tmpRS.close();
				
				if (fo1!=null) fo1.close();
				if (fo2!=null) fo2.close();
				if (fo3!=null) fo3.close();

			} catch (Exception misc) {
				System.out.println("\n--- EBLA.processExperiences() Exception ---\n");
				misc.printStackTrace();
			}
		}

	} // end processExperiences()
	
	
	protected static void close(FileWriter fw) {
	    try {
	        if (fw != null) {
	            fw.close();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}



	/**
	 * Run routine for threaded execution of EBLA's methods.
	 */
	@Override
	public void run() {

		try {

			// PROCESS EXPERIENCES FOR EBLA
				processExperiences();

		} catch (Exception e) {
			System.out.println("\n--- EBLA.run() Exception ---\n");
			e.printStackTrace();
		}

	} // end run()



    /**
     * Main procedure - allows EBLA to be run from the command line.
     *<p>
     * The user can pass a parameter ID from the command line to specify a
     * set of EBLA runtime options to use for a calculation session.
     */
    public static void main(String[] args) {

		// DECLARATIONS
			EBLA myEBLA = null;		// INSTANCE OF EBLA CLASS
			SessionData sd = null; // EBLA CALCULATION SESSION
			DBConnector dbc = null; // DATABASE CONNECTION
			long parameterID = 0;	// ID OF parameter_data RECORD TO USE FOR CALCULATIONS


		try {

			// VERIFY THAT A SINGLE COMMAND LINE ARGUMENT WAS PASSED
				if (args.length != 1) {
					printUsage();
					System.exit(0);
				}

			// EXTRACT PARAMETER ID
				parameterID = Integer.parseInt(args[0]);

			// BUILD SESSION DESCRIPTION
				java.util.Date quickDate = new java.util.Date();
				String desc = "Command line session - " + quickDate;

			// INITIALIZE OTHER SESSION VARIABLES
				boolean regenerateImages = false;
				boolean logToFile = true;
				boolean randomizeExp = true;
				int descToGenerate = 0;
				int minSDStart = 5;
				int minSDStop = 5;
				int minSDStep = 5;
				int loopCount = 1;
				boolean fixedSD = false;
				boolean displayMovie = true;
				boolean displayText = false;
				boolean caseSensitive = false;
				String notes = "";

			// CREATE A SESSION OBJECT
				sd = new SessionData(dbc, parameterID, desc, regenerateImages, logToFile,
					randomizeExp, descToGenerate, minSDStart, minSDStop, minSDStep, loopCount, fixedSD,
					displayMovie, displayText, caseSensitive, notes);

			// CREATE A DATABASE CONNECTION
				dbc = new DBConnector("dbSettings", true);

			// CREATE A NEW EBLA OBJECT
				myEBLA = new EBLA(sd, dbc, null);

			// START EBLA THREAD
				myEBLA.start();

			// CLOSE DATABASE CONNECTION
				dbc.closeConnection();

			// SET OBJECTS THAT ARE NO LONGER NEEDED TO NULL
				dbc = null;
				sd = null;
				myEBLA = null;

			// EXIT
				System.exit(0);

		} catch (Exception e) {
			System.out.println("\n--- EBLA.main() Exception ---\n");
			e.printStackTrace();
		}

    } // end main()



	/**
	 * Display usage info if user trys to run standalone with incorrect parameters
	 */
	private static void printUsage() {

		try {
			// DISPLAY USAGE INSTRUCTIONS
				System.out.println("Usage: java com.greatmindsworking.EBLA.EBLA <parameter ID>");

	  	} catch (Exception e) {
			System.out.println("\n--- EBLA.printUsage() Exception ---\n");
			e.printStackTrace();
		}


	} // end printUsage()

} // end EBLA class



/*
 * Revision history:
 * 	07-24-3001 	- 0.01 - initial coding
 *  07-25-2001  - 0.02 - added parameter lookup and experience processing
 *  					 with frame grabbing and basic frame processing
 *  07-26-2001  - 0.03 - added code to get # frames from FrameGrabber object and
 * 						 passed database connection and experience ID to FrameProcessor
 *  09-05-2001  - 0.04 - added documentation on parameter ID to main subroutine
 *  09-19-2001  - 0.05 - changed name of class from BULA to EBLA to match greatmindsworking.com info
 *  10-23-2001  - 0.06 - added code to build vector of words in language used to describe event
 *                     - expanded notes for identifying lexicon-entity mappings
 *  02-04-2002  - 1.1  - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.46  2014/12/19 23:23:32  yoda2
 * Cleanup of misc compiler warnings. Made EDISON GFunction an abstract class.
 *
 * Revision 1.45  2014/04/24 12:34:25  yoda2
 * potential filewriter memory leak cleanup
 *
 * Revision 1.44  2011/06/03 14:45:45  yoda2
 * Restored RANDOM() in SQL now that it is supported in addition to RAND() for H2. This preserves backward compatibility with PostgreSQL.
 *
 * Revision 1.43  2011/04/28 20:13:54  yoda2
 * Replaced _timestamp with _ts in SELECT aliases.
 *
 * Revision 1.42  2011/04/28 14:55:47  yoda2
 * Addressing Java 1.6 -Xlint warnings and replaced ORDER BY random() with ORDER BY rand() to support H2.
 *
 * Revision 1.41  2005/02/17 23:33:04  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.40  2004/06/24 14:35:02  yoda2
 * Replaced mistyped executeQuery with executeUpdate.  Also fixed bug that was causing all videos to be re-analyized based on date stamp not just videos marked as "checked out."
 *
 * Revision 1.39  2004/02/25 21:58:09  yoda2
 * Updated copyright notice.
 *
 * Revision 1.38  2004/02/25 21:24:45  yoda2
 * Added options for chosing which EDISON port to use and hiding intermediate segmentation results.
 *
 * Revision 1.37  2004/02/06 19:42:12  yoda2
 * Fixed glitch with "Querying Experiences" displaying at top of status screen during entire video processing stage.
 *
 * Revision 1.36  2004/02/06 15:59:52  yoda2
 * Adding file flush after each result file is updated so that results may be viewed while EBLA is still running.
 *
 * Revision 1.35  2004/01/13 21:17:28  yoda2
 * Fixed two bugs one that skipped experiences that required video processing and another that messed up results files.
 *
 * Revision 1.34  2004/01/13 17:11:44  yoda2
 * Added logic to reset calc_status_code in parameter_experience_data to zero if user cancels processing.
 *
 * Revision 1.33  2004/01/05 23:35:57  yoda2
 * Added code to recommend garbage collection following ripping of frames and frame analysis.
 *
 * Revision 1.32  2003/12/31 19:38:24  yoda2
 * Fixed various thread synchronization issues.
 *
 * Revision 1.31  2003/12/30 23:19:41  yoda2
 * Modified for more detailed updating of EBLA Status Screen.
 *
 * Revision 1.30  2003/12/26 20:25:52  yoda2
 * Misc fixes required for renaming of Params.java to ParameterData.java and Session.java to SessionData.java.
 *
 * Revision 1.29  2003/08/08 19:37:10  yoda2
 * Significant rewrite to accomodate new database structure allowing for storage of multiple calculations sessions/runs.
 * Integrated with new graphical user interface (EBLAGui).
 *
 * Revision 1.28  2002/12/11 22:49:43  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.27  2002/11/07 19:58:19  bpangburn
 * Fixed internal (j) loop in processExperiences() method.
 *
 * Revision 1.26  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.25  2002/10/21 15:23:08  bpangburn
 * Parameter cleanup and documentation.
 *
 * Revision 1.24  2002/10/02 20:50:52  bpangburn
 * Added parameters for min SD step size and locking min SD value.
 *
 * Revision 1.23  2002/09/27 23:11:06  bpangburn
 * Moved information for execution mode and # passed to parameters.
 *
 * Revision 1.22  2002/09/08 17:19:58  bpangburn
 * Improved logging for descriptions generated by EBLA.
 *
 * Revision 1.21  2002/09/05 18:13:05  bpangburn
 * Added code for testing EBLA using multiple variances over multiple runs, writing results to semi-colon separated files.
 *
 * Revision 1.20  2002/08/21 03:12:41  bpangburn
 * Added randomized queries, deletion of existing entities & lexemes, setting of experience_index, and protolanguage generation.
 *
 * Revision 1.19  2002/08/20 22:16:17  bpangburn
 * Added code to pass Params object to EntityExtractor class.
 *
 * Revision 1.18  2002/08/20 02:48:40  bpangburn
 * Added experience_index to track processing order and description for language generation to experience_data.  Added resolution_index to experience_lexeme_data for tracking speed of lexical resolution.  Revamped parameter_data table to handle experience selection, reduction of color depth, which stages of EBLA are processed, log file generation, EDISON-based segmentation parameters, background selection, minimum object size, mimimum number of frames for sig. objects, case-sensitivity, and notes.
 *
 * Revision 1.17  2002/08/07 13:53:30  bpangburn
 * Fixed type in DELETE query for frame_analysis_data.
 *
 * Revision 1.16  2002/08/07 13:39:45  bpangburn
 * Moved DELETE query for frame_analysis_data from FrameProcessor and added boolean variables to control video processing, entity extraction, and lexical resolution.
 *
 * Revision 1.15  2002/07/05 22:58:48  bpangburn
 * Renamed FrameProcessor2 to FrameProcessor and evaluated various segmentation settings.
 *
 * Revision 1.14  2002/07/03 22:53:19  bpangburn
 * Added code to write I/O to log file rather than screen.
 *
 * Revision 1.13  2002/07/01 22:20:14  bpangburn
 * Added setting to allow dump of screen output to redirect.out file.
 *
 * Revision 1.12  2002/06/26 13:29:44  bpangburn
 * Removed import com.greatmindsworking.ebla.* command.
 *
 * Revision 1.11  2002/05/31 22:32:36  bpangburn
 * Re-enabled frame processing code (instead of just object recognition and lexical acquisition).
 *
 * Revision 1.10  2002/05/14 22:39:52  bpangburn
 * Debugging lexical resolution code.
 *
 * Revision 1.9  2002/05/14 02:01:06  bpangburn
 * Debugged code that calls LexemeResolver.
 *
 * Revision 1.8  2002/05/08 22:49:57  bpangburn
 * Created LexemeResolver class to handle lexical resolution process and updated EBLA class accordingly.
 *
 * Revision 1.7  2002/04/22 21:06:43  bpangburn
 * Moved EBLA timestamps from FrameProcessor to main EBLA class.
 *
 * Revision 1.6  2002/04/19 22:52:20  bpangburn
 * Debugged EntityExtractor and added writeToDB method.
 *
 * Revision 1.5  2002/04/17 23:05:07  bpangburn
 * Completed code to detect entities (and their attributes) from frame_analysis_data.  Still need to flesh out writeToDB() in EntityExtractor class.
 *
 * Revision 1.4  2002/02/05 20:28:03  bpangburn
 * Added additional CVS keywords for id and log to first comment block and added author, revision, & date to second comment block for integration with JavaDoc.
 *
 * Revision 1.3  2002/02/05 19:47:42  bpangburn
 * Added additional CVS keywords for author, revision, & date integration with JavaDoc.
 *
 * Revision 1.2  2002/02/05 19:31:02  bpangburn
 * Added CVS keywords so that CVS will insert appropriate header and log information into source files.
 *
 */