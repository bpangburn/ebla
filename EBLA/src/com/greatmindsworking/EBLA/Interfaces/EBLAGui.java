/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003, Brian E. Pangburn & Prasanth R. Pasala
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



package com.greatmindsworking.EBLA.Interfaces;



import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.beans.PropertyVetoException;
import com.nqadmin.Utils.DBConnector;



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
	 * EBLA version string
	 */
	private final static String eblaVersion = "version 0.6.0-alpha";

	/**
	 * debugging option
	 */
	boolean guiDebug = false;

	/**
	 * EBLA menu bar instance
	 */
	EBLAMenuBar menuBar = new EBLAMenuBar();

	/**
	 * database connection object
	 */
	DBConnector connector = null;

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
			setTitle("Experience Based Language Acquistion -- " + eblaVersion);

		// MAKE THE FRAME VISIBLE
			setVisible(true);

		// ADD MENU BAR TO THE FRAME
			setJMenuBar(menuBar);

		// ADD THE DESKTOP TO THE FRAME
			getContentPane().add(desktop);

		// TRY LOGGING IN
			if (!login()) {
			// IF LOGIN FAILS SHOW A DIALOG WARNING USER AND THEN SHOW THE DATABASE SETTINGS SCREEN
				int option = JOptionPane.showInternalConfirmDialog
					(desktop,"Login failed. \n Try changing the database settings.",
					"Login failed",JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					showDBSettings();
				}
			}

		// FORCE EBLA TO END IF APPLICATION WINDOW IS CLOSED
			setDefaultCloseOperation(EXIT_ON_CLOSE);

	} // end EBLAGui constructor



	/**
	 * Determines if the database configuration file is present.  If not, the database
	 * settings screen is displayed.
	 *
	 * @return returns true if configuration file is present and is readable
	 */
	private boolean getDBSettings() {

	  	// INITIALIZE FILE
			File dbFile = new File(dbFileName);

		// DETERMINE IF DATABASE CONFIG FILE IS PRESENT
			if (!dbFile.canRead()) {
				// TELL USER THAT DB INFO NOT PRESENT
					int option = JOptionPane.showInternalConfirmDialog
						(desktop,"No database settings found.\nPlease provide the database connection information.",
						"No Database Settings",JOptionPane.OK_CANCEL_OPTION);

				// IF USER WISHES TO ENTER THE INFO, SHOW THE DATABASE SETTINGS SCREEN
					if (option == JOptionPane.OK_OPTION) {
						showDBSettings();
						return true;
					}

				return false;
			}

		// IF DATABASE CONFIG FILE IS PRESENT, RETURN TRUE
			return true;

	} // end getDBSettings()



	/**
	 * Logs into the database. If the login succeeds, show the vision parameter screen.
	 * Otherwise, show the database settings screen and return false.
     *
     * @return return true if login sucessfully
	 */
	private boolean login() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Login");
			}

		// RETRIEVE THE DATABASE CONFIG FILE
			if (getDBSettings()) {
				// TRY CONNECTING TO THE DATABASE. IF SUCCESSFUL SHOW THE PARAMETER SCREEN.
					try {
						connector = new DBConnector("dbSettings",false);
						showParameterScreen();
					} catch(IOException ioe) {
						return false;
					} catch(Exception e) {
						return false;
					}

				// UPDATE THE MENUBAR TO REFLECT SUCCESSFUL LOGIN
					menuBar.setLogin(true);

				// RETURN
					return true;
			}

		// RETURN FALSE IF getDBSetting FAILS
			return false;

	} // end login()



	/**
	 * Disconnects from the database and closes any open screens.
	 *
	 * @return returns true on successful logout
	 */
	private boolean logout() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Logout");
			}

		// IF THE CONNECTION OBJECT IS NOT NULL CLOSE THE CONNECTION.
			if (connector != null) {
				connector.closeConnection();
				connector = null;
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

		// UPDATE THE MENUBAR TO REFLECT SUCCESSFUL LOGOUT
			menuBar.setLogout(true);

		// RETURN
			return true;

	} // end logout()



	/**
	 *	displays the vision parameter screen in the application window
	 */
	private void showParameterScreen() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Parameters Screen");
			}

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP
			if (parameterScreen == null) {
				parameterScreen = new ParameterScreen(desktop);

				parameterScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
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
	private void showAttributesScreen() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Attributes Screen");
			}

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP.
			if (attributeScreen == null) {
				attributeScreen = new AttributeScreen(desktop);

				attributeScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
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
	private void showExperiencesScreen() {
		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Experiences Screen");
			}

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP.
			if (experienceScreen == null) {
				experienceScreen = new ExperienceScreen(desktop);

				experienceScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
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
	private void showDBSettings() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show Database Settings Screen");
			}

		// CREATE A FILE WITH THE DB CONFIG FILE NAME
			File file = new File(dbFileName);

		// IF AN INSTANCE DOES NOT EXIST, CREATE ONE AND DISPLAY IT ON THE DESKTOP.
			if (dbSettingsScreen == null) {
				dbSettingsScreen = new DBSettingsScreen(desktop, file);

				dbSettingsScreen.addInternalFrameListener(new InternalFrameAdapter() {
				// FRAME CLOSED
					public void internalFrameClosed(InternalFrameEvent ife) {
						dbSettingsScreen = null;
					} // end internalFrameClosed()

				});
			}

		// MAKE SCREEN VISIBLE
			dbSettingsScreen.showUp(desktop);

	} // end showDBSettings()



	/**
	 *	displays information about EBLA reports
	 */
	private void showReports() {

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
	private void showAboutScreen() {

		// DEBUG INFO
			if (guiDebug) {
				System.out.println("Show About Screen");
			}

		// GENERATE EBLA ABOUT DIALOG
			JOptionPane.showInternalMessageDialog(desktop,"EBLA -- " + eblaVersion
				+ "\n\u00A9 Brian E. Pangburn 2002-2004"
				+ "\n<html><a href=http:\\www.greatmindsworking.com>www.greatmindsworking.com</a></html>",
					"Experience Based Language Acquisition",JOptionPane.INFORMATION_MESSAGE);

	} // end showAboutScreen()



	/**
	 *	displays the EBLA ReadMe file.
	 */
	private void showReadMe() {

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
				} else {
					menuFileLogin.setEnabled(true);
					menuFileLogout.setEnabled(false);
				}
			} // end setLogin()

		// DISABLES THE LOGOUT BUTTON AND ENABLES LOGIN BUTTON.
			public void setLogout(boolean logout){
				if (logout == true) {
					menuFileLogin.setEnabled(true);
					menuFileLogout.setEnabled(false);
				} else {
					menuFileLogin.setEnabled(false);
					menuFileLogout.setEnabled(true);
				}
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

					menuUtilitiesDBSettings = menuUtilities.add("Database Settings");

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
					menuReportsDataFiles.addActionListener(menuListener);
					menuHelpAbout.addActionListener(menuListener);
					menuHelpReadMe.addActionListener(menuListener);

			} // end EBLAMenuBar constructor

		// LISTENER CLASS FOR THE MENU ITEMS.
			private class EBLAMenuListener implements ActionListener {

				public void actionPerformed(ActionEvent ae){

					// GET THE SOURCE ITEM THAT TRIGGERED THE EVENT.
						JMenuItem menuItem = (JMenuItem)ae.getSource();

					// CHECK WHICH ITEM HAS BEEN CLICKED AND CALL THE CORRESPONDING FUNCTION
						if (menuItem.equals(menuFileLogin)) {
							if (login()){
								menuFileLogin.setEnabled(false);
								menuFileLogout.setEnabled(true);
							}

						} else if (menuItem.equals(menuFileLogout)) {
							if(logout()){
								menuFileLogin.setEnabled(true);
								menuFileLogout.setEnabled(false);
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

		EBLAGui mainFrame = new EBLAGui();

	} // end main()



} // end of EBLAGui class



/*
 * $Log$
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