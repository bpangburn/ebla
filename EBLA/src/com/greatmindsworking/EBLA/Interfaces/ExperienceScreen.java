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

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR EXPERIENCE SCREEN
		Container desktop = null;

	// INITIALIZE DATABASE CONNECTIVITY COMPONENTS FOR EXPERIENCE SCREEN
		DBConnector dbc = null;
		JdbcRowSetImpl rowset = null;

	// INITIALIZE EXPERIENCE SCREEN WIDGETS
		JTextField txtExperienceID 		= new JTextField();
		JTextField txtDescription 		= new JTextField();
		JTextField txtVideoPath 		= new JTextField();
		JTextField txtTmpPath 			= new JTextField();
		JTextField txtExperienceLexemes = new JTextField();
		JTextArea txtNotes 				= new JTextArea(20,10);

		JButton btnClose				= new JButton("Close");

	// INITIALIZE DATA NAVIGATOR
		SSDataNavigator dataNavigator = null;


	/**
	 * ExperienceScreen constructor.
	 *
	 * @param the container in which the screen has to showup.
	 * @param _dbc connection to ebla_data database
	 */
	public ExperienceScreen(Container _desktop, DBConnector _dbc) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE EXPERIENCE SCREEN
			super("EBLA - Experience Screen",false,true,true,true);

		// SET SIZE
			setSize(640,480);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

		// SET DATABASE CONNECTION
			dbc = _dbc;

		// DATABASE CONFIGURATION
			try {

			// INITIALIZE ROWSET FOR EXPERIENCE DATA
				rowset = new JdbcRowSetImpl(dbc.getConnection());

				rowset.setCommand("SELECT * FROM experience_data WHERE experience_id>0 ORDER BY description;");
				dataNavigator = new SSDataNavigator(rowset);
				dataNavigator.setDBNav(new SSDBNavImp(getContentPane()));

			} catch(SQLException se) {
				se.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}


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


		// ADD INTERNAL FRAME LISTENER TO SAVE RECORD UPON LOSS OF FOCUS
			addInternalFrameListener(new InternalFrameAdapter() {
			// FRAME DEACTIVATED
				public void internalFrameDeactivated(InternalFrameEvent ife) {
					dataNavigator.updatePresentRow();
				} // end internalFrameDeactivated()

			});


		// SET DATABASE COLUMNS FOR EACH WIDGET
			txtExperienceID.setDocument(new SSTextDocument(rowset,"experience_id"));

			txtDescription.setDocument(new SSTextDocument(rowset,"description"));

			txtVideoPath.setDocument(new SSTextDocument(rowset,"video_path"));

			txtTmpPath.setDocument(new SSTextDocument(rowset,"tmp_path"));

			txtExperienceLexemes.setDocument(new SSTextDocument(rowset,"experience_lexemes"));

			txtNotes.setDocument(new SSTextDocument(rowset,"notes"));


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			int currentRow = 0;
			GridBagConstraints constraints = new GridBagConstraints();
			Border emptySpace = BorderFactory.createEmptyBorder(0, 0, 10, 0);


		// CREATE/LAYOUT PANEL FOR WIDGETS
			EBLAPanel panel  = new EBLAPanel();
			panel.setLayout(new GridBagLayout());

			panel.addRow(txtExperienceID, currentRow++, "Experience ID");
			panel.addRow(txtDescription, currentRow++, "Description");
			panel.addRow(txtVideoPath, currentRow++, "Video Path");
			panel.addRow(txtTmpPath, currentRow++, "Intermediate Results Directory");
			panel.addRow(txtExperienceLexemes, currentRow++, "Protolanguage Description");
			panel.addRow(txtNotes, currentRow++, "Notes");


		// ADD WIDGET PANEL AND DATA NAVIGATOR TO EXPERIENCE SCREEN
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());

			constraints.gridx = 0;
			constraints.gridy = 0;
			contentPane.add(panel,constraints);

			constraints.gridy = 1;
			contentPane.add(dataNavigator,constraints);

			EBLAPanel buttonPanel = new EBLAPanel();
			buttonPanel.setBorder(emptySpace);
			buttonPanel.add(btnClose);

			constraints.gridy = 2;
			contentPane.add(buttonPanel, constraints);

		// DISABLE PRIMARY KEY
			txtExperienceID.setEnabled(false);

	} // end of ExperienceScreen constructor



	/**
	 * Adds the experience screen to the specified container at the specified position.
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
				if(components[i] instanceof ExperienceScreen ) {
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
	 * Shows the experience screen at the default location on the specified container.
	 *
	 * @param the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	} // end showUp()


} // end of ExperienceScreen class



/*
 * $Log$
 * Revision 1.9  2004/01/09 14:22:31  yoda2
 * Modified screens to use a single database connection.
 *
 * Revision 1.8  2004/01/07 19:44:21  yoda2
 * Verified that primary key is displayed on each screen and is disabled.
 *
 * Revision 1.7  2003/12/31 15:46:59  yoda2
 * Added listener to save current record if form loses focus.
 *
 * Revision 1.6  2003/12/30 23:21:20  yoda2
 * Modified screens so that they are nullifed upon closing and a "fresh" screen is created if a screen is re-opened.
 *
 * Revision 1.5  2003/12/29 23:20:27  yoda2
 * Added close button.
 *
 * Revision 1.4  2003/12/26 20:29:44  yoda2
 * General code cleanup and addition of JavaDoc.  Reflected renaming of Session.java to SessionData.java.
 *
 * Revision 1.3  2003/12/24 19:15:52  yoda2
 * General clean up.  Added JavaDoc and removed explicit coding of labels in favor of automatic labels via EBLAPanel.addRow().
 *
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */