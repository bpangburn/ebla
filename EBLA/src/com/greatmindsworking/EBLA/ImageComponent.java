/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2004, Brian E. Pangburn
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



package com.greatmindsworking.EBLA;



import java.awt.*;
import java.awt.image.*;
import javax.swing.*;



/**
 * <pre>
 * ImageComponent.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * ImageComponent is used to create a Swing JComponent containing a BufferedImage.
 *<p>
 * It will repaint the image as necessary when it's parent paint() method is called.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class ImageComponent extends JComponent {
	/**
	 * BufferedImage to be drawn on JComponent
	 */
  	private BufferedImage icImage = null;



	/**
	 * Class constructor that sets the BufferedImage and it's width and height
	 *
	 * @param _icImage		BufferedImage to be drawn on JComponent
	 */
	public ImageComponent(BufferedImage _icImage) {

		try {

			// POINT INTERNAL IMAGE TO IMAGE PASSED
				icImage = _icImage;

			// SET COMPONENT SIZE BASED ON IMAGE SIZE
				setPreferredSize(new Dimension(icImage.getWidth(), icImage.getHeight()));

	  	} catch (Exception e) {
			System.out.println("\n--- ImageComponent Constructor Exception ---\n");
			System.out.println("Message:    " + e.getMessage());
			System.out.println(e.toString());
			System.out.println("");
		}

	} // end ImageComponent()



	/**
	 * Updates the image for an existing object along with it's width and height
	 *
	 * @param _icImage		BufferedImage to be update
	 */
	public void updateImage(BufferedImage _icImage) {

		try {

			// POINT INTERNAL IMAGE TO IMAGE PASSED
				icImage = _icImage;

			// SET COMPONENT SIZE BASED ON IMAGE SIZE
				setPreferredSize(new Dimension(icImage.getWidth(), icImage.getHeight()));

	  	} catch (Exception e) {
			System.out.println("\n--- ImageComponent.updateImage() Exception ---\n");
			System.out.println("Message:    " + e.getMessage());
			System.out.println(e.toString());
			System.out.println("");
		}

	} // end updateImage()



	/**
	 * Overrides paint() method with specific instructions to draw BufferedImage
	 *
	 * @param _g			Graphics context on which to draw
	 */
	public void paint(Graphics _g) {

		try {

			// DRAW BUFFEREDIMAGE AT X=0, Y=0
    			_g.drawImage(icImage, 0, 0, null);

	  	} catch (Exception e) {
			System.out.println("\n--- ImageComponent.paint() Exception ---\n");
			System.out.println("Message:    " + e.getMessage());
			System.out.println(e.toString());
			System.out.println("");
		}

	} // end paint()

} // end ImageComponent class



/*
 * Revision history:
 * 	03-06-2001 	- 0.01 - initial coding
 *  04-21-2001  - 1.00 - cleaned up code and fleshed out JavaDOC comments
 *  02-04-2002  - 1.1  - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.5  2002/12/11 22:53:24  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.4  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.3  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.2  2002/02/05 20:28:03  bpangburn
 * Added additional CVS keywords for id and log to first comment block and added author, revision, & date to second comment block for integration with JavaDoc.
 *
 */

