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
import java.util.Vector;
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
 * SelectExperiencesScreen.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Screen to display and select experiences for a given set of parameters.
 * Pulls data from parameter_experience_data table for the EBLA graphical
 * user interface (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class SelectExperiencesScreen extends JDialog {

	JList lstExperiencesChoice 	  = null;
	JList lstExperiencesList   	  = null;
	JButton  btnSelectExperiences   = new JButton(">");
	JButton  btnDeselectExperiences = new JButton("<");

	ExperiencesListModel listModel = null;
	SelectedExperiencesListModel selectedExperiencesModel = null;

	long parameterID = -1;

	public SelectExperiencesScreen(long _parameterID){
		//super("Select Experiences",true,true,true,true);
		setTitle("Select Experiences");
		setSize(600,400);

		parameterID = _parameterID;
		System.out.println("parameterID = " + parameterID);
		listModel = new ExperiencesListModel();
		lstExperiencesChoice = new JList(listModel);
		selectedExperiencesModel = new SelectedExperiencesListModel(_parameterID);
		lstExperiencesList = new JList(selectedExperiencesModel);

		btnSelectExperiences.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				long[] ids = listModel.getExperienceIDs(lstExperiencesChoice.getSelectedIndices());
				Statement statement = null;
				DBConnector connector = null;
				try{
					connector = new DBConnector(EBLAGui.dbFileName,true);
					statement = connector.getStatement();
				}catch(Exception e){
					e.printStackTrace();
				}
				for(int i=0;i<ids.length;i++){
					System.out.println(ids[i]);
					try{
						statement.executeUpdate("INSERT INTO parameter_experience_data(parameter_id,experience_id) VALUES(" + parameterID +"," + ids[i] + ");");
					}catch(SQLException se){
						se.printStackTrace();
					}
				}
				connector.closeConnection();
				selectedExperiencesModel = null;
				selectedExperiencesModel = new SelectedExperiencesListModel(parameterID);
				lstExperiencesList.setModel(selectedExperiencesModel);
			}
		});

		btnDeselectExperiences.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				long[] ids = selectedExperiencesModel.getParameterExperienceIDs(lstExperiencesList.getSelectedIndices());
				Statement statement = null;
				DBConnector connector = null;
				try{
					connector = new DBConnector(EBLAGui.dbFileName,true);
					statement = connector.getStatement();
				}catch(Exception e){
					e.printStackTrace();
				}
				String strIDs = null;
				for(int i=0;i<ids.length;i++){
					System.out.println(ids[i]);
					if( i==0)
						strIDs = "(" + ids[i];
					else
						strIDs = strIDs + "," + ids[i];

				}
				strIDs = strIDs + ")";
				try{
					statement.executeUpdate("DELETE FROM parameter_experience_data WHERE parameter_experience_id IN " + strIDs + ";" );
					connector.closeConnection();
				}catch(SQLException se){
					se.printStackTrace();
				}

				selectedExperiencesModel = null;
				selectedExperiencesModel = new SelectedExperiencesListModel(parameterID);
				lstExperiencesList.setModel(selectedExperiencesModel);
			}
		});

		JScrollPane scrollPane1 = new JScrollPane(lstExperiencesChoice);
		scrollPane1.setPreferredSize(new Dimension(250,300));
		JScrollPane scrollPane2 = new JScrollPane(lstExperiencesList);
		scrollPane2.setPreferredSize(new Dimension(250,300));

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 4;
		contentPane.add(scrollPane1,constraints);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		contentPane.add(btnSelectExperiences,constraints);
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridheight = 1;
		contentPane.add(btnDeselectExperiences,constraints);
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridheight = 4;
		contentPane.add(scrollPane2,constraints);
	}

	public SelectExperiencesScreen(){
	}

	public void setParameterID(long _parameterID){
		parameterID = _parameterID;
		selectedExperiencesModel = null;
		selectedExperiencesModel = new SelectedExperiencesListModel(parameterID);
		lstExperiencesList.setModel(selectedExperiencesModel);
	}


	private class ExperiencesListModel implements ListModel {


		DBConnector connector  = null;
		Vector experienceName = new Vector();
		Vector experienceID   = new Vector();

		public ExperiencesListModel(){

			try{
				connector = new DBConnector(EBLAGui.dbFileName,true);
				Statement statement = connector.getStatement();
				String query = "SELECT description, notes, experience_id FROM experience_data"
					+ " ORDER BY description;";
				ResultSet rs = statement.executeQuery(query);
				while(rs.next()){
					experienceName.add(rs.getString("description") + "--" + rs.getString("notes") );
					experienceID.add(new Long(rs.getLong("experience_id")));
				}
			}catch(SQLException se){
				se.printStackTrace();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		public long[] getExperienceIDs(int[] experiences){
			long[] experienceIDs = new long[experiences.length];
			for(int i=0;i<experiences.length;i++){
				experienceIDs[i] = ((Long)experienceID.elementAt(experiences[i])).longValue();


			}
			return experienceIDs;
		}

		public Object getElementAt(int index){
			return experienceName.elementAt(index);
		}

		public int getSize(){
			return experienceName.size();
		}

		public void addListDataListener(ListDataListener l) {
		}

		public void removeListDataListener(ListDataListener l) {
		}

	}

	private class SelectedExperiencesListModel implements ListModel {


		DBConnector connector  = null;
		Vector experienceName = new Vector();
		Vector parameterExperienceID   = new Vector();

		public SelectedExperiencesListModel(long parameterID){

			try{
				connector = new DBConnector(EBLAGui.dbFileName,true);
				Statement statement = connector.getStatement();
				String query = "SELECT description, notes, parameter_experience_id"
					+ " FROM parameter_experience_data,experience_data"
					+ " WHERE experience_data.experience_id = parameter_experience_data.experience_id"
					+ " AND parameter_id=" + parameterID
					+ " ORDER BY description;";
				ResultSet rs = statement.executeQuery(query);
				while(rs.next()){
					experienceName.add(rs.getString("description") + "--" + rs.getString("notes") );
					parameterExperienceID.add(new Long(rs.getLong("parameter_experience_id")));
				}
			}catch(SQLException se){
				se.printStackTrace();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		public long[] getParameterExperienceIDs(int[] experiences){
			long[] experienceIDs = new long[experiences.length + 1];
			for(int i=0;i<experiences.length;i++){
				experienceIDs[i] = ((Long)parameterExperienceID.elementAt(experiences[i])).longValue();
			}
			return experienceIDs;
		}

		public Object getElementAt(int index){
			return experienceName.elementAt(index);
		}

		public int getSize(){
			return experienceName.size();
		}

		public void addListDataListener(ListDataListener l) {
		}

		public void removeListDataListener(ListDataListener l) {
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
				if(components[i] instanceof ExperienceScreen ) {
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
} // end of SelectExperiencesScreen class



/*
 * $Log$
 */