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



package com.greatmindsworking.EDISON.segm;



import java.util.*;
import java.awt.*;



/**
 * MSImageProcessor.java
 *
 * The mean shift library is a collection of routines that use the mean shift
 * algorithm. Using this algorithm, the necessary output will be generated needed
 * to analyze a given input set of data.
 *
 * Mean Shift Image Processor Class:
 *
 * The following class inherits from the mean shift library in order to perform
 * the specialized tasks of image  segmentation and filtering.
 * <pre>
 * To-do:
 *  1. Clean up JavaDoc
 *  2. Remove original C++ based error handlers if possible.
 *
 * @author	$Author$
 * @version	$Revision$
 */
public class MSImageProcessor extends MeanShift {

// PRIVATE CONSTANTS
	/**
	 * Status Prompt
	 */
	private static final boolean PROMPT = true;

	/**
	 * image pruning
	 */
	private static final int TOTAL_ITERATIONS = 14;
	private static final long BIG_NUM = 0xffffffff; //2^32-1;
	private static final int NODE_MULTIPLE = 10;

	/**
	 * data space conversion...
	 */
	private static final double Xn = 0.95050;
	private static final double Yn = 1.00000;
	private static final double Zn = 1.08870;
	private static final double Un_prime = 0.19784977571475;
	private static final double Vn_prime = 0.46834507665248;
	private static final double Lt = 0.008856;

	/**
	 * RGB to LUV conversion
	 */
	private static final double XYZ[][] = {{ 0.4125, 0.3576, 0.1804 },
								   		   { 0.2125, 0.7154, 0.0721 },
								   		   { 0.0193, 0.1192, 0.9502 }};
	/**
	 * LUV to RGB conversion
	 */
	private static final double RGB[][] = {{ 3.2405,-1.5371,-0.4985 },
								   		   {-0.9693, 1.8760, 0.0416 },
								   		   { 0.0556,-0.2040, 1.0573 }};



// PRIVATE DATA MEMBERS
	/**
	 * TIMER...
	 */
	private MSSystem msSystem;

	/**
	 * stores the boundary locations for each region
	 */
	private RegionList regionList;

	/**
	 * stores the number of connected regions contained by the image
	 */
	private int	regionCount;

	/**
	 * 8 Connected Neighbors
	 */
	private int	neigh[] = new int[8];

	/**
	 * used during fill algorithm
	 */
	private int[] indexTable;

	/**
	 * stores modes in integer format on lattice
	 */
	private int[] LUV_data;

	/**
	 * Raw data output of mean shift algorithm to the location of the data point
	 * on the lattice (1 to 1 correlation with input)
	 */
	private float[]	msRawData;


	/**
	 * <pre>
	 * Data Modes
	 * assigns a label to each data point associating it to mode modes[l])
	 * stores the mode data of the input data set, indexed by labels
	 * stores for each mode the number of point mapped to that mode,
	 * indexed by labels
	 */
	private int[] labels;
	private float[] modes;
	private int[] modePointCounts;

	/**
	 * Region Adjacency List
	 *
	 * an array of RAList objects containing an entry for each region of the image
	 */
	private RAList[] raList;


	/**
	 * RAMatrix Free List
	 *
	 * a pointer to the head of a region adjacency list object free list
	 */
	private RAList freeRAList;

	/**
	 * a pool of RAList objects used in the construction of the RAM
	 */
	private RAList[] raPool;

	/**
	 * Epsilon used for transitive closure
	 */
	private float epsilon;

	/**
	 * Table used to keep track of which pixels have been already visited upon
	 * computing the boundary edge strengths
	 */
	private char[] visitTable;

