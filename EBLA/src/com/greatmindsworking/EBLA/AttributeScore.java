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
 * AttributeScore.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This class manages comparison of entities when a candidate entity matches
 * multiple existing entities.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class AttributeScore implements Comparable<AttributeScore> {
	/**
	 * id of entity
	 */
	public long entityID;

	/**
	 * number of times entity has been encountered
	 */
	public int occuranceCount;

	/**
	 * score of how well object in prior frame correlates to object in current frame
	 */
	public double score;



	/**
	 * Public method to compare two attributeScore objects.  Allows use of Java's native sorting.
	 *
	 * @param _o	AttributeScore object to perform comparision against
	 *
	 * @return integer indicating whether passed object is less than, equal to or greater than current object
	 */
    public int compareTo(AttributeScore _as) {

    	//AttributeScore ms = (AttributeScore)_o;

      	if (score < _as.score) {
        	return -1;
      	} else if (_as.score < score) {
        	return 1;
      	}
      	return 0;

    } // end compareTo()

} // end AttributeScore class



/*
 * $Log$
 * Revision 1.5  2004/02/25 21:58:10  yoda2
 * Updated copyright notice.
 *
 * Revision 1.4  2002/12/11 22:48:28  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.3  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.2  2002/09/27 23:11:06  bpangburn
 * Moved information for execution mode and # passed to parameters.
 *
 * Revision 1.1  2002/08/23 18:34:44  bpangburn
 * Used to hold attribute matching scores when comparing multiple, matching entities.
 *
 */