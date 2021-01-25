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



package com.greatmindsworking.ebla.ui;



import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;



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
	 * serial version ID
	 */
	private static final long serialVersionUID = -5167125185772937155L;

	/**
	 * custom widget dimensions for EBLA
	 */
	Dimensions EBLADims = new Dimensions();

	/**
	 * widget border for EBLA
	 */
	SoftBevelBorder softBevelBorder = new SoftBevelBorder(BevelBorder.RAISED);



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

					JScrollPane scrollPane = new JScrollPane(_component);

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
				return super.add(_component);
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
	 */
	public void add(JComponent _component, java.lang.Object _constraints, boolean _setDimensions) {

		if (_setDimensions == false) {
			super.add(_component, _constraints);
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

					JScrollPane scrollPane = new JScrollPane(_component);

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
				super.add(_component, _constraints);
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

		@Override
		public void mouseClicked(MouseEvent ae) {
			if (component instanceof JTextField) {
				((JTextField)component).requestFocus();
				((JTextField)component).selectAll();
			} else {
				((JTextArea)component).requestFocus();
			}
		}

		@Override
		public void mouseEntered(MouseEvent me){
			if (component instanceof JTextField) {
				((JTextField)component).requestFocus();
				((JTextField)component).selectAll();
			} else {
				((JTextArea)component).requestFocus();
			}
		}

		@Override
		public void mouseExited(MouseEvent me){
		}

		@Override
		public void mousePressed(MouseEvent me){
		}

		@Override
		public void mouseReleased(MouseEvent me){
		}
	}



} // end EBLAPanel class



/*
 * $Log$
 * Revision 1.10  2011/04/28 14:55:07  yoda2
 * Addressing Java 1.6 -Xlint warnings.
 *
 * Revision 1.9  2011/04/25 03:52:10  yoda2
 * Fixing compiler warnings for Generics, etc.
 *
 * Revision 1.8  2005/02/17 23:33:45  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.7  2004/02/25 21:58:39  yoda2
 * Updated copyright notice.
 *
 * Revision 1.6  2003/12/24 19:15:02  yoda2
 * Added code to set TextField and TextArea focus when the mouse passes over the corresponding labels.
 * Added code to set Minimum and Preferred size for all widgets.
 *
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