	/**
	 * Transitive Closure
	 * defines square range radius used when clustering pixels
	 * together, thus defining image regions
	 */
	private float rR2;



// PUBLIC METHODS
	/**
	 * Class Constructor
	 */
	public MSImageProcessor() {

		// CALL CONSTRUCTOR FOR MeanShift CLASS
			super();

		try {

		// intialize basin of attraction structure
		// used by the filtering algorithm
			modeTable = null;
			pointList = null;
			pointCount = 0;

		// initialize region list
			regionList = null;

		// initialize output structures...
			msRawData = null;
			labels = null;
			modes = null;
			modePointCounts = null;
			regionCount = 0;

		// intialize temporary buffers used for
		// performing connected components
			indexTable = null;
			LUV_data = null;

		// initialize region adjacency matrix
			raList = null;
			freeRAList = null;
			raPool = null;

		// intialize visit table to having NULL entries
			visitTable = null;

		// initialize epsilon such that transitive closure
		// does not take edge strength into consideration when
		// fusing regions of similar color
			epsilon = (float)1.0;

		// initialize class state to indicate that
		// an output data structure has not yet been
		// created...
			classState.OUTPUT_DEFINED = false;


		// CREATE msSystem OBJECT FOR PROMPTS, STATUS, ETC...
			msSystem = new MSSystem();


		} catch (Exception e) {
			//System.out.println("\n--- MSImageProcessor Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end MSImageProcessor constructor



	/**
	 * <pre>
	 * This method uploads the image and converts its
	 * data into the LUV space. If another conversion
	 * is desired data may be uploaded into this class
	 * via the procedure MeanShift::UploadInput().
	 *
	 * Usage: DefineImage(data, type, height, width)
	 *
	 * Pre:
	 *    - data_ is a one dimensional array of unsigned char RGB vectors
	 *    - type is the type of the image: COLOR or GREYSCALE
	 *    - height_ and width_ define the dimension ofthe image
	 *    - if the image is of type GREYSCALE then data containes only one number
	 *      per pixel location, where a pixel location is defined by the index
	 *      into the data array
	 * Post:
	 *    - the image specified has been uploaded into the image segmenter
	 *      class to be segmented.
	 *
	 * @param data_		A one dimensional unsigned char array of RGB vectors
	 * @param ImageType	specifies the image type: COLOR or GREYSCALE
	 * @param height	the image height
	 * @param width		the image width
	 */
	public void DefineImage(int[] data_, ImageType type, int height_, int width_) {

		try {

		// obtain image dimension from image type
			int dim;

			if (type == ImageType.COLOR) {
				dim	= 3;
			} else {
				dim = 1;
			}

		// perform rgb to luv conversion
			int	i;

			float luv[] = new float [height_*width_*dim];
			if (dim == 1) {
				for(i = 0; i < height_*width_; i++) {
					luv[i]	= (float)(data_[i]);
				}
			} else {
				for(i = 0; i < height_*width_; i++) {
					RGBtoLUV(data_[i], luv, (dim*i));
				}
			}

		// define input defined on a lattice using mean shift base class
			DefineLInput(luv, height_, width_, dim);

		// Define a default kernel if it has not been already
		// defined by user
			if (h == null) {
				// define default kernel paramerters...
					KernelType k[] = {KernelType.UNIFORM, KernelType.UNIFORM};
					int	P[] = {2, N};
					float tempH[] = {(float)1.0 , (float)1.0};

				// define default kernel in mean shift base class
					DefineKernel(k, tempH, P, 2);
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.DefineImage() Exception ---\n");
			e.printStackTrace();
		}

	} // end DefineImage



	/**
	 * <pre>
	 * Uploads weight map specifying for each pixel in the image a value between 0 and 1:
	 * 1 indicaTing the presence of an edge and 0 the absense of an edge.
	 *
	 * Note: DefineImage must be called prior to calling this method. DefineImage is used
	 * to define the dimensions of the image.
	 *
	 * Usage: SetWeightMap(weightMap, epsilon)
	 *
	 * Pre:
	 *      - wm is a floating point array of size
	 *        (height x width) specifying for each pixel
	 *        edge strength.
	 *       - eps is a threshold used to fuse similar
	 *        regions during transitive closure.
	 * Post:
	 *      - wm has been used to populate the weight
	 *        map.
	 *      - the threshold used during transitive closure
	 *        is taken as eps.
	 *
	 * @param wm	a floating point array of size (height x width) specifying at location
	 *  			(i,j) the edge strength of pixel (i,j). (e.g. pixel (i,j) has an edge
	 * 				strength of weightMap[j*width+i])
	 * @param eps	a floating point number specifying the threshold used to fuse
	 *  			regions during transitive closure
	 */
	public void SetWeightMap(float[] wm, float eps) {

		try {

		// initlaize confmap using wm
			SetLatticeWeightMap(wm);

		// set threshold value
			if ((epsilon = eps) < 0) {
				ErrorHandler("msImageProcessor", "SetWeightMap", "Threshold is negative.");
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.SetWeightMap() Exception ---\n");
			e.printStackTrace();
		}

	} // end SetWeightMap



	/**
	 * <pre>
	 * Removes weight map. An error is NOT flagged
	 * if a weight map was not defined prior to calling
	 * this method.
	 *
	 * Usage: RemoveWeightMap(void)
	 *
	 * Post:
	 *    - the weight map has been removed.
	 *    - if a weight map did not exist NO error is flagged.
	 */
	public void RemoveWeightMap() {

		try {

		// remove confmap
			RemoveLatticeWeightMap();

		// set threshold value to zero
			epsilon	= 0;


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.RemoveWeightMap() Exception ---\n");
			e.printStackTrace();
		}


	} // end RemoveWeightMap



	/**
	 * <pre>
	 * Apply mean shift filter to the defined image, defined either via
	 * MeanShift::DefineLInput or msImageProcessor::DefineImage. The resulting
	 * segmented image is stored in the private data members of the image segmenter
	 * class which can be obtained by calling image msImageProcessor::GetResults().
	 *
	 * Usage: Filter(sigmaS, sigmaR, speedUpLevel)
	 *
	 * Pre:
	 *      - the user defined kernel used to apply mean shift filtering to the
	 *        defined input image has spatial bandwidth sigmaS and range band
	 *        width sigmaR
	 *      - speedUpLevel determines whether or not the filtering should be optimized
	 *        for faster execution: a value of NO_SPEEDUP turns this optimization off
	 *        and a value SPEEDUP turns this optimization on
	 *      - a data set has been defined
	 *      - the height and width of the lattice has been
	 *        specified using method DefineLattice()
	 * Post:
	 *      - mean shift filtering has been applied to the input image using a
	 *        user defined kernel
	 *      - the filtered image is stored in the private data members of the
	 *        msImageProcessor class.
	 *
	 * @param sigmaS		the spatial radius of the mean shift window
	 * @param sigmaR		the range radius of the mean shift window
	 * @param speedUpLevel	determines if a speed up optimization should be
	 * 						used to perform image filtering. A value of
	 * 						NO_SPEEDUP turns this optimization off and a
	 * 						value of SPEEDUP turns this optimization on
	 */
	public void Filter(int sigmaS, float sigmaR, SpeedUpLevel speedUpLevel) {

		try {

		// DECLARATIONS
			double timer;

		// Check Class consistency...

		// check:
		// (1) if this operation is consistent
		// (2) if kernel was created
		// (3) if data set is defined
		// (4) if the dimension of the kernel agrees with that
		//     of the defined data set
		// if not, flag an error!
			classConsistencyCheck(N+2, true);

			if (errorStatus == ErrorLevel.EL_ERROR) {
				return;
			}

		// If the image has just been read then allocate memory
		// for and initialize output data structure used to store
		// image modes and their corresponding regions...
			if (classState.OUTPUT_DEFINED == false) {

				InitializeOutput();

				// check for errors...
				if (errorStatus == ErrorLevel.EL_ERROR) {
					return;
				}
			}

		//****************** Allocate Memory ******************
		// Allocate memory for basin of attraction mode structure...
			modeTable = new char [L];
			pointList = new int [L];


		// start timer
			if (PROMPT) {
				msSystem.StartTimer();
			}

		//*****************************************************

		// filter image according to speedup level...
			if (speedUpLevel == SpeedUpLevel.NO_SPEEDUP) {
			// no speedup...
				NonOptimizedFilter((float)(sigmaS), sigmaR);

			} else if (speedUpLevel == SpeedUpLevel.MED_SPEEDUP) {
			// medium speedup
				OptimizedFilter1((float)(sigmaS), sigmaR);

			} else if (speedUpLevel == SpeedUpLevel.HIGH_SPEEDUP) {
			// high speedup
				OptimizedFilter2((float)(sigmaS), sigmaR);
			}

		// re-initialize structure
			modeTable = null;
			pointList = null;
			pointCount = 0;

		// Label image regions, also if segmentation is not to be
		// performed use the resulting classification structure to
		// calculate the image boundaries...

		// copy msRawData into LUV_data, rounding each component of each
		// LUV value stored by msRawData to the nearest integer
			int	i;
			for (i = 0; i < L*N; i++) {
				if (msRawData[i] < 0) {
					LUV_data[i] = (int)(msRawData[i] - 0.5);
				} else {
					LUV_data[i] = (int)(msRawData[i] + 0.5);
				}
			}

		// update status
			if (PROMPT) {
				timer = msSystem.ElapsedTime();
				msSystem.Prompt("(" + timer + " sec)\nConnecting regions...");
				msSystem.StartTimer();
			}

		// Perform connecting (label image regions) using LUV_data
			Connect();

		// update status
			if (PROMPT) {
				timer = msSystem.ElapsedTime();
				msSystem.Prompt("done. (" + timer + " seconds), numRegions = " + regionCount + "\n");
				msSystem.StartTimer();
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.Filter() Exception ---\n");
			e.printStackTrace();
		}


	} // end Filter



	/**
	 * <pre>
	 * Fuses the regions of a filtered image, defined either via MeanShift::
	 * DefineLInput or msImageProcessor::DefineImage. The resulting segmented
	 * image is stored in the private data members of the image segmenter class
	 * which can * be obtained by calling image msImageProcessor::GetResults().
	 *
	 * Usage: FuseRegions(sigmaR, minRegion)
	 *
	 * Pre:
	 *   - the range radius is specified by sigmaR
	 *   - minRegion is the minimum point density that
	 *     a region may have in the resulting segment
	 *     ed image
	 *   - a data set has been defined
	 *   - the height and width of the lattice has been
	 *     specified using method DefineLattice()
	 * Post:
	 *   - the image regions have been fused.
	 *   - if an result is stored by this class then
	 *     this result is used as input to this method.
	 *   - if no result is stored by this class,
	 *     the input image defined by calling the
	 *     method DefineImage is used.
	 *
	 * @param sigmaS		the range radius that defines similar color
	 * 						amongst image regions
	 * @param minRegion		the minimum density a region may have in the resulting
	 * 						segmented image. All regions have point density
	 * 						< minRegion are pruned from the image
	 */
	public void FuseRegions(float sigmaS, int minRegion) {

		try {

		// DECLARATIONS
			double timer;

		// Check Class consistency...

		// check:
		// (1) if this operation is consistent
		// (2) if kernel was created
		// (3) if data set is defined
		// (4) if the dimension of the kernel agrees with that
		//     of the defined data set
		// if not, flag an error!
			classConsistencyCheck(N+2, true);
			if (errorStatus == ErrorLevel.EL_ERROR) {
				return;
			}

		// obtain sigmaS (make sure it is not zero or negative, if not
		// flag an error)
			if ((h[1] = sigmaS) <= 0) {
				ErrorHandler("msImageProcessor", "FuseRegions", "The feature radius must be greater than or equal to zero.");
				return;
			}

		// if output has not yet been generated then classify the input
		// image regions to be fused...
			if (!(classState.OUTPUT_DEFINED)) {

				// Initialize output data structure used to store
				// image modes and their corresponding regions...
					InitializeOutput();

				// check for errors...
					if (errorStatus == ErrorLevel.EL_ERROR) {
						return;
					}

				// copy data into LUV_data used to classify
				// image regions
					int i;
					for (i = 0; i < L*N; i++) {
						if(data[i] < 0) {
							LUV_data[i] = (int)(data[i] - 0.5);
						} else {
							LUV_data[i] = (int)(data[i] + 0.5);
						}
					}

				// update status
					if (PROMPT) {
						msSystem.Prompt("Connecting regions...");
						msSystem.StartTimer();
					}

				// Perform connecting (label image regions) using LUV_data
					Connect();

				// check for errors
					if (errorStatus == ErrorLevel.EL_ERROR) {
						return;
					}

				// update status
					if (PROMPT) {
						timer = msSystem.ElapsedTime();
						msSystem.Prompt("done. (" + timer + " seconds), numRegions = " + regionCount + ".\n");
					}

			} // end if (!(classState.OUTPUT_DEFINED))

		// update status
			if (PROMPT) {
				msSystem.Prompt("Applying transitive closure...");
				msSystem.StartTimer();
			}

		// allocate memory visit table
			visitTable = new char [L];

		// Apply transitive closure iteratively to the regions classified
		// by the RAM updating labels and modes until the color of each neighboring
		// region is within sqrt(rR2) of one another.
			rR2 = (float)(h[1]*h[1]*0.25);

			TransitiveClosure();

			int oldRC = regionCount;
			int deltaRC, counter = 0;

			do {
				TransitiveClosure();
				deltaRC = oldRC-regionCount;
				oldRC = regionCount;
				counter++;
			} while ((deltaRC <= 0) && (counter < 10));

		// de-allocate memory for visit table
			visitTable	= null;

		// update status
			if (PROMPT) {
				timer	= msSystem.ElapsedTime();
				msSystem.Prompt("done. (" + timer + " seconds), numRegions = " + regionCount + "\n Pruning spurious regions...");
				msSystem.StartTimer();
			}

		// Prune spurious regions (regions whose area is under
		// minRegion) using RAM
			Prune(minRegion);

		// update status
			if (PROMPT) {
				timer	= msSystem.ElapsedTime();
				msSystem.Prompt("done. (" + timer + " seconds), numRegions = " + regionCount + "\n");
				msSystem.StartTimer();
			}

		// de-allocate memory for region adjacency matrix
			DestroyRAM();

		// output to msRawData
			int i, j, label;

			for(i = 0; i < L; i++) {
				label	= labels[i];
				for(j = 0; j < N; j++) {
					msRawData[N*i+j] = modes[N*label+j];
				}
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.FuseRegions() Exception ---\n");
			e.printStackTrace();
		}

	} // end FuseRegions



	/**
	 * <pre>
	 * Segments the defined image, defined either via MeanShift::DefineLInput or
	 * msImageProcessor::DefineImage. The resulting segmented image is stored in
	 * the private data members of the image processor class which can be
	 * obtained by calling ImageSegmenter::GetResults().
	 *
	 * Usage: Segment(sigmaS, sigmaR, minRegion, speedUpLevel)
	 * Pre:
	 *   - sigmaS and sigmaR are the spatial and range radii of the search window respectively
	 *   - minRegion is the minimum point density that a region may have in the resulting
	 *     segmented image
	 *   - speedUpLevel determines whether or not the filtering should be optimized
	 *     for faster execution: a value of NO_SPEEDUP turns this optimization off
	 *     and a value SPEEDUP turns this optimization on
	 * Post:
	 *   - the defined image is segmented and the resulting segmented image
	 *     is stored in the private data members of the image segmenter class.
	 *   - any regions whose point densities are less than or equal to minRegion
	 *     have been pruned from the segmented image
	 *
	 * @param sigmaS		the spatial radius of the mean shift window
	 * @param sigmaR		the range radius of the mean shift window
	 * @param minRegion		the minimum density a region may have in the resulting
	 * 						segmented image. All regions have point density < minRegion
	 * 						are pruned from the image
	 * @param speedUpLevel	determines if a speed up optimization should be used to
	 * 						perform image filtering. A value of NO_SPEEDUP turns this
	 *						optimization off and a value of SPEEDUP turns this optimization on
	 */
 	public void Segment(int sigmaS, float sigmaR, int minRegion, SpeedUpLevel speedUpLevel) {

		try {

		// DECLARATIONS
			double timer;

		// make sure kernel is properly defined...
			if ((h == null) || (kp < 2)) {
				ErrorHandler("msImageProcessor", "Segment", "Kernel corrupt or undefined.");
				return;
			}

		// Apply mean shift to data set using sigmaS and sigmaR...
			Filter(sigmaS, sigmaR, speedUpLevel);

		// check for errors
			if (errorStatus == ErrorLevel.EL_ERROR) {
				return;
			}

		// check to see if the system has been halted, if so exit
			if(errorStatus == ErrorLevel.EL_HALT) {
				return;
			}

		// update status
			if (PROMPT) {
				msSystem.Prompt("Applying transitive closure...");
				msSystem.StartTimer();
			}

		// allocate memory visit table
			visitTable = new char[L];

		// Apply transitive closure iteratively to the regions classified
		// by the RAM updating labels and modes until the color of each neighboring
		// region is within sqrt(rR2) of one another.
			rR2 = (float)(h[1]*h[1]*0.25);

			TransitiveClosure();

			int oldRC = regionCount;
			int deltaRC, counter = 0;

			do {
				TransitiveClosure();
				deltaRC = oldRC-regionCount;
				oldRC = regionCount;
				counter++;
			} while ((deltaRC <= 0) && (counter < 10));

		// de-allocate memory for visit table
			visitTable	= null;

		// update status
			if (PROMPT) {
				timer = msSystem.ElapsedTime();
				msSystem.Prompt("done. (" + timer + " seconds), numRegions = " + regionCount + "\nPruning spurious regions...");
				msSystem.StartTimer();
			}

		// Prune spurious regions (regions whose area is under
		// minRegion) using RAM
			Prune(minRegion);

		// update status
			if (PROMPT) {
				timer	= msSystem.ElapsedTime();
				msSystem.Prompt("done. ("+ timer + " seconds), numRegions = " + regionCount + "\nPruning spurious regions...");
				msSystem.StartTimer();
			}

		// de-allocate memory for region adjacency matrix
			DestroyRAM();

		// output to msRawData
			int j, i, label;

			for (i = 0; i < L; i++) {
				label	= labels[i];
				for (j = 0; j < N; j++) {
					msRawData[N*i+j] = modes[N*label+j];
				}
			}


		} catch (Exception e) {
			//System.out.println("\n--- MSImageProcessor.Segment() Exception ---\n");
			e.printStackTrace();
		}

	} // end Segment



	/**
	 * <pre>
	 * Converts an RGB vector to LUV.
	 *
	 * Usage: RGBtoLUV(rgbVal, luvVal)
	 *
	 * See:
	 *   G. Wyszecki and W.S. Stiles: Color Science:
	 *   Concepts and Methods, Quantitative Data and
	 *   Formulae, Wiley, New York, 1982.
	 * Pre:
	 *   - rgbVal is an unsigned char array containing the RGB vector
	 *   - luvVal is a floating point array containing the resulting LUV vector
	 * Post:
	 *   - rgbVal has been converted to LUV and the result has been stored in luvVal.
	 *
	 * @param rgbVal	an integer containing the RGB value to convert
	 * @param luvVal	a floating point array containing the LUV vector
	 * @param offset	the offset in the luvVal array where the luv components are to be written
	 */
	public void RGBtoLUV(int rgbVal, float[] luvVal, int offset) {

		try {

		// delcare variables
			double	x, y, z, L0, u_prime, v_prime, constant;

		// EXTRACT RGB COMPONENTS OF rgbVal
			int r, g, b;
			Color c = new Color(rgbVal);
			r = c.getRed();
			g = c.getGreen();
			b = c.getBlue();

		// convert RGB to XYZ...
			x = XYZ[0][0]*r + XYZ[0][1]*g + XYZ[0][2]*b;
			y = XYZ[1][0]*r + XYZ[1][1]*g + XYZ[1][2]*b;
			z = XYZ[2][0]*r + XYZ[2][1]*g + XYZ[2][2]*b;

		// convert XYZ to LUV...

			// compute L*
				L0 = y / (255.0 * Yn);

				if(L0 > Lt) {
					luvVal[offset + 0] = (float)(116.0 * (Math.pow(L0, 1.0/3.0)) - 16.0);
				} else {
					luvVal[offset + 0] = (float)(903.3 * L0);
				}

			// compute u_prime and v_prime
				constant	= x + 15 * y + 3 * z;

				if (constant != 0) {
					u_prime	= (4 * x) / constant;
					v_prime = (9 * y) / constant;
				} else {
					u_prime	= 4.0;
					v_prime	= 9.0/15.0;
				}

			// compute u* and v*
				luvVal[offset + 1] = (float) (13 * luvVal[offset + 0] * (u_prime - Un_prime));
				luvVal[offset + 2] = (float) (13 * luvVal[offset + 0] * (v_prime - Vn_prime));


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.RGBtoLUV() Exception ---\n");
			e.printStackTrace();
		}

	} // end RGBtoLUV




	// ROUNDING METHOD FOR LUVtoRGB
	/**
	 * <pre>
	 * Rounds double precision numbers to integer values.
	 *
	 * @param in_x value to be rounded
	 *
	 * @return rounded value
	 */
	private int my_round(double in_x) {

		if (in_x < 0) {
			return (int)(in_x - 0.5);
		} else {
			return (int)(in_x + 0.5);
		}

	} // end my_round



	/**
	 * <pre>
	 * Converts LUV values to RGB values.
	 *
	 * Usage: LUVtoRGB(luvVal, rgbVal)
	 *
	 * Pre:
	 *   - luvVal is a floating point array containing
	 *     the LUV vector
	 *   - rgbVal is an unsigned char array containing
	 *     the resulting RGB vector
	 * Post:
	 *   - luvVal has been converted to RGB and the
	 *     result has been stored in rgbVal.
	 *
	 * @param luvVal	a floating point array containing the LUV vector
	 * @param rgbVal	an unsigned char array containing the RGB vector
	 *
	 * @return integer values of RGB
	 */
	public int LUVtoRGB(float[] luvVal, int offset) {

		// declare variables...
			int		r=0, g=0, b=0;
			double	x, y, z, u_prime, v_prime;

		try {

		// perform conversion
			if (luvVal[offset + 0] < 0.1) {
				r = g = b = 0;
			} else {
			// convert luv to xyz...
				if (luvVal[offset + 0] < 8.0) {
					y	= Yn * luvVal[offset + 0] / 903.3;
				} else {
					y = (luvVal[offset + 0] + 16.0) / 116.0;
					y *= Yn * y * y;
				}

				u_prime	= luvVal[offset + 1] / (13 * luvVal[offset + 0]) + Un_prime;
				v_prime	= luvVal[offset + 2] / (13 * luvVal[offset + 0]) + Vn_prime;

				x = 9 * u_prime * y / (4 * v_prime);
				z = (12 - 3 * u_prime - 20 * v_prime) * y / (4 * v_prime);

			// convert xyz to rgb...
			// [r, g, b] = RGB*[x, y, z]*255.0
				r = my_round((RGB[0][0]*x + RGB[0][1]*y + RGB[0][2]*z)*255.0);
				g = my_round((RGB[1][0]*x + RGB[1][1]*y + RGB[1][2]*z)*255.0);
				b = my_round((RGB[2][0]*x + RGB[2][1]*y + RGB[2][2]*z)*255.0);

			// check bounds...
				if(r < 0)	r = 0; if(r > 255)	r = 255;
				if(g < 0)	g = 0; if(g > 255)	g = 255;
				if(b < 0)	b = 0; if(b > 255)	b = 255;

			} // end if (luvVal[offset + 0] < 0.1)


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.LUVtoRGB() Exception ---\n");
			e.printStackTrace();
		}


		// CONVERT RGB COMPONENTS TO SINGLE INTEGER
			Color c = new Color(r, g, b);
			return c.getRGB();

	} // end LUVtoRGB



	/**
	 * <pre>
	 * Returns the resulting filtered or segmented image data after calling Filter()
	 * or Segment().
	 *
	 * NOTE: If DefineImage was used to specify the the input to this class,
	 *       outputImageData is in the LUV data space.
	 *
	 * Usage: GetResults(outputImageData)
	 *
	 * Pre:
	 *   - outputImageData is a pre-allocated floating point array used to store the
	 *     filtered or segmented image pixels.
	 * Post:
	 *   - the filtered or segmented image data is stored by outputImageData.
	 *
	 * @param outputImageData	a floating point array containing the vector data of
	 *							the filtered or segmented image
	 */
	public void GetRawData(float[] outputImageData) {

		try {

		// make sure that outputImageData is not NULL
			if(outputImageData == null) {
				ErrorHandler("msImageProcessor", "GetRawData", "Output image data buffer is null.");
				return;
			}

		// copy msRawData to outputImageData
			int i;

			for (i = 0; i < L*N; i++) {
				outputImageData[i] = msRawData[i];
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.GetRawData() Exception ---\n");
			e.printStackTrace();
		}


	} // end GetRawData



	/**
	 * <pre>
	 * Returns the resulting filtered or segmented im age after calling Filter()
	 * or Segment().
	 *
	 * Usage: GetResults(outputImage)
	 *
	 * Pre:
	 *   - outputImage is a pre-allocated unsinged char array used to store the
	 *      filtered or segmented image pixels
	 * Post:
	 *   - the filtered or segmented image is stored by outputImage.
	 *
	 * @param outputImage	an unsigned char array containing the RGB vector data of the
	 *						output image to obtain the un-converted (LUV) data space output
	 *						one may use msImageProcessor::GetRawData()
	 */
	public void GetResults(int[] outputImage) {

		try {

		// make sure that outpuImage is not NULL
			if (outputImage == null) {
				ErrorHandler("msImageProcessor", "GetResults", "Output image buffer is null.");
				return;
			}

		// if the image type is GREYSCALE simply
		// copy it over to the segmentedImage
			if (N == 1) {
				// copy over msRawData to segmentedImage checking
				// bounds
					int i, pxValue;
					for(i = 0; i < L; i++) {

						// get value
							pxValue = (int)(msRawData[i]+0.5);

						// store into segmented image checking bounds...
							if (pxValue < 0) {
								outputImage[i] = (byte)(0);
							} else if (pxValue > 255) {
								outputImage[i] = (byte)(255);
							} else {
								outputImage[i] = (byte)(pxValue);
							}

					} // end for

			} else if (N == 3) {
				// otherwise convert msRawData from LUV to RGB
				// storing the result in segmentedImage
					int i;
					for(i = 0; i < L; i++) {
						outputImage[i] = LUVtoRGB(msRawData, (N*i));
					}

			} else {
				// Unknown image type: should use MeanShift::GetRawData()...
					ErrorHandler("msImageProcessor", "GetResults", "Unknown image type. Try using MeanShift::GetRawData().");
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.GetResults() Exception ---\n");
			e.printStackTrace();
		}


	} // end GetResults



	/**
	 * Returns the boundaries of each region of the segmented image using a region
	 * list object, available after filtering or segmenting the defined image.
	 *
	 * Usage: regionList = GetBoundaries()
	 *
	 * Post:
	 *   - a region list object containing the boundary
	 *     locations for each region is constructed
	 *   - the region list is returned
	 *   - NULL is returned if the image has not been
	 *     filtered or segmented
	 */
	public RegionList GetBoundaries() {

		try {

		// define bounds using label information
			if (classState.OUTPUT_DEFINED) {
				DefineBoundaries();
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.GetBoundaries() Exception ---\n");
			e.printStackTrace();
		}


		// return region list structure
			return regionList;

	} // end GetBoundaries



	/**
	 * <pre>
	 * Returns the regions of the processed image. Each region in the image is uniquely
	 * characterized by its location and color (e.g. RGB). GetRegions() therefore returns
	 * the following information about the regions of the processed image:
	 *
	 * NOTE: Memory for the above integer and floating point arrays is allocated inside
	 *       this method. Also modes stored by the modes array are not in the RGB space.
	 *       Instead if the method DefineImage was used, these data points are in the LUV
	 *       space, and if the method DefineLInput was used these data points are in
	 *       whatever space you specified them to be in when calling DefineLInput.
	 *
	 * Usage: regionCount = GetRegions(labels, modes modePointCounts)
	 *
	 * Pre:
	 *   - labels_out is an integer array of size height*width that stores for
	 *     each pixel a label relating that pixel to a corresponding
	 *     region in the image
	 *   - modes_out is floating point array of size regionCount*N storing the
	 *     feature component of each region, and indexed by region label
	 *   - modePointCounts is an integer array of size regionCount, indexed by region
	 *     label, that stores the area of each region in pixels.
	 * Post:
	 *   If an input image was defined and processed,
	 *   - memory has been allocated for labels_out, modes_out and MPC_out.
	 *   - labels_out, modes_out, and MPC_out have been populated.
	 *   - the number of regions contained by the segmented image has been returned.
	 *
	 *	 If the image has not been defined or processed or if there is in-sufficient memory,
	 *   - no memory has been allocated for labels_out, modes_out, and MPC_out.
	 *   - -1 is returned for regionCount.
	 *
	 * @param regionCount	an integer that specifies the number of regions contained in
	 *						the processed image
	 * @param labels_out	a floating point array of length regionCount*N containing the
	 *						feature space component of each region (e.g. LUV), and indexed
	 * 						by region label
	 * @param modes_out		an integer array of length (height*width) which contains at every
	 * 						pixel location (x,y) a label relating that pixel to a region
	 *						whose mode is specified by modes and whose area is specified by
	 *						modePointCounts
	 * @param MPC_out		an integer array of length regionCount and indexed by region
	 *						label, that specifies the region area (in pixels) for each
	 *						segmented image region. (e.g. Area of region having label
	 *						specified by l, has area modePointCounts[l] (pixels).)
	 *
	 * @return int			returns the regions of the processed image
	 */
	public int GetRegions(int[] labels_out, float[] modes_out, int[] MPC_out) {

		// check to see if output has been defined for the given input image...
			if (classState.OUTPUT_DEFINED == false) {
				return -1;
			}

		try {

		// allocate memory for labels_out, modes_out and MPC_out based
		// on output storage structure
			int[] labels_, MPC_out_;
			float[]	modes_;

			labels_ = new int [L];
			modes_	= new float [regionCount*N];
			MPC_out_ = new int [regionCount];



		// populate labels_out with image labels
			int	i;
			for (i = 0; i < L; i++) {
				labels_[i] = labels[i];
			}

		// populate modes_out and MPC_out with the color and point
		// count of each region
			for (i = 0; i < regionCount*N; i++) {
				modes_[i]	= modes[i];
			}
			for (i = 0; i < regionCount; i++) {
				MPC_out_[i]	= modePointCounts[i];
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.GetRegions() Exception ---\n");
			e.printStackTrace();
		}


		// done. Return the number of regions resulting from filtering or segmentation.
			return regionCount;

	} // end GetRegions



	/**
	 * <pre>
	 * Second GetRegions method added by BP to return array of REGION objects each
	 * containing the pixels locations for a significant region.
	 *
	 * @return array of REGION objects
	 */
	public REGION[] GetRegions() {

		// DECLARATIONS
			REGION[] tmpRL = new REGION[regionCount];
			int label = 0;

		try {

		// ADD A REGION OBJECT TO ARRAY FOR EACH REGION
			for (int i = 0; i < regionCount; i++) {
				tmpRL[i] = new REGION();
				tmpRL[i].label = i;
				tmpRL[i].pointCount = 0;
				tmpRL[i].region = new int[modePointCounts[i]];
			}

		// FILL REGION ARRAY WITH CORRESPONDING PIXEL LOCATIONS
			for (int i = 0; i < L; i++) {
				label = labels[i];
				tmpRL[label].region[tmpRL[label].pointCount] = i;
				tmpRL[label].pointCount++;
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.GetRegions() Exception ---\n");
			e.printStackTrace();
		}


		return tmpRL;

	} // end GetRegions



// PRIVATE METHODS
	/**
	 * <pre>
	 * Performs mean shift filtering on the specified input image using a user
	 * defined kernel.
	 *
	 * Usage: NonOptimizedFilter(sigmaS, sigmaR)
	 *
	 * Pre:
	 *      - the user defined kernel used to apply mean shift filtering to the
	 *        defined input image has spatial bandwidth sigmaS and range band
	 *        width sigmaR
	 *      - a data set has been defined
	 *      - the height and width of the lattice has been specified using method
	 *		  DefineLattice()
	 * Post:
	 *      - mean shift filtering has been applied to the input image using a user
	 *        defined kernel
	 *      - the filtered image is stored in the private data members of the
	 *        msImageProcessor class.
	 *
	 * @param sigmaS
	 * @param sigmaR
	 */
	private void NonOptimizedFilter(float sigmaS, float sigmaR) {

		try {

		// Declare Variables
			int iterationCount, i, j;
			double mvAbs;

		// make sure that a lattice height and width have
		// been defined...
			if (height == 0) {
				ErrorHandler("msImageProcessor", "LFilter", "Lattice height and width are undefined.");
				return;
			}

		// re-assign bandwidths to sigmaS and sigmaR
			if (((h[0] = sigmaS) <= 0)||((h[1] = sigmaR) <= 0)) {
				ErrorHandler("msImageProcessor", "Segment", "sigmaS and/or sigmaR is zero or negative.");
				return;
			}

		// define input data dimension with lattice
			int lN = N + 2;

		// Traverse each data point applying mean shift
		// to each data point
		// Allcocate memory for yk
			double[] yk = new double [lN];

		// Allocate memory for Mh
			double[] Mh = new double [lN];

		// update status
			if (PROMPT) {
				msSystem.Prompt("Applying mean shift (Using Lattice)... ");
			}

			for(i = 0; i < L; i++) {

				// Assign window center (window centers are
				// initialized by createLattice to be the point
				// data[i])
					yk[0] = i%width;
					yk[1] = i/width;
					for (j = 0; j < N; j++) {
						yk[j+2] = data[N*i+j];
					}

				// Calculate the mean shift vector using the lattice
					LatticeMSVector(Mh, yk);

				// Calculate its magnitude squared
					mvAbs = 0;
					for (j = 0; j < lN; j++) {
						mvAbs += Mh[j]*Mh[j];
					}

				// Keep shifting window center until the magnitude squared of the
				// mean shift vector calculated at the window center location is
				// under a specified threshold (Epsilon)

				// NOTE: iteration count is for speed up purposes only - it
				//       does not have any theoretical importance
					iterationCount = 1;
					while ((mvAbs >= EPSILON) && (iterationCount < LIMIT)) {

						// Shift window location
							for (j = 0; j < lN; j++) {
								yk[j] += Mh[j];
							}

						// Calculate the mean shift vector at the new
						// window location using lattice
							LatticeMSVector(Mh, yk);

						// Calculate its magnitude squared
							mvAbs = 0;
							for (j = 0; j < lN; j++) {
								mvAbs += Mh[j]*Mh[j];
							}

						// Increment interation count
							iterationCount++;

					} // end while

				// Shift window location
					for (j = 0; j < lN; j++) {
						yk[j] += Mh[j];
					}

				// store result into msRawData...
					for (j = 0; j < N; j++) {
						msRawData[N*i+j] = (float)(yk[j+2]);
					}

			} // end for loop

		// Prompt user that filtering is completed
			if (PROMPT) {
				msSystem.Prompt("done.\n");
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.NonOptimizedFilter() Exception ---\n");
			e.printStackTrace();
		}

	} // end NonOptimizedFilter



	/**
	 * <pre>
	 * Performs mean shift filtering on the specified input image using a user defined
	 * kernel. Previous mode information is used to avoid re-applying mean shift on
	 * certain data points to improve performance.
	 *
	 * Usage: OptimizedFilter1(sigmaS, sigmaR)
	 *
	 * Pre:
	 *   - the user defined kernel used to apply mean
	 *     shift filtering to the defined input image
	 *     has spatial bandwidth sigmaS and range band
	 *     width sigmaR
	 *   - a data set has been defined
	 *   - the height and width of the lattice has been
	 *     specified using method DefineLattice()
	 * Post:
	 *   - mean shift filtering has been applied to the
	 *     input image using a user defined kernel
	 *   - the filtered image is stored in the private
	 *     data members of the msImageProcessor class.
	 *
	 * @param sigmaS
	 * @param sigmaR
	 */
 	private void OptimizedFilter1(float sigmaS, float sigmaR) {

		try {

		// Declare Variables
			int	iterationCount, i, j, k, s, p, modeCandidateX, modeCandidateY, modeCandidate_i;
			float[]	modeCandidatePoint;
			double	mvAbs, diff, el;

		// make sure that a lattice height and width have
		// been defined...
			if (height == 0) {
				ErrorHandler("msImageProcessor", "LFilter", "Lattice height and width are undefined.");
				return;
			}

		// re-assign bandwidths to sigmaS and sigmaR
			if (((h[0] = sigmaS) <= 0)||((h[1] = sigmaR) <= 0)) {
				ErrorHandler("msImageProcessor", "Segment", "sigmaS and/or sigmaR is zero or negative.");
				return;
			}

		// define input data dimension with lattice
			int lN = N + 2;

		// Traverse each data point applying mean shift
		// to each data point

		// Allcocate memory for yk
			double[]	yk		= new double [lN];

		// Allocate memory for Mh
			double[]	Mh		= new double [lN];

		// Initialize mode table used for basin of attraction
			Arrays.fill(modeTable, (char)0);

		// Allocate memory mode candidate data point...
		// floating point version
			modeCandidatePoint = new float[N];

		// proceed...

			// update status
				if (PROMPT) {
					msSystem.Prompt("Applying mean shift (Using Lattice)...");
				}


				for (i = 0; i < L; i++) {
					// if a mode was already assigned to this data point
					// then skip this point, otherwise proceed to
					// find its mode by applying mean shift...
						if (modeTable[i] == 1) {
							continue;
						}

					// initialize point list...
						pointCount = 0;

					// Assign window center (window centers are
					// initialized by createLattice to be the point
					// data[i])
						yk[0] = i%width;
						yk[1] = i/width;
						for (j = 0; j < N; j++) {
							yk[j+2] = data[N*i+j];
						}

					// Calculate the mean shift vector using the lattice
						LatticeMSVector(Mh, yk);

					// Calculate its magnitude squared
						mvAbs = 0;
						for (j = 0; j < lN; j++) {
							mvAbs += Mh[j]*Mh[j];
						}

					// Keep shifting window center until the magnitude squared of the
					// mean shift vector calculated at the window center location is
					// under a specified threshold (Epsilon)

					// NOTE: iteration count is for speed up purposes only - it
					//       does not have any theoretical importance
						iterationCount = 1;
						while ((mvAbs >= EPSILON) && (iterationCount < LIMIT)) {

							// Shift window location
								for (j = 0; j < lN; j++) {
									yk[j] += Mh[j];
								}

							// check to see if the current mode location is in the
							// basin of attraction...

							// calculate the location of yk on the lattice
								modeCandidateX = (int) (yk[0]+0.5);
								modeCandidateY = (int) (yk[1]+0.5);
								modeCandidate_i	= modeCandidateY*width + modeCandidateX;

							// if mvAbs != 0 (yk did indeed move) then check
							// location basin_i in the mode table to see if
							// this data point either:

							// (1) has not been associated with a mode yet
							//     (modeTable[basin_i] = 0), so associate
							//     it with this one
							//
							// (2) it has been associated with a mode other
							//     than the one that this data point is converging
							//     to (modeTable[basin_i] = 1), so assign to
							//     this data point the same mode as that of basin_i

								if ((modeTable[modeCandidate_i] != 2) && (modeCandidate_i != i)) {
									// obtain the data point at basin_i to
									// see if it is within h*TC_DIST_FACTOR of
									// of yk
										for (j = 0; j < N; j++) {
											modeCandidatePoint[j] = data[N*modeCandidate_i + j];
										}

									// check basin on non-spatial data spaces only
										k = 1;
										s = 0;
										diff = 0;

										while ((diff < TC_DIST_FACTOR) && (k<kp)) {
											diff = 0;
											for (p=0; p<P[k]; p++) {
												el = (modeCandidatePoint[p+s]-yk[p+s+2])/h[k];
												diff += el*el;
											}
											s+=P[k];
											k++;
										}

									// if the data point at basin_i is within
									// a distance of h*TC_DIST_FACTOR of yk
									// then depending on modeTable[basin_i] perform
									// either (1) or (2)
										if (diff < TC_DIST_FACTOR) {
											// if the data point at basin_i has not
											// been associated to a mode then associate
											// it with the mode that this one will converge
											// to
												if (modeTable[modeCandidate_i] == 0) {
													// no mode associated yet so associate
													// it with this one...
														pointList[pointCount++]	= modeCandidate_i;
														modeTable[modeCandidate_i] = 2;

												} else {

													// the mode has already been associated with
													// another mode, thererfore associate this one
													// mode and the modes in the point list with
													// the mode associated with data[basin_i]...

													// store the mode info into yk using msRawData...
														for (j = 0; j < N; j++) {
															yk[j+2] = msRawData[modeCandidate_i*N+j];
														}

													// update mode table for this data point
													// indicating that a mode has been associated
													// with it
														modeTable[i] = 1;

													// indicate that a mode has been associated
													// to this data point (data[i])
														mvAbs = -1;

													// stop mean shift calculation...
														break;
												}
										} // end if (diff < TC_DIST_FACTOR)

								} // end if ((modeTable[modeCandidate_i] != 2) && (modeCandidate_i != i))

							// Calculate the mean shift vector at the new
							// window location using lattice
								LatticeMSVector(Mh, yk);

							// Calculate its magnitude squared
								mvAbs = 0;
								for (j = 0; j < lN; j++) {
									mvAbs += Mh[j]*Mh[j];
								}

							// Increment iteration count
								iterationCount++;

						} // end while ((mvAbs >= EPSILON) && (iterationCount < LIMIT))

					// if a mode was not associated with this data point
					// yet associate it with yk...
						if (mvAbs >= 0) {
							// Shift window location
								for (j = 0; j < lN; j++) {
									yk[j] += Mh[j];
								}

							// update mode table for this data point
							// indicating that a mode has been associated
							// with it
								modeTable[i] = 1;

						} // end if (mvAbs >= 0)

					// associate the data point indexed by
					// the point list with the mode stored
					// by yk
						for (j = 0; j < pointCount; j++) {
							// obtain the point location from the
							// point list
								modeCandidate_i = pointList[j];

							// update the mode table for this point
								modeTable[modeCandidate_i] = 1;

							//store result into msRawData...
								for (k = 0; k < N; k++) {
									msRawData[N*modeCandidate_i+k] = (float)(yk[k+2]);
								}
						} // end for


					// store result into msRawData...
						for (j = 0; j < N; j++) {
							msRawData[N*i+j] = (float)(yk[j+2]);
						}

				} // end for

		// Prompt user that filtering is completed
			if (PROMPT) {
				msSystem.Prompt("done.\n");
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.OptimizedFilter1() Exception ---\n");
			e.printStackTrace();
		}

	} // end OptimizedFilter1



	/**
	 * <pre>
	 * Performs mean shift filtering on the specified input image using a user defined
	 * kernel. Previous mode information is used to avoid re-applying mean shift on
	 * certain data points to improve performance. To further improve perfmance (during
	 * segmentation) points within h of a window center during the window center's
	 * traversal to a mode are associated with the mode that the window converges to.
	 *
	 * Usage: OptimizedFilter2(sigmaS, sigmaR)
	 *
	 * Pre:
	 *   - the user defined kernel used to apply mean
	 *     shift filtering to the defined input image
	 *     has spatial bandwidth sigmaS and range band
	 *     width sigmaR
	 *   - a data set has been defined
	 *   - the height and width of the lattice has been
	 *     specified using method DefineLattice()
	 * Post:
	 *   - mean shift filtering has been applied to the
	 *     input image using a user defined kernel
	 *   - the filtered image is stored in the private
	 *     data members of the msImageProcessor class.
	 *
	 * @param sigmaS
	 * @param sigmaR
	 */
	private void OptimizedFilter2(float sigmaS, float sigmaR) {

		try {

		// if confidence map is null set it to zero
			if (weightMap ==  null) {
				weightMap = new float [L];
				int i;
				for (i = 0; i < L; i++) {
					weightMap[i] = 0;
				}
			}

		// Declare Variables
			int	iterationCount, i, j, k, s, p, modeCandidateX, modeCandidateY, modeCandidate_i;
			float[]	modeCandidatePoint;
			double mvAbs, diff, el;

		// make sure that a lattice height and width have
		// been defined...
			if (height == 0) {
				ErrorHandler("msImageProcessor", "LFilter", "Lattice height and width are undefined.");
				return;
			}

		// re-assign bandwidths to sigmaS and sigmaR
			if (((h[0] = sigmaS) <= 0)||((h[1] = sigmaR) <= 0)) {
				ErrorHandler("msImageProcessor", "Segment", "sigmaS and/or sigmaR is zero or negative.");
				return;
			}

		// define input data dimension with lattice
			int lN = N + 2;

		// Traverse each data point applying mean shift
		// to each data point

			// Allcocate memory for yk
				double[] yk	= new double [lN];

			// Allocate memory for Mh
				double[] Mh	= new double [lN];

			// Initialize mode table used for basin of attraction
				Arrays.fill(modeTable, (char)0);

			// Allocate memory mode candidate data point...
			// floating point version
				modeCandidatePoint = new float	[N];

			// proceed...

			// update status
				if (PROMPT) {
					msSystem.Prompt("Applying mean shift (Using Lattice)...");
				}

				for (i = 0; i < L; i++) {
					// if a mode was already assigned to this data point
					// then skip this point, otherwise proceed to
					// find its mode by applying mean shift...
						if (modeTable[i] == 1) {
							continue;
						}

					// initialize point list...
						pointCount = 0;

					// Assign window center (window centers are
					// initialized by createLattice to be the point
					// data[i])
						yk[0] = i%width;
						yk[1] = i/width;
						for (j = 0; j < N; j++) {
							yk[j+2] = data[N*i+j];
						}

					// Calculate the mean shift vector using the lattice
						OptLatticeMSVector(Mh, yk);

					// Calculate its magnitude squared
						mvAbs = 0;
						for (j = 0; j < lN; j++) {
							mvAbs += Mh[j]*Mh[j];
						}

					// Keep shifting window center until the magnitude squared of the
					// mean shift vector calculated at the window center location is
					// under a specified threshold (Epsilon)

					// NOTE: iteration count is for speed up purposes only - it
					//       does not have any theoretical importance
						iterationCount = 1;
						while ((mvAbs >= EPSILON)&&(iterationCount < LIMIT)) {

							// Shift window location
								for (j = 0; j < lN; j++) {
									yk[j] += Mh[j];
								}

							// check to see if the current mode location is in the
							// basin of attraction...

							// calculate the location of yk on the lattice
								modeCandidateX = (int) (yk[0]+0.5);
								modeCandidateY = (int) (yk[1]+0.5);
								modeCandidate_i	= modeCandidateY*width + modeCandidateX;

							// if mvAbs != 0 (yk did indeed move) then check
							// location basin_i in the mode table to see if
							// this data point either:

							// (1) has not been associated with a mode yet
							//     (modeTable[basin_i] = 0), so associate
							//     it with this one
							//
							// (2) it has been associated with a mode other
							//     than the one that this data point is converging
							//     to (modeTable[basin_i] = 1), so assign to
							//     this data point the same mode as that of basin_i
								if ((modeTable[modeCandidate_i] != 2) && (modeCandidate_i != i)) {
									// obtain the data point at basin_i to
									// see if it is within h*TC_DIST_FACTOR of
									// of yk
										for (j = 0; j < N; j++) {
											modeCandidatePoint[j] = data[N*modeCandidate_i + j];
										}

									// check basin on non-spatial data spaces only
										k = 1;
										s = 0;
										diff = 0;
										while ((diff < TC_DIST_FACTOR) && (k<kp)) {
											diff = 0;
											for (p=0; p<P[k]; p++) {
												el = (modeCandidatePoint[p+s]-yk[p+s+2])/h[k];
												diff += el*el;
											}
											s+=P[k];
											k++;
										}

									// if the data point at basin_i is within
									// a distance of h*TC_DIST_FACTOR of yk
									// then depending on modeTable[basin_i] perform
									// either (1) or (2)
										if (diff < TC_DIST_FACTOR) {
											// if the data point at basin_i has not
											// been associated to a mode then associate
											// it with the mode that this one will converge
											// to
												if (modeTable[modeCandidate_i] == 0) {
													// no mode associated yet so associate
													// it with this one...
														pointList[pointCount++]	= modeCandidate_i;
														modeTable[modeCandidate_i] = 2;

												} else {

													// the mode has already been associated with
													// another mode, thererfore associate this one
													// mode and the modes in the point list with
													// the mode associated with data[basin_i]...

													// store the mode infor int yk using msRawData...
														for (j = 0; j < N; j++) {
															yk[j+2] = msRawData[modeCandidate_i*N+j];
														}

													// indicate that a mode has been associated
													// to this data point (data[i])
														mvAbs = -1;

													// stop mean shift calculation...
														break;
												}
										} // end if (diff < TC_DIST_FACTOR)

								} // end if ((modeTable[modeCandidate_i] != 2) && (modeCandidate_i != i))

							// Calculate the mean shift vector at the new
							// window location using lattice
								OptLatticeMSVector(Mh, yk);

							// Calculate its magnitude squared
								mvAbs = 0;
								for (j = 0; j < lN; j++) {
									mvAbs += Mh[j]*Mh[j];
								}

							// Increment interation count
								iterationCount++;

						} // end while ((mvAbs >= EPSILON)&&(iterationCount < LIMIT))

					// if a mode was not associated with this data point
					// yet then perform a shift the window center yk one
					// last time using the mean shift vector...
						if (mvAbs >= 0) {
							// Shift window location
								for (j = 0; j < lN; j++) {
									yk[j] += Mh[j];
								}
						}

					// associate the data point indexed by
					// the point list with the mode stored
					// by yk
						for (j = 0; j < pointCount; j++) {
							// obtain the point location from the
							// point list
								modeCandidate_i = pointList[j];

							// update the mode table for this point
								modeTable[modeCandidate_i] = 1;

							//store result into msRawData...
								for (k = 0; k < N; k++) {
									msRawData[N*modeCandidate_i+k] = (float)(yk[k+2]);
								}
						} // end for


					// store result into msRawData...
						for (j = 0; j < N; j++) {
							msRawData[N*i+j] = (float)(yk[j+2]);
						}

				} // end for

		// Prompt user that filtering is completed
			if (PROMPT) {
				msSystem.Prompt("done.\n");
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.OptimizedFilter2() Exception ---\n");
			e.printStackTrace();
		}


	} // end OptimizedFilter2



	/**
	 * <pre>
	 * Classifies the regions of the mean shift filtered image.
	 *
	 * Usage: Connect()
	 *
	 * Post:
	 *   - the regions of the mean shift image have been classified using the private
	 *     classification structure of the msImageProcessor Class. Namely, each region
	 *     uniquely identified by its LUV color  (stored by LUV_data) and location has
	 *     been labeled and its area computed via an eight-connected fill.
	 */
	private void Connect() {

		try {

		// define eight connected neighbors
			neigh[0] = 1;
			neigh[1] = 1-width;
			neigh[2] = -width;
			neigh[3] = -(1+width);
			neigh[4] = -1;
			neigh[5] = width-1;
			neigh[6] = width;
			neigh[7] = width+1;

		// initialize labels and modePointCounts
			int i;
			for (i = 0; i < width*height; i++) {
				labels[i]			= -1;
				modePointCounts[i]	=  0;
			}

		// Traverse the image labeling each new region encountered
			int k, label = -1;
			for (i = 0; i < height*width; i++) {
				//if this region has not yet been labeled - label it
					if (labels[i] < 0) {
						// assign new label to this region
							labels[i] = ++label;

						// copy region color into modes
							for (k = 0; k < N; k++) {
								modes[(N*label)+k]	= (float)(LUV_data[(N*i)+k]);
							}

						// populate labels with label for this specified region
						// calculating modePointCounts[label]...
							Fill(i, label);
					}
			}

		// calculate region count using label
			regionCount	= label+1;


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.Connect() Constructor Exception ---\n");
			e.printStackTrace();
		}


	} // end Connect



	/**
	 * <pre>
	 * Given a region seed and a region label, Fill uses the region seed to perform
	 * an eight-connected fill for the specified region, labeling all pixels contained
	 * by the region with the specified label: label.
	 *
	 * Usage: Fill(regionLoc, label)
	 *
	 * Pre:
	 *   - regionLoc is a region seed - a pixel that is identified as being part of the
	 *     region labled using the label, label.
	 * Post:
	 *   - all pixels belonging to the region specified by regionLoc (having the same
	 *     integer LUV value specified by LUV_data) are classified
	 *     as one region by labeling each pixel in the image clasification structure using
	 *     label via an eight-connected fill.
	 *
	 * @param regionLoc
	 * @param label
	 */
	private void Fill(int regionLoc, int label) {

		try {

		// declare variables
			int	i, k, neighLoc, imageSize = width*height;
			boolean neighborsFound;

		// Fill region starting at region location
		// using labels...

		// initialize indexTable
			int	index = 0;
			indexTable[0] = regionLoc;

		// increment mode point counts for this region to
		// indicate that one pixel belongs to this region
			modePointCounts[label]++;

		while (true) {

			// assume no neighbors will be found
				neighborsFound	= false;

			// check the eight connected neighbors at regionLoc -
			// if a pixel has similar color to that located at
			// regionLoc then declare it as part of this region
				for (i = 0; i < 8; i++) {
					//if at boundary do not check certain neighbors because
					//they do not exist...
						if ((regionLoc%width == 0)&&((i == 3)||(i == 4)||(i == 5))) {
							continue;
						}

						if ((regionLoc%width == 255)&&((i == 0)||(i == 1)||(i == 7))) {
							continue;
						}

					//check bounds and if neighbor has been already labeled
						neighLoc = regionLoc + neigh[i];

						if ((neighLoc >= 0)&&(neighLoc < imageSize)&&(labels[neighLoc] < 0)) {

							for (k = 0; k < N; k++) {
								if (LUV_data[(regionLoc*N)+k] != LUV_data[(neighLoc*N)+k]) {
									break;
								}
							}

							//neighbor i belongs to this region so label it and
							//place it onto the index table buffer for further
							//processing
								if (k == N) {
									// assign label to neighbor i
										labels[neighLoc]	= label;

									// increment region point count
										modePointCounts[label]++;

									// place index of neighbor i onto the index tabel buffer
										indexTable[++index]	= neighLoc;

									// indicate that a neighboring region pixel was
									// identified
										neighborsFound	= true;
								}
						}
				} // end for

			// check the indexTable to see if there are any more
			// entries to be explored - if so explore them, otherwise
			// exit the loop - we are finished
				if (neighborsFound) {
					regionLoc = indexTable[index];
				} else if (index > 1) {
					regionLoc = indexTable[--index];
				} else {
					break; //fill complete
				}

		} // end while


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.Fill() Exception ---\n");
			e.printStackTrace();
		}


	} // end Fill



	/**
	 * <pre>
	 * Constructs a region adjacency matrix.
	 *
	 * Usage: BuildRAM()
	 *
	 * Pre:
	 *   - the classification data structure has been constructed.
	 * Post:
	 *   - a region adjacency matrix has been built using the classification data structure.
	 */
	private void BuildRAM() {

		try {

		// Allocate memory for region adjacency matrix if it hasn't already been allocated
			if (raList == null) {
				raList = new RAList [regionCount];
				raPool = new RAList [NODE_MULTIPLE*regionCount];
			}

		// initialize the region adjacency list
			int i;
			for (i = 0; i < regionCount; i++) {

 				raList[i] = new RAList();

				raList[i].edgeStrength = 0;
				raList[i].edgePixelCount = 0;
				raList[i].label = i;
				raList[i].next = null;
			}

		// initialize RAM free list
			for (i = 0; i < NODE_MULTIPLE*regionCount; i++) {
				raPool[i] = new RAList();
			}

			freeRAList = raPool[0];

			for (i = 0; i < NODE_MULTIPLE*regionCount-1; i++) {
				raPool[i].edgeStrength		= 0;
				raPool[i].edgePixelCount	= 0;
				raPool[i].next = raPool[i+1];
			}

			raPool[NODE_MULTIPLE*regionCount-1].next	= null;

		// traverse the labeled image building
		// the RAM by looking to the right of
		// and below the current pixel location thus
		// determining if a given region is adjacent
		// to another
			int	j, curLabel, rightLabel, bottomLabel, exists;
			RAList	raNode1, raNode2, oldRAFreeList;
			for (i = 0; i < height - 1; i++) {
				// check the right and below neighbors
				// for pixel locations whose x < width - 1
				for (j = 0; j < width - 1; j++) {
					// calculate pixel labels
						curLabel = labels[i*width+j    ];	// current pixel
						rightLabel = labels[i*width+j+1  ];	// right pixel
						bottomLabel	= labels[(i+1)*width+j];// bottom pixel

					// check to the right, if the label of
					// the right pixel is not the same as that
					// of the current one then region[j] and region[j+1]
					// are adjacent to one another - update the RAM
					if (curLabel != rightLabel) {
						// obtain RAList object from region adjacency free
						// list
							raNode1	= freeRAList;
							raNode2	= freeRAList.next;

						// keep a pointer to the old region adj. free
						// list just in case nodes already exist in respective
						// region lists
							oldRAFreeList = freeRAList;

						// update region adjacency free list
							freeRAList = freeRAList.next.next;

						// populate RAList nodes
							raNode1.label = curLabel;
							raNode2.label = rightLabel;

						// insert nodes into the RAM
							exists = 0;
							raList[curLabel].Insert(raNode2);
							exists = raList[rightLabel].Insert(raNode1);

						// if the node already exists then place
						// nodes back onto the region adjacency
						// free list
							if (exists != 0) {
								freeRAList = oldRAFreeList;
							}

					} // end if (curLabel != rightLabel)

					// check below, if the label of
					// the bottom pixel is not the same as that
					// of the current one then region[j] and region[j+width]
					// are adjacent to one another - update the RAM
					if (curLabel != bottomLabel) {
						// obtain RAList object from region adjacency free
						// list
							raNode1	= freeRAList;
							raNode2	= freeRAList.next;

						// keep a pointer to the old region adj. free
						// list just in case nodes already exist in respective
						// region lists
							oldRAFreeList = freeRAList;

						// update region adjacency free list
							freeRAList = freeRAList.next.next;

						// populate RAList nodes
							raNode1.label = curLabel;
							raNode2.label = bottomLabel;

						// insert nodes into the RAM
							exists = 0;
							raList[curLabel].Insert(raNode2);
							exists = raList[bottomLabel].Insert(raNode1);

						// if the node already exists then place
						// nodes back onto the region adjacency
						// free list
							if (exists != 0) {
								freeRAList = oldRAFreeList;
							}

					} // end if (curLabel != bottomLabel)

				} // end for (j = 0; j < width - 1; j++)

				// check only to the bottom neighbors of the right boundary
				// pixels...

				// calculate pixel locations (j = width-1)
					curLabel = labels[i*width+j];			// current pixel
					bottomLabel = labels[(i+1)*width+j];	// bottom  pixel

				// check below, if the label of
				// the bottom pixel is not the same as that
				// of the current one then region[j] and region[j+width]
				// are adjacent to one another - update the RAM
					if (curLabel != bottomLabel) {
						// obtain RAList object from region adjacency free
						// list
							raNode1 = freeRAList;
							raNode2	= freeRAList.next;

						// keep a pointer to the old region adj. free
						// list just in case nodes already exist in respective
						// region lists
							oldRAFreeList = freeRAList;

						// update region adjacency free list
							freeRAList = freeRAList.next.next;

						// populate RAList nodes
							raNode1.label = curLabel;
							raNode2.label = bottomLabel;

						// insert nodes into the RAM
							exists = 0;
							raList[curLabel].Insert(raNode2);
							exists = raList[bottomLabel].Insert(raNode1);

						// if the node already exists then place
						// nodes back onto the region adjacency
						// free list
							if (exists != 0) {
								freeRAList = oldRAFreeList;
							}

					} // if (curLabel != bottomLabel)

			} // end for (i = 0; i < height - 1; i++)

		// check only to the right neighbors of the bottom boundary
		// pixels...

		// check the right for pixel locations whose x < width - 1
			for (j = 0; j < width - 1; j++) {
				// calculate pixel labels (i = height-1)
					curLabel = labels[i*width+j];		// current pixel
					rightLabel = labels[i*width+j+1];	// right pixel

				// check to the right, if the label of
				// the right pixel is not the same as that
				// of the current one then region[j] and region[j+1]
				// are adjacent to one another - update the RAM
				if (curLabel != rightLabel) {
					// obtain RAList object from region adjacency free
					// list
						raNode1	= freeRAList;
						raNode2	= freeRAList.next;

					// keep a pointer to the old region adj. free
					// list just in case nodes already exist in respective
					// region lists
						oldRAFreeList = freeRAList;

					// update region adjacency free list
						freeRAList = freeRAList.next.next;

					// populate RAList nodes
						raNode1.label = curLabel;
						raNode2.label = rightLabel;

					// insert nodes into the RAM
						exists = 0;
						raList[curLabel].Insert(raNode2);
						exists = raList[rightLabel].Insert(raNode1);

					// if the node already exists then place
					// nodes back onto the region adjacency
					// free list
						if (exists != 0) {
							freeRAList = oldRAFreeList;
						}

				} // end if (curLabel != rightLabel)

			} // end for (j = 0; j < width - 1; j++)


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.BuildRAM() Exception ---\n");
			e.printStackTrace();
		}


	} // end BuildRAM



	/**
	 * <pre>
	 * Destroy a region adjacency matrix.
	 *
	 * Usage: DestroyRAM()
	 *
	 * Post:
	 *   - the region adjacency matrix has been destroyed: (1) its memory has been
	 *     de-allocated, (2) the RAM structure has been initialize for re-use.
	 */
	private void DestroyRAM() {

		try {

		// initialize region adjacency matrix
			raList = null;
			freeRAList = null;
			raPool = null;


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.DestroyRAM() Exception ---\n");
			e.printStackTrace();
		}

	} // end DestroyRAM



	/**
	 * <pre>
	 * Applies transitive closure to the RAM updating labels, modes and modePointCounts
	 * to reflect the new set of merged regions resulting from transitive closure.
	 *
	 * Usage: TransitiveClosure()
	 *
	 * Post:
	 *   - transitive closure has been applied to the regions classified by the RAM and
	 *     labels, modes and modePointCounts have been updated to reflect the new set of
	 *     merged regions resulting from transitive closure.
	 */
	private void TransitiveClosure() {

		try {

		//Step (1):

		// Build RAM using classifiction structure originally
		// generated by the method GridTable::Connect()
			BuildRAM();

		// Step (1a):
		// Compute weights of weight graph using confidence map
		// (if defined)
			if (weightMapDefined) {
				ComputeEdgeStrengths();
			}

		// Step (2):

		// Treat each region Ri as a disjoint set:

		// - attempt to join Ri and Rj for all i != j that are neighbors and
		//   whose associated modes are a normalized distance of < 0.5 from one
		//   another

		// - the label of each region in the raList is treated as a pointer to the
		//   canonical element of that region (e.g. raList[i], initially has raList[i].label = i,
		//   namely each region is initialized to have itself as its canonical element).

		// Traverse RAM attempting to join raList[i] with its neighbors...
			int i, iCanEl, neighCanEl;
			float threshold;
			RAList neighbor;

			for (i = 0; i < regionCount; i++) {
				// aquire first neighbor in region adjacency list pointed to
				// by raList[i]
					neighbor = raList[i].next;

				// compute edge strenght threshold using global and local
				// epsilon
					if (epsilon > raList[i].edgeStrength) {
						threshold = epsilon;
					} else {
						threshold = raList[i].edgeStrength;
					}

				// traverse region adjacency list of region i, attempting to join
				// it with regions whose mode is a normalized distance < 0.5 from
				// that of region i...
					while (neighbor != null) {
						// attempt to join region and neighbor...
							if ((InWindow(i, neighbor.label)) && (neighbor.edgeStrength < epsilon)) {
								// region i and neighbor belong together so join them
								// by:

								// (1) find the canonical element of region i
									iCanEl = i;
									while (raList[iCanEl].label != iCanEl) {
										iCanEl = raList[iCanEl].label;
									}

								// (2) find the canonical element of neighboring region
									neighCanEl = neighbor.label;
									while (raList[neighCanEl].label != neighCanEl) {
										neighCanEl = raList[neighCanEl].label;
									}

								// if the canonical elements of are not the same then assign
								// the canonical element having the smaller label to be the parent
								// of the other region...
									if (iCanEl < neighCanEl) {
										raList[neighCanEl].label = iCanEl;
									} else {
										// must replace the canonical element of previous
										// parent as well
											raList[raList[iCanEl].label].label = neighCanEl;

										// re-assign canonical element
											raList[iCanEl].label = neighCanEl;
									}
							}

						// check the next neighbor...
							neighbor = neighbor.next;

					} // end while (neighbor)

			} // end for (i = 0; i < regionCount; i++)

		// Step (3):

		// Level binary trees formed by canonical elements
			for (i = 0; i < regionCount; i++) {
				iCanEl	= i;
				while (raList[iCanEl].label != iCanEl) {
					iCanEl	= raList[iCanEl].label;
				}
				raList[i].label	= iCanEl;
			}

		// Step (4):

		//Traverse joint sets, relabeling image.

		// (a)

		// Accumulate modes and re-compute point counts using canonical
		// elements generated by step 2.

		// allocate memory for mode and point count temporary buffers...
			float modes_buffer[] = new float[N*regionCount];
			int	MPC_buffer[] = new int[regionCount];

		// initialize buffers to zero
			for (i = 0; i < regionCount; i++) {
				MPC_buffer[i] = 0;
			}
			for (i = 0; i < N*regionCount; i++) {
				modes_buffer[i]	= 0;
			}

		// traverse raList accumulating modes and point counts
		// using canoncial element information...
			int k, iMPC;
			for (i = 0; i < regionCount; i++) {

				// obtain canonical element of region i
					iCanEl = raList[i].label;

				// obtain mode point count of region i
					iMPC = modePointCounts[i];

				// accumulate modes_buffer[iCanEl]
					for (k = 0; k < N; k++) {
						modes_buffer[(N*iCanEl)+k] += iMPC*modes[(N*i)+k];
					}

				// accumulate MPC_buffer[iCanEl]
					MPC_buffer[iCanEl] += iMPC;

			}

		// (b)

		// Re-label new regions of the image using the canonical
		// element information generated by step (2)

		// Also use this information to compute the modes of the newly
		// defined regions, and to assign new region point counts in
		// a consecute manner to the modePointCounts array

		// allocate memory for label buffer
			int[] label_buffer = new int [regionCount];

		// initialize label buffer to -1
			for (i = 0; i < regionCount; i++) {
				label_buffer[i]	= -1;
			}

		// traverse raList re-labeling the regions
			int	label = -1;
			for (i = 0; i < regionCount; i++) {
				// obtain canonical element of region i
					iCanEl	= raList[i].label;
					if (label_buffer[iCanEl] < 0) {
						// assign a label to the new region indicated by canonical
						// element of i
							label_buffer[iCanEl] = ++label;

						// recompute mode storing the result in modes[label]...
							iMPC = MPC_buffer[iCanEl];
							for (k = 0; k < N; k++) {
								modes[(N*label)+k]	= (modes_buffer[(N*iCanEl)+k])/(iMPC);
							}

						// assign a corresponding mode point count for this region into
						// the mode point counts array using the MPC buffer...
							modePointCounts[label] = MPC_buffer[iCanEl];
					}
			}

		// re-assign region count using label counter
			int	oldRegionCount = regionCount;
			regionCount	= label+1;

		// (c)

		// Use the label buffer to reconstruct the label map, which specified
		// the new image given its new regions calculated above
			for (i = 0; i < height*width; i++) {
				labels[i] = label_buffer[raList[labels[i]].label];
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.TransitiveClosure() Exception ---\n");
			e.printStackTrace();
		}


	} // end TransitiveClosure

	/**
	 * <pre>
	 * Computes the a weight for each link in the region graph maintined by the RAM,
	 * resulting in a weighted graph in which the weights consist of a confidence
	 * between zero and one indicating if the regions are separated by a strong or
	 * weak edge.
	 *
	 * Usage: ComputeEdgeStrengths()
	 *
	 * Post:
	 *   - an edge strength has been computed between each region of the image and
	 *     placed as a weight in the RAM to be used during transitive closure.
	 */
	private void ComputeEdgeStrengths() {

		try {

		// initialize visit table - used to keep track
		// of which pixels have already been visited such
		// as not to contribute their strength value to
		// a boundary sum multiple times...
			Arrays.fill(visitTable, (char)0);

		// traverse labeled image computing edge strengths
		// (excluding image boundary)...
			int x, y, dp, curLabel, rightLabel, bottomLabel;
			RAList curRegion;

			for (y = 1; y < height-1; y++) {
				for (x = 1; x < width-1; x++) {
					// compute data point location using x and y
						dp = y*width + x;

					// obtain labels at different pixel locations
						curLabel = labels[dp];			// current pixel
						rightLabel = labels[dp+1];		// right   pixel
						bottomLabel	= labels[dp+width];	// bottom  pixel

					// check right and bottom neighbor to see if there is a
					// change in label then we are at an edge therefore record
					// the edge strength at this edge accumulating its value
					// in the RAM...
						if (curLabel != rightLabel) {
							// traverse into RAM...
								curRegion = raList[curLabel];

								while ((curRegion != null) && (curRegion.label != rightLabel)) {
									curRegion = curRegion.next;
								}

//-bp-						// this should not occur...
//-bp-							assert (curRegion);

							// accumulate edge strength
								curRegion.edgeStrength += weightMap[dp] + weightMap[dp+1];
								curRegion.edgePixelCount += 2;
						}

						if (curLabel != bottomLabel) {
							// traverse into RAM...
								curRegion = raList[curLabel];
								while ((curRegion != null) && (curRegion.label != bottomLabel)) {
									curRegion = curRegion.next;
								}

//-bp-						// this should not occur...
//-bp-							assert (curRegion);

							// accumulate edge strength
								if (curLabel == rightLabel) {
									curRegion.edgeStrength += weightMap[dp] + weightMap[dp+width];
									curRegion.edgePixelCount += 2;
								} else {
									curRegion.edgeStrength += weightMap[dp+width];
									curRegion.edgePixelCount += 1;
								}

						}
				} // for (x = 1; x < width-1; x++)
			} // for (y = 1; y < height-1; y++)

		// compute strengths using accumulated strengths obtained above...
			RAList neighborRegion;
			float edgeStrength;
			int	edgePixelCount;
			for (x = 0; x < regionCount; x++) {
				// traverse the region list of the current region
					curRegion = raList[x];
					curRegion = curRegion.next;
					while (curRegion != null) {
						// with the assumption that regions having a smaller
						// label in the current region list have already
						// had their edge strengths computed, only compute
						// edge strengths for the regions whose label is greater
						// than x, the current region (region list) under
						// consideration...
							curLabel = curRegion.label;
							if (curLabel > x) {
								// obtain pointer to the element identifying the
								// current region in the neighbors region list...
									neighborRegion = raList[curLabel];
									while ((neighborRegion != null) && (neighborRegion.label != x)) {
										neighborRegion = neighborRegion.next;
									}

//-bp-							// this should not occur...
//-bp-								assert (neighborRegion);

								// compute edge strengths using accumulated confidence
								// value and pixel count
									if ((edgePixelCount = curRegion.edgePixelCount + neighborRegion.edgePixelCount) != 0) {
										// compute edge strength
											edgeStrength = curRegion.edgeStrength + neighborRegion.edgeStrength;
											edgeStrength /= edgePixelCount;

										// store edge strength and pixel count for corresponding regions
											curRegion.edgeStrength = neighborRegion.edgeStrength = edgeStrength;
											curRegion.edgePixelCount = neighborRegion.edgePixelCount = edgePixelCount;
									}
							} // end if (curLabel > x)

						// traverse to the next region in the region adjacency list
						// of the current region x
							curRegion = curRegion.next;

					} // end while (curRegion != 0)

			} // end for (x = 0; x < regionCount; x++)

		// compute average edge strength amongst the edges connecting
		// it to each of its neighbors
			int numNeighbors;
			for (x = 0; x < regionCount; x++) {
				// traverse the region list of the current region
				// accumulating weights
					curRegion = raList[x];
					curRegion = curRegion.next;
					edgeStrength = 0;
					numNeighbors = 0;
					while (curRegion != null) {
						numNeighbors++;
						edgeStrength += curRegion.edgeStrength;
						curRegion = curRegion.next;
					}

				// divide by the number of regions connected
				// to the current region
					if (numNeighbors != 0) {
						edgeStrength /= numNeighbors;
					}

				// store the result in the raList for region
				// x
					raList[x].edgeStrength = edgeStrength;

			} // end for (x = 0; x < regionCount; x++)

		//traverse raList and output the resulting list
		//to a file

//-bp- NO C++ CODE SUPPLIED FOR ABOVE COMMENT???


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.ComputeEdgeStrengths() Exception ---\n");
			e.printStackTrace();
		}


	} // end ComputeEdgeStrengths



	/**
	 * <pre>
	 * Prunes regions from the image whose pixel density is less than a specified
	 * threshold.
	 *
	 * Usage: Prune(minRegion)
	 *
	 * Pre:
	 *   - minRegion is the minimum allowable pixel density a region may have without
	 *     being pruned from the image
	 * Post:
	 *   - regions whose pixel density is less than or equal to minRegion have been
	 *     pruned from the image.
	 *
	 * @param minRegion
	 */
	private void Prune(int minRegion) {

		try {

		// Allocate Memory for temporary buffers...

		// allocate memory for mode and point count temporary buffers...
			float[] modes_buffer = new float[N*regionCount];
			int[] MPC_buffer = new int[regionCount];

		// allocate memory for label buffer
			int[] label_buffer = new int[regionCount];

		// Declare variables
			int	i, k, candidate, iCanEl, neighCanEl, iMPC, label, oldRegionCount, minRegionCount;
			double minSqDistance, neighborDistance;
			RAList neighbor;

		// Apply pruning algorithm to classification structure, removing all regions whose area
		// is under the threshold area minRegion (pixels)
			do {
				// Assume that no region has area under threshold area  of
					minRegionCount	= 0;

				// Step (1):

				// Build RAM using classifiction structure originally
				// generated by the method GridTable::Connect()
					BuildRAM();

				// Step (2):

				// Traverse the RAM joining regions whose area is less than minRegion (pixels)
				// with its respective candidate region.

				// A candidate region is a region that displays the following properties:

				//	- it is adjacent to the region being pruned

				//  - the distance of its mode is a minimum to that of the region being pruned
				//    such that or it is the only adjacent region having an area greater than
				//    minRegion

					for (i = 0; i < regionCount; i++) {
						// if the area of the ith region is less than minRegion
						// join it with its candidate region...

						//*******************************************************************************

						// Note: Adjust this if statement if a more sophisticated pruning criterion
						//       is desired. Basically in this step a region whose area is less than
						//       minRegion is pruned by joining it with its "closest" neighbor (in color).
						//       Therefore, by placing a different criterion for fusing a region the
						//       pruning method may be altered to implement a more sophisticated algorithm.

						//*******************************************************************************

						if (modePointCounts[i] < minRegion) {
							// update minRegionCount to indicate that a region
							// having area less than minRegion was found
								minRegionCount++;

							// obtain a pointer to the first region in the
							// region adjacency list of the ith region...
								neighbor = raList[i].next;

							// calculate the distance between the mode of the ith
							// region and that of the neighboring region...
								candidate = neighbor.label;
								minSqDistance = SqDistance(i, candidate);

							// traverse region adjacency list of region i and select
							// a candidate region
								neighbor = neighbor.next;
								while (neighbor != null) {

									// calculate the square distance between region i
									// and current neighbor...
										neighborDistance = SqDistance(i, neighbor.label);

									// if this neighbors square distance to region i is less
									// than minSqDistance, then select this neighbor as the
									// candidate region for region i
										if (neighborDistance < minSqDistance) {
											minSqDistance = neighborDistance;
											candidate = neighbor.label;
										}

									// traverse region list of region i
										neighbor = neighbor.next;

								} // end while (neighbor != null)

							// join region i with its candidate region:

							// (1) find the canonical element of region i
								iCanEl = i;
								while (raList[iCanEl].label != iCanEl) {
									iCanEl = raList[iCanEl].label;
								}

							// (2) find the canonical element of neighboring region
								neighCanEl = candidate;
								while (raList[neighCanEl].label != neighCanEl) {
									neighCanEl	= raList[neighCanEl].label;
								}

							// if the canonical elements of are not the same then assign
							// the canonical element having the smaller label to be the parent
							// of the other region...
								if (iCanEl < neighCanEl) {
									raList[neighCanEl].label = iCanEl;
								} else {
									// must replace the canonical element of previous
									// parent as well
										raList[raList[iCanEl].label].label = neighCanEl;

									// re-assign canonical element
										raList[iCanEl].label = neighCanEl;
								}
						} // end if (modePointCounts[i] < minRegion)

					} // end for (i = 0; i < regionCount; i++)

				// Step (3):

				// Level binary trees formed by canonical elements
					for (i = 0; i < regionCount; i++) {
						iCanEl	= i;
						while (raList[iCanEl].label != iCanEl) {
							iCanEl	= raList[iCanEl].label;
						}
						raList[i].label	= iCanEl;
					}

				// Step (4):

				// Traverse joint sets, relabeling image.

				// Accumulate modes and re-compute point counts using canonical
				// elements generated by step 2.

				// initialize buffers to zero
					for (i = 0; i < regionCount; i++) {
						MPC_buffer[i] = 0;
					}
					for (i = 0; i < N*regionCount; i++) {
						modes_buffer[i]	= 0;
					}

				// traverse raList accumulating modes and point counts
				// using canoncial element information...
					for (i = 0; i < regionCount; i++) {

						// obtain canonical element of region i
							iCanEl = raList[i].label;

						// obtain mode point count of region i
							iMPC = modePointCounts[i];

						// accumulate modes_buffer[iCanEl]
							for (k = 0; k < N; k++) {
								modes_buffer[(N*iCanEl)+k] += iMPC*modes[(N*i)+k];
							}

						// accumulate MPC_buffer[iCanEl]
							MPC_buffer[iCanEl] += iMPC;

					}

				// (b)

				// Re-label new regions of the image using the canonical
				// element information generated by step (2)

				// Also use this information to compute the modes of the newly
				// defined regions, and to assign new region point counts in
				// a consecute manner to the modePointCounts array

				// initialize label buffer to -1
					for (i = 0; i < regionCount; i++) {
						label_buffer[i]	= -1;
					}

				// traverse raList re-labeling the regions
					label = -1;
					for (i = 0; i < regionCount; i++) {
						// obtain canonical element of region i
							iCanEl = raList[i].label;
							if (label_buffer[iCanEl] < 0) {
								// assign a label to the new region indicated by canonical
								// element of i
									label_buffer[iCanEl] = ++label;

								// recompute mode storing the result in modes[label]...
									iMPC = MPC_buffer[iCanEl];
									for (k = 0; k < N; k++) {
										modes[(N*label)+k] = (modes_buffer[(N*iCanEl)+k])/(iMPC);
									}

								// assign a corresponding mode point count for this region into
								// the mode point counts array using the MPC buffer...
									modePointCounts[label] = MPC_buffer[iCanEl];
							}
					}

				// re-assign region count using label counter
					oldRegionCount = regionCount;
					regionCount = label+1;

				// (c)

				// Use the label buffer to reconstruct the label map, which specified
				// the new image given its new regions calculated above
					for (i = 0; i < height*width; i++) {
						labels[i] = label_buffer[raList[labels[i]].label];
					}

			}	while(minRegionCount > 0);


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.Prune() Exception ---\n");
			e.printStackTrace();
		}


	} // end Prune



	/**
	 * <pre>
	 * Defines the boundaries for each region of the segmented image storing the result
	 * into a region list object.
	 *
	 * Usage: DefineBoundaries()
	 *
	 * Pre:
	 *  - the image has been segmented and a classification structure has been created
	 *    for this image
	 * Post:
	 *  - the boundaries of the segmented image have been defined and the boundaries of
	 *    each region has been stored into a region list object.
	 */
	private void DefineBoundaries() {

		try {

		// DECLARATIONS
			int	i, j, label, dataPoint;

		// declare and allocate memory for boundary map and count
			int[] boundaryMap, boundaryCount;
			boundaryMap = new int [L];
			boundaryCount = new int [regionCount];

		// initialize boundary map and count
			for (i = 0; i < L; i++) {
				boundaryMap[i] = -1;
			}

			for (i = 0; i < regionCount; i++) {
				boundaryCount[i] =  0;
			}

		// initialize and declare total boundary count -
		// the total number of boundary pixels present in
		// the segmented image
			int	totalBoundaryCount = 0;

		// traverse the image checking the right and bottom
		// four connected neighbors of each pixel marking
		// boundary map with the boundaries of each region and
		// incrementing boundaryCount using the label information

		//***********************************************************************
		//***********************************************************************

			// first row (every pixel is a boundary pixel)
				for (i = 0; i < width; i++) {
						boundaryMap[i] = label = labels[i];
						boundaryCount[label]++;
						totalBoundaryCount++;
				}

			// define boundaries for all rows except for the first
			// and last one...
				for (i = 1; i < height - 1; i++) {
					// mark the first pixel in an image row as an image boundary...
						dataPoint = i*width;
						boundaryMap[dataPoint] = label = labels[dataPoint];
						boundaryCount[label]++;
						totalBoundaryCount++;

						for (j = 1; j < width - 1; j++) {
							// define datapoint and its right and bottom
							// four connected neighbors
								dataPoint = i*width+j;

							// check four connected neighbors if they are
							// different this pixel is a boundary pixel
								label = labels[dataPoint];
								if ((label != labels[dataPoint-1]) || (label != labels[dataPoint+1]) ||
								   (label != labels[dataPoint-width]) || (label != labels[dataPoint+width])) {
									boundaryMap[dataPoint] = label = labels[dataPoint];
									boundaryCount[label]++;
									totalBoundaryCount++;
								}
						}

					// mark the last pixel in an image row as an image boundary...
						dataPoint = (i+1)*width-1;
						boundaryMap[dataPoint] = label = labels[dataPoint];
						boundaryCount[label]++;
						totalBoundaryCount++;

				}

			// last row (every pixel is a boundary pixel) (i = height-1)
				int	start = (height-1)*width, stop = height*width;
				for (i = start; i < stop; i++) {
					boundaryMap[i] = label = labels[i];
					boundaryCount[label]++;
					totalBoundaryCount++;
				}

		//***********************************************************************
		//***********************************************************************

		//store boundary locations into a boundary buffer using
		//boundary map and count

		//***********************************************************************
		//***********************************************************************

			int[] boundaryBuffer = new int[totalBoundaryCount], boundaryIndex = new int [regionCount];

			// use boundary count to initialize boundary index...
				int counter = 0;
				for (i = 0; i < regionCount; i++) {
					boundaryIndex[i] = counter;
					counter += boundaryCount[i];
				}

			// traverse boundary map placing the boundary pixel
			// locations into the boundaryBuffer
				for (i = 0; i < L; i++) {
					// if its a boundary pixel store it into
					// the boundary buffer
						if ((label = boundaryMap[i]) >= 0) {
							boundaryBuffer[boundaryIndex[label]] = i;
							boundaryIndex[label]++;
						}
				}

		//***********************************************************************
		//***********************************************************************

		//store the boundary locations stored by boundaryBuffer into
		//the region list for each region

		//***********************************************************************
		//***********************************************************************

			// create a new region list
				regionList = new RegionList(regionCount, totalBoundaryCount, N);

			// add boundary locations for each region using the boundary
			// buffer and boundary counts
				counter	= 0;
				for (i = 0; i < regionCount; i++) {
					regionList.AddRegion(i, boundaryCount[i], boundaryBuffer, counter);
					counter += boundaryCount[i];
				}

		//***********************************************************************
		//***********************************************************************


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.DefineBoundaries() Exception ---\n");
			e.printStackTrace();
		}


	} // end DefineBoundaries



	/**
	 * <pre>
	 * Image Data Searching/Distance Calculation
	 * Returns true if the two specified data points are within rR of each other.
	 *
	 * Usage: InWindow(mode1, mode2)
	 *
	 * Pre:
	 *   - mode1 and mode2 are indeces into msRawData specifying the modes of the
	 *     pixels having these indeces.
	 * Post:
	 *   - true is returned if mode1 and mode2 are within rR of one another, false
	 *     is returned otherwise.
	 *
	 * @return boolean indicating success (true) or failure (false)
	 */
	private boolean InWindow(int mode1, int mode2) {

		int	k = 1, s = 0, p;
		double diff	= 0, el;

		try {

			while ((diff < 0.25) && (k != kp)) { // Partial Distortion Search
				// Calculate distance squared of sub-space s
					diff = 0;
					for (p = 0; p < P[k]; p++) {
						el = (modes[mode1*N+p+s]-modes[mode2*N+p+s])/(h[k]*offset[k]);
						if ((p == 0) && (k == 1) && (modes[mode1*N] > 80)) {
							diff += 4*el*el;
						} else {
							diff += el*el;
						}
					}

				// next subspace
					s += P[k];
					k++;
			}

		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.InWindow() Exception ---\n");
			e.printStackTrace();
		}

		return (diff < 0.25);

	} // end InWindow



	/**
	 * <pre>
	 * Computs the normalized square distance between two modes.
	 *
	 * Usage: SqDistance(mode1, mode2)
	 *
	 * Pre:
	 *   - mode1 and mode2 are indeces into the modes array specifying two modes
	 *     of the image
	 * Post:
	 *   - the normalized square distance between modes indexed by mode1 and mode2
	 *     has been calculated and the result has been returned.
	 *
	 * @param mode1
	 * @param mode2
	 *
	 * @return float representing the normalized square distance between modes
	 */
	private float SqDistance(int mode1, int mode2) {

		int	k = 1, s = 0, p;
		float dist = 0, el;

		try {

			for (k = 1; k < kp; k++) {

				// Calculate distance squared of sub-space s
					for (p = 0; p < P[k]; p++) {
						el = (modes[mode1*N+p+s]-modes[mode2*N+p+s])/(h[k]*offset[k]);
						dist += el*el;
					}

				// next subspace
					s += P[k];
					k++;
			}


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.SqDistance Exception ---\n");
			e.printStackTrace();
		}

		// return normalized square distance between modes
		// 1 and 2
			return dist;

	} // end SqDistance



	/**
	 * <pre>
	 * Allocates memory needed by the mean shift image processor class output storage
	 * data structure.
	 *
	 * Usage: InitailizeOutput()
	 *
	 * Post:
	 *   - the memory needed by the output storage structure of this class has
	 *     been (re-)allocated.
	 */
	private void InitializeOutput()	{

		try {

		// De-allocate memory if output was defined for previous image
			DestroyOutput();

		// Allocate memory for msRawData (filtered image output)
			msRawData = new float [L*N];

		// Allocate memory used to store image modes and their corresponding regions...
			modes = new float [L*(N+2)];
			labels = new int [L];
			modePointCounts = new int [L];
			indexTable	= new int [L];

		// Allocate memory for integer modes used to perform connected components
		// (image labeling)...
			LUV_data = new int [N*L];

		// indicate that the class output storage structure has been defined
			classState.OUTPUT_DEFINED	= true;


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.InitializeOutput() Exception ---\n");
			e.printStackTrace();
		}


	} // end InitializeOutput



	/**
	 * <pre>
	 * De-allocates memory needed by the mean shift image processor class output
	 * storage data structure.
	 *
	 * Usage: DestroyOutput()
	 *
	 * Post:
	 *      - the memory needed by the output storage structure of this class has
	 *        been de-allocated.
	 *      - the output storage structure has been initialized for re-use.
	 */
	private void DestroyOutput() {

		try {

		// initialize data members for re-use...

			// initialize output structures...
				msRawData = null;

			//re-initialize classification structure
				modes = null;
				labels = null;
				modePointCounts = null;
				regionCount = 0;

		// indicate that the output has been destroyed
			classState.OUTPUT_DEFINED	= false;


		} catch (Exception e) {
			System.out.println("\n--- MSImageProcessor.DestroyOutput() Exception ---\n");
			e.printStackTrace();
		}

	} // end DestroyOutput

} // end MSImageProcessor class



/*
 * $Log$
 * Revision 1.10  2002/09/20 19:49:00  bpangburn
 * Fixed various JavaDoc error messages.
 *
 * Revision 1.9  2002/09/20 19:15:15  bpangburn
 * Added BSD-style license, cleaned up JavaDoc, and moved CVS log to end of each source file.
 *
 * Revision 1.8  2002/08/21 18:31:13  jayo
 * Updated error handlers to reflect class and method names.
 *
 * Revision 1.7  2002/08/20 21:49:15  bpangburn
 * Added crude error handlers to each method.
 *
 * Revision 1.6  2002/07/09 19:42:07  jayo
 * Editing comments to coincide with Javadoc
 *
 * Revision 1.4  2002/07/03 19:57:14  jayo
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