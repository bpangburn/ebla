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
import java.sql.*;
import java.beans.PropertyVetoException;
import com.sun.rowset.JdbcRowSetImpl;
import com.nqadmin.swingSet.*;
import com.greatmindsworking.EBLA.SessionData;
import com.nqadmin.Utils.DBConnector;



/**
 * SessionScreen.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Screen to display session_data for the EBLA graphical user interface
 * (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SessionScreen extends JInternalFrame {

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR SESSION SCREEN
		Container desktop = null;

	// INITIALIZE ID OF PARENT VISION PARAMETER RECORD
		long parameterID = -1;

	// INITIALIZE DATABASE CONNECTIVITY COMPONENTS FOR SESSION SCREEN
		DBConnector connector = null;
		JdbcRowSetImpl rowset = null;

	// INITIALIZE TABBED PANE TO HOLD SCREEN CONTENTS
		JTabbedPane tabbedPane = new JTabbedPane();

	// INITIALIZE "GENERAL" TAB AND CONTENTS
		EBLAPanel generalPanel   			= new EBLAPanel();

		JTextField txtDescription 			= new JTextField();
//		SSComboBox cmbProcessVideosCode   	= new SSComboBox();
//		SSComboBox cmbProcessEntitiesCode 	= new SSComboBox();
//		SSComboBox cmbProcessLexemesCode  	= new SSComboBox();
		SSComboBox cmbLogToFileCode       	= new SSComboBox();
		SSComboBox cmbRandomizeExpCode    	= new SSComboBox();
		SSComboBox cmbRegenerateImages    	= new SSComboBox();
		SSComboBox cmbDisplayVideosCode 	= new SSComboBox();
		SSComboBox cmbDisplayMessages 		= new SSComboBox();

	// INITIALIZE "ENTITIES" TAB AND CONTENTS
		EBLAPanel entitiesPanel    			= new EBLAPanel();

		JTextField txtMinSDStart   			= new JTextField();
		JTextField txtMinSDStop    			= new JTextField();
		JTextField txtMinSDStep    			= new JTextField();
		JTextField txtLoopCount    			= new JTextField();
		SSComboBox cmbFixedSDCode  			= new SSComboBox();

	// INITIALIZE "LEXEMES" TAB AND CONTENTS
		EBLAPanel lexemesPanel    			= new EBLAPanel();

		JTextField txtDescToGenerate     	= new JTextField();
		SSComboBox cmbCaseSensitiveCode 	= new SSComboBox();

	// INITIALIZE "MISC" TAB AND CONTENTS
		EBLAPanel miscPanel      		= new EBLAPanel();

		JTextArea txtNotes 				= new JTextArea(30,15);

	// INITIALIZE SCREENS CALLED FROM SESSION SCREEN AND CORRESPONDING BUTTONS
		StatusScreen statusScreen = null;
		JButton btnStartEBLA  = new JButton("Start EBLA");

	// INITIALIZE DATA NAVIGATOR
		SSDataNavigator dataNavigator = null;



	/**
	 * SessionScreen constructor.
	 *
	 * @param the container in which the screen has to showup.
	 */
	public SessionScreen(Container _desktop,long _parameterID) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE SESSION SCREEN
			super("EBLA - Session Screen",false,true,true,true);

		// SET SIZE
			setSize(640,480);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

		// SET ID OF PARENT VISION PARAMETER RECORD
			parameterID = _parameterID;

