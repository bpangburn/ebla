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
import java.awt.Dimension;



/**
 * Dimensions.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Default widget dimensions for the EBLA graphical user interface (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class Dimensions {

	public Dimension getSmallButtonDimension(){
		return new Dimension(70,30);
	}

	public Dimension getMediumButtonDimension(){
		return new Dimension(125,40);
	}

	public Dimension getLargeButtonDimension(){
		return new Dimension(130,70);
	}

	public Dimension getSmallTextFieldDimension(){
		return new Dimension(100,20);
	}

	public Dimension getMediumTextFieldDimension(){
		return new Dimension(275,20);
	}

	public Dimension getLargeTextFieldDimension(){
		return new Dimension(300,20);
	}

	public Dimension getSmallLabelDimension(){
		return new Dimension(100,20);
	}

	public Dimension getMediumLabelDimension(){
		return new Dimension(200,20);
	}

	public Dimension getLargeLabelDimension(){
		return new Dimension(175,20);
	}

	public Dimension getMediumTextAreaDimension() {
		return new Dimension(275,80);
	}

	public Dimension getPanelAreaDimension() {
		return new Dimension(450,300);
	}

} // end of Dimensions class



/*
 * $Log$
 * Revision 1.2  2003/12/02 04:09:27  yoda2
 * Added default panel dimensions.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */