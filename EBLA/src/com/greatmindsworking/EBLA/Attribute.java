/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002, Brian E. Pangburn
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
 * Attribute.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This class stores the data members for entity attribute values.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class Attribute {
	/**
	 * unique database id for parent attribute_list record
	 */
	public long attributeListID = 0;

	/**
	 * average value of attribute
	 */
	public double avgValue = 0;

	/**
	 * standard deviation of attribute
	 */
	public double stdDeviation = 0;



	/**
	 * Class constructor that sets the values for an attribute object
	 *
	 * @param _attributeListID unique database id for parent attribute_list record
	 * @param _avgValue average value of attribute
	 * @param _stdDeviation standard deviation of attribute
	 */
    public Attribute(long _attributeListID, double _avgValue, double _stdDeviation) {

		try {

			// SET PARAMETERS
				attributeListID = _attributeListID;
				avgValue = _avgValue;
				stdDeviation = _stdDeviation;

		} catch (Exception e) {
			System.out.println("\n--- Attribute Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end Attribute()

} // end Attribute class



/*
 * $Log$
 * Revision 1.4  2002/12/11 22:47:55  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.3  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.2  2002/09/27 23:11:06  bpangburn
 * Moved information for execution mode and # passed to parameters.
 *
 * Revision 1.1  2002/04/17 23:05:07  bpangburn
 * Completed code to detect entities (and their attributes) from frame_analysis_data.  Still need to flesh out writeToDB() in EntityExtractor class.
 *
 */