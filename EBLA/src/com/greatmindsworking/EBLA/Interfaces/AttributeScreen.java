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
 * AttributeScreen.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Screen to display attribute_list_data for the EBLA graphical user interface
 * (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class AttributeScreen extends JInternalFrame {

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR ATTRIBUTE SCREEN
		Container desktop = null;

	// INITIALIZE DATABASE CONNECTIVITY COMPONENTS FOR ATTRIBUTE SCREEN
		DBConnector connector = null;
		JdbcRowSetImpl rowset = null;

	// INITIALIZE ATTRIBUTE SCREEN WIDGETS
		JTextField txtAttributeID 		= new JTextField();
		JTextField txtDescription 		= new JTextField();
		SSComboBox cmbIncludeCode 		= new SSComboBox();
		SSComboBox cmbTypeCode 			= new SSComboBox();
		JTextField txtClassName 		= new JTextField();
		JTextArea txtNotes 				= new JTextArea(20,10);

		JButton btnClose				= new JButton("Close");

	// INITIALIZE DATA NAVIGATOR
		SSDataNavigator dataNavigator = null;


	/**
	 * AttributeScreen constructor.
	 *
	 * @param the container in which the screen has to showup.
	 */
	public AttributeScreen(Container _desktop) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE EXPERIENCE SCREEN
			super("EBLA - Attribute Screen",false,true,true,true);

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

			// INITIALIZE ROWSET FOR ATTRIBUTE LIST DATA
				rowset = new JdbcRowSetImpl(url, username, password);

				rowset.setCommand("SELECT * FROM attribute_list_data WHERE attribute_list_id>0;");
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
			txtAttributeID.setDocument(new SSTextDocument(rowset,"attribute_list_id"));

			txtDescription.setDocument(new SSTextDocument(rowset,"description"));

			cmbIncludeCode.setOption(SSComboBox.YES_NO_OPTION);
			cmbIncludeCode.setDocument(new SSTextDocument(rowset,"include_code"));

			String[] tmpString = {"Object", "Object-Object Relation"};
			cmbTypeCode.setOption(tmpString);
			cmbTypeCode.setDocument(new SSTextDocument(rowset,"type_code"));

			txtClassName.setDocument(new SSTextDocument(rowset,"class_name"));

			txtNotes.setDocument(new SSTextDocument(rowset,"notes"));


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			int currentRow = 0;
			GridBagConstraints constraints = new GridBagConstraints();
			Border emptySpace = BorderFactory.createEmptyBorder(0, 0, 10, 0);


		// CREATE/LAYOUT PANEL FOR WIDGETS
			EBLAPanel panel  = new EBLAPanel();
			panel.setLayout(new GridBagLayout());

			panel.addRow(txtAttributeID, currentRow++, "Attribute ID");
			panel.addRow(txtDescription, currentRow++, "Description");
			panel.addRow(cmbIncludeCode.getComboBox(), currentRow++, "Include Attribute?");
			panel.addRow(cmbTypeCode.getComboBox(), currentRow++, "Attribute Type");
			panel.addRow(txtClassName, currentRow++, "Attribute Calculation Class");
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
			txtAttributeID.setEnabled(false);

		// DISABLE TYPE CODE AND CLASS NAME
			cmbTypeCode.getComboBox().setEnabled(false);
			txtClassName.setEnabled(false);


	} // end of AttributeScreen constructor



	/**
	 * Adds the attribute screen to the specified container at the specified position.
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
				if(components[i] instanceof AttributeScreen ) {
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


} // end of AttributeScreen class



/*
 * $Log$
 * Revision 1.7  2004/01/07 19:44:21  yoda2
 * Verified that primary key is displayed on each screen and is disabled.
 *
 * Revision 1.6  2003/12/31 15:46:59  yoda2
 * Added listener to save current record if form loses focus.
 *
 * Revision 1.5  2003/12/30 23:21:20  yoda2
 * Modified screens so that they are nullifed upon closing and a "fresh" screen is created if a screen is re-opened.
 *
 * Revision 1.4  2003/12/29 23:20:27  yoda2
 * Added close button.
 *
 * Revision 1.3  2003/12/26 22:15:58  yoda2
 * General code cleanup and addition of JavaDoc.
 *
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */