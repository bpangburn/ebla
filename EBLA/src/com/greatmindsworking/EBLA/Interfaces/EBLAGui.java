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
import javax.swing.event.*;
import java.sql.*;
import java.beans.PropertyVetoException;
import com.nqadmin.swingUtils.*;
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

	/*
	 * EBLA menu bar instance.
	 */
	EblaMenuBar menuBar = new EblaMenuBar();
	/*
	 * DataBase connection object
	 */
	DBConnector connector = null;
	/*
	 *Database settings screen object
	 */
	DBSettingsScreen dbSettingsScreen = null;
	/*
	 *EBLA Vision parameter screen object
	 */
	ParameterScreen parameterScreen = null;
	/*
	 *Attributes screen object
	 */
	AttributeScreen attributeScreen = null;
	/*
	 *Experiences screen object
	 */
	ExperienceScreen experienceScreen = null;
	/*
	 *desktop added on to the contentPane of EBLA main frame
	 */
	JDesktopPane desktop = new JDesktopPane();

	/*
	 *File name (including path ) which contains database information
	 */
	public static final String dbFileName = "dbSettings";

	/**
	 *	constructs an EBLA GUI object and tries to login.
	 *If login is successful shows the parameter screen. Login fails if it can't connect to
	 *the database. In which case it informs the same to the user and displays the dbsettings
	 *screen.
	 */
	public EBLAGui() {

		super("Experience Based Language Acquisition v1.0");
		setSize(650, 500);
		setTitle("EBLA");
		// SET THE FRAME VISIBLE
		setVisible(true);
		// ADD MENU BAR TO THE FRAME
		setJMenuBar(menuBar);
		// ADD THE DESKTOP TO THE FRAME
		getContentPane().add(desktop);
		// TRY LOGGING IN
		if(login()){
		 	// do nothing
		}
		// IF LOGIN FAILS SHOW A DIALOG WITH THE SAME MESSAGE AND WHEN HE CLICKS ON OK
		// SHOW THE DBSETTINGS SCREEN
		else{

			int option = JOptionPane.showInternalConfirmDialog(desktop,"Login Failed. \n Try changing the DB Settings","Login Failed",JOptionPane.OK_CANCEL_OPTION);
			if(option == JOptionPane.OK_OPTION){
				showDBSettings();
			}

		}

		setDefaultCloseOperation(EXIT_ON_CLOSE);



	}// END OF CONSTRUCTOR

	/**
	 *  checks if the db configuration file is present and if not shows the db settings screen
	 *for adding the info.
	 *@return returns true if configuration file is present and is readable
	 */
	private boolean getDBSettings(){
		File dbFile = new File(dbFileName);
		// SEE IF DB CONFIG FILE IS PRESENT
		if( !dbFile.canRead()){
			// TELL USER THAT DB INFO NOT PRESENT
			int option = JOptionPane.showInternalConfirmDialog(desktop,"No DB Settings Present.\nPlease provide the DB info","No DB info",JOptionPane.OK_CANCEL_OPTION);
			// IF HE WISHES TO GIVE THE INFO SHOW THE DB SETTINGS SCREEN
			if(option == JOptionPane.OK_OPTION){
				showDBSettings();
				return true;
			}
			return false;
		}
		// IF DB INFO FILE IS PRESENT RETURN TRUE
		return true;
	}

	/**
	 *	login in to the system. login fails if it can't  connect to the database.
	 *@return return true if login sucessfully. else show the db settings screen and returns false
	 */
	private boolean login(){
		System.out.println("Login");
		// GET THE DB SETTINGS IF PRESENT CONNECT TO DB
		// ELSE LOGIN FAILS
		if(getDBSettings()){
			// TRY CONNECTING TO DB IF SUCCESSFUL SHOW THE PARAMETER SCREEN.
			try{
				connector = new DBConnector("dbSettings",false);
				showParameterScreen();
			}catch(IOException ioe){
				return false;
			}catch(Exception e){
				return false;
			}
			// INFORM THE MENU BAR ABOUT SUCCESSFUL LOGIN.
			menuBar.setLogin(true);
			return true;
		}
		return false;
	}

	/**
	 *	disconnects from the database and closes the open screens.
	 *@return returns true on successful logout
	 */
	private boolean logout(){
		System.out.println("Logout");
		// if the connection object is not null close the connection.
		if(connector != null){
			connector.closeConnection();
		}
		// close all the open screens
		try{
			if(parameterScreen != null)
				parameterScreen.setClosed(true);
			if(attributeScreen != null)
				attributeScreen.setClosed(true);
			if(experienceScreen != null)
				experienceScreen.setClosed(true);
		}catch(PropertyVetoException pve){
			pve.printStackTrace();
		}
		// INFORM THE MENU BAR OF SUCCESSFUL LOGOUT.
		menuBar.setLogout(true);
		return true;
	}

	/**
	 *	displays the parameter screen on the desktop.
	 */
	private void showParameterScreen(){
		System.out.println("showPreferencesScreen");
		// IF AN INSTANCE DOES NOT EXIST CREATE ONE AND DISPLAY THAT ON THE DESKTOP.
		if(parameterScreen == null){
			parameterScreen = new ParameterScreen(desktop);
		}
		parameterScreen.showUp(desktop);
	}

	/**
	 *	displays the attribute screen on the desktop
	 */
	private void showAttributesScreen(){
		System.out.println("showAttributesScreen");
		// IF AN INSTANCE DOES NOT EXIST CREATE ONE AND DISPLAY THAT ON THE DESKTOP.
		if(attributeScreen == null){
			attributeScreen = new AttributeScreen(desktop);
		}
		attributeScreen.showUp(desktop);

	}

	/**
	 *	displays the experience screen on the desktop
	 */
	private void showExperiencesScreen(){
		// IF AN INSTANCE DOES NOT EXIST CREATE ONE AND DISPLAY THAT ON THE DESKTOP.
		if(experienceScreen == null){
			experienceScreen = new ExperienceScreen(desktop);
		}
		experienceScreen.showUp(desktop);
	}

	/**
	 *	displays the DB Settings screen on the desktop
	 */
	private void showDBSettings(){
		System.out.println("Show DB Settings");
		// CREATE A FILE WITH THE DB CONFIG FILE NAME
		File file = new File(dbFileName);
		// IF AN INSTANCE DOES NOT EXIST CREATE ONE AND DISPLAY THAT ON THE DESKTOP.
		if(dbSettingsScreen == null ){
			dbSettingsScreen = new DBSettingsScreen(file);
		}
		dbSettingsScreen.showUp(desktop);
		// ADD A LISTENER TO THE SCREEN
		// WHEN THE USER IS DONE ASK HIM TO LOGIN AGAIN
		dbSettingsScreen.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent we){
				JOptionPane.showMessageDialog(desktop,"Please try logging in now.","Try Login",JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}

	/**
	 *	displays the about screen.
	 */
	private void showAboutScreen(){
		System.out.println("about screen");
	}


	/**
	 *	menu bar for the EBLA GUI frame.
	 */
	private class EblaMenuBar extends JMenuBar {

		// MENUS FOR THE EBLA MENU BAR.
		JMenu menuFile = new JMenu("File");
		JMenu menuEdit = new JMenu("Edit");
		JMenu menuUtilities = new JMenu("Utilities");
		JMenu menuReports  = new JMenu("Reports");
		JMenu menuHelp     = new JMenu("Help");

		// MENU ITEMS FOR THE DIFFERENT MENUS
		JMenuItem menuFileLogin  = null;
		JMenuItem menuFileLogout = null;
		JMenuItem menuFileExit = null;
		JMenuItem menuEditExperiences = null;
		JMenuItem menuEditAttributes = null;
		JMenuItem menuEditParameters = null;
		JMenuItem menuUtilitiesDBSettings = null;
		JMenuItem menuHelpAbout = null;

		// INSTANCE OF THE LISTENER FOR THE EBLA MENU ITEMS
		EblaMenuListener menuListener = new EblaMenuListener();

		/**
		 *	disables the login button and enables logout button.
		 */
		public void setLogin(boolean login){
			if(login == true){
				menuFileLogin.setEnabled(false);
				menuFileLogout.setEnabled(true);
			}else{
				menuFileLogin.setEnabled(true);
				menuFileLogout.setEnabled(false);
			}
		}

		/**
		 *	disables the logout button and enables login button.
		 */
		public void setLogout(boolean logout){
			if(logout == true){
				menuFileLogin.setEnabled(true);
				menuFileLogout.setEnabled(false);
			}else{
				menuFileLogin.setEnabled(false);
				menuFileLogout.setEnabled(true);
			}
		}

		/**
		 *	constructs an object of the menu bar.
		 */
		public EblaMenuBar(){

			// add the menus to the menu bar.
			add(menuFile);
			add(menuEdit);
			add(menuUtilities);
			add(menuReports);
			add(menuHelp);

			// ADD THE MENU ITEMS IN THEIR CORRESPONDING  MENUS
			menuFileLogin = menuFile.add("Login");
			menuFileLogout = menuFile.add("Logout");
			menuFileExit   = menuFile.add("Exit");

			menuEditParameters  = menuEdit.add("Parameters");
			menuEditExperiences = menuEdit.add("Experiences");
			menuEditAttributes  = menuEdit.add("Attributes");


			menuUtilitiesDBSettings = menuUtilities.add("DB Settings");

			menuHelpAbout = menuHelp.add("About");

			// ADD LISTENERS  FOR THE MENU ITEMS
			menuFileLogin.addActionListener(menuListener);
			menuFileLogout.addActionListener(menuListener);
			menuFileExit.addActionListener(menuListener);
			menuEditExperiences.addActionListener(menuListener);
			menuEditParameters.addActionListener(menuListener);
			menuEditAttributes.addActionListener(menuListener);
			menuUtilitiesDBSettings.addActionListener(menuListener);
			menuHelpAbout.addActionListener(menuListener);


		}

		/**
		 *	listener class for the menu items.
		 */
		private class EblaMenuListener implements ActionListener {

			public void actionPerformed(ActionEvent ae){
				// get the source item that triggered the event.
				JMenuItem menuItem = (JMenuItem)ae.getSource();
				// CHECK WHICH ITEM HAS BEEN CLICKED AND CALL THE CORRESPONDING FUNCTION
				if(menuItem.equals(menuFileLogin) ){
					if(login()){
						menuFileLogin.setEnabled(false);
						menuFileLogout.setEnabled(true);
					}

				}else if(menuItem.equals(menuFileLogout) ){
					if(logout()){
						menuFileLogin.setEnabled(true);
						menuFileLogout.setEnabled(false);
					}
				}else if(menuItem.equals(menuFileExit) ){
					System.exit(0);
				}else if(menuItem.equals(menuEditParameters) ){
					showParameterScreen();
				}else if(menuItem.equals(menuEditExperiences) ){
					showExperiencesScreen();
				}else if(menuItem.equals(menuEditAttributes) ){
					showAttributesScreen();
				}else if(menuItem.equals(menuUtilitiesDBSettings) ){
					showDBSettings();
				}else if(menuItem.equals(menuHelpAbout)){
					showAboutScreen();
				}

			}//END OF FUNCTION ACTION PERFORMED

		}//END OF LISTENER

	}//END OF EblaMenuBar

	public static void main(String args[]) {
		System.out.println("Starting EBLA...");
		EBLAGui mainFrame = new EBLAGui();

	}


} // end of EBLAGui class



/*
 * $Log$
 */