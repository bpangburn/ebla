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
import java.beans.PropertyVetoException;
import java.util.StringTokenizer;



/**
 * DBSettingsScreen.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Screen to display database connection settings for the EBLA graphical user
 * interface (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class DBSettingsScreen extends JDialog {

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR EXPERIENCE SCREEN
		Container desktop = null;

	// INITIALIZE DB SETTINGS SCREEN WIDGETS
		JCheckBox chkRemoteDB 		= new JCheckBox("Remote Database");

		JTextField txtIP 			= new JTextField();
		JTextField txtPort 			= new JTextField();
		JTextField txtDBPath 		= new JTextField();
		JTextField txtUsername 		= new JTextField();
		JPasswordField txtPassword 	= new JPasswordField();

		JButton btnSubmit 			= new JButton("Submit");
		SubmitButtonListener submitListener = new SubmitButtonListener();


	// FILE CONTAINING DATABASE CONNECTION SETTINGS
		File dbFile = null;



	/**
	 * DBSettingsScreen constructor.
	 *
//	 * @param the container in which the screen has to showup.
	 * @param file containing database connection settings
	 */
	public DBSettingsScreen(File _file){

//		super("Database information",false,true,false,true);
		setModal(true);
		setSize(500,250);
		setTitle("EBLA - Database Connection Screen");
		dbFile = _file;

		try {
			// CREATE BUFFERED READER TO READ IN DATABASE CONNECTION INFO FROM FILE
			// (IF AVAILABLE)
				BufferedReader bufRead = new BufferedReader(new FileReader("dbSettings"));

			// READ DATA FROM FIRST LINE AND TOKENIZE
				String dbLine = bufRead.readLine();
				StringTokenizer st = new StringTokenizer(dbLine,":/",false);

			// EXTRACT NEEDED TOKENS FROM CONNECTION STRING
				// NEGLECT JDBC PART
					st.nextToken();
				// NEGLECT POSTGRES PART
					st.nextToken();
				// GIVES THE IP ADDRESS
					txtIP.setText(st.nextToken());
				// GIVES THE PORT NUMBER
					txtPort.setText(st.nextToken());
				// GIVES THE DATABASE NAME
					txtDBPath.setText(st.nextToken());

			// READ USERNAME
				txtUsername.setText(bufRead.readLine());

			// READ PASSWORD
				txtPassword.setText(bufRead.readLine());

		} catch(FileNotFoundException fnfe) {
			System.out.println(fnfe.getMessage());
		} catch(IOException ioe) {
			System.out.println(ioe.getMessage());
		}

		if(txtPort.getText().trim().equals(""))
			txtPort.setText("5432");

		chkRemoteDB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				JCheckBox checkBox = (JCheckBox)ae.getSource();
				if (checkBox.isSelected()) {
					txtIP.setEnabled(true);
					txtPort.setEnabled(true);
					//lblRemoteIP.setEnabled(true);
					//lblRemotePort.setEnabled(true);
				} else {
					txtIP.setEnabled(false);
					txtPort.setEnabled(false);
					//lblRemoteIP.setEnabled(false);
					//lblRemotePort.setEnabled(false);
				}

			}
		});


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			int currentRow = 0;
			GridBagConstraints constraints = new GridBagConstraints();


		// CREATE/LAYOUT PANEL FOR WIDGETS
			EBLAPanel panel  = new EBLAPanel();
			panel.setLayout(new GridBagLayout());

			panel.addRow(txtIP, currentRow++, "Database Server IP");
			panel.addRow(txtPort, currentRow++, "Port Number");
			panel.addRow(txtDBPath, currentRow++, "Database Name");
			panel.addRow(txtUsername, currentRow++, "Username");
			panel.addRow(txtPassword, currentRow++, "Password");


chkRemoteDB.setSelected(true);

			constraints.gridx =0;
			constraints.gridy =5;
			panel.add(chkRemoteDB,constraints);

			constraints.gridx =1;
			constraints.gridy =5;
			panel.add(btnSubmit,constraints);

			getContentPane().add(panel);

			btnSubmit.addActionListener(submitListener);

	}

	public void closeWindow() {
		this.dispose();
/*		try{
			this.setClosed(true);
		}catch(PropertyVetoException pve){
			pve.printStackTrace();
		}
*/	}

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
				if(components[i] instanceof DBSettingsScreen) {
					System.out.println("Already on desktop");
					break;
				}
			}
			// IF IT IS NOT THERE ADD THE SCREEN TO THE CONTAINER
			if(i == components.length) {
//				container.add(this);
			}
			this.setVisible(true);
			// MOVE THE SCREEN TO THE FRONT
//			this.moveToFront();

			// REQUEST FOCUS FOR THE SCREEN
			this.requestFocus();
			// MAKE THE SCREEN SELECTED SCREEN
/*			try{
				this.setClosed(false);
				this.setSelected(true);
			}catch(PropertyVetoException pve){
				pve.printStackTrace();
			}
*/
	}

	/**
	 * shows the census screen at the default location on the specified container.
	 *@param container the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	}


	private class SubmitButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			String dbPath = "jdbc:postgresql://" + txtIP.getText() + ":" + txtPort.getText() +"/" + txtDBPath.getText();
			String username = txtUsername.getText();
			String password = new String(txtPassword.getPassword());

			try{
				FileWriter file = new FileWriter(dbFile);
				BufferedWriter bufWritter = new BufferedWriter(file);
				bufWritter.write(dbPath + "\n");
				bufWritter.write(username + "\n");
				bufWritter.write(password);
				bufWritter.close();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			closeWindow();

		}

	}
} // end of DBSettingsScreen class



/*
 * $Log$
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */