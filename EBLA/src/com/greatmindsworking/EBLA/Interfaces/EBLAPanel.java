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

		setPreferredSize(EBLADims.getPanelAreaDimension());

	} // end EBLAPanel constructor



	/**
	 * Method to add a component to the panel using the default dimensions.
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

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumLabelDimension());
					_component.setMaximumSize(EBLADims.getMediumLabelDimension());
			} else if (_component instanceof JTextField) {
			// SINGLE-LINE TEXTBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumTextFieldDimension());
					_component.setMaximumSize  (EBLADims.getMediumTextFieldDimension());
			} else if (_component instanceof JButton){
			// BUTTON
				// border
					_component.setBorder(softBevelBorder);

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumButtonDimension());
					_component.setMaximumSize  (EBLADims.getMediumButtonDimension());
			} else if (_component instanceof JComboBox) {
			// COMBOBOX
				// border
					_component.setBorder(softBevelBorder);

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumTextFieldDimension());
					_component.setMaximumSize  (EBLADims.getMediumTextFieldDimension());
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
	 * @param _component widget to add to panel
	 * @param _constraints (grid bag) constraints for widget
	 */
	public void add(JComponent _component, java.lang.Object _constraints) {

		// SET WIDGET OPTIONS BASED ON TYPE
			if (_component instanceof JLabel){
			// LABEL
				// border
					_component.setBorder(softBevelBorder);

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumLabelDimension());
					_component.setMaximumSize(EBLADims.getMediumLabelDimension());
			} else if (_component instanceof JTextField) {
			// SINGLE-LINE TEXTBOX
				// border
				//	_component.setBorder(softBevelBorder);

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumTextFieldDimension());
					_component.setMaximumSize  (EBLADims.getMediumTextFieldDimension());
			} else if (_component instanceof JButton){
			// BUTTON
				// border
					_component.setBorder(softBevelBorder);

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumButtonDimension());
					_component.setMaximumSize  (EBLADims.getMediumButtonDimension());
			} else if (_component instanceof JComboBox) {
			// COMBOBOX
				// border
					_component.setBorder(softBevelBorder);

				// preferred and maximum dimensions
					_component.setPreferredSize(EBLADims.getMediumTextFieldDimension());
					_component.setMaximumSize  (EBLADims.getMediumTextFieldDimension());
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

} // end EBLAPanel class



/*
 * $Log$
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