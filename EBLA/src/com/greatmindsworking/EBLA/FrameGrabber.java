/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2003, Brian E. Pangburn
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
import java.awt.event.*;
import javax.media.*;
import javax.media.control.FramePositioningControl;
import javax.media.util.BufferToImage;
import java.awt.image.*;
import java.io.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import com.greatmindsworking.EBLA.Interfaces.StatusScreen;



/**
 * <pre>
 * FrameGrabber.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * FrameGrabber takes a AVI or MOV file and extracts the frames to PNG files.
 *<p>
 * If AVI files are used, they must be uncompressed or encoded with RGB or
 * MJPG codecs.  If MOV files are used, they must be uncompressed or encoded
 * with RGB or JPEG codecs.  The MPEG file format does not work.
 *<p>
 * FrameGrabber makes extensive use of the Java Media Framework (JMF).
 *<p>
 * FrameGrabber adds a 4-digit extension to each file name so up to 9999
 * images can be processed.
 *<P>
 * @author	$Author$
 * @version	$Revision$
 */
public class FrameGrabber extends JFrame {
	/**
	 * string containing URL to source movie file
	 */
	private String sourceURL;

	/**
	 * string containing directory path and first part of file name for image files generated from frames
	 */
	private String targetPath;

	/**
	 * boolean indicating whether or not to display movie while processing
	 */
	private boolean displayFlag;

	/**
	 * JMF MediaLocator object created for opening movie
	 */
	private MediaLocator ml = null;

	/**
	 * JMF Player object created for manipulating movie
	 */
	private Player player = null;

	/**
	 * JMF FramePositioningControl object for navigating movie frames
	 */
	private FramePositioningControl fpc = null;

	/**
	 * JMF FrameGrabbingControl object for extracting movie frame
	 */
	private FrameGrabbingControl fgc = null;


	/**
	 * EBLA status screen where frames should be displayed as they are ripped
	 */
	private StatusScreen statusScreen = null;



