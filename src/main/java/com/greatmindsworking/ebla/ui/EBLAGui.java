/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, Brian E. Pangburn & Prasanth R. Pasala
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



package com.greatmindsworking.ebla.ui;



import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.JTextComponent;

import com.greatmindsworking.utils.DBConnector;



/**
 * EBLAGui.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * EBLA graphical user interface (GUI) main screen. This screen acts like a
 * place holder for all the other screens and the menu bar.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class EBLAGui extends JFrame {


	/**
	 * serial version ID
	 */
	private static final long serialVersionUID = 2914270816554999394L;

	/**
	 * EBLA version string
	 */
	private final static String eblaVersion = "version 1.0";
	
	/**
	 * EBLA Copyright string
	 */
	private final static String eblaCopyright = "\u00A9 Brian E. Pangburn 2002-2014";
		
	/**
	 * EBLA default H2 connection string
	 */
	private final static String defaultH2Connection = "jdbc:h2:~/ebla";
	
	/**
	 * EBLA default H2 username string
	 */
	private final static String defaultH2Username = "sa";
	
	/**
	 * EBLA default H2 password string
	 */
	private final static String defaultH2Password = "";	
	
	/**
	 * debugging option
	 */
	boolean guiDebug = false;

	/**
	 * EBLA menu bar instance
	 */
	EBLAMenuBar menuBar = new EBLAMenuBar();

	/**
	 * database connector
	 */
	DBConnector dbc = null;

	/**
	 * database settings screen object
	 */
	DBSettingsScreen dbSettingsScreen = null;

	/**
	 * EBLA vision parameters screen object
	 */
	ParameterScreen parameterScreen = null;

	/**
	 * EBLA perceptual attributes screen object
	 */
	AttributeScreen attributeScreen = null;

	/**
	 * EBLA experiences screen object
	 */
	ExperienceScreen experienceScreen = null;

	/**
	 * desktop added to the content pane of the EBLA application frame
	 */
	JDesktopPane desktop = new JDesktopPane();

	/**
	 * file name (including path) containing the database connection information
	 */
	public static final String dbFileName = "dbSettings";
	
	/**
	 * EBLA H2 connection string
	 */
	String h2Connection = "jdbc:h2:~/ebla";
	
	/**
	 * EBLA H2 username string
	 */
	String h2Username = "sa";
	
	/**
	 * EBLA H2 password string
	 */
	String h2Password = "";		



	/**
	 * Constructs the application window for EBLA and attempts to connect to the database.
	 *
	 * If the database login is successful, it shows the vision parameter screen. Otherwise
	 * it informs the user and displays the database settings screen.
	 */
	public EBLAGui() {

		// SET APPLICATION TITLE BAR TEXT
			super("Experience Based Language Acquisition -- " + eblaVersion);

		// SET DIMENSIONS
			setSize(800, 600);

		// SET APPLICATION NAME
			setTitle("Experience Based Language Acquisition -- " + eblaVersion);

		// MAKE THE FRAME VISIBLE
			setVisible(true);

		// ADD MENU BAR TO THE FRAME
			setJMenuBar(menuBar);

		// ADD THE DESKTOP TO THE FRAME
			getContentPane().add(desktop);

		// TRY LOGGING IN AND OPEN THE PARAMETER SCREEN IF SUCCESSFUL
			if (login()) {
				menuBar.setLogin(true);
				showParameterScreen();
			}

		// FORCE EBLA TO END IF APPLICATION WINDOW IS CLOSED
			setDefaultCloseOperation(EXIT_ON_CLOSE);

	} // end EBLAGui constructor



	/**
	 * Logs into the database. If the login succeeds, show the vision parameter screen.
	 * Otherwise, show the database settings screen and return false.
     *
     * @return return true if login successfully
	 */
	boolean login() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Login");
			}
			
		// ATTEMPT TO READ DATABASE CONFIGURATION FILE
			try {
				// CREATE BUFFERED READER TO READ IN DATABASE CONNECTION INFO FROM FILE
				// (IF AVAILABLE)
					BufferedReader bufRead = new BufferedReader(new FileReader(dbFileName));
					
				// READ CONNECTION STRING
					h2Connection = bufRead.readLine();

				// READ USERNAME
					h2Username = bufRead.readLine();

				// READ PASSWORD
					h2Password = bufRead.readLine();
					
				// CLOSE BUFFERED READER
					bufRead.close();

			} catch(Exception e) {
				// let user know that no config file was found so going with defaults
				JOptionPane.showInternalConfirmDialog
						(desktop,"Database config file not found or not formatted correctly. Using system default database values.",
						"No Configuration File",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				h2Connection = defaultH2Connection;
				h2Username = defaultH2Username;
				h2Password = defaultH2Password;
			}				
			
			
		// TRY CONNECTING TO THE DATABASE
			try {
				dbc = new DBConnector(true, h2Connection, h2Username, h2Password);
			} catch(Exception e) {
				// let user know that no config file was found so going with defaults
				JOptionPane.showInternalConfirmDialog
						(desktop,"Unable to connect to database. Please check configuration settings then Login again.",
						"Database Connection Error",JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				showDBSettings();
				return false;
			}
			
		// CHECK FOR parameter_data TABLE
		// IF NOT FOUND, LOAD DEFAULT STRUCTURE
		// WHEN QUERYING META DATA, NEED TO USE CAPS FOR TABLE NAME
			try {
				ResultSet r1 = dbc.getConnection().getMetaData().getTables(null, null, "PARAMETER_DATA", null);
				if (r1.next()) {
					ResultSet r2 = dbc.getStatement().executeQuery("SELECT COUNT(*) AS count FROM parameter_data;");
					r2.next();
					if (r2.getInt("count")==0) {
					// PROMPT USER TO LOAD SAMPLE DATASET
						int promptResponse =  JOptionPane.showInternalConfirmDialog
							(desktop,"The EBLA database structure exists, but it does not contain any records. Would you like to load the sample dataset?",
							"No Records Found",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	
						if (promptResponse == JOptionPane.YES_OPTION) {
							loadSampleData(dbc, parameterScreen);
						}
					}
					//System.out.println("# records: " + r2.getInt("count"));
					r2.close();
	
				} else {
				// INFORM USER THAT DATABASE
					JOptionPane.showInternalConfirmDialog
						(desktop,"The EBLA database does not contain any tables so the table structure will be loaded now.",
						"No Tables Found",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);	
					loadSQL(dbc);
					
				// PROMPT USER TO LOAD SAMPLE DATASET
					int promptResponse =  JOptionPane.showInternalConfirmDialog
						(desktop,"The EBLA database structure exists, but it does not contain any records. Would you like to load the sample dataset?",
						"No Records Found",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	
					if (promptResponse == JOptionPane.YES_OPTION) {
						loadSampleData(dbc, parameterScreen);
					}
				}
				r1.close();
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}		

		// INDICATE SUCCESS
			return true;

	} // end login()



	/**
	 * Disconnects from the database and closes any open screens.
	 *
	 * @return returns true on successful logout
	 */
	boolean logout() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Logout");
			}

		// CLOSE ALL THE OPEN SCREENS
			try {
				if (parameterScreen != null) {
					parameterScreen.setClosed(true);
					parameterScreen = null;
				}
				if (attributeScreen != null) {
					attributeScreen.setClosed(true);
					attributeScreen = null;
				}
				if (experienceScreen != null) {
					experienceScreen.setClosed(true);
					experienceScreen = null;
				}

			} catch(PropertyVetoException pve) {
				pve.printStackTrace();
			}

		// IF THE CONNECTION OBJECT IS NOT NULL CLOSE THE CONNECTION.
			if (dbc != null) {
				dbc.closeConnection();
				dbc = null;
			}

		// RETURN
			return true;

	} // end logout()



	/**
	 *	displays the vision parameter screen in the application window
	 */
	void showParameterScreen() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Parameters Screen");
			}

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP
			if (parameterScreen == null) {
				parameterScreen = new ParameterScreen(desktop, dbc);

				parameterScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
					@Override
					public void internalFrameClosed(InternalFrameEvent ife) {
						parameterScreen = null;
					} // end internalFrameClosed()

				});
			}

		// MAKE SCREEN VISIBLE
			parameterScreen.showUp(desktop);

	} // end showParameterScreen()



	/**
	 *	displays the attribute screen in the application window
	 */
	void showAttributesScreen() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Attributes Screen");
			}

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP.
			if (attributeScreen == null) {
				attributeScreen = new AttributeScreen(desktop, dbc);

				attributeScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
					@Override
					public void internalFrameClosed(InternalFrameEvent ife) {
						attributeScreen = null;
					} // end internalFrameClosed()

				});
			}

		// MAKE SCREEN VISIBLE
			attributeScreen.showUp(desktop);

	} // end showAttributesScreen()



	/**
	 *	displays the experience screen in the application window
	 */
	void showExperiencesScreen() {
		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Experiences Screen");
			}

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP.
			if (experienceScreen == null) {
				experienceScreen = new ExperienceScreen(desktop, dbc);

				experienceScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
					@Override
					public void internalFrameClosed(InternalFrameEvent ife) {
						experienceScreen = null;
					} // end internalFrameClosed()

				});
			}

		// MAKE SCREEN VISIBLE
			experienceScreen.showUp(desktop);

	} // end showExperiencesScreen()



	/**
	 *	displays the database settings screen in the application window
	 */
	void showDBSettings() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Database Settings Screen");
			}

		// CREATE A FILE WITH THE DB CONFIG FILE NAME
		//	File file = new File(dbFileName);

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP.
			if (dbSettingsScreen == null) {
				dbSettingsScreen = new DBSettingsScreen(desktop, dbFileName, h2Connection, h2Username, h2Password);

				dbSettingsScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
					@Override
					public void internalFrameClosed(InternalFrameEvent ife) {
						dbSettingsScreen = null;
					} // end internalFrameClosed()

				});
			}

		// MAKE SCREEN VISIBLE
			dbSettingsScreen.showUp(desktop);

	} // end showDBSettings()
	
	
	/**
	 * method to create database tables for EBLA
	 */
	static void loadSQL(DBConnector _dbc) {
		
		try {
			_dbc.getStatement().execute("RUNSCRIPT FROM './database/ebla_data_H2.sql';");
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	
	/**
	 * method to populate database tables for EBLA with sample dataset
	 */
	static void loadSampleData(DBConnector _dbc, ParameterScreen _ps) {
		
		try {
			_dbc.getStatement().execute("RUNSCRIPT FROM './database/ebla_sample_H2.sql';");
			if (_ps!=null) {
				_ps.dataNavigator.getSSRowSet().execute();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}



	/**
	 *	displays information about EBLA reports
	 */
	void showReports() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Reports Screen");
			}

		// GENERATE REPORTS DIALOG
			JOptionPane.showInternalMessageDialog(desktop,"Reporting has not yet been integrated into EBLA."
				+ "\n\nAll of the calculation results can be found in the following files in the EBLA installation directory:"
				+ "\n   'session_###_performance.ssv'"
				+ "\n   'session_###_mappings.ssv'"
				+ "\n   'session_###_descriptions.ssv'"
				+ "\nSee the EBLA ReadMe for additional details.",
				"Experience Based Language Acquisition",JOptionPane.INFORMATION_MESSAGE);

	} // end showReports()



	/**
	 *	displays the EBLA about screen.
	 */
	void showAboutScreen() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show About Screen");
			}

		// GENERATE EBLA ABOUT DIALOG
			JOptionPane.showInternalMessageDialog(desktop,"EBLA -- " + eblaVersion
				+ "\n" + eblaCopyright
				+ "\n<html><a href=http:\\www.greatmindsworking.com>www.greatmindsworking.com</a></html>",
					"Experience Based Language Acquisition",JOptionPane.INFORMATION_MESSAGE);

	} // end showAboutScreen()



	/**
	 *	displays the EBLA ReadMe file.
	 */
	void showReadMe() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show EBLA ReadMe File");
			}

		// LOAD EBLA README INTO A NEW JINTERNALFRAME
			JInternalFrame readme = new JInternalFrame("EBLA - ReadMe File",false,true,true,true);
			readme.setSize(640, 480);

			JTextComponent textpane = new JTextArea();
			JScrollPane pane = new JScrollPane(textpane);
			pane.setPreferredSize(new Dimension(600, 400));

			try {
				FileReader fr = new FileReader("readme.txt");
				textpane.read(fr, null);
				fr.close();
			}
			catch (IOException e) {
				System.out.println(e);
			}

			readme.getContentPane().add(pane);
            desktop.add(readme);
            readme.setVisible(true);
			readme.moveToFront();
			readme.requestFocus();

	} // end showReadMe()



	/**
	 *	menu bar for the EBLA GUI application window
	 */
	private class EBLAMenuBar extends JMenuBar {

		private static final long serialVersionUID = -8156953510977685939L;
		
		// MENUS FOR THE EBLA MENU BAR.
			JMenu menuFile = new JMenu("File");
			JMenu menuEdit = new JMenu("Edit");
			JMenu menuUtilities = new JMenu("Utilities");
			JMenu menuReports = new JMenu("Reports");
			JMenu menuHelp = new JMenu("Help");

		// MENU ITEMS FOR THE DIFFERENT MENUS
			JMenuItem menuFileLogin = null;
			JMenuItem menuFileLogout = null;
			JMenuItem menuFileExit = null;
			JMenuItem menuEditExperiences = null;
			JMenuItem menuEditAttributes = null;
			JMenuItem menuEditParameters = null;
			JMenuItem menuUtilitiesDBSettings = null;
			JMenuItem menuUtilitiesDBLoadSQL = null;
			JMenuItem menuUtilitiesDBLoadSampleData = null;
			JMenuItem menuReportsDataFiles = null;
			JMenuItem menuHelpAbout = null;
			JMenuItem menuHelpReadMe = null;

		// INSTANCE OF THE LISTENER FOR THE EBLA MENU ITEMS
			EBLAMenuListener menuListener = new EBLAMenuListener();

		// DISABLES THE LOGIN BUTTON AND ENABLES LOGOUT BUTTON.
			public void setLogin(boolean login) {
				if (login == true) {
					menuFileLogin.setEnabled(false);
					menuFileLogout.setEnabled(true);
					menuEditExperiences.setEnabled(true);
					menuEditAttributes.setEnabled(true);
					menuEditParameters.setEnabled(true);
					menuUtilitiesDBSettings.setEnabled(false);
					menuUtilitiesDBLoadSQL.setEnabled(true);
					menuUtilitiesDBLoadSampleData.setEnabled(true);
					
				} else {
					menuFileLogin.setEnabled(true);
					menuFileLogout.setEnabled(false);
					menuEditExperiences.setEnabled(false);
					menuEditAttributes.setEnabled(false);
					menuEditParameters.setEnabled(false);
					menuUtilitiesDBSettings.setEnabled(true);
					menuUtilitiesDBLoadSQL.setEnabled(false);
					menuUtilitiesDBLoadSampleData.setEnabled(false);
				}
			} // end setLogin()

		// DISABLES THE LOGOUT BUTTON AND ENABLES LOGIN BUTTON.
			public void setLogout(boolean logout){
				setLogin(!logout);
			} // end setLogout()

		// CONSTRUCTS AN OBJECT OF THE MENU BAR.
			public EBLAMenuBar(){

				// ADD THE MENUS TO THE MENU BAR.
					add(menuFile);
					add(menuEdit);
					add(menuUtilities);
					add(menuReports);
					add(menuHelp);

				// ADD THE MENU ITEMS IN THEIR CORRESPONDING  MENUS
					menuFileLogin = menuFile.add("Login");
					menuFileLogout = menuFile.add("Logout");
					menuFileExit = menuFile.add("Exit");

					menuEditParameters = menuEdit.add("Parameters");
					menuEditExperiences = menuEdit.add("Experiences");
					menuEditAttributes = menuEdit.add("Attributes");

					menuUtilitiesDBSettings = menuUtilities.add("Database Connection Settings");
					menuUtilitiesDBLoadSQL = menuUtilities.add("Create Database Structure");
					menuUtilitiesDBLoadSampleData = menuUtilities.add("Load Sample Dataset");
					
					menuReportsDataFiles = menuReports.add("Data Files");

					menuHelpAbout = menuHelp.add("About");
					menuHelpReadMe = menuHelp.add("ReadMe");

				// ADD LISTENERS  FOR THE MENU ITEMS
					menuFileLogin.addActionListener(menuListener);
					menuFileLogout.addActionListener(menuListener);
					menuFileExit.addActionListener(menuListener);
					menuEditExperiences.addActionListener(menuListener);
					menuEditParameters.addActionListener(menuListener);
					menuEditAttributes.addActionListener(menuListener);
					menuUtilitiesDBSettings.addActionListener(menuListener);
					menuUtilitiesDBLoadSQL.addActionListener(menuListener);
					menuUtilitiesDBLoadSampleData.addActionListener(menuListener);
					menuReportsDataFiles.addActionListener(menuListener);
					menuHelpAbout.addActionListener(menuListener);
					menuHelpReadMe.addActionListener(menuListener);

			} // end EBLAMenuBar constructor

		// LISTENER CLASS FOR THE MENU ITEMS.
			private class EBLAMenuListener implements ActionListener {
				
				// EMPTY CONSTRUCTOR
				EBLAMenuListener() {}

				@Override
				public void actionPerformed(ActionEvent ae){

					// GET THE SOURCE ITEM THAT TRIGGERED THE EVENT.
						JMenuItem menuItem = (JMenuItem)ae.getSource();

					// CHECK WHICH ITEM HAS BEEN CLICKED AND CALL THE CORRESPONDING FUNCTION
						if (menuItem.equals(menuFileLogin)) {
							if (login()){
								setLogin(true);
								showParameterScreen();
							}

						} else if (menuItem.equals(menuFileLogout)) {
							if(logout()){
								setLogout(true);
							}
						} else if (menuItem.equals(menuFileExit)) {
							System.exit(0);
						} else if (menuItem.equals(menuEditParameters)) {
							showParameterScreen();
						} else if (menuItem.equals(menuEditExperiences)) {
							showExperiencesScreen();
						} else if (menuItem.equals(menuEditAttributes)) {
							showAttributesScreen();
						} else if (menuItem.equals(menuUtilitiesDBSettings)) {
							showDBSettings();
						} else if (menuItem.equals(menuUtilitiesDBLoadSQL)) {
							loadSQL(dbc);
						} else if (menuItem.equals(menuUtilitiesDBLoadSampleData)) {
							int promptResponse =  JOptionPane.showInternalConfirmDialog
								(desktop,"Loading the sample dataset will overwrite any data already in the EBLA database. Are you sure?",
								"Confirm Dataset Load",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	
							if (promptResponse == JOptionPane.YES_OPTION) {
								loadSampleData(dbc, parameterScreen);
							}							
						} else if (menuItem.equals(menuReportsDataFiles)) {
							showReports();
						} else if (menuItem.equals(menuHelpAbout)) {
							showAboutScreen();
						} else if (menuItem.equals(menuHelpReadMe)) {
							showReadMe();
						}

				} // end actionPerformed()

			} // end EBLAMenuListener()

	} // end EBLAMenuBar class



	/**
	 * allows instantation of EBLAGui from command line
	 */
	public static void main(String args[]) {

		System.out.println("Starting EBLA...");

		@SuppressWarnings("unused")
		EBLAGui mainFrame = new EBLAGui();

	} // end main()



} // end of EBLAGui class



