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
import java.util.*;



/**
 * DBConnector.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Establishes a database connection for the EBLA software system.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class DBConnector {
	/**
	 * constant string containing database driver
	 */
	private final static String jdbcDriver = "org.postgresql.Driver";

	/**
	 * constant string containing path to database containing data for EBLA
	 */
	private String dbPath = "jdbc:postgresql://";
    //private final static String dbPath = "jdbc:postgresql://127.0.0.1:5432/ebla_data";

    /**
     * constant string containing database username
     */
    private String username = "postgres";

    /**
     * constant string containing database password
     */
    private String password = "password";

	/**
	 * connection to the "server" database file
	 */
	private Connection dbConnection;

	/**
	 * boolean to indicate whether to auto-commit database changes
	 */
	private boolean autoCommit = false;

	/**
	 * boolean to indicate whether to run in test mode (basic diagnostics)
	 */
	private static boolean testFlag = false;



	/**
	 * Class constructor that calls appropriate methods to initialize database connection
	 *
	 * @param _autoCommit	boolean to indicate whether to auto-commit database changes
	 */
    public DBConnector(boolean _autoCommit) {

		try {

			// INITIALIZE DATABASE
				autoCommit = _autoCommit;
				initializeDatabase();


		} catch (Exception e) {
			System.out.println("\n--- DBConnector Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end DBConnector()



	/**
	 * Initialize connection to database
	 */
	private void initializeDatabase() {

		// DECLARATIONS
			String ip = "";				// IP ADDRESS OF DATABASE SERVER
			String port = "";			// DATABASE PORT ON DATABASE SERVER
			String dbName = "";			// NAME OF EBLA DATABASE ON DATABASE SERVER

		try {
			// EXTRACT DATABASE PARAMETERS FROM TEXT FILE
				FileReader fr = new FileReader("connect.txt");
				BufferedReader br = new BufferedReader(fr);
				String connectInfo = br.readLine();
				fr.close();

			// BUILD STRING TOKENIZER AND EXTRACT TOKENS FROM STRING
				// CONSTRUCT TOKENIZER
					StringTokenizer st = new StringTokenizer(connectInfo, ";");

				// EXTRACT IP ADDRESS
					ip = st.nextToken();

				// EXTRACT PORT
					port = st.nextToken();

				// EXTRACT DATABASE NAME
					dbName = st.nextToken();

				// EXTRACT USER NAME
					username = st.nextToken();

				// EXTRACT PASSWORD
					password = st.nextToken();

			// BUILD CONNECTION STRING
    			dbPath = dbPath + ip + ":" + port + "/" + dbName;

			// SET CLASS
				Class.forName(jdbcDriver);

			// ESTABLISH DATABASE CONNECTIONS
				dbConnection = DriverManager.getConnection(dbPath, username, password);

			// SET TRANSACTION PROCESSING BASED ON VALUE OF AUTOCOMMIT
				if (autoCommit) {
					dbConnection.setAutoCommit(true);
				} else {
					dbConnection.setAutoCommit(false);
				}

			// RUN BASIC DIAGNOSTIC IF APPLICABLE
				if (testFlag) {

				// ***********************************************************************************************
				// TESTING BLOCK !!!

					// Declarations
						Statement myState;	// statement for test query
						ResultSet myRS; 	// resultset for test query
						String sql;			// used to build query against parameter_data table

					// Build client data query string
						sql = "SELECT * FROM parameter_data;";

					// Create statement
						myState = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

					// Execute query
						//myRS = getStatement().executeQuery(sql);
						myRS = myState.executeQuery(sql);

					// If query returns a record, read data members from database
						while (myRS.next()) {
							// initialize current object
								System.out.println(myRS.getString("description"));
								//NEXT FEW LINES WON'T WORK UNLESS FULL JDBC 2.0 COMPLIANCE EXISTS
								//myRS.updateString("notes", "Hello World!");
								//myRS.updateRow();
						}

					// MAKE AN UPDATE TO THE DATABASE (WHEN NOT FULLY JDBC 2.0 COMPLIANT)
						sql = "UPDATE parameter_data SET description = 'Hello World!' WHERE parameter_id = 1;";
						myState.executeUpdate(sql);

					// Close resultset
						myRS.close();

					// COMMIT CHANGES
						dbConnection.commit();


				// END TESTING BLOCK !!!
				// ***********************************************************************************************
				}

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.initializeDatabase() Exception ---\n");
			e.printStackTrace();
		}

	} // end initializeDatabase()



	/**
	 * Returns a statement for running queries, deletes, & updates against the database
	 *
	 * @return a new statement for the database connection
	 */
	public Statement getStatement() {

		// DECLARATIONS
			Statement myState = null;			// STATEMENT FOR CURRENT DB CONNECTION

		try {

			// CREATE NEW STATEMENT
				myState = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.getStatement() Exception ---\n");
			e.printStackTrace();
		}

		return myState;

	} // end getStatement()



	/**
	 * Commit changes to database
	 */
	public void commitChanges() {

		try {

			// COMMIT CHANGES
				dbConnection.commit();

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.commitChanges() Exception ---\n");
			e.printStackTrace();
		}

	} // end commitChanges()



	/**
	 * Close connection to database
	 */
	public void closeConnection() {

		try {

			// CLOSE CONNECTION
				dbConnection.close();

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.closeConnection() Exception ---\n");
			e.printStackTrace();
		}

	} // end closeConnection()



    /**
     * Main procedure - allows DBConnector to be run in stand-alone mode
     */
    public static void main(String[] args) {

		try {
			// SET TEST FLAG TO TRUE SO THAT BASIC CONNECTION DIAGNOSTIC WILL BE RUN
				testFlag = true;

			// INITIALIZE DATABASE DRIVER AND CONNECTION
				DBConnector myDBC = new DBConnector(false);

			// CLOSE CONNECTION
				myDBC.closeConnection();

			// INDICATE SUCCESS TO USER
				System.out.println("Database opened and closed successfully!");

			// EXIT
				System.out.println("Exiting DBConnector.main() standalone testing routine.");
				System.exit(0);

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.main() Exception ---\n");
			e.printStackTrace();
		}

    } // end main()

} // end DBConnector class



/*
 * Revision history:
 * 	07-23-2001 	- 0.01 - initial coding
 *  09-06-2001  - 0.02 - added runQuery routine and made data members private
 *  09-18-2001  - 0.03 - added runUpdate routine to execute update and delete commands
 *  09-19-2001  - 0.04 - removed runUpdate and runQuery and added getStatement -- this
 *                       was needed because resultSets should be created using different statements
 *  01-31-2002  - 0.05 - made postgres modification to use with PolicyFeedProcessor
 *                       cleaned up test code and added logic to write to the database
 *  02-04-2002  - 1.1  - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.8  2002/10/27 23:04:20  bpangburn
 * Finished logic for file with database connection parameters and cleaned up JavaDoc.
 *
 * Revision 1.7  2002/10/27 15:19:43  bpangburn
 * Placed database connection parameters in a separate text file - connect.txt.
 *
 * Revision 1.6  2002/09/27 23:12:07  bpangburn
 * Added license information and moved log to end of file.
 *
 * Revision 1.5  2002/05/31 22:32:03  bpangburn
 * Set database address back to machine with Postgres 7.1 (instead of 7.2)
 *
 * Revision 1.4  2002/05/14 22:39:19  bpangburn
 * Changed database IP address to machine with Postgres 7.2.1.
 *
 * Revision 1.3  2002/04/18 23:02:34  bpangburn
 * Made modifications to work with new ebla_data Postgres database instead of Access database via JDBC-ODBC bridge.
 *
 * Revision 1.2  2002/02/05 20:28:03  bpangburn
 * Added additional CVS keywords for id and log to first comment block and added author, revision, & date to second comment block for integration with JavaDoc.
 *
 */