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
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;



/**
 * EBLAPanel.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Helper class to setup layout of panels on various screens for
 * for the EBLA graphical user interface (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class EBLAPanel extends JPanel {

	/**
	 * custom widget dimensions for EBLA
	 */
	Dimensions EBLADims = new Dimensions();

	/**
	 * widget border for EBLA
	 */
	SoftBevelBorder softBevelBorder = new SoftBevelBorder(SoftBevelBorder.RAISED);



	/**
	 * Constructs a panel for the EBLA GUI.
	 */
	public EBLAPanel() {

	} // end EBLAPanel constructor



	/**
	 * Method to add a component to the panel using the default dimensions.
	 *
	 * For consistent behavior, both minimum and preferred size are set for all
	 * widgets.
	 *
	 * @param _component widget to add to panel
	 *
	 * @return new component added to panel
	 */
	public Component add(JComponent _component) {

		// SET WIDGET OPTIONS BASED ON TYPE
			if (_component instanceof JLabel){
			// LABEL
				// border
					_component.setBorder(softBevelBorder);

				// set minimum & preferred dimensions
					_component.setMinimumSize(EBLADims.getMediumLabelDimension());
					_component.setPreferredSize(EBLADims.getMediumLabelDimension());
			} else if (_component instanceof JTextField) {
			// SINGLE-LINE TEXTBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// set minimum and preferred dimensions
					_component.setMinimumSize(EBLADims.getMediumTextFieldDimension());
					_component.setPreferredSize(EBLADims.getMediumTextFieldDimension());
			} else if (_component instanceof JButton){
			// BUTTON
				// border
					_component.setBorder(softBevelBorder);

				// set minimum and preferred dimensions
					_component.setMinimumSize  (EBLADims.getMediumButtonDimension());
					_component.setPreferredSize  (EBLADims.getMediumButtonDimension());
			} else if (_component instanceof JComboBox) {
			// COMBOBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// set minimum and preferred dimensions
					_component.setMinimumSize  (EBLADims.getMediumTextFieldDimension());
					_component.setPreferredSize  (EBLADims.getMediumTextFieldDimension());
			} else if (_component instanceof JTextArea) {
			// MULTI-LINE TEXTBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// line wrap options
					((JTextArea)_component).setLineWrap(true);
					((JTextArea)_component).setWrapStyleWord(true);

				// scrollpane options
					int columns = ((JTextArea)_component).getColumns();
					int rows = ((JTextArea)_component).getRows();

					JScrollPane scrollPane = new JScrollPane((JTextArea)_component);

					if ((columns > 1) && (rows > 1)) {
						scrollPane.setPreferredSize(new Dimension(columns*10, rows*20));
					} else {
						scrollPane.setPreferredSize(new Dimension(3,15));
					}

					scrollPane.setMinimumSize(EBLADims.getMediumTextAreaDimension());

					return super.add(scrollPane);
			} // end if

		// ADD WIDGET AND RETURN FOR ALL EXCEPT MULTI-LINE TEXTBOX
			if (! (_component instanceof JTextArea)) {
				return super.add((Component)_component);
			}

		// THIS STATEMENT IS NOT REQUIRED BUT THE RETURN STATEMENT IS REQUIRED BY THE COMPILER
			return _component;

	} // end public Component add



	/**
	 * Method to add a component to the panel with the specified (grid bag) constraints.
	 *
	 * Can use either the custom EBLA dimensions or the default JPanel dimensions.
	 *
	 * @param _component widget to add to panel
	 * @param _constraints (grid bag) constraints for widget
	 * @param _setDimensions boolean indicating whether or not to use custom EBLA dimensions
	 *
	 * @return new component added to panel
	 */
	public void add(JComponent _component, java.lang.Object _constraints, boolean _setDimensions) {

		if (_setDimensions == false) {
			super.add((Component)_component, _constraints);
		} else {
			add(_component, _constraints);
		}
	} // end public void add



	/**
	 * Method to add a component to the panel with the specified (grid bag) constraints.
	 *
	 * For consistent behavior, both minimum and preferred size are set for all
	 * widgets.
	 *
	 * @param _component widget to add to panel
	 * @param _constraints (grid bag) constraints for widget
	 */
	public void add(JComponent _component, java.lang.Object _constraints) {

		// SET WIDGET OPTIONS BASED ON TYPE
			if (_component instanceof JLabel){
			// LABEL
				// border
					_component.setBorder(softBevelBorder);

				// set minimum and preferred dimensions
					_component.setMinimumSize(EBLADims.getMediumLabelDimension());
					_component.setPreferredSize(EBLADims.getMediumLabelDimension());
			} else if (_component instanceof JTextField) {
			// SINGLE-LINE TEXTBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// set minimum and preferred dimensions
					_component.setMinimumSize(EBLADims.getMediumTextFieldDimension());
					_component.setPreferredSize(EBLADims.getMediumTextFieldDimension());
			} else if (_component instanceof JButton){
			// BUTTON
				// border
					_component.setBorder(softBevelBorder);

				// set minimum and preferred dimensions
					_component.setMinimumSize(EBLADims.getMediumButtonDimension());
					_component.setPreferredSize(EBLADims.getMediumButtonDimension());
			} else if (_component instanceof JComboBox) {
			// COMBOBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// set minimum and preferred dimensions
					_component.setMinimumSize(EBLADims.getMediumTextFieldDimension());
					_component.setPreferredSize(EBLADims.getMediumTextFieldDimension());
			} else if (_component instanceof JTextArea) {
			// MULTI-LINE TEXTBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// line wrap options
					((JTextArea)_component).setLineWrap(true);
					((JTextArea)_component).setWrapStyleWord(true);

				// scrollpane options
					int columns = ((JTextArea)_component).getColumns();
					int rows = ((JTextArea)_component).getRows();

					JScrollPane scrollPane = new JScrollPane((JTextArea)_component);

					if ((columns > 1) && (rows > 1)) {
						scrollPane.setPreferredSize(new Dimension(columns*10, rows*20));
					} else {
						scrollPane.setPreferredSize(new Dimension(3,15));
					}

					scrollPane.setMinimumSize(EBLADims.getMediumTextAreaDimension());

					super.add(scrollPane, _constraints);
			} // end if

		// ADD WIDGET AND RETURN FOR ALL EXCEPT MULTI-LINE TEXTBOX
			if (! (_component instanceof JTextArea)) {
				super.add((Component)_component, _constraints);
			}

	} // end public void add



	/**
	 * Method to add a component and corresponding label to the specified grid row of a panel
	 *
	 * @param _component widget to add to panel
	 * @param _row panel row (1st row is 0) to add widget and label to
	 * @param _labelText text for label
	 */
	public void addRow(JComponent _component, int _row, String _labelText) {

		// CREATE LABEL
			JLabel tmpLabel	= new JLabel(_labelText);

		// GENERATE CONSTRAINTS FOR POSITIONING WIDGETS
			GridBagConstraints constraints = new GridBagConstraints();

		// SET LABEL POSITION
			constraints.gridx = 0;
			constraints.gridy = _row;

		// ADD LABEL
			add(tmpLabel, constraints);

		// SET WIDGET POSITION
			constraints.gridx++;

		// ADD WIDGET
			add(_component, constraints);

		// ADD LISTENER TO BRING FOCUS TO TEXT FIELDS WHEN MOUSE IS MOVED OVER LABEL
			if (_component instanceof JTextField || _component instanceof JTextArea) {
				tmpLabel.addMouseListener(new MyMouseListener(_component));
			}

	} // end public void addRow



	protected class MyMouseListener implements MouseListener {

		Component component = null;

		public MyMouseListener(Component _component){
			component = _component;
		}

		public void mouseClicked(MouseEvent ae) {
			if (component instanceof JTextField) {
				((JTextField)component).requestFocus();
				((JTextField)component).selectAll();
			} else {
				((JTextArea)component).requestFocus();
			}
		}

		public void mouseEntered(MouseEvent me){
			if (component instanceof JTextField) {
				((JTextField)component).requestFocus();
				((JTextField)component).selectAll();
			} else {
				((JTextArea)component).requestFocus();
			}
		}

		public void mouseExited(MouseEvent me){
		}

		public void mousePressed(MouseEvent me){
		}

		public void mouseReleased(MouseEvent me){
		}
	}



} // end EBLAPanel class



/*
 * $Log$
 * Revision 1.5  2003/12/23 23:18:47  yoda2
 * Continued code cleanup.
 * Discovered that both PreferredSize and MinimumSize must be set to generate consistent widget widths across all tabs on a given screen.
 * Added addRow() method to EBLAPanel to auto-add labels to widgets on a given panel.
 *
 * Revision 1.4  2003/12/03 02:04:21  yoda2
 * Widget border tweaks.
 *
 * Revision 1.3  2003/12/02 04:24:46  yoda2
 * JavaDoc and code cleanup.
 *
 * Revision 1.2  2003/09/23 02:07:45  yoda2
 * Code cleanup & documentation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */