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

import com.greatmindsworking.utils.DBConnector;

import java.net.InetAddress;



/**
 * SessionData.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Manages calculation session for EBLA.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SessionData {
	/**
	 * ID of session_data record
	 */
	private long sessionID = 0;

	/**
	 * ID of parent parameter_data record in ebla_data
	 */
	private long parameterID = 0;

	/**
	 * text description of session set
	 */
	private String description;

	/**
	 * boolean flag indicating whether to regenerate all of the intermediate images
	 *
	 * The images are original, segmented, and polygon trance PNG files produced
	 * by the vision system for each frame.
	 */
	private boolean regenIntImages = false;

	/**
	 * boolean flag indicating whether to write results to screen or log file
	 */
	private boolean logToFile = true;

	/**
	 * boolean flag indicating whether to randomize the experiences processed by EBLA
	 */
	private boolean randomizeExp = true;

	/**
	 * number of experiences to generate descriptions for
	 */
	private int descToGenerate = 0;

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
	 * boolean flag indicating whether to display movies while extracting frames
	 */
	private boolean displayMovie = false;

	/**
	 * boolean flag indicating whether to show detailed intermediate results on the terminal
	 */
	private boolean displayText = false;

	/**
	 * boolean flag indicating whether lexemes are case sensitive
	 */
	private boolean caseSensitive = false;

	/**
	 * string containing notes about the current session
	 */
	private String notes = "";

	/**
	 * string containing localhost IP address
	 */
	private String ipAddress = "";



	/**
	 * Class constructor that creates a SessionData object with all of the required session parameters.
	 *
	 * @param _dbc			connection to database containing parameter table
	 * @param _parameterID	ID of parameter record to lookup
	 */
    public SessionData(DBConnector _dbc, long _parameterID, String _description, boolean _regenIntImages, boolean _logToFile,
    	boolean _randomizeExp, int _descToGenerate, int _minStdDevStart, int _minStdDevStop,
    	int _minStdDevStep, int _eblaLoopCount, boolean _fixedStdDev, boolean _displayMovie,
    	boolean _displayText, boolean _caseSensitive, String _notes) {

		try {

			// SET DATAMEMBERS
				parameterID = _parameterID;
				description = _description;
				regenIntImages = _regenIntImages;
				logToFile = _logToFile;
				randomizeExp = _randomizeExp;
				descToGenerate = _descToGenerate;
				minStdDevStart = _minStdDevStart;
				minStdDevStop = _minStdDevStop;
				minStdDevStep = _minStdDevStep;
				eblaLoopCount = _eblaLoopCount;
				fixedStdDev = _fixedStdDev;
				displayMovie = _displayMovie;
				displayText = _displayText;
				caseSensitive = _caseSensitive;
				notes = _notes;

			// DETERMINE IP ADDRESS
				InetAddress addr = InetAddress.getLocalHost();
				ipAddress = addr.toString();

			// WRITE DATA MEMBERS TO DATABASE
				writeToDB(_dbc);

		} catch (Exception e) {
			System.out.println("\n--- SessionData Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end SessionData constructor



	/**
	 * Private method to add single quotes to Strings for writing to Postgres
	 *
	 * @param _tmpString Java String object
	 *
	 * @return String enclosed in single quotes
	 */
	private static String addQuotes(String _tmpString) {

		return "'" + _tmpString + "'";

	} // end addQuotes



	/**
	 * Private method to convert booleans to integers for writing to Postgres
	 *
	 * @param _tmpBool Java boolean object
	 *
	 * @return integer for boolean (0=false, 1=true)
	 */
	private static int boolInt(boolean _tmpBool) {

		int tmpInt = 0;

		if (_tmpBool) {
			tmpInt = 1;
		}

		return tmpInt;

	} // end boolInt



	/**
	 * Public method to write data members to the database
	 *
	 * @param _dbc	    		database connection
	 *
	 * @return boolean indicating success (true) or failure (false)
	 */
	private boolean writeToDB(DBConnector _dbc) {

		// DECLARATIONS
			boolean result = true;		// ASSUME DB WRITE IS OK UNTIL ERROR IS ENCOUNTERED
			ResultSet tmpRS = null;
			String sql = "";

		try {

			// RETRIEVE SESSION ID
				sql = "SELECT nextval('session_data_seq') AS next_index;";
				tmpRS = _dbc.getStatement().executeQuery(sql);
				tmpRS.next();
				sessionID = tmpRS.getLong("next_index");
				tmpRS.close();

			// BUILD SQL
			// NOTE DB DEFAULT TAKES CARE OF WRITING SESSION START TIME
				sql = "INSERT INTO session_data (session_id, parameter_id, description, "
					+ " regen_int_images_code, log_to_file_code, randomize_exp_code,"
					+ " desc_to_generate, min_sd_start, min_sd_stop, min_sd_step, loop_count,"
					+ " fixed_sd_code, display_movie_code, display_text_code,"
					+ " case_sensitive_code, notes, session_ip)"
					+ " VALUES (" + sessionID + ","
					+ parameterID + ","
					+ addQuotes(description) + ","
					+ boolInt(regenIntImages) + ","
					+ boolInt(logToFile) + ","
					+ boolInt(randomizeExp) + ","
					+ descToGenerate + ","
					+ minStdDevStart + ","
					+ minStdDevStop + ","
					+ minStdDevStep + ","
					+ eblaLoopCount + ","
					+ boolInt(fixedStdDev) + ","
					+ boolInt(displayMovie) + ","
					+ boolInt(displayText) + ","
					+ boolInt(caseSensitive) + ","
					+ addQuotes(notes) + ","
					+ addQuotes(ipAddress) + ");";

			// EXECUTE QUERY
				_dbc.getStatement().executeUpdate(sql);

		} catch (Exception e) {
			result = false;
			System.out.println("\n--- SessionData.writeToDB() Exception ---\n");
			e.printStackTrace();
		} finally {
		// CLOSE OPEN FILES/RESULTSETS
			try {
				if (tmpRS!=null) tmpRS.close();
			} catch (Exception misc) {
				System.out.println("\n--- SessionData.writeToDB() Exception ---\n");
				misc.printStackTrace();
			}
		}

		// RETURN RESULT
			return result;

	} // end writeToDB()



	/**
	 * Public method to update the session_stop field in the database
	 *
	 * @param _dbc	    		database connection
	 *
	 * @return boolean indicating success (true) or failure (false)
	 */
	public boolean updateSessionStop(DBConnector _dbc) {

		// DECLARATIONS
			boolean result = true;		// ASSUME DB WRITE IS OK UNTIL ERROR IS ENCOUNTERED
			String sql = "";

		try {
			// BUILD SQL
			// NOTE DB DEFAULT TAKES CARE OF WRITING SESSION START TIME
				sql = "UPDATE session_data SET session_stop = now()"
					+ " WHERE session_id=" + sessionID + ";";

			// EXECUTE QUERY
				_dbc.getStatement().executeUpdate(sql);

		} catch (Exception e) {
			result = false;
			System.out.println("\n--- SessionData.updateSessionStop() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN RESULT
			return result;

	} // end updateSessionStop()



	/**
	 * Returns the database ID for the current session.
	 *
	 * @return database ID for session
	 */
	public long getSessionID() {
		return sessionID;
	} // end getSessionID()



	/**
	 * Returns the database ID for the parent parameter_data record.
	 *
	 * @return database ID for vision parameters
	 */
	public long getParameterID() {
		return parameterID;
	} // end getParameterID()



	/**
	 * Returns the current session description.
	 *
	 * @return description for current session
	 */
	public String getDescription() {
		return description;
	} // end getDescription()



	/**
	 * Returns a boolean flag indicating whether to regenerate all of the intermediate
	 * images for the vision processing system.
	 *
	 * @return regenerate intermediate images flag for the current session
	 */
	public boolean getRegenIntImages() {
		return regenIntImages;
	} // end getRegenIntImages()



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
	 * Returns the number of experiences to generate descriptions for.
	 *
	 * @return # experience to generate descriptions for
	 */
	public int getDescToGenerate() {
		return descToGenerate;
	} // end getDescToGenerate()



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

} // end Session class



/*
 * $Log$
 * Revision 1.6  2005/02/17 23:33:54  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.5  2004/02/25 21:58:10  yoda2
 * Updated copyright notice.
 *
 * Revision 1.4  2004/01/13 17:11:03  yoda2
 * Added code to store local hostname & IP.
 *
 * Revision 1.3  2003/12/26 20:27:08  yoda2
 * Misc fixes required for renaming of Params.java to ParameterData.java and Session.java to SessionData.java.
 *
 * Revision 1.2  2003/12/26 19:53:23  yoda2
 * Renamed Session.java to SessionData.java
 *
 * Revision 1.1  2003/08/08 13:38:48  yoda2
 * Added for use with new database structure.
 * Contains several data members originally in the Params class along with several new data members.
 * This allows for execution & storage of multiple calculation sessions for a single set of parameter values.
 *
 *
 */