/*
		// DATABASE CONFIGURATION
			try {

			// INITIALIZE DATABASE CONNECTION
				connector = new DBConnector(EBLAGui.dbFileName,true);

			// EXTRACT DATABASE LOGIN INFO FROM DATABASE CONFIG FILE
				BufferedReader bufRead = new BufferedReader(new FileReader(EBLAGui.dbFileName));

				String url = bufRead.readLine();
				if (url == null) {
					url = "";
				}

				String username = bufRead.readLine();
				if (username == null) {
					username = "";
				}

				String password = bufRead.readLine();
				if (password == null) {
					password = "";
				}

			// INITIALIZE ROWSET FOR PARAMETER DATA
				rowset = new JdbcRowSetImpl(url, username, password);

				rowset.setCommand("SELECT * FROM parameter_data WHERE parameter_id>0 ORDER BY description;");
				dataNavigator = new SSDataNavigator(rowset);
				dataNavigator.setDBNav(new SSDBNavImp(getContentPane()));

			} catch(SQLException se) {
				se.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
*/

		// SETUP ACTION LISTENER FOR START EBLA BUTTON
			btnStartEBLA.addActionListener(new StartEBLAListener());


		// SET COMBOBOX ITEMS AND VARIOUS DEFAULTS FOR EACH PANEL
			// "GENERAL" TAB
				cmbLogToFileCode.setOption(SSComboBox.YES_NO_OPTION);
				cmbLogToFileCode.getComboBox().setSelectedIndex(1);

				cmbRandomizeExpCode.setOption(SSComboBox.YES_NO_OPTION);
				cmbRandomizeExpCode.getComboBox().setSelectedIndex(1);

				cmbRegenerateImages.setOption(SSComboBox.YES_NO_OPTION);
				cmbRegenerateImages.getComboBox().setSelectedIndex(0);

				cmbDisplayVideosCode.setOption(SSComboBox.YES_NO_OPTION);
				cmbDisplayVideosCode.getComboBox().setSelectedIndex(1);

				cmbDisplayMessages.setOption(SSComboBox.YES_NO_OPTION);
				cmbDisplayMessages.getComboBox().setSelectedIndex(0);

			// "ENTITIES" TAB
				txtMinSDStart.setText("5");

				txtMinSDStop.setText("15");

				txtMinSDStep.setText("5");

				txtLoopCount.setText("1");

				cmbFixedSDCode.setOption(SSComboBox.YES_NO_OPTION);
				cmbFixedSDCode.getComboBox().setSelectedIndex(0);

			// "LEXEMES" TAB
				txtDescToGenerate.setText("0");

				cmbCaseSensitiveCode.setOption(SSComboBox.YES_NO_OPTION);
				cmbCaseSensitiveCode.getComboBox().setSelectedIndex(0);

			// "MISC" TAB
				// NOTHING TO DO...


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			int currentRow = 0;
			GridBagConstraints constraints = new GridBagConstraints();

		// LAYOUT EACH TAB
			// "GENERAL" TAB
				// SET LAYOUT
					generalPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					generalPanel.addRow(txtDescription, currentRow++, "Description");
					generalPanel.addRow(cmbLogToFileCode.getComboBox(), currentRow++, "Log To File?");
					generalPanel.addRow(cmbRandomizeExpCode.getComboBox(), currentRow++, "Randomize Experiences?");
					generalPanel.addRow(cmbRegenerateImages.getComboBox(), currentRow++, "Regenerate Images?");
					generalPanel.addRow(cmbDisplayVideosCode.getComboBox(), currentRow++, "Display Videos When Ripping?");
					generalPanel.addRow(cmbDisplayMessages.getComboBox(), currentRow++, "Display Detailed Messages?");


			// "ENTITIES" TAB
				// SET LAYOUT
					entitiesPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					entitiesPanel.addRow(txtMinSDStart, currentRow++, "Starting Min. Std. Dev.");
					entitiesPanel.addRow(txtMinSDStop, currentRow++, "Stopping Min. Std. Dev.");
					entitiesPanel.addRow(txtMinSDStep, currentRow++, "Min. Std. Dev. Step Size");
					entitiesPanel.addRow(txtLoopCount, currentRow++, "# Runs for Each Std. Dev.");
					entitiesPanel.addRow(cmbFixedSDCode.getComboBox(), currentRow++, "Limit Actual Std. Dev?");


			// "LEXEMES" TAB
				// SET LAYOUT
					lexemesPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					lexemesPanel.addRow(txtDescToGenerate, currentRow++, "# Descriptions To Generate");
					lexemesPanel.addRow(cmbCaseSensitiveCode.getComboBox(), currentRow++, "Are Lexemes Case Sensitive?");


			// "MISC" TAB
				// SET LAYOUT
					miscPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					miscPanel.addRow(txtNotes, currentRow++, "Notes");


		// ADD TABS TO TABBED PANE
			tabbedPane.addTab("General", generalPanel);
			tabbedPane.addTab("Entity Recognition", entitiesPanel);
			tabbedPane.addTab("Lexeme Generation", lexemesPanel);
			tabbedPane.addTab("Misc", miscPanel);

		// CREATE PANEL FOR BUTTON
			EBLAPanel btnPanel = new EBLAPanel();
			//btnPanel.setLayout(new GridBagLayout());

			//constraints.gridx = 0;
			//constraints.gridy = 0;
			//btnPanel.add(btnStartEBLA,constraints);
			btnPanel.add(btnStartEBLA);

		// ADD TABBED PANE AND BUTTON PANE TO SESSION SCREEN
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());

			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(tabbedPane,constraints);

			constraints.gridy = 1;
			contentPane.add(btnPanel,constraints);

	} // end of SessionScreen constructor



	/**
	 * Listener to launch create a Session object and launch the EBLA controller/status screen.
	 */
	private class StartEBLAListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			// EXTRACT "GENERAL" TAB WIDGET VALUES
				String desc = txtDescription.getText();

				boolean boolLogToFile = false;
				if (cmbLogToFileCode.getComboBox().getSelectedIndex() == 1) {
					boolLogToFile = true;
				}

				boolean boolRandomizeExp = false;
				if (cmbRandomizeExpCode.getComboBox().getSelectedIndex() == 1) {
					boolRandomizeExp = true;
				}

				boolean boolRegenerateImages = false;
				if (cmbRegenerateImages.getComboBox().getSelectedIndex() == 1) {
					boolRegenerateImages = true;
				}

				boolean boolDisplayVideos = false;
				if (cmbDisplayVideosCode.getComboBox().getSelectedIndex() == 1) {
					boolDisplayVideos = true;
				}

				boolean boolDisplayMessages = false;
				if (cmbDisplayMessages.getComboBox().getSelectedIndex() == 1) {
					boolDisplayMessages = true;
				}


			// EXTRACT "ENTITY" TAB WIDGET VALUES
				int minSDStart = 5;
				if (! txtMinSDStart.getText().equals("")) {
					minSDStart = Integer.parseInt(txtMinSDStart.getText());
				}

				int minSDStop = 15;
				if (! txtMinSDStop.getText().equals("")) {
					minSDStop = Integer.parseInt(txtMinSDStop.getText());
				}

				int minSDStep = 5;
				if (! txtMinSDStep.getText().equals("")) {
					minSDStep = Integer.parseInt(txtMinSDStep.getText());
				}

				int loopCount = 1;
				if (! txtLoopCount.getText().equals("")) {
					loopCount = Integer.parseInt(txtLoopCount.getText());
				}

				boolean boolFixedSD = false;
				if (cmbFixedSDCode.getComboBox().getSelectedIndex() == 1) {
					boolFixedSD = true;
				}


			// EXTRACT "LEXEME" TAB WIDGET VALUES
				int descToGenerate = 0;
				if (! txtDescToGenerate.getText().equals("")) {
					descToGenerate = Integer.parseInt(txtDescToGenerate.getText());
				}

				boolean boolCaseSensitive = false;
				if (cmbCaseSensitiveCode.getComboBox().getSelectedIndex() == 1) {
					boolCaseSensitive = true;
				}


			// EXTRACT "MISC" TAB WIDGET VALUES
				String notes = txtNotes.getText();


			// INITIALIZE EBLA DATABASE CONNECTION
				try {
					connector = new DBConnector(EBLAGui.dbFileName, true);
				} catch(Exception e) {
					e.printStackTrace();
				}

			// CREATE EBLA SESSION
				SessionData sd = new SessionData(connector,parameterID,desc,boolRegenerateImages,boolLogToFile,
					boolRandomizeExp, descToGenerate, minSDStart, minSDStop, minSDStep, loopCount, boolFixedSD,
					boolDisplayVideos, boolDisplayMessages, boolCaseSensitive, notes);

			// CREATE STATUS SCREEN IF IT DOESN'T YET EXIST
				if (statusScreen == null) {
					statusScreen = new StatusScreen(desktop, sd, connector);
				}

			// DISPLAY STATUS SCREEN
				statusScreen.showUp(desktop);

		} // end actionPerformed() method

	} // end StartEBLAListener class



	/**
	 * Adds the session screen to the specified container at the specified position.
	 *
	 * @param the container in which the screen has to showup.
	 * @param the x co-ordinate of the position where the screen has to showup.
	 * @param the y co-ordinate of the position where the screen has to showup.
	 */
	public void showUp(Container container,double positionX, double positionY){

		// SET THE POSITION OF THE SCREEN.
			this.setLocation((int)positionX, (int)positionY);

		// IF THE USER WANTS TO ADD A RECORD OR IF THERE ARE RECORDS IN DB SHOW THE SCREEN
			Component[] components = container.getComponents();
			int i=0;
			for(i=0; i< components.length;i++){
				if(components[i] instanceof SessionScreen ) {
					System.out.println("Already on desktop");
					break;
				}
			}

		// IF IT IS NOT THERE ADD THE SCREEN TO THE CONTAINER
			if(i == components.length) {
				container.add(this);
			}

		// MAKE SCREEN VISIBLE, MOVE TO FRONT, & REQUEST FOCUS
			this.setVisible(true);
			this.moveToFront();
			this.requestFocus();

		// MAKE THE SCREEN SELECTED SCREEN
			try{
				this.setClosed(false);
				this.setSelected(true);
			} catch(PropertyVetoException pve) {
				pve.printStackTrace();
			}

	} // end showUp()



	/**
	 * Shows the session screen at the default location on the specified container.
	 *
	 * @param the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	} // end showUp()


} // end of SessionScreen class



/*
 * $Log$
 * Revision 1.3  2003/12/26 20:31:31  yoda2
 * General code cleanup and addition of JavaDoc.  Reflected renaming of Session.java to SessionData.java.
 *
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */