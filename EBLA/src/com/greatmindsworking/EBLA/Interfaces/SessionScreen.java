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
import com.greatmindsworking.EBLA.Session;
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

	StatusScreen statusScreen = null;

	JTabbedPane tabbedPane = new JTabbedPane();

	EBLAPanel generalPanel   = new EBLAPanel();
	EBLAPanel entitiesPanel  = new EBLAPanel();
	EBLAPanel lexemesPanel   = new EBLAPanel();
	EBLAPanel resultsPanel   = new EBLAPanel();
	EBLAPanel miscPanel      = new EBLAPanel();

	DBConnector connector       = null;
	SSDataNavigator dataNavigator = null;
	JdbcRowSetImpl rowset           = null;



	JLabel lblDescription 		= new JLabel("Description");
	JTextField txtDescription 	= new JTextField();

	JButton btnStartEBLA  = new JButton("Start EBLA");

/*	JLabel lblParameterCode 		= new JLabel("Parameter Code");
	SSDBComboBox cmbParameterCode 	= new SSDBComboBox();
	JButton btnParameterScreen = new JButton("...");
*/
	//LABELS FOR GENERAL PANEL
	JLabel lblProcessVideosCode   	  = new JLabel("Process Videos Code");
	JLabel lblProcessEntitiesCode  	  = new JLabel("Process Entities Code");
	JLabel lblProcessLexemesCode   	  = new JLabel("Process Lexemes Code");
	JLabel lblLogToFileCode  	   	  = new JLabel("Log To File?");
	JLabel lblRandomizeExpCode     	  = new JLabel("Randomize Experiences?");
	JLabel lblRegenerateImages        = new JLabel("Regenerate Local Images?");

	//COMBOS FOR THE GENERAL PANEL
	SSComboBox cmbProcessVideosCode   = new SSComboBox();
	SSComboBox cmbProcessEntitiesCode = new SSComboBox();
	SSComboBox cmbProcessLexemesCode  = new SSComboBox();
	SSComboBox cmbLogToFileCode       = new SSComboBox();
	SSComboBox cmbRandomizeExpCode    = new SSComboBox();
	SSComboBox cmbRegenerateImages    = new SSComboBox();

	//LABELS FOR THE ENTITIES PANEL
	JLabel lblMinSDStart       = new JLabel("Starting Min. Std. Dev.");
	JLabel lblMinSDStop        = new JLabel("Stopping Min. Std. Dev.");
	JLabel lblMinSDStep        = new JLabel("Min. Std. Dev. Step Size");
	JLabel lblLoopCount  	   = new JLabel("# Runs for Each Std. Dev.");
	JLabel lblFixedSDCode      = new JLabel("Limit Actual Std. Dev?");

	//TEXT FIELDS FOR THE ENTITIES PANEL
	JTextField txtMinSDStart   = new JTextField();
	JTextField txtMinSDStop    = new JTextField();
	JTextField txtMinSDStep    = new JTextField();
	JTextField txtLoopCount    = new JTextField();
	SSComboBox cmbFixedSDCode  = new SSComboBox();

	// LABELS FOR LEXEMES PANEL

	JLabel lblDescToGenerate  	    = new JLabel("# Descriptions To Generate");
	JLabel lblCaseSensitiveCode     = new JLabel("Are Lexemes Case Sensitive?");

	JTextField txtDescToGenerate     = new JTextField();
	SSComboBox cmbCaseSensitiveCode = new SSComboBox();

	JLabel lblDisplayMovieCode  		= new JLabel("Display Videos When Ripping?");
	JLabel lblDisplayText  			= new JLabel("Display Detailed Messages");
//	JLabel lblSaveImagesCode  		= new JLabel("Save Images Code");

	SSComboBox cmbDisplayMovieCode 	= new SSComboBox();
	SSComboBox cmbDisplayText 		= new SSComboBox();
//	SSComboBox cmbSaveImagesCode	= new SSComboBox();

	JLabel lblNotes  				= new JLabel("Notes");
	JTextArea txtNotes 				= new JTextArea(30,15);

	long parameterID = -1;
	Container desktop = null;

	public SessionScreen(Container _desktop,long _parameterID){
		super("Session Form", false,true,true,true);
		setSize(550,400);

		desktop = _desktop;
		parameterID = _parameterID;

		cmbLogToFileCode.setOption(SSComboBox.YES_NO_OPTION);

//		cmbLogToFileCode.setDocument(new SSTextDocument(rowset,"log_to_file_code"));

		cmbRandomizeExpCode.setOption(SSComboBox.YES_NO_OPTION);
		cmbRegenerateImages.setOption(SSComboBox.YES_NO_OPTION);

//		cmbRandomizeExpCode.setDocument(new SSTextDocument(rowset,"randomize_exp_code"));

/*
		txtMinSDStart.setDocument(new SSTextDocument(rowset,"min_sd_start"));
		txtMinSDStop.setDocument(new SSTextDocument(rowset,"min_sd_stop"));
		txtMinSDStep.setDocument(new SSTextDocument(rowset,"min_sd_step"));
		txtLoopCount.setDocument(new SSTextDocument(rowset,"loop_count"));
*/
		cmbFixedSDCode.setOption(SSComboBox.YES_NO_OPTION);
//		cmbFixedSDCode.setDocument(new SSTextDocument(rowset,"fixed_sd_code"));



//		txtDescToGenerate.setDocument(new SSTextDocument(rowset,"desc_to_generate"));
		cmbCaseSensitiveCode.setOption(SSComboBox.YES_NO_OPTION);
//		cmbCaseSensitiveCode.setDocument(new SSTextDocument(rowset,"case_sensitive_code"));

		cmbDisplayMovieCode.setOption(SSComboBox.YES_NO_OPTION);
//		cmbDisplayMovieCode.setDocument(new SSTextDocument(rowset,"display_movie_code"));
		cmbDisplayText.setOption(SSComboBox.YES_NO_OPTION);
//		cmbDisplayText.setDocument(new SSTextDocument(rowset,"display_text_code"));
//		cmbSaveImagesCode.setOption(SSComboBox.YES_NO_OPTION);
//		cmbSaveImagesCode.setDocument(new SSTextDocument(rowset,"save_images_code"));

/*
		btnParameterScreen.setPreferredSize(new Dimension(25,20));
		btnParameterScreen.setMaximumSize(new Dimension(25,20));

		btnParameterScreen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				long parameterCode = -1;
				if( cmbParameterCode.getText().trim().equals("") ){
				}
				else{
					try{
						parameterCode = Integer.parseInt(txtParameterCode.getText());
					}catch(NumberFormatException nfe){
						nfe.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(ParameterScreen == null){
					ParameterScreen = new ParameterScreen(includeCode);
				}else{
					ParameterScreen.setIncludeCode(includeCode);
				}
				ParameterScreen.showUp(desktop);
			}
		});
*/

		cmbLogToFileCode.getComboBox().setSelectedIndex(1);
		cmbRandomizeExpCode.getComboBox().setSelectedIndex(1);
		cmbRegenerateImages.getComboBox().setSelectedIndex(0);

		txtMinSDStart.setText("5");
		txtMinSDStop.setText("15");
		txtMinSDStep.setText("5");
		txtLoopCount.setText("1");
		cmbFixedSDCode.getComboBox().setSelectedIndex(0);

		txtDescToGenerate.setText("0");
		cmbCaseSensitiveCode.getComboBox().setSelectedIndex(0);

		cmbDisplayMovieCode.getComboBox().setSelectedIndex(1);
		cmbDisplayText.getComboBox().setSelectedIndex(0);

		btnStartEBLA.addActionListener(new StartEBLAListener());

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		generalPanel.setLayout(new GridBagLayout());
		entitiesPanel.setLayout(new GridBagLayout());
		lexemesPanel.setLayout(new GridBagLayout());
		resultsPanel.setLayout(new GridBagLayout());
		miscPanel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();


		constraints.gridx = 0;
		constraints.gridy = 0;
		generalPanel.add(lblLogToFileCode,constraints);
		constraints.gridy = 1;
		generalPanel.add(lblRandomizeExpCode,constraints);
		constraints.gridy = 2;
		generalPanel.add(lblRegenerateImages,constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		generalPanel.add(cmbLogToFileCode.getComboBox(),constraints);
		constraints.gridy = 1;
		generalPanel.add(cmbRandomizeExpCode.getComboBox(),constraints);
		constraints.gridy = 2;
		generalPanel.add(cmbRegenerateImages.getComboBox(),constraints);

		constraints.gridx = 0;
		constraints.gridy = 0;
		entitiesPanel.add(lblMinSDStart,constraints);
		constraints.gridy = 1;
		entitiesPanel.add(lblMinSDStop,constraints);
		constraints.gridy = 2;
		entitiesPanel.add(lblMinSDStep,constraints);
		constraints.gridy = 3;
		entitiesPanel.add(lblLoopCount,constraints);
		constraints.gridy = 4;
		entitiesPanel.add(lblFixedSDCode,constraints);
		constraints.gridy = 5;
		entitiesPanel.add(lblFixedSDCode,constraints);



		constraints.gridx = 1;
		constraints.gridy = 0;
		entitiesPanel.add(txtMinSDStart,constraints);
		constraints.gridy = 1;
		entitiesPanel.add(txtMinSDStop,constraints);
		constraints.gridy = 2;
		entitiesPanel.add(txtMinSDStep,constraints);
		constraints.gridy = 3;
		entitiesPanel.add(txtLoopCount,constraints);
		constraints.gridy = 4;
		entitiesPanel.add(cmbFixedSDCode.getComboBox(),constraints);
		constraints.gridy = 5;
		entitiesPanel.add(cmbFixedSDCode.getComboBox(),constraints);


		constraints.gridx = 0;
		constraints.gridy = 1;
		lexemesPanel.add(lblDescToGenerate,constraints);
		constraints.gridy = 2;
		lexemesPanel.add(lblCaseSensitiveCode,constraints);


		constraints.gridx = 1;
		constraints.gridy = 1;
		lexemesPanel.add(txtDescToGenerate,constraints);
		constraints.gridy = 2;
		lexemesPanel.add(cmbCaseSensitiveCode.getComboBox(),constraints);

		constraints.gridx =0;
		constraints.gridy =1;
		resultsPanel.add(lblDisplayMovieCode,constraints);
		constraints.gridy =2;
		resultsPanel.add(lblDisplayText,constraints);

		constraints.gridx =1;
		constraints.gridy =1;
		resultsPanel.add(cmbDisplayMovieCode.getComboBox(),constraints);
		constraints.gridy =2;
		resultsPanel.add(cmbDisplayText.getComboBox(),constraints);

		miscPanel.add(lblNotes);
		miscPanel.add(txtNotes);

		tabbedPane.addTab("General",generalPanel);
		tabbedPane.addTab("Entities",entitiesPanel);
		tabbedPane.addTab("Lexemes",lexemesPanel);
		tabbedPane.addTab("Results",resultsPanel);
		tabbedPane.addTab("Misc",miscPanel);

		EBLAPanel panel = new EBLAPanel();
		panel.setLayout(new GridBagLayout());
		constraints.gridx =0;
		constraints.gridy =0;
		panel.add(lblDescription,constraints);
		constraints.gridx =1;
		panel.add(txtDescription,constraints);
/*
		constraints.gridx =0;
		constraints.gridy =1;
		panel.add(lblParameterCode,constraints);
		constraints.gridx =1;
		panel.add(cmbParameterCode.getComboBox(),constraints);
		constraints.gridx =2;
		panel.add(btnParameterScreen,constraints,false);
*/
		constraints.gridx =0;
		constraints.gridy =0;
		constraints.gridwidth = 2;
		contentPane.add(panel,constraints);
		constraints.gridy =1;
		contentPane.add(tabbedPane,constraints);
		constraints.gridy =2;
		contentPane.add(btnStartEBLA,constraints);
	}

	private class StartEBLAListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			int logToFileCode = cmbLogToFileCode.getComboBox().getSelectedIndex();
			int randomizeExpCode = cmbRandomizeExpCode.getComboBox().getSelectedIndex();
			int regenerateImages = cmbRegenerateImages.getComboBox().getSelectedIndex();

			boolean boolLogToFile = logToFileCode == 0? false : true;
			boolean boolRandomizeExp = 	randomizeExpCode == 0? false : true;
			boolean boolRegenerateImages = 	regenerateImages == 0? false : true;

			int minSDStart   = txtMinSDStart.getText().equals("") ? 5: Integer.parseInt(txtMinSDStart.getText());
			int minSDStop	 = txtMinSDStop.getText().equals("") ? 15: Integer.parseInt(txtMinSDStop.getText());
			int minSDStep	 = txtMinSDStep.getText().equals("") ? 5: Integer.parseInt(txtMinSDStep.getText());
			int loopCount    = txtLoopCount.getText().equals("") ? 1: Integer.parseInt(txtLoopCount.getText());

			int fixedSDCode = cmbFixedSDCode.getComboBox().getSelectedIndex();
			boolean boolFixedSD = 	fixedSDCode == 0? false : true;


			int descToGenerate = txtDescToGenerate.getText().equals("") ? 0: Integer.parseInt(txtDescToGenerate.getText());
			int caseSensitiveCode = cmbCaseSensitiveCode.getComboBox().getSelectedIndex();
			boolean boolCaseSensitive = 	caseSensitiveCode == 0? false : true;

			int displayMovieCode = cmbDisplayMovieCode.getComboBox().getSelectedIndex();
			boolean boolDisplayMovie = displayMovieCode == 0 ? false : true ;

			int displayText  = cmbDisplayText.getComboBox().getSelectedIndex();
			boolean boolDisplayText = displayText == 0 ? false : true ;

			String desc = txtDescription.getText();

			String notes = txtNotes.getText();

			try{
				connector = new DBConnector(EBLAGui.dbFileName, true);
			}catch(Exception e){
				e.printStackTrace();
			}


			Session session = new Session(connector,parameterID,desc,boolRegenerateImages,boolLogToFile,
				boolRandomizeExp, descToGenerate, minSDStart, minSDStop, minSDStep, loopCount, boolFixedSD,
				boolDisplayMovie, boolDisplayText, boolCaseSensitive, notes);

			if(statusScreen == null){
				statusScreen = new StatusScreen(desktop, session, connector);
			}
			statusScreen.showUp(desktop);

		}
	}

	/**
	 *	adds the census screen to the specified container at the specified position.
	 *@param container the container in which the screen has to showup.
	 *@param positionX the x co-ordinate of the position where the screen has to showup.
	 *@param positionY the y co-ordinate of the position where the screen has to showup.
	 */
	public void showUp(Container container,double positionX, double positionY){

		int optionChoosen = -1;
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
	 *@param container the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	}

} // end of SessionScreen class



/*
 * $Log$
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */