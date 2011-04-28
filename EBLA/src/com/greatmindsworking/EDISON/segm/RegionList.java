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
 * RegionList.java
 *
 * The mean shift library is a collection of routines that use the mean shift
 * algorithm. Using this algorithm, the necessary output will be generated needed
 * to analyze a given input set of data.
 *
 * Region List Class:
 *
 * During segmentation, data regions are defined. The RegionList class provides a
 * mechanism for doing so, as well as defines some basic operations, such as region
 * growing or small region pruning, on the defined regions. It is defined below.
 * <pre>
 * To-do:
 *  1. Clean up JavaDoc
 *  2. Remove original C++ based error handler if possible.
 *
 * @author	$Author$
 * @version	$Revision$
 */
public class RegionList {

// PRIVATE DATA MEMBERS

	/**
	 * region list partitioned array
	 */
	private REGION regionList[];

	/**
	 * defines the number maximum number of regions allowed (determined by
	 * user during class construction)
	 */
	private int	maxRegions;

	/**
	 * the number of regions currently stored by the region list
	 */
	private int	numRegions;

	/**
	 * dimension of data set being classified by region list class
	 */
	@SuppressWarnings("unused")
	private int	N;

	/**
	 * number of points contained by the data set being classified byregion list class
	 */
	@SuppressWarnings("unused")
	private int	L;



// PUBLIC METHODS
	/**
	 * <pre>
	 * Constructs a region list object.
	 *
	 * Usage: RegionList(maxRegions, L, N)
	 *
	 * Pre:
	 *   - modesPtr is a pointer to an array of modes
	 *   - maxRegions_ is the maximum number of regions
	 *     that can be defined
	 *   - L_ is the number of data points being class
	 *     ified by the region list class
	 *   - N is the dimension of the data set being cl
	 *     assified by the region list class
	 * Post:
	 *   - a region list object has been properly init
	 *     ialized.
	 *
	 * @param maxRegions_	the maximum amount of regions that can be classified by
	 *						the region list
	 * @param L_			the length of the input data set being classified by the
	 *						region list object
	 * @param N_			the dimension of the input data set being classified by
	 *						the region list object
	 */
 	public RegionList(int maxRegions_, int L_, int N_) {

		try {

		// Obtain maximum number of regions that can be
		// defined by user
			if ((maxRegions = maxRegions_) <= 0) {
				ErrorHandler("RegionList", "Maximum number of regions is zero or negative.", ErrorType.FATAL);
			}

		// Obtain dimension of data set being classified by
		// region list class
			if ((N = N_) <= 0) {
				ErrorHandler("RegionList", "Dimension is zero or negative.", ErrorType.FATAL);
			}

		// Obtain length of input data set...
			if ((L = L_) <= 0) {
				ErrorHandler("RegionList", "Length of data set is zero or negative.", ErrorType.FATAL);
			}

		// Allocate memory for region list array
			regionList = new REGION [maxRegions];

		//Initialize region list...
			numRegions = 0;


		} catch (Exception e) {
			//System.out.println("\n--- RegionList Constructor Exception ---\n");
			e.printStackTrace();
		}


	} // end constructor



	/**
	 * <pre>
	 * Adds a region to the region list.
	 *
	 * Usage: AddRegion(label, pointCount, indeces)
	 *
	 * Pre:
	 *   - label is a positive integer used to uniquely identify a region
	 *   - pointCount is the number of N-dimensional data points that exist in the
	 *     region being classified.
	 *   - indeces is a set of indeces specifying the data points contained within
	 *     this region
	 *   - pointCount must be > 0
	 * Post:
	 *   - a new region labeled using label and containing pointCount number of
	 *     points has been added to the region list.
	 *
	 * @param label			a positive integer used to uniquely identify a region
	 * @param pointCount	a positive integer that specifies the number of N-dimensional
	 *						data points that exist in the region being classified
	 * @param indeces		an integer array that specifies the set of indeces of the
	 *						data points that are contianed with in this region
	 * @param offset
	 */
	public void AddRegion(int label, int pointCount, int indeces[], int offset) {

		try {

		// make sure that there is enough room for this new region
		// in the region list array...
			if (numRegions >= maxRegions) {
				ErrorHandler("AddRegion", "Not enough memory allocated.", ErrorType.FATAL);
			}

		// make sure that label is positive and point Count > 0...
			if ((label < 0)||(pointCount <= 0)) {
				ErrorHandler("AddRegion", "Label is negative or number of points in region is invalid.", ErrorType.FATAL);
			}

		// place new region into region list array using
		// numRegions index
			regionList[numRegions] = new REGION();
			regionList[numRegions].label = label;
			regionList[numRegions].pointCount = pointCount;
			regionList[numRegions].region = new int[pointCount];

			System.arraycopy(indeces, offset, regionList[numRegions].region, 0, pointCount);

		// increment numRegions to indicate that another
		// region has been added to the region list
			numRegions++;


		} catch (Exception e) {
			System.out.println("\n--- RegionList.AddRegion() Exception ---\n");
			e.printStackTrace();
		}

	} // end AddRegion



