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



package com.greatmindsworking.EBLA.Interfaces;



import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.greatmindsworking.EBLA.SessionData;
import com.greatmindsworking.utils.DBConnector;
import com.nqadmin.swingSet.SSComboBox;
import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.SSTextArea;
import com.nqadmin.swingSet.SSTextField;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;



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
		DBConnector dbc = null;
		SSJdbcRowSetImpl rowset = null;

	// INITIALIZE TABBED PANE TO HOLD SCREEN CONTENTS
		JTabbedPane tabbedPane = new JTabbedPane();

	// INITIALIZE "GENERAL" TAB AND CONTENTS
		EBLAPanel generalPanel   			= new EBLAPanel();

		SSTextField txtDescription 			= new SSTextField();
		SSComboBox cmbLogToFileCode       	= new SSComboBox();
		SSComboBox cmbRandomizeExpCode    	= new SSComboBox();
		SSComboBox cmbRegenerateImages    	= new SSComboBox();
		//SSComboBox cmbDisplayVideosCode 	= new SSComboBox();
		SSComboBox cmbDisplayMessages 		= new SSComboBox();

	// INITIALIZE "ENTITIES" TAB AND CONTENTS
		EBLAPanel entitiesPanel    			= new EBLAPanel();

		SSTextField txtMinSDStart   			= new SSTextField();
		SSTextField txtMinSDStop    			= new SSTextField();
		SSTextField txtMinSDStep    			= new SSTextField();
		SSTextField txtLoopCount    			= new SSTextField();
		SSComboBox cmbFixedSDCode  			= new SSComboBox();

	// INITIALIZE "LEXEMES" TAB AND CONTENTS
		EBLAPanel lexemesPanel    			= new EBLAPanel();

		SSTextField txtDescToGenerate     	= new SSTextField();
		SSComboBox cmbCaseSensitiveCode 	= new SSComboBox();

	// INITIALIZE "MISC" TAB AND CONTENTS
		EBLAPanel miscPanel      		= new EBLAPanel();

		SSTextArea txtNotes 				= new SSTextArea(30,15);

	// INITIALIZE SCREENS CALLED FROM SESSION SCREEN AND CORRESPONDING BUTTONS
		StatusScreen statusScreen = null;
		JButton btnStartEBLA = new JButton("Start EBLA");
		JButton btnClose = new JButton("Close");


	// INITIALIZE DATA NAVIGATOR
		SSDataNavigator dataNavigator = null;



	/**
	 * SessionScreen constructor.
	 *
	 * @param _desktop    the container in which the screen has to showup.
	 * @param _dbc    connection to ebla_data database
	 */
	public SessionScreen(Container _desktop, DBConnector _dbc, long _parameterID, String _parameterDesc) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE SESSION SCREEN
			super("EBLA - Session Screen",false,true,true,true);

		// SET SIZE
			setSize(640,480);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

		// SET DATABASE CONNECTION
			dbc = _dbc;

		// SET ID OF PARENT VISION PARAMETER RECORD
			parameterID = _parameterID;

		// SETUP ACTION LISTENER FOR START EBLA BUTTON
			btnStartEBLA.addActionListener(new StartEBLAListener());

		// ADD ACTION LISTENER TO "CLOSE" BUTTON
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
				// CLOSE WINDOW
					try {
						setClosed(true);
					} catch(PropertyVetoException pve) {
						pve.printStackTrace();
					}
				} // end actionPerformed()
			});


		// SET COMBOBOX ITEMS AND VARIOUS DEFAULTS FOR EACH PANEL
			// "GENERAL" TAB
				java.util.Date now = new java.util.Date();
				txtDescription.setText(_parameterDesc + " - " + now);

				cmbLogToFileCode.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
				cmbLogToFileCode.setSelectedIndex(1);

				cmbRandomizeExpCode.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
				cmbRandomizeExpCode.setSelectedIndex(1);

				cmbRegenerateImages.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
				cmbRegenerateImages.setSelectedIndex(0);

				//cmbDisplayVideosCode.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
				//cmbDisplayVideosCode.setSelectedIndex(1);

				cmbDisplayMessages.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
				cmbDisplayMessages.setSelectedIndex(0);

			// "ENTITIES" TAB
				txtMinSDStart.setText("5");

				txtMinSDStop.setText("15");

				txtMinSDStep.setText("5");

				txtLoopCount.setText("1");

				cmbFixedSDCode.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
				cmbFixedSDCode.setSelectedIndex(0);

			// "LEXEMES" TAB
				txtDescToGenerate.setText("0");

				cmbCaseSensitiveCode.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
				cmbCaseSensitiveCode.setSelectedIndex(0);

			// "MISC" TAB
				// NOTHING TO DO...


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			int currentRow = 0;
			GridBagConstraints constraints = new GridBagConstraints();
			Border emptySpace = BorderFactory.createEmptyBorder(0, 0, 10, 0);

		// LAYOUT EACH TAB
			// "GENERAL" TAB
				// SET LAYOUT
					generalPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					generalPanel.addRow(txtDescription, currentRow++, "Description");
					generalPanel.addRow(cmbLogToFileCode, currentRow++, "Log To File?");
					generalPanel.addRow(cmbRandomizeExpCode, currentRow++, "Randomize Experiences?");
					generalPanel.addRow(cmbRegenerateImages, currentRow++, "Regenerate Images?");
					//generalPanel.addRow(cmbDisplayVideosCode, currentRow++, "Display Videos When Ripping?");
					generalPanel.addRow(cmbDisplayMessages, currentRow++, "Display Detailed Messages?");


			// "ENTITIES" TAB
				// SET LAYOUT
					entitiesPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					entitiesPanel.addRow(txtMinSDStart, currentRow++, "Starting Min. Std. Dev.");
					entitiesPanel.addRow(txtMinSDStop, currentRow++, "Stopping Min. Std. Dev.");
					entitiesPanel.addRow(txtMinSDStep, currentRow++, "Min. Std. Dev. Step Size");
					entitiesPanel.addRow(txtLoopCount, currentRow++, "# Runs for Each Std. Dev.");
					entitiesPanel.addRow(cmbFixedSDCode, currentRow++, "Limit Actual Std. Dev?");


			// "LEXEMES" TAB
				// SET LAYOUT
					lexemesPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					lexemesPanel.addRow(txtDescToGenerate, currentRow++, "# Descriptions To Generate");
					lexemesPanel.addRow(cmbCaseSensitiveCode, currentRow++, "Are Lexemes Case Sensitive?");


			// "MISC" TAB
				// SET LAYOUT
					miscPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					miscPanel.addRow(txtNotes, currentRow++, "Notes");


		// ADD TABS TO TABBED PANE
			tabbedPane.addTab("General", generalPanel);
			tabbedPane.addTab("Entity Recognition", entitiesPanel);
			tabbedPane.addTab("Lexical Analysis and Generation", lexemesPanel);
			tabbedPane.addTab("Misc.", miscPanel);


		// CREATE PANEL FOR BUTTON
			EBLAPanel buttonPanel = new EBLAPanel();
			buttonPanel.setBorder(emptySpace);

			constraints.gridx = 0;
			constraints.gridy = 0;
			buttonPanel.add(btnStartEBLA, constraints);

			constraints.gridx = 1;
			buttonPanel.add(btnClose, constraints);


		// ADD TABBED PANE AND BUTTON PANE TO SESSION SCREEN
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());

			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(tabbedPane,constraints);

			constraints.gridy = 1;
			contentPane.add(buttonPanel,constraints);

	} // end of SessionScreen constructor



	/**
	 * Listener to launch create a Session object and launch the EBLA controller/status screen.
	 */
	private class StartEBLAListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			// EXTRACT "GENERAL" TAB WIDGET VALUES
				String desc = txtDescription.getText();

				boolean boolLogToFile = false;
				if (cmbLogToFileCode.getSelectedIndex() == 1) {
					boolLogToFile = true;
				}

				boolean boolRandomizeExp = false;
				if (cmbRandomizeExpCode.getSelectedIndex() == 1) {
					boolRandomizeExp = true;
				}

				boolean boolRegenerateImages = false;
				if (cmbRegenerateImages.getSelectedIndex() == 1) {
					boolRegenerateImages = true;
				}

				//boolean boolDisplayVideos = false;
				//if (cmbDisplayVideosCode.getSelectedIndex() == 1) {
				//	boolDisplayVideos = true;
				//}
				boolean boolDisplayVideos = true;

				boolean boolDisplayMessages = false;
				if (cmbDisplayMessages.getSelectedIndex() == 1) {
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
				if (cmbFixedSDCode.getSelectedIndex() == 1) {
					boolFixedSD = true;
				}


			// EXTRACT "LEXEME" TAB WIDGET VALUES
				int descToGenerate = 0;
				if (! txtDescToGenerate.getText().equals("")) {
					descToGenerate = Integer.parseInt(txtDescToGenerate.getText());
				}

				boolean boolCaseSensitive = false;
				if (cmbCaseSensitiveCode.getSelectedIndex() == 1) {
					boolCaseSensitive = true;
				}


			// EXTRACT "MISC" TAB WIDGET VALUES
				String notes = txtNotes.getText();


			// CREATE EBLA SESSION
				SessionData sd = new SessionData(dbc, parameterID,desc,boolRegenerateImages,boolLogToFile,
					boolRandomizeExp, descToGenerate, minSDStart, minSDStop, minSDStep, loopCount, boolFixedSD,
					boolDisplayVideos, boolDisplayMessages, boolCaseSensitive, notes);

			// CREATE STATUS SCREEN IF IT DOESN'T YET EXIST
				if (statusScreen == null) {
					statusScreen = new StatusScreen(desktop, sd, dbc);

					statusScreen.addInternalFrameListener(new InternalFrameAdapter() {
					// FRAME CLOSED
						public void internalFrameClosed(InternalFrameEvent ife) {
							statusScreen = null;
						} // end internalFrameClosed()

					});
				}

			// DISPLAY STATUS SCREEN
				statusScreen.showUp(desktop);

		} // end actionPerformed() method

	} // end StartEBLAListener class



	/**
	 * Adds the session screen to the specified container at the specified position.
	 *
	 * @param _container    the container in which the screen has to showup.
	 * @param _positionX    the x coordinate of the position where the screen has to showup.
	 * @param _positionY    the y coordinate of the position where the screen has to showup.
	 */
	public void showUp(Container _container, double _positionX, double _positionY) {

		// SET THE POSITION OF THE SCREEN.
			this.setLocation((int)_positionX, (int)_positionY);

		// IF THE USER WANTS TO ADD A RECORD OR IF THERE ARE RECORDS IN DB SHOW THE SCREEN
			Component[] components = _container.getComponents();
			int i=0;
			for (i=0; i< components.length;i++) {
				if (components[i] instanceof SessionScreen) {
					System.out.println("Already on desktop");
					break;
				}
			}

		// IF IT IS NOT THERE ADD THE SCREEN TO THE CONTAINER
			if (i == components.length) {
				_container.add(this);
			}

		// MAKE SCREEN VISIBLE, MOVE TO FRONT, & REQUEST FOCUS
			this.setVisible(true);
			this.moveToFront();
			this.requestFocus();

		// MAKE THE SCREEN SELECTED SCREEN
			try {
				this.setClosed(false);
				this.setSelected(true);
			} catch(PropertyVetoException pve) {
				pve.printStackTrace();
			}

	} // end showUp()



	/**
	 * Shows the session screen at the default location on the specified container.
	 *
	 * @param _container    the container in which the screen has to showup.
	 */
	public void showUp(Container _container) {
		showUp(_container, 30,30);
	} // end showUp()

} // end of SessionScreen class



