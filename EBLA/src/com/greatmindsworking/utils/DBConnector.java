/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003 - 2005, Brian E. Pangburn
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



package com.greatmindsworking.utils;



import java.sql.*;
import java.io.*;
import com.nqadmin.swingSet.datasources.*;



/**
 * DBConnector.java
 *
 * Connects to database using the JDBC.
 *
 * @author	$Author$
 * @version	$Revision$
 */

public class DBConnector {
	  
	/**
	 * constant string containing database driver
	 */
	private final static String jdbcDriver = "org.postgresql.Driver";

	/**
	 * constant string containing path to database 
	 */
    private static String eblaPath = "jdbc:postgresql://pgserver.greatmindsworking.com:5432/ebla_data";

	/**
	 * constant string containing database username
	 */
    private static String eblaUser = "eblauser";

    /**
	 * constant string containing database password
	 */
    private static String eblaPass = "guest";
    	
    /**
     * string containing database driver
     */
    private String driver = "";
    
    /**
     * string containing database path
     */
    private static String dbPath = "";

    /**
     * string containing database username
     */
    private static String username = "";

    /**
     * string containing database password
     */
    private static String password = "";

	/**
	 * connection to the "server" database file
	 */
	private SSConnection dbConnection;

	/**
	 * boolean to indicate whether to auto-commit database changes
	 */
	private boolean autoCommit = false;



	/**
	 * Class constructor that calls appropriate methods to initialize database connection
	 *
	 * @param _dbFile       String to specify the path of file that has database path, username and password
	 * @param _autoCommit	boolean to indicate whether to auto-commit database changes
	 */
    public DBConnector(String _dbFile, boolean _autoCommit) throws IOException {

		 this(new FileReader(_dbFile), _autoCommit);
		 System.out.println(dbPath);

	} // end DBConnector()

	/**
	 * Class constructor that calls appropriate methods to initialize database connection
	 *
	 * @param _dbFile       String to specify the path of file that has database path, username and password
	 * @param _autoCommit	boolean to indicate whether to auto-commit database changes
	 */
    public DBConnector(FileReader _dbFile, boolean _autoCommit) throws IOException {

		try {

			// READ CONNECTION INFO FROM FILE 
				if (!getDBInformation(_dbFile)) {
					throw new IOException("Failed to read input file : " + _dbFile);
				}

			// INITIALIZE DATABASE
				autoCommit = _autoCommit;
				initializeDatabase();

		} catch (Exception e) {
			System.out.println("\n--- DBConnector Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end DBConnector()

	/**
	 * Class constructor that calls appropriate methods to initialize database connection
	 *
	 * @param _autoCommit	boolean to indicate whether to auto-commit database changes
     * @param _dbPath   path to database
     * @param _user database username
     * @param _pass database password
	 */
	public DBConnector(boolean _autoCommit, String _dbPath, String _user, String _pass) {

		try {

			// INITIALIZE DATABASE
				dbPath    = _dbPath;
				username  = _user;
				password  = _pass;
				autoCommit = _autoCommit;
				initializeDatabase();

		} catch (Exception e) {
			System.out.println("\n--- DBConnector Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end DBConnector()
	
	/**
	 * Class constructor that calls appropriate methods to initialize database connection
	 *
	 * @param _autoCommit	boolean to indicate whether to auto-commit database changes
	 */
	public DBConnector(boolean _autoCommit) {
		
		try {
            dbPath = eblaPath;
            driver = jdbcDriver;
            username = eblaUser;
            password = eblaPass;
    
            autoCommit = _autoCommit;
            
            initializeDatabase();

		} catch (Exception e) {
			System.out.println("\n--- DBConnector Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end DBConnector()
	
	/**
	*	Collects the information regarding the database from the text file
	*	@param fileReader a file reader from the database path, username and password can be read
	*	@return returns true if file is read successfully else false
	*/
	private boolean getDBInformation(FileReader _file) {

		try {

			BufferedReader buf = new BufferedReader(_file);
			dbPath = buf.readLine();
			username = buf.readLine();
			password = buf.readLine();
			return true;

		} catch(IOException ioe) {
			System.out.println(" DBConnection Exception while reading input file");
			return false;
		}
	}

	/**
	 * Initialize connection to database
	 */
	private void initializeDatabase() {

		try {
			
			if (driver == null) {
				driver = jdbcDriver;
			}

            dbConnection = new SSConnection(dbPath, username, password);
            dbConnection.setDriverName(driver);
            dbConnection.createConnection();				

            if (autoCommit) {
                dbConnection.getConnection().setAutoCommit(true);
            } else {
                dbConnection.getConnection().setAutoCommit(false);
            }

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.initializeDatabase() Exception ---\n");
			e.printStackTrace();
		}

	} // end initializeDatabase()

	/**
	 * Returns a Connection to the database
	 *
	 * @return a Connection to the database
	 */
    public Connection getConnection(){
		return dbConnection.getConnection();
	}
	
	/**
	 * Returns a SSConnection to the database
	 *
	 * @return a SwingSet SSConnection to the database
	 */
    public SSConnection getSSConnection(){
		return dbConnection;
	}	

	/**
	 * Returns a statement for running queries, deletes, & updates against the database
	 *
	 * @return a new statement for the database connection
	 */
	public Statement getStatement() {

		// DECLARATIONS
			Statement myState = null;			// STATEMENT FOR CURRENT DB CONNECTION

		try {

			myState = dbConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

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
				dbConnection.getConnection().commit();

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.commitChanges() Exception ---\n");
			e.printStackTrace();
		}

	} // end commitChanges()


    /**
     * Rollback changes made after previous commit.
     */
	public void rollbackChanges() {

		try {

			// ROLLBACK CHANGES
			dbConnection.getConnection().rollback();

		} catch (Exception e) {
			System.out.println("\n ------DBConnector.rollbackChanges() Exception------\n");
			e.printStackTrace();
		}

	} //end of rollbackChanges()


	/**
	 * Close connection to database
	 */
	public void closeConnection() {

		try {

			// CLOSE CONNECTION
				dbConnection.getConnection().close();

		} catch (Exception e) {
			System.out.println("\n--- DBConnector.closeConnection() Exception ---\n");
			e.printStackTrace();
		}

	} // end closeConnection()

    /**
     * Method to load database connection info from a text file.
     *
     * @param _fileName name of file containing database connection info
     */
	public static boolean setGasServerInfo(String _fileName) {
        
		try {
            
			BufferedReader buf = new BufferedReader(new FileReader(new File(_fileName)));
			eblaPath = buf.readLine();
			eblaUser = buf.readLine();
			eblaPass = buf.readLine();
			
			return true;

		} catch(IOException ioe) {
			System.out.println(" DBConnection Exception while reading input file");
			return false;
		}
	}

    /**
     * Method to load database connection info from an input stream.
     *
     * @param _is   name of input stream containing database connection info
     */    
	public static boolean setGasServerInfo(InputStream _is) {
        
		try {

			BufferedReader  buf = new BufferedReader(new InputStreamReader(_is));
			eblaPath = buf.readLine();
			eblaUser = buf.readLine();
			eblaPass = buf.readLine();
			
			return true;

		} catch(IOException ioe) {
			System.out.println(" DBConnection Exception while reading input file");
			return false;
		}
	}

} // end DBConnector class

/*
 * $Log$
 */