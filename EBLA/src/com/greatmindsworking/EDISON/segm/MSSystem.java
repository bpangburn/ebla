/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2003, Brian E. Pangburn & Jonathan P. Ayo
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



package com.greatmindsworking.EDISON.segm;



/**
 * MSSystem.java
 *
 * The mean shift library is a collection of routines that use the mean shift
 * algorithm. Using this algorithm, the necessary output will be generated needed
 * to analyze a given input set of data.
 *
 * Mean Shift System:
 *
 * The Mean Shift System class provides a mechanism for the mean shift library
 * classes to prompt progress and to time its computations. When porting the mean
 * shift library to an application the methods of this class may be changed such
 * that the output of the mean shift class prompts will be given to whatever hardware
 * or software device that is desired.
 * <pre>
 * Command Line Version:
 *   This version of mean shift system is written for the command
 *   line version of EDISON.
 *
 * To-do:
 *  1. Clean up JavaDoc
 *
 * @author	$Author$
 * @version	$Revision$
 */
public class MSSystem {

// PUBLIC DATA MEMBERS
	/**
	 * percent of mean-shift calculations completed
	 */
	public int percentDone  = 0;


// PRIVATE DATA MEMBERS
	/**
	 * time that calculations were started
	 */
	private java.util.Date currentTime;


// PUBLIC METHODS

	/**
	 * Constructs a mean shift system object.
	 */
	public MSSystem() {

		try {

			// initialize currentTime
				currentTime = new java.util.Date();

		} catch (Exception e) {
			//System.out.println("\n--- MSSystem Constructor Exception ---\n");
			e.printStackTrace();
		}


	} // end constructor



	/**
	 * <pre>
	 * Initializes the system timer. The timer object synthesized by this class is
	 * initialized during construction of the msSystem class to be the current time
	 * during construction.
	 *
	 * Usage: StartTimer()
	 *
	 * Post:
	 *   - the mean shift system time has been set to the current system time.
	 */
	public void StartTimer() {

		try {

		// set msSystem time to system time
			currentTime = new java.util.Date();


		} catch (Exception e) {
			System.out.println("\n--- MSSystem.StartTimer() Exception ---\n");
			e.printStackTrace();
		}

	} //end StartTimer



	/**
	 * <pre>
	 * Returns the amount of time elapsed in seconds from when StartTimer() was called.
	 * If StartTimer() was not called, the time returned is the time elapsed from the
	 * construction of the msSystem object.
	 *
	 * Usage: ElapsedTime()
	 *
	 * Post:
	 *   - the amount of time in seconds since the mean shift system time was last set
	 *     is returned.
	 *
	 * @return long integer of the amount of time elapsed
	 */
	public long ElapsedTime() {

		// DECLARATIONS
			java.util.Date tmpTime = new java.util.Date();

		// return the amount of time elapsed in seconds
		// since the MSSystem time was last set...
			return (tmpTime.getTime() - currentTime.getTime()) / 1000;

	}



	/**
	 * <pre>
	 * Outputs to a device a character message containing delimeters. These delimeters
	 * are replaced by the variable input parameters passed to prompt.(Like printf.)                                   |//
	 * This method should be altered if a special device either than stderr is desired
	 * to be used as an output prompt.
	 *
	 * Usage: Prompt(varArgs)
	 *
	 * Pre:
	 *   - a variable set of arguments
	 * Post:
	 *   - string has been output to the user
	 *
	 * @param args		a variable set of arguments to be placed into the prompt string
	 */
	public void Prompt(String args) {

		try {

			System.out.print(args);

		} catch (Exception e) {
			//System.out.println("\n--- MSSystem.Prompt() Exception ---\n");
			e.printStackTrace();
		}


	} // end Prompt

} // end MSSystem class



/*
 * $Log$
 * Revision 1.10  2003/11/24 16:20:10  yoda2
 * Updated copyright to 2002-2003.
 *
 * Revision 1.9  2002/12/11 23:04:21  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.8  2002/09/20 19:49:00  bpangburn
 * Fixed various JavaDoc error messages.
 *
 * Revision 1.7  2002/09/20 19:15:15  bpangburn
 * Added BSD-style license, cleaned up JavaDoc, and moved CVS log to end of each source file.
 *
 * Revision 1.6  2002/08/21 18:31:13  jayo
 * Updated error handlers to reflect class and method names.
 *
 * Revision 1.5  2002/08/20 21:49:15  bpangburn
 * Added crude error handlers to each method.
 *
 * Revision 1.4  2002/07/09 18:02:17  jayo
 * Editing comments to coincide with Javadoc
 *
 * Revision 1.3  2002/06/28 19:17:34  bpangburn
 * Cleaned up code.
 *
 * Revision 1.2  2002/06/26 22:24:20  bpangburn
 * Debugging segmentation code.
 *
 * Revision 1.1  2002/06/26 13:36:00  bpangburn
 * Initial CVS commit after porting EDISON segmentation code from C++ to Java.
 *
 */