	/**
	 * <pre>
	 * Resets the region list for re-use (for new classification).
	 *
	 * Usage: Reset()
	 *
	 * Post:
	 *   - the region list has been reset.
	 */
	public void Reset() {

		try {

		// reset region list
			numRegions = 0;


		} catch (Exception e) {
			System.out.println("\n--- RegionList.Reset Exception ---\n");
			e.printStackTrace();
		}

	} // end Reset



	/**
	 * <pre>
	 * Returns the number of regions stored by the region list.
	 *
	 * Usage: GetNumRegions()
	 *
	 * Post:
	 *   - the number of regions stored by the region list is returned.
	 *
	 * @return integer indicating how many regions have been found
	 */
	public int GetNumRegions() {

		// return region count
			return numRegions;

	} // end GetNumRegions



	/**
	 * <pre>
	 * Returns the label of a specified region.
	 *
	 * Usage: label = GetLabel(regionNumber)
	 *
	 * Pre:
	 *   - regionNum is an index into the region list array.
	 * Post:
	 *   - the label of the region having region index specified by regionNum has
	 *     been returned.
	 * @param regionNum	the index of the region in the region list array
	 *
	 * @return integer that is the label of a specified region
	 */
	public int GetLabel(int regionNum) {

		// return the label of a specified region
			return regionList[regionNum].label;

	} // end GetLabel



	/**
	 * <pre>
	 * Returns number of data points contained by a specified region.
	 *
	 * Usage: pointCount = GetRegionCount(regionNumber)
	 *
	 * Pre:
	 *   - regionNum is an index into the region list array.
	 * Post:
	 *   - the number of points that classify the region whose index is specified
	 *     by regionNum is returned.
	 *
	 * @param regionNum	the index of the region in the region list array
	 *
	 * @return integer of the return the region count of a specified region
	 */
	public int GetRegionCount(int regionNum) {

		// return the region count of a specified region
			return regionList[regionNum].pointCount;

	} // end GetRegionCount



	/**
	 * <pre>
	 * Returns a pointer to a set of grid location indeces specifying the data points
	 * belonging to a specified region.
	 *
	 * Usage: indeces = GetRegionIndeces(regionNumber)
	 *
	 * Pre:
	 *   - regionNum is an index into the region list array.
	 * Post:
	 *   - the region indeces specifying the points contained by the region specified
	 *     by regionNum are returned.
	 *
	 * @param regionNum	the index of the region in the region list array
	 *
	 * @return integer array of the return point indeces using regionNum
	 */
	public int[] GetRegionIndeces(int regionNum) {

		// return point indeces using regionNum
			return regionList[regionNum].region;

	} // end GetRegionIndeces



// PRIVATE METHODS
	/**
	 * Class Error Handler
	 *
	 * Usage; ErrorHandler(functname, errmsg, status)
	 *
	 * Pre:
	 *   - functName is the name of the function that caused an error
	 *   - errmsg is the error message given by the calling function
	 *   - status is the error status: ErrorType.FATAL or NONErrorType.FATAL
	 * Post:
	 *   - the error message errmsg is flagged on behave of function functName.
	 *   - if the error status is ErrorType.FATAL then the program is halted, otherwise
	 *     execution is continued, error recovery is assumed to be handled by
	 *     the calling function.
	 *
	 * @param functName		the name of the corresponding function
	 * @param errmsg		the error message associated with the function name
	 * @param status		the error type that has occured
	 */
	private void ErrorHandler(String functName, String errmsg, ErrorType status) {

		try {

		// flag error message on behalf of calling function, error format
		// specified by the error status...
		if (status == ErrorType.NONFATAL) {
			System.out.println(functName + " Error: " + errmsg + "\n");
		} else {
			System.out.println(functName + " FATAL Error: " + errmsg + "\nAborting Program.\n");
			System.exit(1);
		}


		} catch (Exception e) {
			System.out.println("\n--- RegionList.ErrorHandler() Exception ---\n");
			e.printStackTrace();
		}

	} // end ErrorHandler

} // end RegionList class



/*
 * $Log$
 * Revision 1.12  2004/02/25 21:59:22  yoda2
 * Updated copyright notice.
 *
 * Revision 1.11  2003/11/24 16:34:41  yoda2
 * Small JavaDoc fixes to get rid of warnings.
 *
 * Revision 1.10  2003/11/24 16:20:10  yoda2
 * Updated copyright to 2002-2003.
 *
 * Revision 1.9  2002/12/11 23:06:15  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.8  2002/09/20 19:49:00  bpangburn
 * Fixed various JavaDoc error messages.
 *
 * Revision 1.7  2002/09/20 19:15:16  bpangburn
 * Added BSD-style license, cleaned up JavaDoc, and moved CVS log to end of each source file.
 *
 * Revision 1.6  2002/08/21 18:31:13  jayo
 * Updated error handlers to reflect class and method names.
 *
 * Revision 1.5  2002/08/20 21:49:15  bpangburn
 * Added crude error handlers to each method.
 *
 * Revision 1.4  2002/07/09 18:03:48  jayo
 * Editing comments to coincide with Javadoc
 *
 * Revision 1.3  2002/06/28 19:17:34  bpangburn
 * Cleaned up code.
 *
 * Revision 1.2  2002/06/26 22:24:21  bpangburn
 * Debugging segmentation code.
 *
 * Revision 1.1  2002/06/26 13:36:00  bpangburn
 * Initial CVS commit after porting EDISON segmentation code from C++ to Java.
 *
 */