/*
 * $Log$
 * Revision 1.18  2014/12/19 23:23:32  yoda2
 * Cleanup of misc compiler warnings. Made EDISON GFunction an abstract class.
 *
 * Revision 1.17  2011/04/29 19:56:41  yoda2
 * Added ability to create EBLA H2 tables & load sample dataset from EBLA GUI.
 *
 * Revision 1.16  2011/04/28 14:55:07  yoda2
 * Addressing Java 1.6 -Xlint warnings.
 *
 * Revision 1.15  2011/04/25 03:52:10  yoda2
 * Fixing compiler warnings for Generics, etc.
 *
 * Revision 1.14  2005/02/17 23:33:45  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.13  2004/02/25 21:58:59  yoda2
 * Updated copyright notice & version (0.7.0-alpha).
 *
 * Revision 1.12  2004/01/13 18:33:22  yoda2
 * Fixed dialog behavior when database settings are missing.
 *
 * Revision 1.11  2004/01/13 17:12:13  yoda2
 * Changed database behavior to autocommit changes.
 *
 * Revision 1.10  2004/01/09 14:22:31  yoda2
 * Modified screens to use a single database connection.
 *
 * Revision 1.9  2003/12/31 21:17:39  yoda2
 * Added menu items to display a popup under "Reports" and the readme file under "Help"
 *
 * Revision 1.8  2003/12/30 23:21:20  yoda2
 * Modified screens so that they are nullifed upon closing and a "fresh" screen is created if a screen is re-opened.
 *
 * Revision 1.7  2003/12/29 23:20:56  yoda2
 * Added "About" dialog box.
 *
 * Revision 1.6  2003/12/26 20:30:13  yoda2
 * Removed unnecessary import statements.
 *
 * Revision 1.5  2003/12/24 19:14:00  yoda2
 * Small JavaDoc fixes, added version to titlebar, and set application size to 800x600.
 *
 * Revision 1.4  2003/12/02 03:53:22  yoda2
 * Misc. code formatting changes.
 *
 * Revision 1.3  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.2  2003/09/23 03:01:30  yoda2
 * Code cleanup & documentation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */