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

	JLabel lblDescription = new JLabel("Description");
	JLabel lblIncludeCode = new JLabel("Include Code");
	JLabel lblTypeCode = new JLabel("Type Code");
	JLabel lblClassName = new JLabel("Class Name");
	JLabel lblNotes = new JLabel("Notes");

	JTextField txtDescription = new JTextField();
	JTextField txtIncludeCode = new JTextField();
	JTextField txtTypeCode = new JTextField();
	JTextField txtClassName = new JTextField();
	JTextField txtNotes = new JTextField();

	Container desktop = null;
	DBConnector connector       = null;
	SSDataNavigator dataNavigator = null;
	JdbcRowSetImpl rowset           = null;

	public AttributeScreen(Container _desktop){

		super("Attributes Form",false,true,true,true);
		setSize(550,400);

		desktop = _desktop;

		try{
			connector = new DBConnector(EBLAGui.dbFileName,true);

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

			rowset = new JdbcRowSetImpl(url, username, password);

			rowset.setCommand("SELECT * FROM attribute_list_data WHERE attribute_list_id>0;");
			dataNavigator = new SSDataNavigator(rowset);
			dataNavigator.setDBNav(new SSDBNavImp(getContentPane()));
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		txtDescription.setDocument(new SSTextDocument(rowset,"description"));
		txtIncludeCode.setDocument(new SSTextDocument(rowset,"include_code"));
		txtTypeCode.setDocument(new SSTextDocument(rowset,"type_code"));
		txtClassName.setDocument(new SSTextDocument(rowset,"class_name"));
		txtNotes.setDocument(new SSTextDocument(rowset,"notes"));

		EBLAPanel panel = new EBLAPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();


		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(lblDescription,constraints);
		constraints.gridy = 1;
		panel.add(lblIncludeCode,constraints);
		constraints.gridy = 2;
		panel.add(lblTypeCode,constraints);
		constraints.gridy = 3;
		panel.add(lblClassName,constraints);
		constraints.gridy = 4;
		panel.add(lblNotes,constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		panel.add(txtDescription,constraints);
		constraints.gridy = 1;
		panel.add(txtIncludeCode,constraints);
		constraints.gridy = 2;
		panel.add(txtTypeCode,constraints);
		constraints.gridy = 3;
		panel.add(txtClassName,constraints);
		constraints.gridy = 4;
		panel.add(txtNotes,constraints);

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		constraints.gridx=0;
		constraints.gridy=0;
		contentPane.add(panel,constraints);
		constraints.gridy=1;
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

} // end of AttributeScreen class



/*
 * $Log$
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */