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
 * ExperienceScreen.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Screen to display experience_data for the EBLA graphical user interface
 * (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class ExperienceScreen extends JInternalFrame {


	JLabel lblDescription = new JLabel("Description");
	//JLabel lblMatchingCode = new JLabel("Matching Code");
	JLabel lblVideoPath = new JLabel("Video Path");
	JLabel lblTmpPath = new JLabel("Tmp Path");
	JLabel lblExperienceLexemes = new JLabel("Experience Lexemes");
	JLabel lblNotes = new JLabel("Notes");

	JTextField txtDescription = new JTextField();
	JTextField txtVideoPath = new JTextField();
	JTextField txtTmpPath = new JTextField();
	JTextField txtExperienceLexemes = new JTextField();
	//JTextField txtMatchingCode = new JTextField();
	JTextArea txtNotes = new JTextArea(20,10);

	DBConnector connector       = null;
	DataNavigator dataNavigator = null;
	JdbcRowSet rowset           = null;

	int includeCode =-1;
	Container desktop = null;

	public void setIncludeCode(int _includeCode){
		if(includeCode != _includeCode){
			includeCode = _includeCode;
			try{
				rowset.setCommand("SELECT * FROM experience_data WHERE experience_id>0 ORDER BY description;");
				dataNavigator.setRowSet(rowset);
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
	}

	public ExperienceScreen(Container _desktop){

		super("Experience Form",false,true,true,true);
		setSize(550,400);

		//includeCode = _includeCode;
		desktop = _desktop;

		try{
			connector = new DBConnector(EBLAGui.dbFileName,true);
			BufferedReader bufRead = new BufferedReader(new FileReader(EBLAGui.dbFileName));
			rowset    = new JdbcRowSet();
			rowset.setUrl(bufRead.readLine());
			rowset.setUsername(bufRead.readLine());
			rowset.setPassword(bufRead.readLine());

			rowset.setCommand("SELECT * FROM experience_data WHERE experience_id>0 ORDER BY description;");
			dataNavigator = new DataNavigator(rowset);
			dataNavigator.setDBNav(new DBNavImp(getContentPane()));
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		txtDescription.setDocument(new TextDocument(rowset,"description"));
		txtVideoPath.setDocument(new TextDocument(rowset,"video_path"));
		txtTmpPath.setDocument(new TextDocument(rowset,"tmp_path"));
		txtExperienceLexemes.setDocument(new TextDocument(rowset,"experience_lexemes"));
		//txtMatchingCode.setDocument(new TextDocument(rowset,"include_code"));
		txtNotes.setDocument(new TextDocument(rowset,"notes"));

		EBLAPanel panel  = new EBLAPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();


		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(lblDescription,constraints);
		constraints.gridy = 1;
		//panel.add(lblMatchingCode,constraints);
		constraints.gridy = 2;
		panel.add(lblVideoPath,constraints);
		constraints.gridy = 3;
		panel.add(lblTmpPath,constraints);
		constraints.gridy = 4;
		panel.add(lblExperienceLexemes,constraints);
		constraints.gridy = 5;
		panel.add(lblNotes,constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		panel.add(txtDescription,constraints);
		constraints.gridy = 1;
	//	panel.add(txtMatchingCode,constraints);
		constraints.gridy = 2;
		panel.add(txtVideoPath,constraints);
		constraints.gridy = 3;
		panel.add(txtTmpPath,constraints);
		constraints.gridy = 4;
		panel.add(txtExperienceLexemes,constraints);
		constraints.gridy = 5;
		panel.add(txtNotes,constraints);

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		constraints.gridx = 0;
		constraints.gridy = 0;
		contentPane.add(panel,constraints);
		constraints.gridy = 1;
		contentPane.add(dataNavigator,constraints);
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
				if(components[i] instanceof ExperienceScreen ) {
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

} // end of ExperienceScreen class



/*
 * $Log$
 */