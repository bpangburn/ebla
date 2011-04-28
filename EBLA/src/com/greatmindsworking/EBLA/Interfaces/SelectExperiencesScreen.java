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
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.sql.*;
import java.beans.PropertyVetoException;
import com.greatmindsworking.utils.DBConnector;



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
public class SelectExperiencesScreen extends JInternalFrame {

	/**
	 * serial version ID
	 */
	private static final long serialVersionUID = 5463455717167911267L;

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR EXPERIENCE SCREEN
		Container desktop = null;

	// INITIALIZE DATABASE CONNECTIVITY COMPONENTS FOR SELECT EXPERIENCES SCREEN
		DBConnector dbc = null;

	// INITIALIZE EXPERIENCE SELECTION SCREEN WIDGETS
		JList lstAvailableExperiences 		= null;
		JList lstSelectedExperiences   	  	= null;
		JButton btnAddExperiences   		= new JButton(">");
		JButton btnRemoveExperiences 		= new JButton("<");
		JButton btnClose 					= new JButton("Close");

	// INITIALIZE LIST MODELS
		AvailableExperiencesListModel availableListModel = null;
		SelectedExperiencesListModel selectedListModel = null;

	// INITIALIZE ID OF PARENT parameter_data RECORD
		long parameterID = -1;



	/**
	 * SelectExperiencesScreen constructor.
	 *
	 * @param _desktop    the container in which the screen has to showup.
	 * @param _dbc    connection to ebla_data database
	 * @param _parameterID    ID of parent parameter_data record
	 */
	public SelectExperiencesScreen(Container _desktop, DBConnector _dbc, long _parameterID) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE PARAMETER SCREEN
			super("EBLA - Select Experiences Screen",false,false,false,false);

		// SET SIZE
			setSize(640,480);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

		// SET DATABASE CONNECTION
			dbc = _dbc;

		// SET PARENT parameter ID
			parameterID = _parameterID;

		// INITIALIZE LIST MODELS AND WIDGETS WITH SCROLL PANES
			availableListModel = new AvailableExperiencesListModel();
			lstAvailableExperiences = new JList(availableListModel);
			JScrollPane availableScrollPane = new JScrollPane(lstAvailableExperiences);
			availableScrollPane.setPreferredSize(new Dimension(250,300));

			selectedListModel = new SelectedExperiencesListModel(_parameterID);
			lstSelectedExperiences = new JList(selectedListModel);
			JScrollPane selectedScrollPane = new JScrollPane(lstSelectedExperiences);
			selectedScrollPane.setPreferredSize(new Dimension(250,300));

		// CREATE ACTION LISTENER FOR ADD BUTTON
			btnAddExperiences.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					long[] ids = availableListModel.getExperienceIDs(lstAvailableExperiences.getSelectedIndices());
					Statement statement = null;

					try {
						statement = dbc.getStatement();
					} catch(Exception e) {
						e.printStackTrace();
					}

					try {

						for (int i=0;i<ids.length;i++) {
							System.out.println(ids[i]);
							statement.executeUpdate("INSERT INTO parameter_experience_data(parameter_id,experience_id) VALUES(" + parameterID +"," + ids[i] + ");");
						}

						statement.close();
					} catch(SQLException se) {
						se.printStackTrace();
					}

					selectedListModel = null;
					selectedListModel = new SelectedExperiencesListModel(parameterID);
					lstSelectedExperiences.setModel(selectedListModel);

				} // end actionPerformed()
			});


		// CREATE ACTION LISTENER FOR REMOVE BUTTON
			btnRemoveExperiences.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					long[] ids = selectedListModel.getParameterExperienceIDs(lstSelectedExperiences.getSelectedIndices());
					Statement statement = null;

					try {
						statement = dbc.getStatement();
					} catch(Exception e) {
						e.printStackTrace();
					}

					String strIDs = null;
					for(int i=0;i<ids.length;i++) {
						System.out.println(ids[i]);
						if( i==0)
							strIDs = "(" + ids[i];
						else
							strIDs = strIDs + "," + ids[i];

					}

					strIDs = strIDs + ")";

					try {
						statement.executeUpdate("DELETE FROM parameter_experience_data WHERE parameter_experience_id IN " + strIDs + ";" );
						statement.close();
					} catch(SQLException se) {
						se.printStackTrace();
					}

					selectedListModel = null;
					selectedListModel = new SelectedExperiencesListModel(parameterID);
					lstSelectedExperiences.setModel(selectedListModel);

				} // end actionPerformed()
			});


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


		// ADD INTERNAL FRAME LISTENER TO FORM TO CHECK FOR LOSS OF FOCUS
			addInternalFrameListener(new InternalFrameAdapter() {
			// FRAME DEACTIVATED
				public void internalFrameDeactivated(InternalFrameEvent ife) {
					SwingUtilities.invokeLater(new Thread() {
						public void run() {
							SelectExperiencesScreen.this.moveToFront();
							SelectExperiencesScreen.this.requestFocus();
							try {
								SelectExperiencesScreen.this.setSelected(true);
							} catch(PropertyVetoException pve) {
								System.out.println(pve.getMessage());
							}

						}
					});
				} // end internalFrameDeactivated()

			});


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			GridBagConstraints constraints = new GridBagConstraints();
			Border emptySpace = BorderFactory.createEmptyBorder(0, 0, 10, 0);


		// CREATE/LAYOUT PANEL FOR WIDGETS
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridBagLayout());

			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridheight = 4;
			contentPane.add(availableScrollPane,constraints);

			constraints.gridx = 1;
			constraints.gridy = 2;
			constraints.gridheight = 1;
			contentPane.add(btnAddExperiences,constraints);

			constraints.gridx = 1;
			constraints.gridy = 3;
			constraints.gridheight = 1;
			contentPane.add(btnRemoveExperiences,constraints);

			constraints.gridx = 2;
			constraints.gridy = 0;
			constraints.gridheight = 4;
			contentPane.add(selectedScrollPane,constraints);

			EBLAPanel buttonPanel = new EBLAPanel();
			buttonPanel.setBorder(emptySpace);
			buttonPanel.add(btnClose);

			constraints.gridx=0;
			constraints.gridwidth = 3;
			constraints.gridy=4;
			constraints.gridheight = 1;

			contentPane.add(buttonPanel,constraints);

	} // end SelcectExperiencesScreen constructor



	/**
	 * Method to reset the ID of the parent parameter_data record and reset
	 * the available/selected lists accordingly.
	 *
	 * @param _parameterID    ID of parent parameter_data record
	 */
	public void setParameterID(long _parameterID) {
		parameterID = _parameterID;
		selectedListModel = null;
		selectedListModel = new SelectedExperiencesListModel(parameterID);
		lstSelectedExperiences.setModel(selectedListModel);
	} // end setParameterID method



	/**
	 * Private class to build the list of available experiences.
	 */
	private class AvailableExperiencesListModel implements ListModel {

		DBConnector connector = null;
		Vector<String> experienceName = new Vector<String>();
		Vector<Long> experienceID = new Vector<Long>();

		public AvailableExperiencesListModel(){

			try {
				connector = new DBConnector(EBLAGui.dbFileName,true);
				Statement statement = connector.getStatement();
				String query = "SELECT description, notes, experience_id FROM experience_data"
					+ " ORDER BY description;";
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					experienceName.add(rs.getString("description") + "--" + rs.getString("notes") );
					experienceID.add(new Long(rs.getLong("experience_id")));
				} // end while
				rs.close();
				statement.close();

			} catch(SQLException se) {
				se.printStackTrace();
			} catch(IOException ioe){
				ioe.printStackTrace( );
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		public long[] getExperienceIDs(int[] experiences) {
			long[] experienceIDs = new long[experiences.length];
			for (int i=0;i<experiences.length;i++) {
				experienceIDs[i] = experienceID.elementAt(experiences[i]);
			}
			return experienceIDs;
		}

		public Object getElementAt(int index) {
			return experienceName.elementAt(index);
		}

		public int getSize() {
			return experienceName.size();
		}

		public void addListDataListener(ListDataListener l) {
		}

		public void removeListDataListener(ListDataListener l) {
		}

	} // end AvailableExperiencesListModel class


	/**
	 * Private class to build the list of selected experiences.
	 */
	private class SelectedExperiencesListModel implements ListModel {

		//DBConnector connector  = null;
		Vector<String> experienceName = new Vector<String>();
		Vector<Long> parameterExperienceID   = new Vector<Long>();

		public SelectedExperiencesListModel(long parameterID){

			try {
				Statement statement = dbc.getStatement();
				String query = "SELECT description, notes, parameter_experience_id"
					+ " FROM parameter_experience_data,experience_data"
					+ " WHERE experience_data.experience_id = parameter_experience_data.experience_id"
					+ " AND parameter_id=" + parameterID
					+ " ORDER BY description;";
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					experienceName.add(rs.getString("description") + "--" + rs.getString("notes") );
					parameterExperienceID.add(new Long(rs.getLong("parameter_experience_id")));
				}
				rs.close();
				statement.close();
			} catch(SQLException se) {
				se.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		public long[] getParameterExperienceIDs(int[] experiences){
			long[] experienceIDs = new long[experiences.length + 1];
			for (int i=0;i<experiences.length;i++) {
				experienceIDs[i] = parameterExperienceID.elementAt(experiences[i]);
			}
			return experienceIDs;
		}

		public Object getElementAt(int index) {
			return experienceName.elementAt(index);
		}

		public int getSize() {
			return experienceName.size();
		}

		public void addListDataListener(ListDataListener l) {
		}

		public void removeListDataListener(ListDataListener l) {
		}

	} // end SelectedExperiencesListModel class



	/**
	 * Adds the select experiences screen to the specified container at the specified position.
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
				if (components[i] instanceof SelectExperiencesScreen) {
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
	 * Shows the select experiences screen at the default location on the specified container.
	 *
	 * @param _container    the container in which the screen has to showup.
	 */
	public void showUp(Container _container) {
		showUp(_container, 30,30);
	} // end showUp()


} // end of SelectExperiencesScreen class



/*
 * $Log$
 * Revision 1.8  2011/04/25 02:34:51  yoda2
 * Coding for Java Generics.
 *
 * Revision 1.7  2005/02/17 23:33:45  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.6  2004/02/25 21:58:39  yoda2
 * Updated copyright notice.
 *
 * Revision 1.5  2004/01/09 14:22:31  yoda2
 * Modified screens to use a single database connection.
 *
 * Revision 1.4  2003/12/30 23:21:20  yoda2
 * Modified screens so that they are nullifed upon closing and a "fresh" screen is created if a screen is re-opened.
 *
 * Revision 1.3  2003/12/29 23:19:42  yoda2
 * Finished JavaDoc and code cleanup.
 * Simulated modal behavior by retaking focus when lost.
 * Added close button.
 *
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */