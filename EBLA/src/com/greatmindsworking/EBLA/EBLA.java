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
import java.io.*;



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
 *  4. add logic to FrameProcessor to discard intermediate images based on
 *     saveImages Boolean in Params class
 *  5. revise drawPolys method in FrameProcessor to use cfoArrayList rather
 *     than _polyList
 *</pre>
 *<p>
 * @author	$Author$
 * @version	$Revision$ $Date$
 */
public class EBLA {
	/**
	 * database connection info
	 */
	private DBConnector dbc = null;

	/**
	 * runtime parameters
	 */
	private Params p = null;

	/**
	 * name of semi-colon separated text file for performance results
	 */
	private final static String performanceFN = "performance.ssv";

	/**
	 * name of semi-colon separated text file for mapping results
	 */
	private final static String mappingFN = "mapping.ssv";

	/**
	 * name of semi-colon separated text file for generated descriptions
	 */
	private final static String descriptionFN = "description.ssv";



	/**
	 * Class constructor that initializes database connection and looks up runtime
	 * parameters based on the user specified parameter ID
	 *
	 * @param _parameterID	long containing database record id for runtime parameters
	 *						(-1 if unavailable)
	 */
    public EBLA(long _parameterID) {

		try {

			// INITIALIZE DATABASE DRIVER AND CONNECTION WITH AUTOCOMMIT ON
				dbc = new DBConnector(true);

			// INITIALIZE RUNTIME PARAMETER OBJECT
				p = new Params(dbc, _parameterID);

			// REDIRECT OUTPUT TO LOG FILE
				if (p.getLogToFile()) {
					PrintStream outputPS = new PrintStream (new FileOutputStream ("ebla_log.txt"));
					System.setOut (outputPS);
					System.setErr (outputPS);
				}

		} catch (Exception e) {
			System.out.println("\n--- EBLA Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end EBLA()



	/**
	 * Class constructor that initializes database connection and sets the runtime
	 * parameters based on the defaults in the Params class
	 */
    public EBLA() {

		try {

			// INITIALIZE DATABASE DRIVER AND CONNECTION WITH AUTOCOMMIT ON
				dbc = new DBConnector(true);

			// INITIALIZE RUNTIME PARAMETER OBJECT
				p = new Params();

			// REDIRECT OUTPUT TO LOG FILE
				if (p.getLogToFile()) {
					PrintStream outputPS = new PrintStream (new FileOutputStream ("ebla_log.txt"));
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
	 * The subset of experiences to be processed is specified by the
	 * Params class.  The experiences will processed a set number of times for
	 * a range of minimum standard deviation values.  These minimum standard
	 * deviation values determine how closely the attribute values for two
	 * objects or object-object relations must match for them to be considered
	 * instances of the same entity.  The starting and stopping minimum
	 * standard deviation values along with the step size and number of
	 * iterations are all specified in the Params class.
	 *<p>
	 * The video processing, entity extraction, and lexical resolution phases
	 * of EBLA can be executed independently based on boolean flags in the
	 * Params class.  This allows the computationally expensive video
	 * processing phase to be run separately from the other phases.
	 */
	public void processExperiences() {

		// DECLARATIONS
			Statement tmpState;			// USED TO EXECUTE DELETE STATEMENTS AGAINST frame_analysis_data
			Statement experienceState;  // STATEMENT FOR CREATING EXPERIENCE DATA RESULTSET
			ResultSet experienceRS; 	// RESULTSET FOR QUERIES AGAINST experience_data TABLE
			String sql;					// USED TO BUILD QUERIES AGAINST THE ebla_data DATABASE

			long experienceID;			// DATABASE RECORD ID OF "CURRENT" EXPERIENCE
			String moviePath;			// PATH TO "CURRENT" EXPERIENCE SOURCE MOVIE
			String experienceLabel; 	// LABEL OF "CURRENT" EXPERIENCE - USED FOR NAMING TEMP FILE DIRECTORY
			String experiencePath;		// PATH TO TMP DIRECTORY FOR PROCESSING "CURRENT" EXPERIENCE
			int frameCount;				// NUMBER OF FRAMES IN "CURRENT" EXPERIENCE
			String lexemes;				// LEXEMES (WORDS) IN LANGUAGE ASSOCIATED WITH "CURRENT" EXPERIENCE

			long experienceIndex;		// COUNTER USED TO TRACK ORDER THAT EXPERIENCES ARE PROCESSES

			java.util.Date startTime;	// USED TO TIME DURATION OF PROCESSING FOR EACH SET OF EXPERIENCES

			int loopCount;				// CUMULATIVE # OF EXPERIENCES PROCESSED (i.e. NOT RESET FOR EACH BATCH)


		try {

			// INITIALIZE LOG FILES
				FileWriter fo1 = new FileWriter("performance.ssv");
				fo1.write("loopCount;stdDev;runNumber;experienceIndex;totalSec;totalLex;totalUMLex;totalEnt;totalUMEnt\n");
				FileWriter fo2 = new FileWriter("mappings.ssv");
				fo2.write("loopCount;experienceIndex;resolutionCount\n");
				FileWriter fo3 = new FileWriter("descriptions.ssv");
				fo3.write("loopCount;stdDev;experienceIndex;generatedDescription;numCorrect;numWrong;numUnknown;origDescription\n");

			// INITIALIZE COUNTER FOR ALL EXPERINCES PROCESSED
				loopCount = 0;

			// LOOP THROUGH EXPERIENCES FOR ALL STANDARD DEVIATIONS BETWEEN MIN AND MAX PARAMETER VALUES
			// USING SPECIFIED STEP SIZE
			// EVALUATE EACH STANDARD DEVIATION BASED ON SPECIFIED # OF ITERATIONS
				for (int i=p.getMinStdDevStart(); i<=p.getMinStdDevStop(); i=i+p.getMinStdDevStep()) {
					for (int j=1;j<=p.getEBLALoopCount(); j++) {
						// INCREMENT LOOP COUNTER TO TRACK TOTAL EXPERIENCES PROCESSED
							loopCount++;

						// DETERMINE START TIME FOR CURRENT RUN
							startTime = new java.util.Date();


						// BUILD PARAMETER_DATA QUERY STRING
							if (p.getRandomizeExp()) {
								sql = "SELECT * FROM experience_data WHERE include_code = " + p.getIncludeCode()
									+ " ORDER BY random();";
							} else {
								sql = "SELECT * FROM experience_data WHERE include_code = " + p.getIncludeCode()
									+ " ORDER BY experience_id ASC;";
							}

						// CREATE STATEMENT
							experienceState = dbc.getStatement();

						// EXECUTE QUERY
							experienceRS = experienceState.executeQuery(sql);

						// CREATE STATEMENT
							tmpState = dbc.getStatement();

						// DELETE ANY EXISTING ENTITIES
							sql = "DELETE FROM entity_data;";
							tmpState.executeUpdate(sql);

						// DELETE ANY EXISTING LEXEMES
							sql = "DELETE FROM lexeme_data;";
							tmpState.executeUpdate(sql);

						// INITIALIZE experienceIndex;
							experienceIndex = 0;

						// IF A RECORD IS RETURNED, EXTRACT PARAMETERS, OTHERWISE WARN USER
							while (experienceRS.next()) {
								// EXTRACT EXPERIENCE ID
									experienceID = experienceRS.getLong("experience_id");

								// INCREMENT EXPERINCE INDEX
									experienceIndex++;

								// SET EXPERIENCE INDEX IN experience_data
									sql = "UPDATE experience_data SET experience_index = " + experienceIndex
										+ " WHERE experience_id = " + experienceID + ";";
									tmpState.executeUpdate(sql);

								// EXTRACT PATH TO MOVIE
									moviePath = experienceRS.getString("source_movie");

								// EXTRACT LABEL FOR PROCESSING DIRECTORY
									experienceLabel = experienceRS.getString("label");

								// BUILD PATH FOR TMP FILES GENERATED WHILE PROCESSING EXPERIENCE
									experiencePath = p.getTmpPath() + experienceLabel;

								// DELETE EXISTING frame_analysis_data FOR CURRENT FRAME AND RIP AND PRE-PROCESS FRAMES
									if (p.getProcessVideos()) {

										// DELETE ANY EXISTING DATA FOR CURRENT FRAME IN CURRENT EXPERIENCE
											sql = "DELETE FROM frame_analysis_data WHERE experience_id = " + experienceID + ";";
											tmpState.executeUpdate(sql);

										// IF PATH/DIRECTORY EXISTS, DELETE ANY FILES - OTHERWISE CREATE IT
											File experienceDir = new File(experiencePath);
											if (experienceDir.isDirectory()) {
												String fileList[] = experienceDir.list();
												for (int k=0; k<fileList.length; k++) {
													File currentFile = new File(experiencePath + "/" + fileList[k]);
													currentFile.delete();
												}
											} else {
												experienceDir.mkdirs();
											}

										// CREATE A FRAME GRABBER TO EXTRACT IMAGES
											FrameGrabber fg = new FrameGrabber(moviePath, (experiencePath + p.getFramePrefix()),
												p.getDisplayMovie());

										// RIP FRAMES
											frameCount = fg.ripFrames();

										// DISPOSE OF FRAME GRABBER AND SET TO NULL
											fg.dispose();
											fg = null;

										// CREATE A FRAMEP ROCESSOR TO PERFORM INITIAL ANALYSIS OF FRAMES
											FrameProcessor fp = new FrameProcessor(1, frameCount, experienceID, experiencePath, dbc, p);

										// PROCESS FRAMES
											fp.processFrames();

										// SET FRAME PROCESSOR TO NULL
											fp = null;
									} // end if (processVideos) {

								// PROCESS ENTITIES
									if (p.getProcessEntities()) {
										// CREATE AN ENTITY EXTRACTOR TO PERFORM MORE DETAILED ANALYSIS
										// OF OBJECTS AND RELATIONSHIPS BASED ON BASIC FRAME ANALYSIS
											EntityExtractor ee = new EntityExtractor(experienceID, dbc, p, ((double)i/100.0));

										// EXTRACT ENTITIES & WRITE RESULTS TO DATABASE
											ee.extractEntities();
											ee.writeToDB();

										// SET ENTITY EXTRACTOR TO NULL
											ee = null;
									} // end if (processEntities) {

								// PERFORM LEXICAL RESOLUTION
									if (p.getProcessLexemes()) {

										// EXTRACT EXPERIENCE DESCRIPTION
											lexemes = experienceRS.getString("language_text");

										// DROP CASE ON ALL LEXEMES IF NOT CASE-SENSITIVE
											if (! p.getCaseSensitive()) {
												lexemes = lexemes.toLowerCase();
											}

										// CREATE A LEXEME RESOLVER TO PERFORM LEXICAL RESOLUTION
										// OR GENENRATE DESCRIPTIONS
											LexemeResolver lr = new LexemeResolver(lexemes, experienceID, experienceIndex, dbc);

										// RESOLVE LEXEMES OR GENERATE DESCRIPTIONS
											if ((p.getGenerateDesc()) && (experienceIndex > p.getDescThreshold())) {
												lr.generateDescriptions(fo3, loopCount, i);
											} else {
												lr.resolveLexemes();
											}

										// SET LEXEME RESOLVER TO NULL
											lr = null;
									} // end if (processLexemes) {

							} // end while

						// CLOSE RESULTSET
							experienceRS.close();

						// CLOSE STATEMENTS
							tmpState.close();
							experienceState.close();

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

							tmpState = dbc.getStatement();
							ResultSet tmpRS = null;

							tmpRS = tmpState.executeQuery("SELECT COUNT(*) AS lex_count FROM experience_lexeme_data;");
							tmpRS.next();
							int totalLex = tmpRS.getInt("lex_count");
							tmpRS.close();

							tmpRS = tmpState.executeQuery("SELECT COUNT(*) AS ent_count FROM experience_entity_data;");
							tmpRS.next();
							int totalEnt = tmpRS.getInt("ent_count");
							tmpRS.close();

							tmpRS = tmpState.executeQuery("SELECT COUNT(*) AS um_lex_count FROM experience_lexeme_data WHERE resolution_code = 0;");
							tmpRS.next();
							int totalUMLex = tmpRS.getInt("um_lex_count");
							tmpRS.close();

							tmpRS = tmpState.executeQuery("SELECT COUNT(*) AS um_ent_count FROM experience_entity_data WHERE resolution_code = 0;");
							tmpRS.next();
							int totalUMEnt = tmpRS.getInt("um_ent_count");
							tmpRS.close();

							fo1.write(loopCount + ";" + i + ";" + j + ";" + experienceIndex + ";" + secCopy + ";" + totalLex + ";"
								+ totalUMLex + ";" + totalEnt + ";" + totalUMEnt + "\n");


						// WRITE MAPPING LOG INFO
						// NEED: loopCount, experienceIndex, (resolutionIndex-experienceIndex)
							tmpRS = tmpState.executeQuery("SELECT * FROM experience_data, experience_lexeme_data WHERE experience_lexeme_data.resolution_code = 1"
								+ " AND experience_data.experience_id = experience_lexeme_data.experience_id ORDER BY experience_data.experience_index;");

							while (tmpRS.next()) {
								int expIndex = tmpRS.getInt("experience_index");
								int resIndex = tmpRS.getInt("resolution_index");
								fo2.write(loopCount + ";" + expIndex + ";" + (resIndex-expIndex) + "\n");
							}
							tmpRS.close();

							tmpState.close();

					} // end j loop

				} // end i loop

			// CLOSE LOG FILES
				fo3.close();
				fo2.close();
				fo1.close();

		} catch (Exception e) {
			System.out.println("\n--- EBLA.processExperiences() Exception ---\n");
			e.printStackTrace();
		}

	} // end processExperiences()



	/**
	 * Closes database connection and sets objects that are no longer needed to null
	 *
	 * @param _saveChanges	boolean indicating whether or not to save database changes
	 */
	public void dispose(boolean _saveChanges) {

		try {

			// SAVE DATABASE CHANGES
				if (_saveChanges) {
					dbc.commitChanges();
				}

			// CLOSE DATABASE CONNECTION
				dbc.closeConnection();

			// SET OBJECTS THAT ARE NO LONGER NEEDED TO NULL
				dbc = null;
				p = null;

		} catch (Exception e) {
			System.out.println("\n--- EBLA.dispose() Exception ---\n");
			e.printStackTrace();
		}

	} // end dispose()



    /**
     * Main procedure - allows EBLA to be run from the command line.
     *<p>
     * The user can pass a parameter ID from the command line to specify a
     * set of EBLA parameters in the parameter_data table in the ebla_data
     * database.  Otherwise EBLA will initialize without a parameter ID and
     * use the hard-coded default parameters in the Params class.
     */
    public static void main(String[] args) {

		// DECLARATIONS
			EBLA myEBLA = null;		// INSTANCE OF EBLA CLASS
			long parameterID;		// ID OF parameter_data RECORD IN sensor_data DATABASE

		try {

			// CHECK TO SEE IF A PARAMETER ID WAS PASSED FROM THE COMMAND LINE
				if (args.length > 0) {
					// EXTRACT ID
						parameterID = Integer.parseInt(args[0]);
					// INITIALIZE EBLA WITH SPECIFIED PARAMETERS
						myEBLA = new EBLA(parameterID);
				} else {
					// INITIALIZE EBLA WITH SYSTEM DEFAULTS
						myEBLA = new EBLA();
				}

			// PROCESS MOVIES (EXPERIENCES)
				myEBLA.processExperiences();

			// SAVE CHANGES AND CLOSE DB CONNECTION
				myEBLA.dispose(true);

			// SET OBJECT TO NULL
				myEBLA = null;

			// EXIT
				System.exit(0);

		} catch (Exception e) {
			System.out.println("\n--- EBLA.main() Exception ---\n");
			e.printStackTrace();
		}

    } // end main()

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
 * Removed import com.greatmindsworking.EBLA.* command.
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