/*
 * $Log$
 * Revision 1.16  2005/02/17 23:33:45  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.15  2005/02/16 02:36:06  yoda2
 * Began updating EBLA GUI to work with SwingSet 1.0 RC.
 *
 * Revision 1.14  2004/02/25 21:58:39  yoda2
 * Updated copyright notice.
 *
 * Revision 1.13  2004/01/10 04:08:54  yoda2
 * Renamed the "Lexeme Generation" tab "Lexical Analysis and Generation"
 *
 * Revision 1.12  2004/01/09 18:56:35  yoda2
 * Small eyewash changes to labels/tabs.
 *
 * Revision 1.11  2004/01/09 14:22:31  yoda2
 * Modified screens to use a single database connection.
 *
 * Revision 1.10  2003/12/31 19:44:26  yoda2
 * Added logic to set a default session description based on the parameter description and current date/time.
 *
 * Revision 1.9  2003/12/31 19:38:08  yoda2
 * Fixed various thread synchronization issues.
 *
 * Revision 1.8  2003/12/31 16:21:35  yoda2
 * Removed display video option - doesn't apply to GUI as frames are always displayed during rip.
 *
 * Revision 1.7  2003/12/31 15:47:40  yoda2
 * Removed old database synchronization code and unused fields.
 *
 * Revision 1.6  2003/12/30 23:21:20  yoda2
 * Modified screens so that they are nullifed upon closing and a "fresh" screen is created if a screen is re-opened.
 *
 * Revision 1.5  2003/12/29 23:20:27  yoda2
 * Added close button.
 *
 * Revision 1.4  2003/12/26 22:15:19  yoda2
 * Fixed typo on widget label.
 *
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