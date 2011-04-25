/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2002-2004, Brian E. Pangburn & Jonathan P. Ayo
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
 * RAList.java
 *
 * The mean shift library is a collection of routines that use the mean shift
 * algorithm. Using this algorithm, the necessary output will be generated needed
 * to analyze a given input set of data.
 *
 * Region Adjacency List:
 *
 * The Region Adjacency List class is used by the Image Processor class in the
 * construction of a Region Adjacency Matrix, used by this class to applying
 * transitive closure and to prune spurious regions during image segmentation.
 * <pre>
 * To-do:
 *  1. Clean up JavaDoc
 *
 * @author	$Author$
 * @version	$Revision$
 */
public class RAList {

// PUBLIC DATA MEMBERS
	/**
	 * RAM Label
	 */
	public int label;

	/**
	 * RAM Weight
	 */
	public float edgeStrength;

	/**
	 * # edge pixels
	 */
	public int	edgePixelCount;

	/**
	 * RAM Link
	 */
	public RAList next;



// PRIVATE DATA MEMBERS
	/**
	 * current and previous pointer
	 */
	private RAList	cur, prev;

	/**
	 * flag
	 */
	private char exists;



// PUBLIC METHODS
	/**
	 * Constructs a RAList object.
	 *
	 * Usage: RAList()
	 *
	 * Post:
	 *   - a RAlist object has been properly constructed.
	 */
	public RAList() {

		try {

		// initialize label and link
			label = -1;
			next = null;

		// initialize edge strenght weight and count
			edgeStrength	= 0;
			edgePixelCount	= 0;


		} catch (Exception e) {
			//System.out.println("\n--- RAList Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end constructor



	/**
	 * <pre>
	 * Insert a region node into the region adjacency list.
	 *
	 * Usage: Insert(RAList entry)
	 *
	 * Pre:
	 *   - entry is a node representing a connected region
	 * Post:
	 *   - entry has been inserted into the region adjacency list if it does not
	 *     already exist there.
	 *   - if the entry already exists in the region adjacency list 1 is returned
	 *     otherwise 0 is returned.
	 *
	 * @param entry
	 *
	 * @return integer  indicating whether or not a new node was
	 *   				actually inserted into the region adjacency list???????????
	 */
	public int Insert(RAList entry) {

		// if the list contains only one element
		// then insert this element into next
			if (next == null) {
				// insert entry
					next = entry;
					entry.next = null;

				// done
					return 0;
			}

		// traverse the list until either:

		// (a) entry's label already exists - do nothing
		// (b) the list ends or the current label is
		//     greater than entry's label, thus insert the entry
		//     at this location

		// check first entry
			if (next.label > entry.label) {
				// insert entry into the list at this location
					entry.next = next;
					next = entry;

				// done
					return 0;
			}

		// check the rest of the list...
			exists	= 0;
			cur		= next;
			while (cur != null) {
				if (entry.label == cur.label) {
					// node already exists
						exists = 1;
						break;
				} else if ((cur.next == null)||(cur.next.label > entry.label)) {
					// insert entry into the list at this location
						entry.next = cur.next;
						cur.next = entry;
						break;
				}

				// traverse the region adjacency list
					cur = cur.next;
			}

		// done. Return exists indicating whether or not a new node was
		//       actually inserted into the region adjacency list.
			return (int)(exists);

	} // end Insert

} // end RAList class



/*
 * $Log$
 * Revision 1.9  2004/02/25 21:59:22  yoda2
 * Updated copyright notice.
 *
 * Revision 1.8  2003/11/24 16:20:10  yoda2
 * Updated copyright to 2002-2003.
 *
 * Revision 1.7  2002/12/11 23:05:00  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.6  2002/09/20 19:49:00  bpangburn
 * Fixed various JavaDoc error messages.
 *
 * Revision 1.5  2002/09/20 19:15:16  bpangburn
 * Added BSD-style license, cleaned up JavaDoc, and moved CVS log to end of each source file.
 *
 * Revision 1.4  2002/08/21 18:31:13  jayo
 * Updated error handlers to reflect class and method names.
 *
 * Revision 1.3  2002/08/20 21:49:15  bpangburn
 * Added crude error handlers to each method.
 *
 * Revision 1.2  2002/07/09 18:03:06  jayo
 * Editing comments to coincide with Javadoc
 *
 * Revision 1.1  2002/06/26 13:36:00  bpangburn
 * Initial CVS commit after porting EDISON segmentation code from C++ to Java.
 *
 */