	/**
	 * Class constructor that sets parameters and calls appropriate initialization methods
	 *
	 * @param _sourcePath	directory path and full file name for the source movie file
	 * @param _targetPath	directory path and first part of file name for image files generated from frames
	 * @param _statusScreen	EBLA status window where video should be displayed (if applicable)
	 * @param _displayFlag	flag indicating whether or not to display movie while processing
	 */
	public FrameGrabber(String _sourcePath, String _targetPath, StatusScreen _statusScreen, boolean _displayFlag) {

		try {

			// CONVERT SOURCE PATH TO URL
				sourceURL = "file:" + _sourcePath;

			// SET REMAINING PARAMETERS
				targetPath = _targetPath;
				displayFlag = _displayFlag;

			// CALL FUNCTION TO INITIALIZE PLAYER AND CONTROLS
				if (initializePlayer()==false) {
					System.out.println("Error initializing player and / or controllers ... exiting.");
					System.exit(0);
				}

			// SET STATUS WINDOW
				statusScreen = _statusScreen;

			// CALL FUNCTION TO INITIALIZE DISPLAY
			// IF STATUS WINDOW IS NOT NULL, DON'T INITIALIZE A SEPARATE WINDOW
				if ((displayFlag) && (statusScreen==null)) {
					if (initializeDisplay()==false) {
						System.out.println("Unable to initialize display.");
					}
				}

	  	} catch (Exception e) {
			System.out.println("\n--- FrameGrabber Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end FrameGrabber()



	/**
	 * Creates MediaLocator based on movie URL,
	 * creates Player based on MediaLocator,
	 * and creates FramePositioningControl and FrameGrabbingControl based on Player
	 *
	 * @return flag indicating success of media player initialization
	 */
	private boolean initializePlayer() {

		boolean result = false;

		try {
			// ATTEMPT TO BUILD MEDIA LOCATOR
				if ((ml = new MediaLocator(sourceURL)) == null) {
					System.err.println("Cannot build media locator from: " + sourceURL);
					return false;
				}

			// OUTPUT VIDEO URL
				System.out.println("Extracting frames from: " + sourceURL);

			// CREATE PLAYER FOR MEDIA LOCATOR
				player = Manager.createRealizedPlayer(ml);

			// PREFETCH VIDEO
				player.prefetch();

			// INTIALIZE FRAME POSITIONER
				fpc = (FramePositioningControl) player.getControl("javax.media.control.FramePositioningControl");
				if (fpc == null) {
					System.out.println("The player for this video format does not support the FramePositioningControl.");
					return false;
				}

			// INTIALIZE FRAME GRABBER
				fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
				if (fgc == null) {
					System.out.println("The player for this video format does not support the FrameGrabbingControl.");
					return false;
				}

			// INDICATE SUCCESS
				result = true;

	  	} catch (Exception e) {
			System.out.println("\n--- FrameGrabber.initializePlayer() Exception ---\n");
			e.printStackTrace();
		}

		// INDICATE SUCCESS AND RETURN
			return result;

	} // end initializePlayer()



	/**
	 * Creates window and adds Player's visual component for displaying movie
	 *
	 * @return flag indicating success of display initialization
	 */
	private boolean initializeDisplay() {

		try {

			// USE BORDER LAYOUT
				getContentPane().setLayout(new BorderLayout());

			// ADD VISUAL COMPONENT FOR PLAYER
				Component vc;
				if ((vc = player.getVisualComponent()) != null) {

					// ADD COMPONENT TO CENTER OF FRAME
						getContentPane().add("Center", vc);

					// MAKE BACKGROUND GREEN
						getContentPane().setBackground(Color.green);

					// PACK FRAME TO COMPONENT SIZE (WILL FIT VIDEO)
						pack();

					// ADD A LISTENER TO CLOSE THE APPLICATION IF USER CLOSES WINDOW
						addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent evt) {
								System.out.println("User closed window ... exiting.");
								System.exit(0);
							}
						});

					// MAKE FRAME VISIBLE
						setVisible(true);

				} // end if ((vc = player.getVisualComponent()) != null)

	  	} catch (Exception e) {
			System.out.println("\n--- FrameGrabber.initializeDisplay() Exception ---\n");
			e.printStackTrace();
		}

		// INDICATE SUCCESS AND RETURN
			return true;

	} // end initializeDisplay()



	/**
	 * Rip frames from movie and save as image files:
	 * <pre>
	 *   1. determine number of frames
	 *   2. loop through each frame
	 *   3. grab each frame and place in BufferedImage
	 *   4. build target file name
	 *   5. save file in portable network graphics (PNG) format
	 * </pre>
	 *
	 * @return number of frames ripped/extracted from movie
	 */
	public int ripFrames() {

		// DECLARATIONS
			Buffer buf = null;					// BUFFER FOR "CURRENT" FRAME
			Image tmpImage = null;				// CURRENT IMAGE
			BufferedImage tmpBufImage = null;	// "CURRENT" BUFFERED IMAGE
			Graphics2D g2D = null;				// JAVA 2D GRAPHIC CONTEXT FOR BUFFEREDIMAGE
			BufferToImage bi = null;			// BUFFER TO IMAGE CONVERTER
			int totalFrames = 0;				// TOTAL NUMBER OF FRAMES IN MOVIE
			Time duration = null;				// DURATION OF VIDEO
			String targetFile = "";				// TARGET FILE NAME FOR EXTRACTED FRAME
			int sleepInterval = 1;


		try {

			// DETERMINE NUMBER OF FRAMES
				duration = player.getDuration();
				if (duration != Duration.DURATION_UNKNOWN) {
					System.out.println("Movie duration: " + duration.getSeconds());
					totalFrames = fpc.mapTimeToFrame(duration);
				} else {
					System.out.println("Unable to determine number of frames.");
				}

			// CONTINUE ONLY IF ONE OR MORE FRAMES EXIST
				if (totalFrames > 0) {

					// MOVE PLAYER TO FIRST FRAME
						fpc.seek(1);

					// EXTRACT FIRST FRAME INTO BUFFER
						buf = fgc.grabFrame();

					// INITIALIZE CONVERTER FOR BUFFER
						bi = new BufferToImage((VideoFormat)buf.getFormat());

					// INITIALIZE PROGRESS BAR
						statusScreen.setBarMax(3, totalFrames);

					// LOOP THROUGH ALL FRAMES HERE...
						for (int frameCounter=1; frameCounter<=totalFrames; frameCounter++) {
							// CHECK TO SEE IF CANCEL BUTTON HAS BEEN PRESSED
								if (statusScreen.getEBLACanceled()) {
									return(-1);
								}

							// SHOW FRAME BEING PROCESSED
								System.out.println("Ripping frame #" + frameCounter);

							// GRAB CURRENT FRAME (REDUNDANT FOR 1ST FRAME)
								buf = fgc.grabFrame();

							// EXTRACT IMAGE FROM BUFFER
								tmpImage = bi.createImage(buf);

							// CONVERT IMAGE TO BUFFERED IMAGE
								tmpBufImage = new BufferedImage(tmpImage.getWidth(null), tmpImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
								g2D = tmpBufImage.createGraphics();
								g2D.drawImage(tmpImage, null, null);

							// ENCODE BUFFERED IMAGE AS JPEG
								if (frameCounter<10) {
									targetFile = targetPath + "000" + frameCounter + ".png";
								} else if (frameCounter<100) {
									targetFile = targetPath + "00" + frameCounter + ".png";
								} else if (frameCounter<1000) {
									targetFile = targetPath + "0" + frameCounter + ".png";
								} else {
									targetFile = targetPath + frameCounter + ".png";
								}

							// SAVE FILE
								// next line is OK if using JDK 1.4 beta 1 or later
									ImageIO.write(tmpBufImage, "png", new File(targetFile));
								// this line must be used instead for JDK 1.3 or earlier
								//	ImageIOWorkaround.write(tmpBufImage, "png", new File(targetFile));

							// IF UPDATE GUI IF APPLICABLE
								if ((displayFlag) && (statusScreen!=null)) {
									// UPDATE STATUS
										statusScreen.updateStatus(3, "Ripped frame " + frameCounter + " of " + totalFrames);

									// UPDATE PROGRESS BAR
										statusScreen.updateBar(3, frameCounter);

									// ADD CURRENT FRAME ON STATUS SCREEN
										statusScreen.updateImage(1, tmpBufImage);

									// SET STATUS SCREEN REFRESH
										Thread.sleep(sleepInterval);
								}


							// ADVANCE TO NEXT FRAME
								fpc.skip(1);

						} // for loop

				} // if (totalFrames > 0)

			// CLOSE PLAYER
				player.close();
				player.deallocate();

			// NULLIFY VARIABLES
				ml = null;
				player = null;
				fpc = null;
				fgc = null;

	  	} catch (Exception e) {
			System.out.println("\n--- FrameGrabber.ripFrames() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN TOTAL NUMBER OF FRAMES EXTRACTED
			return totalFrames;

	} // end ripFrames()



    /**
     * Main procedure - allows FrameGrabber to be run in stand-alone mode
     */
    public static void main(String [] args) {

		// DECLARATIONS
			String sourcePath;
			String targetPath;
			boolean displayFlag = false;
			FrameGrabber myFG = null;


		try {

			// VERIFY THAT THREE COMMAND LINE ARGUMENTS WERE PASSED
				if (args.length != 3) {
					printUsage();
					System.exit(0);
				}

			// BUILD SOURCE PATH FROM 1ST ARGUMENT
				sourcePath = args[0];

			// EXTRACT EXTRACTED FRAME DESTINATION PATH AND FILE PREFIX FROM 2ND ARGUMENT
				targetPath = args[1];

			// EXTRACT VIDEO OUTPUT PREFERENCE FROM 3RD ARGUMENT
				if (args[2].equals("true")) {
					displayFlag = true;
				}

			// CREATE INSTANCE OF OBJECT AND CALL CONSTRUCTOR
				myFG = new FrameGrabber(sourcePath, targetPath, null, displayFlag);

			// RIP FRAMES
				if (myFG.ripFrames()==0) {
					System.out.println("Zero frames ripped.");
				}

			// DISPOSE OF OBJECTS THAT ARE NO LONGER NEEDED
				myFG.dispose();

			// SET FRAMEGRABBER TO NULL
				myFG = null;

			// EXIT
				System.out.println("Exiting FrameGrabber.main() standalone testing routine.");
				System.exit(0);

	  	} catch (Exception e) {
			System.out.println("\n--- FrameGrabber.main() Exception ---\n");
			e.printStackTrace();
		}

	} // end main()



	/**
	 * Display usage info if user trys to run standalone with incorrect parameters
	 */
	private static void printUsage() {

		try {
			// DISPLAY USAGE INSTRUCTIONS
				System.out.println("Usage: java com.greatmindsworking.EBLA.FrameGrabber <path and name of video file> <PNG output path and file prefix> <display movie: true / false>");

	  	} catch (Exception e) {
			System.out.println("\n--- FrameGrabber.printUsage() Exception ---\n");
			e.printStackTrace();
		}


	} // end printUsage()

} // end FrameGrabber class



/*
 * Revision history:
 *  02-02-2001  - 0.01 - initial coding
 *  02-25-2001  - 0.02 - got FramePositioningControl and FrameGrabbingControl to work properly
 *  02-26-2001  - 0.03 - logical breakout of functions
 *  03-24-2001  - 0.04 - added Java ImageIO for saving images
 *					   - changed image format from JPEG to PNG
 *  04-21-2001  - 1.00 - cleaned up code and fleshed out JavaDOC comments
 *  07-10-2001  - 1.01 - removed ImageIO workaround (not needed for JDK 1.4 beta)
 *  07-26-2001  - 1.02 - made frameCount a class data member so that it can be accessed
 *  08-02-2001  - 1.03 - made members public/private as appropriate and added dispose() method
 *  09-05-2001  - 1.04 - modified ripFrames() to return number of frames extracted
 *  02-04-2002  - 1.10 - migrated code to CVS
 *
 ******************************************************************************
 *
 * $Log$
 * Revision 1.13  2004/01/13 17:11:44  yoda2
 * Added logic to reset calc_status_code in parameter_experience_data to zero if user cancels processing.
 *
 * Revision 1.12  2004/01/05 23:35:57  yoda2
 * Added code to recommend garbage collection following ripping of frames and frame analysis.
 *
 * Revision 1.11  2003/12/31 19:38:24  yoda2
 * Fixed various thread synchronization issues.
 *
 * Revision 1.10  2003/12/30 23:19:41  yoda2
 * Modified for more detailed updating of EBLA Status Screen.
 *
 * Revision 1.9  2003/08/08 17:45:50  yoda2
 * Updated commandline execution instructions.
 *
 * Revision 1.8  2003/08/08 15:59:40  yoda2
 * Added logic to display frames to EBLA GUI during rip if applicable.
 *
 * Revision 1.7  2002/12/11 22:51:37  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.6  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.5  2002/10/22 20:14:46  bpangburn
 * JavaDoc cleanup.
 *
 * Revision 1.4  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.3  2002/06/26 13:29:14  bpangburn
 * Modified for compatiblity with JDK 1.4 (removed ImageIOWorkaround code).
 *
 * Revision 1.2  2002/02/05 20:28:03  bpangburn
 * Added additional CVS keywords for id and log to first comment block and added author, revision, & date to second comment block for integration with JavaDoc.
 *
 */
