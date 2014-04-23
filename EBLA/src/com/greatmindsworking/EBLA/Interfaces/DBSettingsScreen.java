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



import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
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
public class DBSettingsScreen extends JInternalFrame {

	/**
	 * serial version ID
	 */
	private static final long serialVersionUID = 4930775863265771831L;

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR EXPERIENCE SCREEN
		Container desktop = null;

	// INITIALIZE DB SETTINGS SCREEN WIDGETS
		JCheckBox chkRemoteDB 		= new JCheckBox("Remote Database");

		JTextField txtIP 			= new JTextField();
		JTextField txtPort 			= new JTextField();
		JTextField txtDBPath 		= new JTextField();
		JTextField txtUsername 		= new JTextField();
		JPasswordField txtPassword 	= new JPasswordField();

		JButton btnClose			= new JButton("Close");


	// FILE CONTAINING DATABASE CONNECTION SETTINGS
		File dbFile = null;



	/**
	 * DBSettingsScreen constructor.
	 *
	 * @param _desktop    the container in which the screen has to showup.
	 * @param _file    file containing database connection settings
	 */
	public DBSettingsScreen(Container _desktop, File _file) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE EXPERIENCE SCREEN
			super("EBLA - Database Connection Screen",false,false,false,false);

		// SET SIZE
			setSize(640,240);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

		// SET FILE CONTAINING DATABASE CONNNECTION SETTINGS
			dbFile = _file;

		// ATTEMPT TO READ DATABASE CONFIGURATION FILE
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
					
				// CLOSE BUFFERED READER
					bufRead.close();

			} catch(FileNotFoundException fnfe) {
				System.out.println(fnfe.getMessage());
			} catch(IOException ioe) {
				System.out.println(ioe.getMessage());
			}


		// IF DATABASE PORT IS NOT SUPPLIED, USE POSTGRES DEFAULT PORT
			if (txtPort.getText().trim().equals("")) {
				txtPort.setText("5432");
			}


		// ADD ACTION LISTENER TO CHECKBOX TO DISABLE FIELDS NOT NECESSARY FOR A LOCAL CONNECTION
			chkRemoteDB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
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

				} // end actionPerformed()
			});


		// ADD ACTION LISTENER TO "CLOSE" BUTTON
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					// WRITE DB SETTINGS TO LOCAL FILE
						String dbPath = "jdbc:postgresql://" + txtIP.getText() + ":" + txtPort.getText() +"/" + txtDBPath.getText();
						String username = txtUsername.getText();
						String password = new String(txtPassword.getPassword());

						try {
							FileWriter file = new FileWriter(dbFile);
							BufferedWriter bufWritter = new BufferedWriter(file);
							bufWritter.write(dbPath + "\n");
							bufWritter.write(username + "\n");
							bufWritter.write(password);
							bufWritter.close();
						} catch(IOException ioe) {
							ioe.printStackTrace();
						}

					// CLOSE WINDOW
						try {
							setClosed(true);
						} catch(PropertyVetoException pve) {
							pve.printStackTrace();
						}

				} // end actionPerformed()
			});


		// ADD INTERNAL FRAME LISTENER TO FORM TO CHECK FOR LOSS OF FOCUS OR CLOSE
			addInternalFrameListener(new InternalFrameAdapter() {
			// FRAME DEACTIVATED
				public void internalFrameDeactivated(InternalFrameEvent ife) {
					SwingUtilities.invokeLater(new Thread() {
						public void run() {
							DBSettingsScreen.this.moveToFront();
							DBSettingsScreen.this.requestFocus();
							try {
								DBSettingsScreen.this.setSelected(true);
							} catch(PropertyVetoException pve) {
								System.out.println(pve.getMessage());
							}

						}
					});
				} // end internalFrameDeactivated()

			// FRAME CLOSED
				public void internalFrameClosed(InternalFrameEvent e) {
				// DISPLAY MESSAGE
					JOptionPane.showInternalMessageDialog(desktop,"If you are connected to another database you must logout and log back in for any new settings to take effect.",
					"Database Connection",JOptionPane.INFORMATION_MESSAGE);
				}

			});


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			int currentRow = 0;
			GridBagConstraints constraints = new GridBagConstraints();
			Border emptySpace = BorderFactory.createEmptyBorder(0, 0, 10, 0);


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

			EBLAPanel buttonPanel = new EBLAPanel();
			buttonPanel.setBorder(emptySpace);
			buttonPanel.add(btnClose);

			constraints.gridx =1;
			constraints.gridy =5;
			panel.add(buttonPanel,constraints);

			getContentPane().add(panel);

	} // end DBSettingsScreen constructor



	/**
	 * Adds the database settings screen to the specified container at the specified position.
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
				if (components[i] instanceof DBSettingsScreen) {
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
	 * Shows the database settings screen at the default location on the specified container.
	 *
	 * @param _container    the container in which the screen has to showup.
	 */
	public void showUp(Container _container) {
		showUp(_container, 30,30);
	} // end showUp()



} // end of DBSettingsScreen class



/*
 * $Log$
 * Revision 1.9  2011/04/28 14:55:07  yoda2
 * Addressing Java 1.6 -Xlint warnings.
 *
 * Revision 1.8  2005/02/17 23:33:45  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.7  2004/02/25 21:58:39  yoda2
 * Updated copyright notice.
 *
 * Revision 1.6  2004/01/09 14:21:55  yoda2
 * Created more useful message to display when screen is closed.
 *
 * Revision 1.5  2003/12/30 23:21:20  yoda2
 * Modified screens so that they are nullifed upon closing and a "fresh" screen is created if a screen is re-opened.
 *
 * Revision 1.4  2003/12/29 23:19:42  yoda2
 * Finished JavaDoc and code cleanup.
 * Simulated modal behavior by retaking focus when lost.
 * Added close button.
 *
 * Revision 1.3  2003/12/29 04:22:24  yoda2
 * Code cleanup & JavaDoc.
 *
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */