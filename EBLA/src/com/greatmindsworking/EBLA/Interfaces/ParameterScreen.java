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
import sun.jdbc.rowset.JdbcRowSet;
import com.nqadmin.swingUtils.*;
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

	SelectExperiencesScreen selectExperiencesScreen = null;

	JTabbedPane tabbedPane = new JTabbedPane();

	EBLAPanel generalPanel   = new EBLAPanel();
	EBLAPanel entitiesPanel  = new EBLAPanel();
	EBLAPanel lexemesPanel   = new EBLAPanel();
	EBLAPanel visionPanel    = new EBLAPanel();
	EBLAPanel resultsPanel   = new EBLAPanel();
	EBLAPanel miscPanel      = new EBLAPanel();

	DBConnector connector       = null;
	DataNavigator dataNavigator = null;
	JdbcRowSet rowset           = null;



	JLabel lblDescription 		= new JLabel("Description");
	JTextField txtDescription 	= new JTextField();

	JButton btnSelectExperiences = new JButton("Select \n Experiences");
	JButton btnSetSession = new JButton("Start Session");

	JTextField txtParameterID = new JTextField();

	//LABELS FOR VISION PANEL
	JLabel lblSegColorRadius  		= new JLabel("Segmentation Color Radius");
	JLabel lblSegSpatialRadius  	= new JLabel("Segmentation Spatial Radius");
	JLabel lblSegMinRegion  		= new JLabel("Segmentation Minimum Region");
	JLabel lblSegSpeedUpCode  		= new JLabel("Segmentation Speed Up");
	JLabel lblBackGroundPixels  	= new JLabel("Background Pixel Threshold");
	JLabel lblMinPixelCount  		= new JLabel("Min Pixels for Object");
	JLabel lblMinFrameCount  		= new JLabel("Min Frames for Object");
	JLabel lblReduceColorCode  		= new JLabel("Reduce Color Depth?");

	JTextField txtSegColorRadius 	= new JTextField();
	JTextField txtSegSpatialRadius 	= new JTextField();
	JTextField txtSegMinRegion 		= new JTextField();
	MyComboBox cmbSegSpeedUpCode 	= new MyComboBox();
	JTextField txtBackGroundPixels 	= new JTextField();
	JTextField txtMinPixelCount 	= new JTextField();
	JTextField txtMinFrameCount 	= new JTextField();
	MyComboBox cmbReduceColorCode 	= new MyComboBox();

	//LABELS FOR RESULTS PANEL
	JLabel lblTmpPath  				= new JLabel("Intermediate Results Path");
	JLabel lblFramePrefix 			= new JLabel("Frame Image File Prefix");
	JLabel lblSegPrefix 			= new JLabel("Segmented Image File Prefix");
	JLabel lblPolyPrefix  			= new JLabel("Polygon Image File Prefix");

	JTextField txtTmpPath 			= new JTextField();
	JTextField txtFramePrefix 		= new JTextField();
	JTextField txtSegPrefix 		= new JTextField();
	JTextField txtPolyPrefix 		= new JTextField();

	JLabel lblNotes  				= new JLabel("Notes");
	JTextArea txtNotes = new JTextArea(30,15);
	Container desktop =null;

	SessionScreen sessionScreen = null;

	public ParameterScreen(Container _desktop){
		super("EBLA Vision Parameter Form",false,true,true,true);
		setSize(550,400);
		desktop = _desktop;
		btnSetSession.setToolTipText("Prasanth");
		try{
			connector = new DBConnector(EBLAGui.dbFileName,true);
			BufferedReader bufRead = new BufferedReader(new FileReader(EBLAGui.dbFileName));
			rowset = new JdbcRowSet();
			rowset.setUrl(bufRead.readLine());
			String username = bufRead.readLine();
			if (username == null) {
				username = "";
			}
			rowset.setUsername(username);
			String password = bufRead.readLine();
			if (password == null) {
				password = "";
			}
			rowset.setPassword(password);
			rowset.setCommand("SELECT * FROM parameter_data WHERE parameter_id>0 ORDER BY description;");
			dataNavigator = new DataNavigator(rowset);
			dataNavigator.setDBNav(new DBNavImp(getContentPane()));
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		btnSelectExperiences.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				long parameterID = -1;
				if( txtParameterID.getText().trim().equals("") ){
				}
				else{
					try{
						parameterID = Long.parseLong(txtParameterID.getText());
					}catch(NumberFormatException nfe){
						nfe.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(selectExperiencesScreen == null){
					selectExperiencesScreen = new SelectExperiencesScreen(parameterID);
				}else{
					selectExperiencesScreen.setParameterID(parameterID);
				}
				selectExperiencesScreen.showUp(desktop);
			}
		});

		btnSetSession.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				long parameterID = -1;
				parameterID = txtParameterID.getText().equals("") ? 0 : Long.parseLong(txtParameterID.getText());

				if(sessionScreen == null){
					sessionScreen = new SessionScreen(desktop,parameterID);
				}
				sessionScreen.showUp(desktop);
			}
		});

		txtParameterID.setDocument(new TextDocument(rowset,"parameter_id"));

		txtDescription.setDocument(new TextDocument(rowset,"description"));

		txtSegColorRadius.setDocument(new TextDocument(rowset,"seg_color_radius"));
		txtSegSpatialRadius.setDocument(new TextDocument(rowset,"seg_spatial_radius"));
		txtSegMinRegion.setDocument(new TextDocument(rowset,"seg_min_region"));
		String[] tmpString = {"None", "Medium", "High"};
		cmbSegSpeedUpCode.setOption(tmpString);
		cmbSegSpeedUpCode.setDocument(new TextDocument(rowset,"seg_speed_up_code"));
		txtBackGroundPixels.setDocument(new TextDocument(rowset,"background_pixels"));
		txtMinPixelCount.setDocument(new TextDocument(rowset,"min_pixel_count"));
		txtMinFrameCount.setDocument(new TextDocument(rowset,"min_frame_count"));
		cmbReduceColorCode.setOption(MyComboBox.YES_NO_OPTION);
		cmbReduceColorCode.setDocument(new TextDocument(rowset,"reduce_color_code"));


		txtTmpPath.setDocument(new TextDocument(rowset,"tmp_path"));
		txtFramePrefix.setDocument(new TextDocument(rowset,"frame_prefix"));
		txtSegPrefix.setDocument(new TextDocument(rowset,"seg_prefix"));
		txtPolyPrefix.setDocument(new TextDocument(rowset,"poly_prefix"));

		txtNotes.setDocument(new TextDocument(rowset,"notes"));


		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		generalPanel.setLayout(new GridBagLayout());
		entitiesPanel.setLayout(new GridBagLayout());
		lexemesPanel.setLayout(new GridBagLayout());
		visionPanel.setLayout(new GridBagLayout());
		resultsPanel.setLayout(new GridBagLayout());
		miscPanel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();

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

		tabbedPane.addTab("Vision",visionPanel);
		tabbedPane.addTab("Results",resultsPanel);
		tabbedPane.addTab("Misc",miscPanel);

		EBLAPanel panel = new EBLAPanel();
		panel.setLayout(new GridBagLayout());
		constraints.gridx =0;
		constraints.gridy =0;
		panel.add(lblDescription,constraints);
		constraints.gridx =1;
		panel.add(txtDescription,constraints);

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

		constraints.gridy =0;
		contentPane.add(panel,constraints);
		constraints.gridy =1;
		contentPane.add(tabbedPane,constraints);
		constraints.gridy =2;
		contentPane.add(dataNavigator,constraints);
		constraints.gridy =3;
		contentPane.add(btnPanel,constraints);



	}// END OF CONSTRUCTOR

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
 */




