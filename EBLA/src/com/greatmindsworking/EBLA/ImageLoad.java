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



import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;



/**
 * <pre>
 * ImageLoad.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * ImageLoad is used to create a BufferedImage from the specified image file.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class ImageLoad {



	/**
	 * Load specified image using Java ImageIO API and store in BufferedImage
	 *
	 * @param _imageFile	file path to source image file
	 *
	 * @return buffered image for specified file
	 */
	public static BufferedImage loadImage(String _imageFile) {

		// 	DECLARATIONS
			BufferedImage tmpImage = null;		// IMAGE LOADED WITH JAVA IMAGE I/O
			int rgbData[];						// RGB PIXEL DATA FROM tmpImage
			BufferedImage loadedImage = null;	// RGB IMAGE CREATED FROM rgbData ARRAY
			int w = 0;							// WIDTH OF IMAGE
			int h = 0;							// HEIGHT OF IMAGE


		try {

			// LOAD tmpImage USING JAVA ImageIO API
				tmpImage = ImageIO.read(new File(_imageFile));

			// DETERMINE IMAGE DIMENSIONS
				w=tmpImage.getWidth();
				h=tmpImage.getHeight();

			// INITIALIZE ARRAY OF RGB PIXEL VALUES
				rgbData = new int[w * h];

			// EXTRACT RGB PIXEL DATA FROM tmpImage
				tmpImage.getRGB(0, 0, w, h, rgbData, 0, w);

			// INITIALIZE loadedImage AS TYPE_INT_RGB
				loadedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

			// SET PIXEL VALUES FOR loadedImage
				loadedImage.setRGB(0, 0, w, h, rgbData, 0, w);


	  	} catch (Exception e) {
			System.out.println("\n--- ImageLoad.loadImage() Exception ---\n");
			System.out.println("Message:    " + e.getMessage());
			System.out.println(e.toString());
			System.out.println("");
		}

		// RETURN LOADED IMAGE
			return(loadedImage);

	} // end loadImage()

} // end ImageLoad class



/*
 * Revision history:
 * 	03-06-2001 	- 0.01 - initial coding
 *  04-21-2001  - 1.00 - cleaned up code and fleshed out JavaDOC comments
 *  02-04-2002  - 1.1  - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.5  2002/12/11 22:54:06  yoda2
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