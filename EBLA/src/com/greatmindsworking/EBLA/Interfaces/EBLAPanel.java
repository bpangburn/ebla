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

	Dimensions dimensions = new Dimensions();

	SoftBevelBorder  softBevelBorder = new SoftBevelBorder(SoftBevelBorder.RAISED);

	public EBLAPanel() {

		// setPreferredSize(new Dimension(450,300));

	}

	public Component add(JComponent component) {

		if (component instanceof JLabel){
		// label
			component.setBorder(softBevelBorder);
			component.setPreferredSize(dimensions.getMediumLabelDimension());
			component.setMaximumSize(dimensions.getMediumLabelDimension());
		} else if (component instanceof JTextField) {
		// single-line textbox
			component.setPreferredSize(dimensions.getMediumTextFieldDimension());
			component.setMaximumSize  (dimensions.getMediumTextFieldDimension());
		} else if (component instanceof JButton){
		// button
			component.setBorder(softBevelBorder);
			component.setPreferredSize(dimensions.getMediumButtonDimension());
			component.setMaximumSize  (dimensions.getMediumButtonDimension());
		} else if (component instanceof JComboBox) {
		// combobox
			component.setPreferredSize(dimensions.getMediumTextFieldDimension());
			component.setMaximumSize  (dimensions.getMediumTextFieldDimension());
		} else if (component instanceof JTextArea) {
		// multi-line textbox
			component.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
			((JTextArea)component).setLineWrap(true);
			((JTextArea)component).setWrapStyleWord(true);

			int columns = ((JTextArea)component).getColumns();
			int rows = ((JTextArea)component).getRows();

			JScrollPane scrollPane = new JScrollPane((JTextArea)component);

			if ((columns > 1) && (rows > 1))
				scrollPane.setPreferredSize(new Dimension(columns*10, rows*20));
			else
				scrollPane.setPreferredSize(new Dimension(3,15));

			scrollPane.setMinimumSize(dimensions.getMediumTextAreaDimension());

			return super.add(scrollPane);
		}

		if (! (component instanceof JTextArea)) {
			return super.add((Component)component);
		}

		// This statement is not required but the return statement is required by the compiler
		return component;

	} // end public Component add


	public void add(JComponent component, java.lang.Object constraints, boolean setDefaults) {

		if ( setDefaults == false) {
			super.add((Component)component, constraints);
		} else {
			add(component, constraints);
		}
	} // end public void add


	public void add(JComponent component, java.lang.Object constraints) {

			if (component instanceof JLabel) {
			// label
				component.setBorder(softBevelBorder);
				component.setPreferredSize(dimensions.getMediumLabelDimension());
				component.setMaximumSize(dimensions.getMediumLabelDimension());
				component.setMinimumSize(dimensions.getMediumLabelDimension());
			} else if (component instanceof JTextField) {
			// single-line textbox
				component.setPreferredSize(dimensions.getMediumTextFieldDimension());
				component.setMaximumSize(dimensions.getMediumTextFieldDimension());
				component.setMinimumSize(dimensions.getMediumTextFieldDimension());
			} else if (component instanceof JButton) {
			// button
				component.setBorder(softBevelBorder);
				component.setPreferredSize(dimensions.getMediumButtonDimension());
				component.setMaximumSize(dimensions.getMediumButtonDimension());
				component.setMinimumSize(dimensions.getMediumButtonDimension());
			} else if (component instanceof JComboBox) {
			// combobox
				component.setPreferredSize(dimensions.getMediumTextFieldDimension());
				component.setMaximumSize(dimensions.getMediumTextFieldDimension());
				component.setMinimumSize(dimensions.getMediumTextFieldDimension());
			} else if (component instanceof JTextArea) {
			// multi-line textbox
				component.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

				((JTextArea)component).setLineWrap(true);
				((JTextArea)component).setWrapStyleWord(true);

				JScrollPane scrollPane = new JScrollPane((JTextArea)component);

				scrollPane.setPreferredSize(dimensions.getMediumTextAreaDimension());
				scrollPane.setMinimumSize(dimensions.getMediumTextAreaDimension());

				super.add(scrollPane, constraints);
			}

			if (! (component instanceof JTextArea)) {
				super.add((Component)component, constraints);
			}

			//return component;

	} // end public void add

} // end EBLAPanel class



/*
 * $Log$
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */