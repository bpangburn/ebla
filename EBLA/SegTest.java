/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002, Brian E. Pangburn & Jonathan P. Ayo
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
 * This software is a partial port of the EDISON system developed by
 * Chris M. Christoudias and Bogdan Georgescu at the Robust Image
 * Understanding Laboratory at Rutgers University
 * (http://www.caip.rutgers.edu/riul/).
 *
 * EDISON is available from:
 * http://www.caip.rutgers.edu/riul/research/code/EDISON/index.html
 *
 * It is based on the following references:
 *
 * [1] D. Comanicu, P. Meer: "Mean shift: A robust approach toward feature
 *     space analysis". IEEE Trans. Pattern Anal. Machine Intell., May 2002.
 *
 * [2] P. Meer, B. Georgescu: "Edge detection with embedded confidence".
 *     IEEE Trans. Pattern Anal. Machine Intell., 28, 2001.
 *
 * [3] C. Christoudias, B. Georgescu, P. Meer: "Synergism in low level vision".
 *     16th International Conference of Pattern Recognition, Track 1 - Computer
 *     Vision and Robotics, Quebec City, Canada, August 2001.
 *
 * The above cited papers are available from:
 * http://www.caip.rutgers.edu/riul/research/robust.html
 *
 */



import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import com.greatmindsworking.EDISON.segm.*;



/**
 * SegTest.java
 *
 * SegTest is used to demonstrate the mean shift image segmentation code.
 *
 * @author	$Author$
 * @version	$Revision$
 */
public class SegTest {

    /**
     * Main procedure - allows SegTest to be run from the command line
     *
     * The user can pass a graphics file name and the desired segmentation parameters
     * to the main method which will then perform mean shift based image segmentation
     * on that image.
     */
    public static void main(String[] args) {

		// DECLARATIONS
			String inputFile = "./input.png"; 	// INPUT FILE NAME
			String outputFile = "./segment.png";// OUTPUT FILE NAME

			float colorRadius = (float)6.5; 	// COLOR RADIUS FOR MEAN SHIFT ANALYSIS IMAGE SEGMENTATION
			int spatialRadius = 7; 				// SPATIAL RADIUS FOR MEAN SHIFT ANALYSIS IMAGE SEGMENTATION
			int minRegion = 20; 				// MINIMUM NUMBER OF PIXEL THAT CONSTITUTE A REGION FOR
												// MEAN SHIFT ANALYSIS IMAGE SEGMENTATION
			int speedUp = 1; 					// SPEED-UP LEVEL FOR MEAN SHIFT ANALYSIS IMAGE SEGMENTATION
												// 0=NO SPEEDUP, 1=MEDIUM SPEEDUP, 2=HIGH SPEEDUP

		try {

			// CHECK TO SEE SIX PARAMETERS WERE PASSED FROM THE COMMAND LINE
				if (args.length == 5) {
					// EXTRACT SOURCE IMAGE
						inputFile = args[0];

					// EXTRACT COLOR RADIUS
						colorRadius = Float.parseFloat(args[1]);

					// EXTRACT SPATIAL RADIUS
						colorRadius = Integer.parseInt(args[2]);

					// EXTRACT MINIMUM REGION
						minRegion = Integer.parseInt(args[3]);

					// EXTRACT SPEEDUP LEVEL
						speedUp = Integer.parseInt(args[4]);

				} else {
					// TELL USER HOW TO USE TEST CLASS
						System.out.println("Usage: java SegTest <source image> <color radius> <spatial radius>"
							+ " <min region> <speedup: 0=none, 1=medium, 2=high>");
						System.out.println("");
						System.out.println("e.g. java SegTest my_image.png 6.5 7 20 1");
						System.out.println("");
						System.out.println("For more information on mean shift image segmentaton, consult:");
						System.out.println(" [1] D. Comanicu, P. Meer: 'Mean shift: A robust approach toward feature");
						System.out.println("     space analysis'. IEEE Trans. Pattern Anal. Machine Intell., May 2002.");
						System.out.println("");
						System.out.println(" [2] P. Meer, B. Georgescu: 'Edge detection with embedded confidence'.");
						System.out.println("     IEEE Trans. Pattern Anal. Machine Intell., 28, 2001.");
						System.out.println("");
						System.out.println(" [3] C. Christoudias, B. Georgescu, P. Meer: 'Synergism in low level vision'.");
						System.out.println("     16th International Conference of Pattern Recognition, Track 1 - Computer");
						System.out.println("     Vision and Robotics, Quebec City, Canada, August 2001.");
						System.out.println("");
						System.out.println(" The above cited papers are available from:");
						System.out.println(" http://www.caip.rutgers.edu/riul/research/robust.html");

					// EXIT
						System.exit(0);
				}


			// LOAD SOURCE IMAGE USING LOADER CLASS
				BufferedImage tmpImage = ImageIO.read(new File(inputFile));

			// DETERMINE WIDTH AND HEIGHT
				int width = tmpImage.getWidth();
				int height = tmpImage.getHeight();

			// CROP IMAGE
				tmpImage = tmpImage.getSubimage(5, 5, width-5, height-5);

			// RECALCULATE WIDTH AND HEIGHT
				width = tmpImage.getWidth();
				height = tmpImage.getHeight();

			// DETERMINE NUMBER OF PIXELS
				int pixelCount = width * height;

			// INITIALIZE ARRAYS FOR RGB PIXEL VALUES
				int rgbPixels[] = new int[pixelCount];
				tmpImage.getRGB(0, 0, width, height, rgbPixels, 0, width);

			// CREATE MSImageProcessor OBJECT
				MSImageProcessor mySegm = new MSImageProcessor();

			// SET IMAGE
				mySegm.DefineImage(rgbPixels, ImageType.COLOR, height, width);

			// SEGMENT IMAGE
				if (speedUp == 0) {
					mySegm.Segment(spatialRadius, colorRadius, minRegion, SpeedUpLevel.NO_SPEEDUP);
				} else if (speedUp == 1) {
					mySegm.Segment(spatialRadius, colorRadius, minRegion, SpeedUpLevel.MED_SPEEDUP);
				} else {
					mySegm.Segment(spatialRadius, colorRadius, minRegion, SpeedUpLevel.HIGH_SPEEDUP);
				}

			// GET RESULTING SEGMENTED IMAGE (RGB) PIXELS
				int segpixels[] = new int[pixelCount];
				mySegm.GetResults(segpixels);

			// BUILD BUFFERED IMAGE FROM RGB PIXEL DATA
				BufferedImage segImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				segImage.setRGB(0, 0, width, height, segpixels, 0, width);

			// SAVE BUFFERED IMAGE(S) AS PNG
				ImageIO.write(segImage, "png", new File(outputFile));

			// EXIT
				System.exit(0);

		} catch (Exception e) {
			System.out.println("\n--- SegTest.main() Exception ---\n");
			e.printStackTrace();
		}

    } // end main()

}; // end SegTest class



/*
 * $Log$
 * Revision 1.1  2002/09/20 19:48:04  bpangburn
 * Added stand-alone test class for EDISON port to CVS.
 *
 *
 */