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
import javax.swing.border.*;
import java.sql.*;
import java.beans.PropertyVetoException;
import com.sun.rowset.JdbcRowSetImpl;
import com.nqadmin.swingSet.*;
import com.nqadmin.Utils.DBConnector;



/**
 * ParameterScreen.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Screen to display parameter_data for the EBLA graphical user interface
 * (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class ParameterScreen extends JInternalFrame {

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR PARAMETER SCREEN
		Container desktop = null;

	// INITIALIZE DATABASE CONNECTIVITY COMPONENTS FOR PARAMETER SCREEN
		DBConnector connector = null;
		JdbcRowSetImpl rowset = null;

	// INITIALIZE TABBED PANE TO HOLD SCREEN CONTENTS
		JTabbedPane tabbedPane = new JTabbedPane();

	// INITIALIZE "GENERAL" TAB AND CONTENTS
		EBLAPanel generalPanel   		= new EBLAPanel();

		JTextField txtParameterID 		= new JTextField();
		JTextField txtDescription 		= new JTextField();

	// INITIALIZE "VISION" TAB AND CONTENTS
		EBLAPanel visionPanel    		= new EBLAPanel();

		JTextField txtSegColorRadius 	= new JTextField();
		JTextField txtSegSpatialRadius 	= new JTextField();
		JTextField txtSegMinRegion 		= new JTextField();
		SSComboBox cmbSegSpeedUpCode 	= new SSComboBox();
		JTextField txtBackGroundPixels 	= new JTextField();
		JTextField txtMinPixelCount 	= new JTextField();
		JTextField txtMinFrameCount 	= new JTextField();
		SSComboBox cmbReduceColorCode 	= new SSComboBox();

	// INITIALIZE "RESULTS" TAB AND CONTENTS
		EBLAPanel resultsPanel   		= new EBLAPanel();

		JTextField txtTmpPath 			= new JTextField();
		JTextField txtFramePrefix 		= new JTextField();
		JTextField txtSegPrefix 		= new JTextField();
		JTextField txtPolyPrefix 		= new JTextField();

	// INITIALIZE "MISC" TAB AND CONTENTS
		EBLAPanel miscPanel      		= new EBLAPanel();

		JTextArea txtNotes 				= new JTextArea(30,15);

	// INITIALIZE SCREENS CALLED FROM PARAMETER SCREEN AND CORRESPONDING BUTTONS
		SelectExperiencesScreen selectExperiencesScreen = null;
		JButton btnSelectExperiences = new JButton("Select Experiences");
		SessionScreen sessionScreen = null;
		JButton btnStartSession = new JButton("Start Session");

	// INITIALIZE DATA NAVIGATOR
		SSDataNavigator dataNavigator = null;



	/**
	 * ParameterScreen constructor.
	 *
	 * @param the container in which the screen has to showup.
	 */
	public ParameterScreen(Container _desktop) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE PARAMETER SCREEN
			super("EBLA - Parameter Screen",false,true,true,true);

		// SET SIZE
			setSize(640,480);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

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


		// SETUP ACTION LISTENER FOR SELECT EXPERIENCES BUTTON
			btnSelectExperiences.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					long parameterID = -1;
					if (! txtParameterID.getText().trim().equals("")) {
						try {
							parameterID = Long.parseLong(txtParameterID.getText());
						} catch(NumberFormatException nfe) {
							nfe.printStackTrace();
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					if (selectExperiencesScreen == null) {
						selectExperiencesScreen = new SelectExperiencesScreen(desktop, parameterID);

						selectExperiencesScreen.addInternalFrameListener(new InternalFrameAdapter() {
						// FRAME CLOSED
							public void internalFrameClosed(InternalFrameEvent ife) {
								selectExperiencesScreen = null;
							} // end internalFrameClosed()

						});

					} else {
						selectExperiencesScreen.setParameterID(parameterID);
					}
					selectExperiencesScreen.showUp(desktop);
				}
			});


		// SETUP ACTION LISTENER FOR START SESSION BUTTON
			btnStartSession.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					long parameterID = -1;
					parameterID = txtParameterID.getText().equals("") ? 0 : Long.parseLong(txtParameterID.getText());

					if (sessionScreen == null) {
						sessionScreen = new SessionScreen(desktop, parameterID);

						sessionScreen.addInternalFrameListener(new InternalFrameAdapter() {
						// FRAME CLOSED
							public void internalFrameClosed(InternalFrameEvent ife) {
								sessionScreen = null;
							} // end internalFrameClosed()

						});
					}
					sessionScreen.showUp(desktop);
				}
			});


		// SET DATABASE COLUMNS FOR EACH PANEL'S WIDGETS ALONG WITH ANY COMBOBOX ITEMS
			// "GENERAL" TAB
				txtParameterID.setDocument(new SSTextDocument(rowset,"parameter_id"));

				txtDescription.setDocument(new SSTextDocument(rowset,"description"));

			// "VISION" TAB
				txtSegColorRadius.setDocument(new SSTextDocument(rowset,"seg_color_radius"));

				txtSegSpatialRadius.setDocument(new SSTextDocument(rowset,"seg_spatial_radius"));

				txtSegMinRegion.setDocument(new SSTextDocument(rowset,"seg_min_region"));

				String[] tmpString = {"None", "Medium", "High"};
				cmbSegSpeedUpCode.setOption(tmpString);
				cmbSegSpeedUpCode.setDocument(new SSTextDocument(rowset,"seg_speed_up_code"));

				txtBackGroundPixels.setDocument(new SSTextDocument(rowset,"background_pixels"));

				txtMinPixelCount.setDocument(new SSTextDocument(rowset,"min_pixel_count"));

				txtMinFrameCount.setDocument(new SSTextDocument(rowset,"min_frame_count"));

				cmbReduceColorCode.setOption(SSComboBox.YES_NO_OPTION);
				cmbReduceColorCode.setDocument(new SSTextDocument(rowset,"reduce_color_code"));

			// "RESULTS" TAB
				txtTmpPath.setDocument(new SSTextDocument(rowset,"tmp_path"));

				txtFramePrefix.setDocument(new SSTextDocument(rowset,"frame_prefix"));

				txtSegPrefix.setDocument(new SSTextDocument(rowset,"seg_prefix"));

				txtPolyPrefix.setDocument(new SSTextDocument(rowset,"poly_prefix"));

			// "MISC" TAB
				txtNotes.setDocument(new SSTextDocument(rowset,"notes"));


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

					generalPanel.addRow(txtParameterID, currentRow++, "Parameter ID");
					generalPanel.addRow(txtDescription, currentRow++, "Description");


			// "VISION" TAB
				// SET LAYOUT
					visionPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					visionPanel.addRow(txtSegColorRadius, currentRow++, "Segmentation Color Radius");
					visionPanel.addRow(txtSegSpatialRadius, currentRow++, "Segmentation Spatial Radius");
					visionPanel.addRow(txtSegMinRegion, currentRow++, "Segmentation Minimum Region");
					visionPanel.addRow(cmbSegSpeedUpCode.getComboBox(), currentRow++, "Segmentation Speed Up");
					visionPanel.addRow(txtBackGroundPixels, currentRow++, "Background Pixel Threshold");
					visionPanel.addRow(txtMinPixelCount, currentRow++, "Min Pixels for Object");
					visionPanel.addRow(txtMinFrameCount, currentRow++, "Min Frames for Object");
					visionPanel.addRow(cmbReduceColorCode.getComboBox(), currentRow++, "Reduce Color Depth?");


			// "RESULTS" TAB
				// SET LAYOUT
					resultsPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					resultsPanel.addRow(txtTmpPath, currentRow++, "Intermediate Results Path");
					resultsPanel.addRow(txtFramePrefix, currentRow++, "Frame Image File Prefix");
					resultsPanel.addRow(txtSegPrefix, currentRow++, "Segmented Image File Prefix");
					resultsPanel.addRow(txtPolyPrefix, currentRow++, "Polygon Image File Prefix");


			// "MISC" TAB
				// SET LAYOUT
					miscPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					miscPanel.addRow(txtNotes, currentRow++, "Notes");


		// ADD TABS TO TABBED PANE
			tabbedPane.addTab("General", generalPanel);
			tabbedPane.addTab("Vision System", visionPanel);
			tabbedPane.addTab("Intermediate Results", resultsPanel);
			tabbedPane.addTab("Misc", miscPanel);

		// CREATE PANEL FOR BUTTONS
			EBLAPanel buttonPanel = new EBLAPanel();
			buttonPanel.setBorder(emptySpace);
			//buttonPanel.setLayout(new GridBagLayout());

			constraints.gridx = 0;
			constraints.gridy = 0;
			buttonPanel.add(btnStartSession,constraints);

			constraints.gridx = 1;
			buttonPanel.add(btnSelectExperiences,constraints);

		// ADD TABBED PANE, DATA NAVIGATOR AND BUTTON PANE TO PARAMETER SCREEN
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());

			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(tabbedPane,constraints);

			constraints.gridy = 1;
			contentPane.add(dataNavigator,constraints);

			constraints.gridy = 2;
			contentPane.add(buttonPanel,constraints);

	} // end of ParameterScreen constructor



	/**
	 * Adds the parameter screen to the specified container at the specified position.
	 *
	 * @param the container in which the screen has to showup.
	 * @param the x co-ordinate of the position where the screen has to showup.
	 * @param the y co-ordinate of the position where the screen has to showup.
	 */
	public void showUp(Container container,double positionX, double positionY) {

		// SET THE POSITION OF THE SCREEN.
			this.setLocation((int)positionX, (int)positionY);

		// IF THE USER WANTS TO ADD A RECORD OR IF THERE ARE RECORDS IN DB SHOW THE SCREEN
			Component[] components = container.getComponents();
			int i=0;
			for (i=0; i< components.length;i++) {
				if (components[i] instanceof ParameterScreen ) {
					System.out.println("Already on desktop");
					break;
				}
			}

		// IF IT IS NOT THERE ADD THE SCREEN TO THE CONTAINER
			if (i == components.length) {
				container.add(this);
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
	 * Shows the parameter screen at the default location on the specified container.
	 *
	 * @param the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	} // end showUp()


} // end of ParameterScreen class



/*
 * $Log$
 * Revision 1.7  2003/12/29 23:20:27  yoda2
 * Added close button.
 *
 * Revision 1.6  2003/12/26 20:30:13  yoda2
 * Removed unnecessary import statements.
 *
 * Revision 1.5  2003/12/24 19:15:52  yoda2
 * General clean up.  Added JavaDoc and removed explicit coding of labels in favor of automatic labels via EBLAPanel.addRow().
 *
 * Revision 1.4  2003/12/23 23:18:47  yoda2
 * Continued code cleanup.
 * Discovered that both PreferredSize and MinimumSize must be set to generate consistent widget widths across all tabs on a given screen.
 * Added addRow() method to EBLAPanel to auto-add labels to widgets on a given panel.
 *
 * Revision 1.3  2003/12/04 04:47:11  yoda2
 * Started code cleanup/documentation.
 *
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */





