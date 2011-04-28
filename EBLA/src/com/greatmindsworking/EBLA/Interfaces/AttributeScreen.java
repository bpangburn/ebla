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
import javax.swing.border.Border;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.greatmindsworking.utils.DBConnector;
import com.nqadmin.swingSet.SSComboBox;
import com.nqadmin.swingSet.SSDBNavImp;
import com.nqadmin.swingSet.SSDataNavigator;
import com.nqadmin.swingSet.SSTextArea;
import com.nqadmin.swingSet.SSTextField;
import com.nqadmin.swingSet.datasources.SSJdbcRowSetImpl;



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

	/**
	 * serial version ID
	 */
	private static final long serialVersionUID = -5759888818077113192L;

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR ATTRIBUTE SCREEN
		Container desktop = null;

	// INITIALIZE DATABASE CONNECTIVITY COMPONENTS FOR ATTRIBUTE SCREEN
		DBConnector dbc = null;
		SSJdbcRowSetImpl rowset = null;

	// INITIALIZE ATTRIBUTE SCREEN WIDGETS
		SSTextField txtAttributeID 		= new SSTextField();
		SSTextField txtDescription 		= new SSTextField();
		SSComboBox cmbIncludeCode 		= new SSComboBox();
		SSComboBox cmbTypeCode 			= new SSComboBox();
		SSTextField txtClassName 		= new SSTextField();
		SSTextArea txtNotes 			= new SSTextArea(20,10);

		JButton btnClose				= new JButton("Close");

	// INITIALIZE DATA NAVIGATOR
		SSDataNavigator dataNavigator = null;


	/**
	 * AttributeScreen constructor.
	 *
	 * @param _desktop the container in which the screen has to showup.
	 * @param _dbc connection to ebla_data database
	 */
	public AttributeScreen(Container _desktop, DBConnector _dbc) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE EXPERIENCE SCREEN
			super("EBLA - Attribute Screen",false,true,true,true);

		// SET SIZE
			setSize(640,480);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

		// SET DATABASE CONNECTION
			dbc = _dbc;

		// ROWSET CONFIGURATION
			try {

			// INITIALIZE ROWSET FOR ATTRIBUTE LIST DATA
				rowset = new SSJdbcRowSetImpl(dbc.getSSConnection());

				rowset.setCommand("SELECT * FROM attribute_list_data WHERE attribute_list_id>0;");
				dataNavigator = new SSDataNavigator(rowset);
				dataNavigator.setDBNav(new SSDBNavImp(getContentPane()));

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
			txtAttributeID.bind(rowset,"attribute_list_id");

			txtDescription.bind(rowset,"description");

			cmbIncludeCode.setPredefinedOptions(SSComboBox.YES_NO_OPTION);
			cmbIncludeCode.bind(rowset,"include_code");

			String[] tmpString = {"Object", "Object-Object Relation"};
			cmbTypeCode.setOptions(tmpString);
			cmbTypeCode.bind(rowset,"type_code");

			txtClassName.bind(rowset,"class_name");

			txtNotes.bind(rowset,"notes");


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			int currentRow = 0;
			GridBagConstraints constraints = new GridBagConstraints();
			Border emptySpace = BorderFactory.createEmptyBorder(0, 0, 10, 0);


		// CREATE/LAYOUT PANEL FOR WIDGETS
			EBLAPanel panel  = new EBLAPanel();
			panel.setLayout(new GridBagLayout());

			panel.addRow(txtAttributeID, currentRow++, "Attribute ID");
			panel.addRow(txtDescription, currentRow++, "Description");
			panel.addRow(cmbIncludeCode, currentRow++, "Include Attribute?");
			panel.addRow(cmbTypeCode, currentRow++, "Attribute Type");
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
			cmbTypeCode.setEnabled(false);
			txtClassName.setEnabled(false);


	} // end of AttributeScreen constructor



	/**
	 * Adds the attribute screen to the specified container at the specified position.
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
				if (components[i] instanceof AttributeScreen) {
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
	 * Shows the experience screen at the default location on the specified container.
	 *
	 * @param _container    the container in which the screen has to showup.
	 */
	public void showUp(Container _container) {
		showUp(_container, 30,30);
	} // end showUp()


} // end of AttributeScreen class



/*
 * $Log$
 * Revision 1.13  2011/04/25 03:52:10  yoda2
 * Fixing compiler warnings for Generics, etc.
 *
 * Revision 1.12  2005/02/17 23:33:45  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.11  2005/02/16 02:36:06  yoda2
 * Began updating EBLA GUI to work with SwingSet 1.0 RC.
 *
 * Revision 1.10  2004/02/25 21:58:39  yoda2
 * Updated copyright notice.
 *
 * Revision 1.9  2004/01/09 14:22:31  yoda2
 * Modified screens to use a single database connection.
 *
 * Revision 1.8  2004/01/07 21:33:00  yoda2
 * Updated labels, changed fields for include_code and type_code to SSComboBoxes, and disabled Attribute Type and Attribute Calculation Class fields (not currently used).
 *
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