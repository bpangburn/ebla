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



/**
 * MatchScore.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Contains data members needed during the object correlation process that takes place
 * while processing frames in the FrameProcessor class.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class MatchScore implements Comparable<MatchScore> {
	/**
	 * correlation index of object from prior frame
	 */
	public int correlationIndex;

	/**
	 * array index of object in current frame
	 */
	public int arrayIndex;

	/**
	 * score of how well object in prior frame correlates to object in current frame
	 */
	public double score;



	/**
	 * Public method to compare two matchScore objects.  Allows use of Java's native sorting.
	 *
	 * @param _o	MatchScore object to perform comparision against
	 *
	 * @return integer indicating whether passed object is less than, equal to or greater than current object
	 */
    public int compareTo(MatchScore _ms) {

    	//MatchScore ms = (MatchScore)_o;

      	if (score < _ms.score) {
        	return -1;
      	} else if (_ms.score < score) {
        	return 1;
      	}
      	return 0;

    } // end compareTo()

} // end MatchScore class



/*
 * $Log$
 * Revision 1.6  2004/02/25 21:58:10  yoda2
 * Updated copyright notice.
 *
 * Revision 1.5  2002/12/11 22:55:04  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.4  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.3  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.2  2002/04/03 23:17:43  bpangburn
 * Implemented Comparable and added compareTo method to perform comparisons and allow objects to be sorted.
 *
 * Revision 1.1  2002/03/31 04:17:54  bpangburn
 * Added MatchScore class to hold data members used during the object correlation process.
 * Started object numbering scheme.
 *
 */