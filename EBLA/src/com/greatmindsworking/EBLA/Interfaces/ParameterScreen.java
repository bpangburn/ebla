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

		//JLabel lblParameterID			= new JLabel("Paramter ID");
		JTextField txtParameterID 		= new JTextField();

		//JLabel lblDescription 			= new JLabel("Description");
		JTextField txtDescription 		= new JTextField();

	// INITIALIZE "ENTITIES" TAB AND CONTENTS
		EBLAPanel entitiesPanel  		= new EBLAPanel();

	// INITIALIZE "LEXEMES" TAB AND CONTENTS
		EBLAPanel lexemesPanel   		= new EBLAPanel();

	// INITIALIZE "VISION PARAMETERS" TAB AND CONTENTS
		EBLAPanel visionPanel    		= new EBLAPanel();

		JLabel lblSegColorRadius  		= new JLabel("Segmentation Color Radius");
		JTextField txtSegColorRadius 	= new JTextField();

		JLabel lblSegSpatialRadius  	= new JLabel("Segmentation Spatial Radius");
		JTextField txtSegSpatialRadius 	= new JTextField();

		JLabel lblSegMinRegion  		= new JLabel("Segmentation Minimum Region");
		JTextField txtSegMinRegion 		= new JTextField();

		JLabel lblSegSpeedUpCode  		= new JLabel("Segmentation Speed Up");
		SSComboBox cmbSegSpeedUpCode 	= new SSComboBox();

		JLabel lblBackGroundPixels  	= new JLabel("Background Pixel Threshold");
		JTextField txtBackGroundPixels 	= new JTextField();

		JLabel lblMinPixelCount  		= new JLabel("Min Pixels for Object");
		JTextField txtMinPixelCount 	= new JTextField();

		JLabel lblMinFrameCount  		= new JLabel("Min Frames for Object");
		JTextField txtMinFrameCount 	= new JTextField();

		JLabel lblReduceColorCode  		= new JLabel("Reduce Color Depth?");
		SSComboBox cmbReduceColorCode 	= new SSComboBox();

	// INITIALIZE "RESULTS" TAB AND CONTENTS
		EBLAPanel resultsPanel   		= new EBLAPanel();

		JLabel lblTmpPath  				= new JLabel("Intermediate Results Path");
		JTextField txtTmpPath 			= new JTextField();

		JLabel lblFramePrefix 			= new JLabel("Frame Image File Prefix");
		JTextField txtFramePrefix 		= new JTextField();

		JLabel lblSegPrefix 			= new JLabel("Segmented Image File Prefix");
		JTextField txtSegPrefix 		= new JTextField();

		JLabel lblPolyPrefix  			= new JLabel("Polygon Image File Prefix");
		JTextField txtPolyPrefix 		= new JTextField();

	// INITIALIZE "MISC" TAB AND CONTENTS
		EBLAPanel miscPanel      		= new EBLAPanel();

		JLabel lblNotes  				= new JLabel("Notes");
		JTextArea txtNotes 				= new JTextArea(30,15);

	// INITIALIZE SCREENS CALLED FROM PARAMETER SCREEN AND CORRESPONDING BUTTONS
		SelectExperiencesScreen selectExperiencesScreen = null;
		JButton btnSelectExperiences = new JButton("Select\nExperiences");
		SessionScreen sessionScreen = null;
		JButton btnSetSession = new JButton("Start Session");

	// INITIALIZE DATA NAVIGATOR
		SSDataNavigator dataNavigator = null;


	public ParameterScreen(Container _desktop) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE VISION PARAMETER SCREEN
			super("EBLA Vision Parameter Form",false,true,true,true);

		// SET SIZE
			setSize(650,450);

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
						} catch(NumberFormatException nfe){
							nfe.printStackTrace();
						} catch(Exception e){
							e.printStackTrace();
						}
					}
					if (selectExperiencesScreen == null) {
						selectExperiencesScreen = new SelectExperiencesScreen(parameterID);
					} else {
						selectExperiencesScreen.setParameterID(parameterID);
					}
					selectExperiencesScreen.showUp(desktop);
				}
			});

		// SETUP ACTION LISTENER FOR START SESSION BUTTON
			btnSetSession.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					long parameterID = -1;
					parameterID = txtParameterID.getText().equals("") ? 0 : Long.parseLong(txtParameterID.getText());

					if(sessionScreen == null) {
						sessionScreen = new SessionScreen(desktop,parameterID);
					}
					sessionScreen.showUp(desktop);
				}
			});


		// SET DATABASE COLUMNS FOR EACH PANEL'S WIDGETS ALONG WITH ANY COMBOBOX ITEMS
			// "GENERAL" TAB
				txtParameterID.setDocument(new SSTextDocument(rowset,"parameter_id"));

				txtDescription.setDocument(new SSTextDocument(rowset,"description"));

			// "VISION PARAMETERS" TAB
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

		// LAYOUT EACH TAB
			// CREATE ROW COUNTER
				int currentRow = 0;

			// "GENERAL" TAB
				// SET LAYOUT
					generalPanel.setLayout(new GridBagLayout());

				// ADD WIDGETS
					currentRow=0;

					generalPanel.addRow(txtParameterID, currentRow++, "Parameter ID");
					generalPanel.addRow(txtDescription, currentRow++, "Description");


			// "VISION PARAMETERS" TAB



			// "RESULTS" TAB



			// "MISC" TAB





		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());


		entitiesPanel.setLayout(new GridBagLayout());
		lexemesPanel.setLayout(new GridBagLayout());
		visionPanel.setLayout(new GridBagLayout());
		resultsPanel.setLayout(new GridBagLayout());
		miscPanel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridwidth = 1;

		constraints.gridx =0;
		constraints.gridy =0;
		visionPanel.add(lblSegColorRadius,constraints);
		constraints.gridy =1;
		visionPanel.add(lblSegSpatialRadius,constraints);
		constraints.gridy =2;
		visionPanel.add(lblSegMinRegion,constraints);
		constraints.gridy =3;
		visionPanel.add(lblSegSpeedUpCode,constraints);
		constraints.gridy =4;
		visionPanel.add(lblBackGroundPixels,constraints);
		constraints.gridy =5;
		visionPanel.add(lblMinPixelCount,constraints);
		constraints.gridy =6;
		visionPanel.add(lblMinFrameCount,constraints);
		constraints.gridy =7;
		visionPanel.add(lblReduceColorCode,constraints);

		constraints.gridx =1;
		constraints.gridy =0;
		visionPanel.add(txtSegColorRadius,constraints);
		constraints.gridy =1;
		visionPanel.add(txtSegSpatialRadius,constraints);
		constraints.gridy =2;
		visionPanel.add(txtSegMinRegion,constraints);
		constraints.gridy =3;
		visionPanel.add(cmbSegSpeedUpCode.getComboBox(),constraints);
		constraints.gridy =4;
		visionPanel.add(txtBackGroundPixels,constraints);
		constraints.gridy =5;
		visionPanel.add(txtMinPixelCount,constraints);
		constraints.gridy =6;
		visionPanel.add(txtMinFrameCount,constraints);
		constraints.gridy =7;
		visionPanel.add(cmbReduceColorCode.getComboBox(),constraints);

		constraints.gridx =0;
		constraints.gridy =0;
		resultsPanel.add(lblTmpPath,constraints);
		constraints.gridy =1;
		constraints.gridy =2;
		constraints.gridy =3;
		constraints.gridy =4;
		resultsPanel.add(lblFramePrefix,constraints);
		constraints.gridy =5;
		resultsPanel.add(lblSegPrefix,constraints);
		constraints.gridy =6;
		resultsPanel.add(lblPolyPrefix,constraints);

		constraints.gridx =1;
		constraints.gridy =0;
		resultsPanel.add(txtTmpPath,constraints);
		constraints.gridy =1;
		constraints.gridy =2;
		constraints.gridy =3;
		constraints.gridy =4;
		resultsPanel.add(txtFramePrefix,constraints);
		constraints.gridy =5;
		resultsPanel.add(txtSegPrefix,constraints);
		constraints.gridy =6;
		resultsPanel.add(txtPolyPrefix,constraints);

		constraints.gridx = 0;
		constraints.gridy = 0;
		miscPanel.add(lblNotes, constraints);
		constraints.gridx = 1;
		miscPanel.add(txtNotes, constraints);

		tabbedPane.addTab("General", generalPanel);

		tabbedPane.addTab("Vision",visionPanel);
		tabbedPane.addTab("Results",resultsPanel);
		tabbedPane.addTab("Misc",miscPanel);

		EBLAPanel btnPanel = new EBLAPanel();
		btnPanel.setLayout(new GridBagLayout());
		constraints.gridwidth = 1;
		constraints.gridx =0;
		constraints.gridy =0;

		btnPanel.add(btnSetSession,constraints);
		constraints.gridx =1;

		btnPanel.add(btnSelectExperiences,constraints);

		constraints.gridwidth = 2;
		constraints.gridx =0;

		constraints.gridy =1;
		contentPane.add(tabbedPane,constraints);
		constraints.gridy =2;
		contentPane.add(dataNavigator,constraints);
		constraints.gridy =3;
		contentPane.add(btnPanel,constraints);



	} // END OF CONSTRUCTOR

	/**
	 * adds the census screen to the specified container at the specified position.
	 *
	 * @param container the container in which the screen has to showup.
	 * @param positionX the x co-ordinate of the position where the screen has to showup.
	 * @param positionY the y co-ordinate of the position where the screen has to showup.
	 */
	public void showUp(Container container,double positionX, double positionY){

		int optionChoosen = -1;
		// SET THE POSITION OF THE SCREEN.
		this.setLocation((int)positionX, (int)positionY);

		// IF THE USER WANTS TO ADD A RECORD OR IF THERE ARE RECORDS IN DB SHOW THE SCREEN

			Component[] components = container.getComponents();
			int i=0;
			for(i=0; i< components.length;i++){
				if(components[i] instanceof ParameterScreen ) {
					System.out.println("Already on desktop");
					break;
				}
			}
			// IF IT IS NOT THERE ADD THE SCREEN TO THE CONTAINER
			if(i == components.length) {
				container.add(this);
			}
			this.setVisible(true);
			// MOVE THE SCREEN TO THE FRONT
			this.moveToFront();

			// REQUEST FOCUS FOR THE SCREEN
			this.requestFocus();
			// MAKE THE SCREEN SELECTED SCREEN
			try{
				this.setClosed(false);
				this.setSelected(true);
			}catch(PropertyVetoException pve){
				pve.printStackTrace();
			}

	}

	/**
	 * shows the census screen at the default location on the specified container.
	 * @param container the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	}


} // end of ParameterScreen class



/*
 * $Log$
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





