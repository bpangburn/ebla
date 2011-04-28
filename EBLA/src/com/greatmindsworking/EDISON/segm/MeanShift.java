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



import java.util.*;



/**
 * MeanShift.java
 *
 * The mean shift library is a collection of routines that use the mean shift
 * algorithm. Using this algorithm, the necessary output will be generated needed
 * to analyze a given input set of data.<p>
 *
 * The mean shift library of routines is realized via the creation of a MeanShift
 * base class. This class provides a mechanism for calculating the mean shift
 * vector at a specified data point, using an arbitrary N-dimensional data set,
 * and a user-defined kernel.<p>
 *
 * For image processing the mean shift base class also allows for the definition of
 * a data set that is on a two-dimensional lattice. The amount of time needed to
 * compute the mean shift vector using such a data set is much less than that of an
 * arbitrary one. Because images usually contain many data points, defining the
 * image input data points as being on a lattice greatly improves computation time
 * and makes algorithms such as image filtering practical.<p>
 * <pre>
 * To-do:
 *  1. FINISH AddWeightFunction (need to work with GFunction.java to create a
 *	   way for users to supply a custom function to the routine (can't pass
 *	   functions as method parameters in Java))
 *  2. Clean up JavaDoc
 *  3. Remove original C++ based error handlers if possible.
 *
 * @author	$Author$
 * @version	$Revision$
 */
public class MeanShift {

// PRIVATE CONSTANTS



// INTERNAL PRIVATE CLASSES THAT REPLACE ORIGINAL C++ DATA STRUCTURES
	/**
	 *	k-Dimensional Binary Search Tree
	 */
	private class Tree {
		float[] x;
		Tree  right;
		Tree  left;
		Tree  parent;
	};

	/**
	 * User Defined Weight Function
	 */
	private class UserWeightFunc {
		double[] w;
		double halfWindow;
		int sampleNumber;
		@SuppressWarnings("unused")
		int subspace;
	};

	/**
	 * Define class state structure
	 */
	protected class ClassStateStruct {
		boolean	KERNEL_DEFINED;
		boolean	INPUT_DEFINED;
		boolean	LATTICE_DEFINED;
		boolean	OUTPUT_DEFINED;
	};



// PUBLIC DATA MEMBERS
	/**
	 * Determine whether to use port based on 04-25-2002 (0) or 04-14-2003 (1) EDISON
	 */
	public int edisonPortVersion = 0;

	/**
	 * errorMessage is an error message that is set by a mean shift library class
	 * when an error occurs.
	 */
	public String errorMessage;

	/**
	 * errorStatus indicates if an error has occured as a result of improper use of a
	 * mean shift library class method or because of insufficient resourses.
	 * errorStatus is set to EL_ERROR (errorStatus = 1) if an error has occured. If no
	 * error occured when calling a particular method errorStatus is set to
	 * EL_OKAY (errorStatus = 0).
	 */
	public ErrorLevel errorStatus;



// PROTECTED CONSTANTS
	/**
	 * Thresholds
	 * define threshold (approx. Value of Mh at a peak or plateau) define threshold
	 * required that window is near convergence cluster search windows near
	 * convergence that are a distance h[i]*TC_DIST_FACTOR of one another (transitive
	 * closure) (TC_DIST_FACTOR)^2
	 */
	protected static final double EPSILON = 0.01;
	protected static final double MU = 0.05;
	protected static final double TC_DIST_FACTOR = 0.5;
	protected static final double SQ_TC_DFACTOR = 0.0625;

	/**
	 * define max # of iterations to find mode
	 */
	protected static final int LIMIT = 100;

	/**
	 * Gaussian Lookup Table take 16 samples of exp(-u/2)
	 */
	protected static final int GAUSS_NUM_ELS = 16;

	/**
	 * GAUSS_LIMIT = c
	 */
	protected static final double GAUSS_LIMIT = 2.9;

	/**
	 * GAUSS_INCREMENT = (c^2)/(# of samples)
	 */
	protected static final double GAUSS_INCREMENT
		= GAUSS_LIMIT*GAUSS_LIMIT/GAUSS_NUM_ELS;

	/**
	 * Numerical Analysis used for floating point to integer conversion
	 */
	protected static final double DELTA = 0.00001;



// PROTECTED DATA MEMBERS
	/**
	 * Mean shift system used for function timing and system output
	 */
	protected MSSystem msSys;

	/**
	 * Input data parameters - length, dimension, subspace number, and subspace dimensions
	 */
	protected int L, N, kp, P[];

	/**
	 * Linear Storage (used by lattice and bst)
	 *
	 * memory allocated for data points stored by Tree nodes when used by the
	 * lattice data structure data does not store the lattice information
	 *
	 * format of data: data = [x11, x12,  , x1N,  , xL1, xL2,  , xLN] in the case of
	 * the lattice the i in data(i,j) corresponds
	 */
	protected float data[];

	/**
	 * Height and width of lattice
	 */
	protected int height, width;

	/**
	 * bandwidth vector for kernel
	 */
	protected float h[];

	/**
	 * defines bandwidth offset caused by the use of a Gaussian kernel (for example)
	 */
	protected float offset[];

	/**
	 * Assigns a marking to each data point specifying whether or not it has been
	 * assigned a mode. These labels are: modeTable[i] = 0 - data point i is not
	 * associated with a mode modeTable[i] = 1 - data point i is associated with a
	 * mode modeTable[i] = 2 - data point i is associated with a mode however its
	 * mode is yet to be determined
	 */
	protected char modeTable[];

	/**
	 * a list of data points that due to basin of attraction will converge to the
	 * same mode as the mode that mean shift is currently being applied to
	 */
	protected int	pointList[];

	/**
	 * the number of points stored by the point list
	 */
	protected int pointCount;

	/**
	 * weight map that may be used to weight the kernel upon performing mean shift on
	 * a lattice
	 */
	protected float weightMap[];

	/**
	 * used to indicate if a lattice weight map has been defined
	 */
	protected boolean weightMapDefined;

	/**
	 * specifies the state of the class (i.e if data has been loaded into
	 * the class, if a kernel has been defined, etc.)
	 */
	protected ClassStateStruct classState;



// PRIVATE DATA MEMBERS
	/**
	 * kernel types for each subspace S[i]
	 */
	private KernelType kernel[];

	/**
	 * weight function lookup table
	 */
	private double w[][];

	/**
	 * increment used by weight hashing function
	 */
	private double increment[];

	/**
	 * flag used to indicate if the kernel is uniform or not
	 */
	private boolean	uniformKernel;

	/**
	 * implementing user defined weight function with Java Map(key/value pairs)
	 */
	private TreeMap<Object, Object> userWFMap;

	/**
	 * root of kdBST used to store input
	 */
	private Tree root;

	/**
	 * memory allocated for Tree nodes
	 */
	private Tree forest[];

	/**
	 * range vector used to perform range search on kd Tree, indexed by dimension
	 * of input - format: range = {Lower_Limit_1, Upper_Limit_1, ...,
	 * Lower_Limit_N, Upper_Limit_N}
	 */
	private float range[];

	/**
	 * stores normalized distance vector between yk and xi
	 */
	private double uv[];

	/**
	 * sum of weights calculated at data points within the sphere
	 */
	private double wsum;

	/**
	 * Upper and lower bounds for lattice search window in the x dimension
	 */
	private int	LowerBoundX, UpperBoundX;

	/**
	 * Upper and lower bounds for lattice search window in the y dimension
	 */
	private int	LowerBoundY, UpperBoundY;



// PUBLIC METHODS
	/**
	 * Class Constructor
	 */
	public MeanShift() {

		try {

		// intialize input data set parameters...
			P = null;
			L = 0;
			N = 0;
			kp = 0;

		// initialize input data set storage structures...
			data = null;

		// initialize input data set kd-Tree
			root = null;
			forest = null;
			range = null;

		// intialize lattice structure...
			height = 0;
			width = 0;

		// intialize kernel strucuture...
			h = null;
			kernel = null;
			w = null;
			offset = null;
			increment = null;
			uniformKernel = false;

		// initialize weight function linked list...
		// initialize user defined weight function as Java Map
			userWFMap = new TreeMap<Object, Object>();

		// intialize mean shift processing data structures...
			uv = null;

		// set lattice weight map to null
			weightMap = null;

		// indicate that the lattice weight map is undefined
			weightMapDefined= false;

		// allocate memory for error message buffer...
			errorMessage = "";

		// initialize error status to OKAY
			errorStatus	= ErrorLevel.EL_OKAY;

		// Initialize class state...
			classState = new ClassStateStruct();
			classState.INPUT_DEFINED = false;
			classState.KERNEL_DEFINED = false;
			classState.LATTICE_DEFINED = false;
			classState.OUTPUT_DEFINED = false;

		} catch (Exception e) {
			System.out.println("\n--- MeanShift Constructor Exception ---\n");
			e.printStackTrace();
		}

	} // end MeanShift constructor



	/**
	 * <pre>
	 * Creation/Initialization of Mean Shift Kernel
	 *
	 * Uploads a custom kernel into the private data members of the mean shift class.
	 * This kernel is used by the mean shift class to perform mean shift.
	 *
	 * Usage: DefineKernel(kernel_, h_, P_, kp_)
	 *
	 * Pre:
	 *  - kernel is an array of KernelTypes specifying the type of kernel to be
	 *    used on each subspace of the input data set x
	 *
	 *  - h is the set of bandwidths used to define the search window
	 *
	 *  - P is a one dimensional array of integers of size kp, that specifies the
	 *    dimension of each subspace of the input data set x
	 *
	 *  - kp is the total number of subspaces used to the input data set x
	 *
	 * Post:
	 *  - the custom kernel has been created for used by the mean shift procedure.
	 *
	 * @param kernel_	A one dimensional array of type KernelType used to specify
	 *					the kernel type (Uniform, Gaussian, or User Defined) of a
	 *  				given subspace of the input set.  Entry i of kernel correlates
	 * 					to the i-th subspace of the input data set.
	 * @param h_		A one dimensional array of floating point numbers that are used
	 * 					to normalize the input dataset, each bandwidth specifying the
	 * 					relative importance of a subspace of the input data set.
	 * @param P_		use P to calculate the dimension (N_) of the potential input data set x
	 * @param kp_		An integer that specifies the number of subcontained by the input
	 *					data set. Both P and h therefore consist of kp entries.
	 */
	public void DefineKernel(KernelType[] kernel_, float[] h_, int[] P_, int kp_) {

		try {

		// Declare variables
			int i, kN;

		// if a kernel has already been created then destroy it
			if (kp!=0) {
				DestroyKernel();
			}

		// Obtain kp...
			if((kp = kp_) <= 0)	{
				ErrorHandler("MeanShift", "CreateKernel", "Subspace count (kp) is zero or negative.");
				return;
			}

		// Dimension arrays for h, P, kernel, offset, and increment
			P = new int [kp];
			h = new float [kp];
			kernel = new KernelType [kp];
			offset = new float [kp];
			increment = new double [kp];

		// Populate h, P and kernel, also use P to calculate
		// the dimension (N_) of the potential input data set x
			kN = 0;

			for (i = 0; i < kp; i++) {
				if((h[i] = h_[i]) <= 0) {
					ErrorHandler("MeanShift", "CreateKernel", "Negative or zero valued bandwidths are prohibited.");
					return;
				}
				if((P[i] = P_[i]) <= 0) {
					ErrorHandler("MeanShift", "CreateKernel", "Negative or zero valued subspace dimensions are prohibited.");
					return;
				}
				kernel[i] = kernel_[i];
				kN += P[i];
			}

		// Dimension arrays for range vector and uv using N_
			range = new float [2*kN];
			uv = new double [kN];

		// Generate weight function lookup table
		// using above information and user
		// defined weight function list
			generateLookupTable();

		// check for errors
			if (errorStatus == ErrorLevel.EL_ERROR) {
				return;
			}

		// indicate that the kernel has been defined
			classState.KERNEL_DEFINED = true;

		} catch (Exception e) {
			System.out.println("\n--- MeanShift.DefineKernel() Exception ---\n");
			e.printStackTrace();
		}

	} // end DefineKernel



	/**
	 * <pre>
	 * Each subspace specified as User Defined is unquely defined by a correlating
	 * weight function which is user defined.
	 *
	 * A weight function w(u) exhibits the following
	 * Properties:
	 *
	 * (1) w(u) = w(-u)
	 * (2) u = ((x_i-y_k)^2)/(h^2) (see docs)
	 * (3) w(u) = 0, for |u| >= halfWindow
	 *
	 * Weight functions are accounted for every time a new kernel is created.
	 *
	 * If a weight function is added to non-existing subspace of the input data set
	 * (example: the input data set containes 3 subspaces and this method is given
	 * subspace = 4) then the weight defintion will simply be ignored by the mean
	 * shift class.
	 *
	 * If a subspace is declared as kernel type User Defined and a weight function
	 * is not defined for that subspace a fatal error will occur.
	 *
	 * Usage: AddWeightFunction(g(u), halfWindow, sampleNumber, subspace);
	 *
	 *
	 * Pre:
	 *   - g(u) is the normalized weight function with respect to u = (norm(x-xi))^2/h^2
	 *
	 *   - sampleNumber is the number of samples to be taken of g(u) over halfWindow
	 *     interval
	 *
	 *   - halfWindow is the radius of g(u) such that g(u) is defined for 0 <= u <= halfWindow
	 *
	 *   - subspace is the subspace number for which g(u) is to be applied during the
	 *     mean shift procedure.
	 * Post:
	 *   - g(u) has been added to the Mean Shift class private data structure to be
	 *     used by the mean shift procedure.
	 *
	 *   - if a weight function has already been specified for the specified subspace,
	 *     the weight function for this subspace has been replaced.
	 *
	 * @param gf			an array of the weight function w(u) exhibiting the above properties
	 * @param halfWindow	a floating point number specifying where w(u) exists (is non zero)
	 * 						[See Property 3 Above]
	 * @param sampleNumber	an integer used to specify the number of samples used to describe
	 * 						w(u). Linear interpolation isused during the mean shift calculation
	 *						using the samples of w(u) to determine the value of w at a location
	 *						|u| < halfWindow
	 * @param subspace		an integer specifying which kernel w(u) defines
	 */
	// MeanShift::AddWeightFunction(double g(double), float halfWindow, int sampleNumber, int subspace)
	public void AddWeightFunction(GFunction gf, float halfWindow, int sampleNumber, int subspace) {

		try{

	// re-implemented using Java Map...

		// Declare Variables
			int i;
			double increment;


		// create new weight function
			UserWeightFunc userWF = new UserWeightFunc();

		// Generate lookup table
			increment = halfWindow/(double)(sampleNumber);

// NEED TO FINISH THIS SECTIONS (see original code below) !!!
			userWF.w = new double [sampleNumber+1];
			for (i = 0; i <= sampleNumber; i++) {
				userWF.w[i] = gf.g(i*increment);
			}
// END PROBLEM SECTION

		// Set weight function parameters
			userWF.halfWindow = halfWindow;
			userWF.sampleNumber = sampleNumber;
			userWF.subspace = subspace;

		// Add newly defined function to userWeightMap (using put(key, value))
			userWFMap.put(subspace, userWF);

/*
		// Declare Variables
			int i;
			double increment;

		// Search to see if a weight function has already been
		// defined for specified subspace, if not then insert
		// into the head of the weight function list, otherwise
		// replace entry

		// Perform Search
		cur = head;
		while((cur)&&(cur->subspace != subspace))
			cur = cur->next;

		// Entry Exists - Replace It!
		// Otherwise insert at the head of the the weight functon list
		if(cur)
			delete cur->w;
		else
		{
			cur       = new UserWeightFunct;
			cur->next = head;
			head      = cur;
		}

		// Generate lookup table
		increment = halfWindow/(double)(sampleNumber);

		cur->w = new double [sampleNumber+1];
		for(i = 0; i <= sampleNumber; i++)
			cur->w[i] = g((double)(i*increment));

		// Set weight function parameters
		cur->halfWindow   = halfWindow;
		cur->sampleNumber = sampleNumber;
		cur->subspace     = subspace;
*/
		} catch (Exception e) {
			System.out.println("\n--- MeanShift.AddWeightFunction() Exception ---\n");
			e.printStackTrace();
		}

	} // end AddWeightFunction



	/**
	 * <pre>
	 * Removes all user defined weight functions added using method
	 * AddWeightFunction() from theprivate data members of the mean shift class.
	 *
	 * Clear Weight Functions
	 *
	 * Clears user defined weight from the Mean Shift class private data structure.                              *
	 *
	 * Post:
	 *   - all user defined weight functions ahve been cleared from the private data
	 *     structure of the mean shift class.
	 */
	public void ClearWeightFunctions() {

		try {

		// empty Java Map
			userWFMap.clear();

		} catch (Exception e) {
			System.out.println("\n--- MeanShift.ClearWeightFunction() Exception ---\n");
			e.printStackTrace();
		}

	} // end ClearWeightFunctions



	/**
	 * <pre>
	 * Uploads a one dimensional array containing N-dimensional data points into
	 * the mean shift class.
	 *
	 * Usage: DefineInput(x, L, N)
	 *
	 * Pre:
	 *  - x is a one dimensional array of L N-dimenional data points.
	 *
	 * Post:
	 *  - x has been uploaded into the mean shift class.
	 *
	 *  - the height and width of a previous data set has been undefined.
	 *
	 * @param x		a floating point array
	 * @param L_	the number of data points stored by x
	 * @param N_	the dimension of the data points stored by x
	 */
	public void DefineInput(float[] x, int L_, int N_) {

		try {

		// if input data is defined de-allocate memory, and
		// re-initialize the input data structure
			if ((classState.INPUT_DEFINED)||(classState.LATTICE_DEFINED)) {
				ResetInput();
			}

		// make sure x is not NULL...
			if (x == null) {
				ErrorHandler("MeanShift", "UploadInput", "Input data set is null.");
				return;
			}

		// Obtain L and N
			if (((L = L_) <= 0)||((N = N_) <= 0)) {
				ErrorHandler("MeanShift", "UploadInput", "Input data set has negative or zero length or dimension.");
				return;
			}

		// Dimension array for data
			data = new float [L*N];

		// Allocate memory for input data set, and copy
		// x into the private data members of the mean
		// shift class
			InitializeInput(x);

		// check for errors
			if (errorStatus == ErrorLevel.EL_ERROR) {
				return;
			}

		// Load x into the MeanShift object using
		// using a kd-Tree, resulting in better
		// range searching of the input data points
		// x - also upload window centers into
		// msRawData
			CreateBST();

		// indicate that the input has been recently defined
			classState.INPUT_DEFINED	= true;
			classState.LATTICE_DEFINED	= false;
			classState.OUTPUT_DEFINED	= false;


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.DefineInput() Exception ---\n");
			e.printStackTrace();
		}

	} // end DefineInput



	/**
	 * <pre>
	 * Use this method to specify define an input data set defined on a lattice.
	 *
	 * Usage: DefineLInput(x, height, width, N)
	 *
	 * Define Lattice Input defines the height and width of the input lattice.
	 *
	 * Pre:
	 * - ht is the height of the lattice
	 *
	 * - wt is the width of the lattice
	 *
	 * Post:
	 *   - the height and width of the lattice has been specified.
	 *
	 *   - if a data set is presently loaded into the mean shift class, an error is
	 *     flagged if the number of elements in that data set does not
	 *     equal the product ht*wt.
	 *
	 * @param x 	a floating point array containing height*width, N-dimensional data points
	 * @param ht 	an integer specifying the height of the lattice
	 * @param wt	an integer specifying the width of the lattice
	 * @param N_	the dimension of the data points stored by x
	 */
	public void DefineLInput(float[] x, int ht, int wt, int N_) {

		try {

		// if input data is defined de-allocate memory, and
		// re-initialize the input data structure
			if ((classState.INPUT_DEFINED)||(classState.LATTICE_DEFINED)) {
				ResetInput();
			}

		// Obtain lattice height and width
			if (((height = ht) <= 0)||((width = wt) <= 0)) {
				ErrorHandler("MeanShift", "DefineLInput", "Lattice defined using zero or negative height and/or width.");
				return;
			}

		// Obtain input data dimension
			if ((N = N_) <= 0) {
				ErrorHandler("MeanShift", "DefineInput", "Input defined using zero or negative dimension.");
				return;
			}

		// compute the data length, L, of input data set
		// using height and width
			L = height*width;

		// Allocate memory for input data set, and copy
		// x into the private data members of the mean
		// shift class
			InitializeInput(x);

		// check for errors
			if (errorStatus == ErrorLevel.EL_ERROR) {
				return;
			}

		// Dimension array for weight map
			weightMap = new float[L];

		// initialize weightMap to an array of zeros
			Arrays.fill(weightMap, (float)0.0);

		// Indicate that a lattice input has recently been
		// defined
			classState.LATTICE_DEFINED = true;
			classState.INPUT_DEFINED = false;
			classState.OUTPUT_DEFINED = false;

		} catch (Exception e) {
			System.out.println("\n--- MeanShift.DefineLInput() Exception ---\n");
			e.printStackTrace();
		}

	} // end DefineLInput



	/**
	 * <pre>
	 * Uploads weight map specifying for each data point a value used to weight the
	 * uniform kernel when computing mean shift.
	 *
	 * Note: DefineLInput must be called prior to calling this method. DefineLInput
	 *       is used to define the dimensions of the input dataset.
	 *
	 * The weight map is used to weight the uniform kernel used to computing meanshift
	 * on a data point situated on a lattice. Alternatively, a weight function may
	 * defined, however, if speed is an issue, the lattice may be exploited to result
	 * in a faster implementation of a weighted kernel.
	 *
	 * Usage: SetLatticeWeightMap(weightMap)
	 *
	 * Pre:
	 * - wm is a floating point array of size L specifying for each data point a weight
	 *   value
	 *
	 * Post:
	 *   - wm has been used to populate the lattice weight map.
	 *
	 * @param wm	A floating point array of size L specifying for each data point a weight.
	 */
	public void SetLatticeWeightMap(float[] wm) {

		try {

		// make sure wm is not NULL
			if (wm == null) {
				ErrorHandler("MeanShift", "SetWeightMap", "Specified weight map is null.");
				return;
			}

		// populate weightMap using wm
		// System.arraycopy(source, sourceStart, target, targetStart, size);
			System.arraycopy(wm, 0, weightMap, 0, L);

		// indicate that a lattice weight map has been specified
			weightMapDefined = true;


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.SetLatticeWeightMap() Exception ---\n");
			e.printStackTrace();
		}


	} // end SetLatticeWeightMap



	/**
	 * <pre>
	 * Removes lattice weight map. An error is NOT flagged if a weight map was not
	 * defined prior to calling this method.
	 *
	 * Usage: RemoveLatticeWeightMap(weightMap)
	 *
	 * Post:
	 *  - the lattice weight map has been removed.
	 *
	 *  - if a weight map did not exist NO error is flagged.
	 */
	public void RemoveLatticeWeightMap() {

		try {

		// only remove weight map if it exists, otherwise
		// do nothing...
			if (weightMapDefined) {
				// set values of lattice weight map to zero
					Arrays.fill(weightMap, (float)0.0);

				// indicate that a lattice weight map is no longer
				// defined
					weightMapDefined = false;
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.RemoveLatticeWeightMap() Exception ---\n");
			e.printStackTrace();
		}


	} // end RemoveLatticeWeightMap



	/**
	 * <pre>
	 * If a kernel is created and input is uploaded,this method calcualtes the mean
	 * shift vector,Mh, at specific data point yk.
	 *
	 * Usage: msVector(Mh, yk)
	 *
	 * Pre:
	 * - a kernel has been created
	 *
	 * - a data set has been uploaded
	 *
	 * - Mh is an N dimensional mean shift vector
	 *
	 * - yk is an N dimensional data point
	 *
	 * Post:
	 * - the mean shift vector at yk has been calculated and stored in and returned
	 *   by Mh.
	 *
	 * @param Mh	an array of N doubles storing the N dimensional mean shift vector
	 * @param yk	an array of N doubles storing the N dimensional data point where
	 * 				the mean shift vector is to be calculate
	 */
	public void msVector(double[] Mh, double[] yk) {

		try {

		// make sure that Mh and/or yk are not NULL...
			if ((Mh==null) || (yk==null)) {
				ErrorHandler("MeanShift", "msVector", "Invalid argument(s) passed to this method.");
				return;
			}

		// make sure that a kernel has been created, data has
		// been uploaded, and that they are consistent with one
		// another...
			classConsistencyCheck(N, false);

		// calculate mean shift vector at yk using created kernel
		// and uploaded data set
			MSVector(Mh, yk);

		} catch (Exception e) {
			System.out.println("\n--- MeanShift.msVector() Exception ---\n");
			e.printStackTrace();
		}

	} // end msVector



	/**
	 * <pre>
	 * If a kernel is created and input is uploaded, this method calcualtes the mean
	 * shift vector,Mh, at specific data point yk, assuming that the data set exhists
	 * on a height x width two dimensional lattice.
	 *
	 * The height and width of the lattice must be specified using DefineLattice()
	 * method.If this is not performed prior to calling this method a fatal error will
	 * be flagged.
	 *
	 * Usage: latticeMSVector(Mh, yk)
	 *
	 * Pre:
	 *   - a kernel has been created
	 *
	 *   - a data set has been uploaded
	 *
	 *   - the height and width of the lattice has been specified using method
	 *	   DefineLattice()
	 *
	 *   - Mh is an N dimensional mean shift vector
	 *
	 *   - yk is an N dimensional data point
	 *
	 * Post:
	 *   - the mean shift vector at yk has been calculated and stored in and
	 *      returned by Mh.
	 *
	 *   - Mh was calculated using the defined input lattice.
	 *
	 * @param Mh	an array of N doubles storing the N dimensional mean shift vector
	 * @param yk	an array of N doubles storing the N dimensional data point where
	 * 				the mean shift vector is to be calculate
	 */
	public void latticeMSVector(double[] Mh, double[] yk) {

		try {

		// make sure that Mh and/or yk are not NULL...
			if ((Mh==null) || (yk==null)) {
				ErrorHandler("MeanShift", "lmsVector", "Invalid argument(s) passed to this method.");
				return;
			}

		// make sure that a kernel has been created, data has
		// been uploaded, and that they are consistent with one
		// another...
			classConsistencyCheck(N+2, true);

		// calculate mean shift vector at yk using created kernel
		// and uploaded data set
			LatticeMSVector(Mh, yk);


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.latticeMSVector() Exception ---\n");
			e.printStackTrace();
		}

	} // end latticeMSVector



	/**
	 * <pre>
	 * If a kernel is created and input is uploaded, this method calcualtes the mode
	 * of a specified data point yk.
	 *
	 * Usage: FindMode(mode, yk)
	 *
	 * Pre:
	 *  - a kernel has been created
	 *
	 *  - a data set has been uploaded
	 *
	 *  - mode is the N dimensional mode of the N-dimensional data point yk
	 *
	 * Post:
	 *  - the mode of yk has been calculated and stored in mode.
	 *
	 * @param mode	an array of N doubles storing the N dimensional mode of yk
	 * @param yk	an array of N doubles storing the N dimensional data point
	 * 				where the mean shift vector is to be calculate
	 */
	public void FindMode(double[] mode, double[] yk) {

		try {

		// DECLARATIONS
			int i;

		// make sure that mode and/or yk are not NULL...
			if ((mode==null) || (yk==null)) {
				ErrorHandler("MeanShift", "FindMode", "Invalid argument(s) passed to this method.");
				return;
			}

		// make sure that a kernel has been created, data has
		// been uploaded, and that they are consistent with one
		// another...
			classConsistencyCheck(N, false);

		// allocate memory for Mh
			double[] Mh = new double [N];

		// copy yk into mode
		// System.arraycopy(source, sourceStart, target, targetStart, size);
			System.arraycopy(yk, 0, mode, 0, N);

		// calculate mean shift vector at yk
			MSVector(Mh, yk);

		// calculate mvAbs = |Mh|^2
			double mvAbs = 0;
			for (i = 0; i < N; i++) {
				mvAbs += Mh[i]*Mh[i];
			}

		// shift mode until convergence (mvAbs = 0)...
			int iterationCount = 1;
			while ((mvAbs >= EPSILON)&&(iterationCount < LIMIT)) {
				// shift mode...
					for (i = 0; i < N; i++) {
						mode[i]	+= Mh[i];
					}

				// re-calculate mean shift vector at new
				// window location have center defined by
				// mode
					MSVector(Mh, mode);

				// calculate mvAbs = |Mh|^2
					mvAbs = 0;
					for (i = 0; i < N; i++) {
						mvAbs += Mh[i]*Mh[i];
					}

				// increment interation count...
					iterationCount++;

			} // end while

		// shift mode...
			for (i = 0; i < N; i++) {
				mode[i]	+= Mh[i];
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.FindMode() Exception ---\n");
			e.printStackTrace();
		}

	} // end FindMode



	/**
	 * <pre>
	 * If a kernel is created and input is uploaded, this method calcualtes the mode
	 * of a specified data point yk, assuming that the data set exhists on a height x
	 * width two dimensional lattice.
	 *
	 * The height and width of the lattice must be specified using DefineLattice()
	 * method. If this is not performed prior to calling this method a fatal error
	 * will be flagged.
	 *
	 * Usage: FindLMode(mode, yk)
	 *
	 * Pre:
	 *    - a kernel has been created
	 *
	 *    - a data set has been uploaded
	 *
	 *    - the height and width of the lattice has been specified using method
	 *		DefineLattice()
	 *
	 *    - mode is the N dimensional mode of the N-dimensional data point yk
	 * Post:
	 *    - the mode of yk has been calculated and stored in mode.
	 *
	 *    - mode was calculated using the defined input lattice.
	 *
	 * @param mode	an array of N doubles storing the N dimensional mode of yk
	 * @param yk	an array of N doubles storing the N dimensional data point
	 * 				where the mean shift vector is to be calculate
	 */
	public void FindLMode(double[] mode, double[] yk) {

		try {

		// DECLARATIONS
			int i;

		// make sure that mode and/or yk are not NULL...
			if ((mode==null) || (yk==null)) {
				ErrorHandler("MeanShift", "FindLMode", "Invalid argument(s) passed to this method.");
				return;
			}

		//make sure the lattice height and width have been defined...
			if (height==0) {
				ErrorHandler("MeanShift", "FindLMode", "Lattice height and width is undefined.");
				return;
			}

		// make sure that a kernel has been created, data has
		// been uploaded, and that they are consistent with one
		// another...
			classConsistencyCheck(N+2, true);

		// define gridN
			int gridN = N+2;

		// allocate memory for Mh
			double[] Mh = new double [gridN];

		// copy yk into mode
		// System.arraycopy(source, sourceStart, target, targetStart, size);
			System.arraycopy(yk, 0, mode, 0, gridN);

		// calculate mean shift vector at yk
			LatticeMSVector(Mh, mode);

		//	calculate mvAbs = |Mh|^2
			double mvAbs = 0;
			for (i = 0; i < gridN; i++) {
				mvAbs	+= Mh[i]*Mh[i];
			}

		// shift mode until convergence (mvAbs = 0)...
			int iterationCount = 1;
			while ((mvAbs >= EPSILON)&&(iterationCount < LIMIT)) {

				// shift mode...
					for (i = 0; i < gridN; i++) {
						mode[i]	+= Mh[i];
					}

				// re-calculate mean shift vector at new
				// window location have center defined by
				// mode
					LatticeMSVector(Mh, mode);

				// calculate mvAbs = |Mh|^2
					mvAbs = 0;
					for (i = 0; i < gridN; i++) {
						mvAbs	+= Mh[i]*Mh[i];
					}

				// increment interation count...
					iterationCount++;

			} // end while

		// shift mode...
			for (i = 0; i < gridN; i++) {
				mode[i]	+= Mh[i];
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.FindLMode() Exception ---\n");
			e.printStackTrace();
		}

	} // end FindLMode



// PROTECTED METHODS
	/**
	 * <pre>
	 * Computes the mean shift vector at a window location yk using input data set
	 * x using a custom, user defined kernel.
	 *
	 * Usage: MSVector(Mh_ptr, yk_ptr)
	 *
	 * Pre:
	 *  - input data has been uploaded into the private data members of the MeanShift
	 *    class
	 *
	 *  - a window center yk has been defined
	 *
	 *  - uniformKernel indicates the which type of kernel to be used by this
	 * 	  procedure: uniform or general
	 *
	 * Post:
	 *  - the mean shift vector calculated at yk using a either a custom, user defined
	 * 	  kernel or a uniform kernel is returned
	 *
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	protected void MSVector(double[] Mh_ptr, double[] yk_ptr) {

		try {

		// Declare Variables
			int i,j;

		// Initialize mean shift vector
			Arrays.fill(Mh_ptr, 0, N, 0);

		// Initialize wsum to zero, the sum of the weights of each
		// data point found to lie within the search window (sphere)
			wsum = 0;

		// Build Range Vector using h[i] and yk

			int s = 0;

			// The flag uniformKernel is used to determine which
			// kernel function is to be used in the calculation
			// of the mean shift vector
			if (uniformKernel) {

				for (i = 0; i < kp; i++) {

					for(j = 0; j < P[i]; j++) {
						range[2*(s+j)  ] = (float)(yk_ptr[s+j] - h[i]);
						range[2*(s+j)+1] = (float)(yk_ptr[s+j] + h[i]);
					}

					s += P[i];
				}
			} else {

				for(i = 0; i < kp; i++) {

					for(j = 0; j < P[i]; j++) {
						range[2*(s+j)  ] = (float)(yk_ptr[s+j] - h[i]*(float)(Math.sqrt(offset[i])));
						range[2*(s+j)+1] = (float)(yk_ptr[s+j] + h[i]*(float)(Math.sqrt(offset[i])));
					}

					s += P[i];
				}
			}

			// Traverse through the data set x, performing the
			// weighted sum of each point xi that lies within
			// the search window (sphere) using a general,
			// user defined kernel or uniform kernel depending
			// on the uniformKernel flag
				if (uniformKernel) {
					uniformSearch(root, 0, Mh_ptr, yk_ptr);
				} else {
					generalSearch(root, 0, Mh_ptr, yk_ptr);
				}

				// Calculate the mean shift vector using Mh and wsum
					for(i = 0; i < N; i++) {

						// Divide Sum by wsum
							Mh_ptr[i] /= wsum;

						// Calculate mean shift vector: Mh(yk) = y(k+1) - y(k)
							Mh_ptr[i] -= yk_ptr[i];

					}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.MSVector() Exception ---\n");
			e.printStackTrace();
		}

	} // end MSVector



	/**
	 * <pre>
	 * Computes the mean shift vector at a specfied window
	 * yk using the lattice data structure.
	 *
	 * Usage: LatticeMSVector(Mh_ptr, yk_ptr)
	 *
	 * Pre:
	 * 		- Mh_ptr and yh_ptr are arrays of doubles conaining N+2 elements
	 *
	 * 		- Mh_ptr is the mean shift vector calculated at window center yk_ptr
	 *
	 * Post:
	 * 		- the mean shift vector at the window center pointed to by yk_ptr has
	 *   	  been calculated and stored in the memory location pointed to by Mh_ptr
	 *
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	protected void LatticeMSVector(double[] Mh_ptr, double[] yk_ptr) {

		try {

		// DECLARATIONS
			int i;

		// Initialize mean shift vector
			Arrays.fill(Mh_ptr, 0, N+2, 0);

		// Initialize wsum
			wsum = 0;

		// Perform lattice search summing
		// all the points that lie within the search
		// window defined using the kernel specified
		// by uniformKernel
			if(uniformKernel) {
				uniformLSearch(Mh_ptr, yk_ptr);
			} else {
				generalLSearch(Mh_ptr, yk_ptr);
			}

		// Compute mean shift vector using sum computed
		// by lattice search, wsum, and yk_ptr:
		// Mh = Mh/wsum - yk_ptr
			if (wsum > 0) {
				for (i = 0; i < N+2; i++) {
					Mh_ptr[i] = Mh_ptr[i]/wsum - yk_ptr[i];
				}
			} else {
				for (i = 0; i < N+2; i++) {
					Mh_ptr[i] = 0;
				}
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.LatticeMSVector() Exception ---\n");
			e.printStackTrace();
		}

	} // end LatticeMSVector



	/**
	 * <pre>
	 * Computes the mean shift vector at a specfied window yk using the lattice data
	 * structure. Also the points that lie within the window are stored into the basin
	 * of attraction structure used by the optimized mean shift algorithms.
	 *
	 * Usage: OptLatticeMSVector(Mh_ptr, yk_ptr)
	 *
	 * Pre:
	 *  - Mh_ptr and yh_ptr are arrays of doubles conaining N+2 elements
	 *
	 *  - Mh_ptr is the mean shift vector calculated at window center yk_ptr
	 *
	 * Post:
	 *  - the mean shift vector at the window center pointed to by yk_ptr has been
	 *    calculated and stored in the memory location pointed to by Mh_ptr
	 *
	 *  - the data points lying within h of of yk_ptr have been stored into the basin
	 *    of attraction data structure.
	 *
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	protected void OptLatticeMSVector(double[] Mh_ptr, double[] yk_ptr) {

		try {

		// DECLARATIONS
			int i;

		// Initialize mean shift vector
			Arrays.fill(Mh_ptr, 0, N+2, 0);

		// Initialize wsum
			wsum = 0;

		// Perform lattice search summing
		// all the points that lie within the search
		// window defined using the kernel specified
		// by uniformKernel
			if (uniformKernel) {
				optUniformLSearch(Mh_ptr, yk_ptr);
			} else {
				optGeneralLSearch(Mh_ptr, yk_ptr);
			}

		// Compute mean shift vector using sum computed
		// by lattice search, wsum, and yk_ptr:
		// Mh = Mh/wsum - yk_ptr
			if (edisonPortVersion==0) {
				for (i = 0; i < N+2; i++) {
					Mh_ptr[i] = Mh_ptr[i]/wsum - yk_ptr[i];
				}
			} else {
				if (wsum > 0) {
					for(i = 0; i < N+2; i++) {
						Mh_ptr[i] = Mh_ptr[i]/wsum - yk_ptr[i];
					}
				} else {
					for (i=0; i< N+2; i++) {
						Mh_ptr[i] = 0;
					}
				}
			}

		} catch (Exception e) {
			System.out.println("\n--- MeanShift.OptLatticeMSVector() Exception ---\n");
			e.printStackTrace();
		}

	} // end OptLatticeMSVector



	/**
	 * <pre>
	 * Checks the state of the class prior to the application of mean shift.
	 *
	 * Usage: classConsistencyCheck(iN, usingLattice)
	 *
	 * Pre:
	 *  - iN is the specified dimension of the input, iN = N for a general
	 *    input data set, iN = N + 2 for a input set defined using a lattice
	 *
	 * Post:
	 *  - if the kernel has not been created, an input has not been defined and/or
	 *    the specifiedinput dimension (iN) does not match that of the kernel a
	 *    fatal error is flagged.
	 *
	 * @param iN
	 * @param usingLattice
	 */
	protected void classConsistencyCheck(int iN, boolean usingLattice) {

		try {

		// make sure that kernel has been created...
			if(classState.KERNEL_DEFINED == false) {
				ErrorHandler("MeanShift", "classConsistencyCheck", "Kernel not created.");
				return;
			}

		// make sure input data set has been loaded into mean shift object...
			if ((classState.INPUT_DEFINED == false) && (!usingLattice)) {
				ErrorHandler("MeanShift", "classConsistencyCheck", "No input data specified.");
				return;
			}

		// make sure that the lattice is defined if it is being used
			if((classState.LATTICE_DEFINED == false) && (usingLattice)) {
				ErrorHandler("MeanShift", "classConsistencyCheck", "Latice not created.");
				return;
			}

		// make sure that dimension of the kernel and the input data set
		// agree

			// calculate dimension of kernel (kN)
				int i, kN	= 0;
				for(i = 0; i < kp; i++) {
					kN	+= P[i];
				}

			// perform comparison...
				if(iN != kN) {
					ErrorHandler("MeanShift", "classConsitencyCheck", "Kernel dimension does not match defined input data dimension.");
					return;
				}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.classConsistencyCheck() Exception ---\n");
			e.printStackTrace();
		}

	} // end classConsistencyCheck



	/**
	 * <pre>
	 * Class error handler.
	 *
	 * Usage: ErrorHandler(className, methodName, errmsg)
	 *
	 * Pre:
	 *   - className is the name of the class that fl-
	 *     agged an error
	 *   - methodName is the name of the method that
	 *     flagged an error
	 *   - errmsg is the error message given by the
	 *     calling function
	 *
	 * Post:
	 *   - the error message errmsg is flagged on beh-
	 *     ave of method methodName belonging to class
	 *     className:
	 *
	 *      (1) errorMessage has been updated with the
	 *          appropriate error message using the arg-
	 *          ments passed to this method.
	 *      (2) errorStatus is set to ERROR
	 *          (errorStatus = 1)
	 *
	 * @param className
	 * @param methodName
	 * @param errmsg
	 */
	protected void ErrorHandler(String className, String methodName, String errmsg) {

		try {

		// build error message
			errorMessage = className + "::" + methodName + " Error: " + errmsg;

		// set error status to ERROR
			errorStatus = ErrorLevel.EL_ERROR;


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.ErrorHandler() Exception ---\n");
			e.printStackTrace();
		}

	} // end ErrorHandler



// PRIVATE METHODS
	/**
	 * <pre>
	 * A weight function look up table is generated.
	 *
	 * Usage: generateLookupTable()
	 *
	 * Pre:
	 *   - kernel is an array of KernelTypes specifying the type of kernel to be
	 *   used on each subspace of the input data set x the input data set x
	 *
	 *   - kp is the total number of subspaces used to
	 *
	 *   - the above information has been pre-loaded into the MeanShift class
	 *     private members
	 *
	 * Post:
	 *   - a lookup table is generated for the weight function of the resulting
	 *     kernel
	 *
	 *   - uniformKernel is set to true if the kernel to be used is uniform,
	 *     false is returned otherwise
	 *
	 *   - if a user defined weight function is requred for a given subspace
	 *     but not defined in the user defined weight function list, an error
	 *     is flagged and the program is halted
	 */
	private void generateLookupTable() {

		try {

		// Declare Variables
			int i,j;

		// Allocate memory for lookup table w
			w = new double[kp][];

		// Traverse through kernel generating weight function
		// lookup table w

		// Assume kernel is uniform
			uniformKernel = true;

			for(i = 0; i < kp; i++) {
				if (kernel[i] == KernelType.UNIFORM) {
				// *Uniform Kernel* has weight funciton w(u) = 1
				// therefore, a weight funciton lookup table is
				// not needed for this kernel --> w[i] = NULL indicates
				// this

					w        [i] = null;  // weight function not needed for this kernel
					offset   [i] =    1;  // uniform kernel has u < 1.0
					increment[i] =    1;  // has no meaning

				} else if (kernel[i] == KernelType.GAUSSIAN) {
				// *Gaussian Kernel* has weight function w(u) = constant*exp(-u^2/[2h[i]^2])

					// Set uniformKernel to false
						uniformKernel = false;

					// generate weight function using expression,
					// exp(-u/2), where u = norm(xi - x)^2/h^2

					// Allocate memory for weight table
						w[i] = new double [GAUSS_NUM_ELS+1];

						for (j = 0; j <= GAUSS_NUM_ELS; j++) {
							w[i][j] = Math.exp(-j*GAUSS_INCREMENT/2);
						}

					// Set offset = offset^2, and set increment
						offset   [i] = (float)(GAUSS_LIMIT*GAUSS_LIMIT);
						increment[i] = GAUSS_INCREMENT;

				} else if (kernel[i] == KernelType.USER_DEFINED) {
				// *User Define Kernel* uses the weight function wf(u)

					// Set uniformKernel to false
						uniformKernel = false;

					// RETRIEVE USER-DEFINED WEIGHT FUNCTION FOR SUBSPACE i+1
						Integer tmpInt = new Integer(i+1);
						if (userWFMap.containsKey((Object)tmpInt)) {
						// retrieve weight function
							UserWeightFunc uWF = (UserWeightFunc)userWFMap.get((Object)tmpInt);

						// Otherwise, copy weight function lookup table to w[i]
							w[i] = new double [uWF.sampleNumber+1];
							System.arraycopy(uWF.w, 0, w[i], 0, uWF.sampleNumber);

						// Set offset and increment accordingly
							offset   [i] = (float)(uWF.halfWindow);
							increment[i] = uWF.halfWindow/(float)uWF.sampleNumber;

						} else {
						// error message
							System.out.println("generateLookupTable Fatal Error: User defined kernel for subspace " + i+1 + " is undefined.\nAborting Program.\n\n");
							System.exit(1);
						}

				} else {

					ErrorHandler("MeanShift", "generateLookupTable", "Unknown kernel type.");

				} // end switch

			} // end for


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.generateLookupTable() Exception ---\n");
			e.printStackTrace();
		}

	} // end generateLookupTable



	/**
	 * <pre>
	 * Destroys and initializes kernel.
	 *
	 * Usage; DestroyKernel()
	 *
	 * Post:
	 *   - memory for the kernel private data members have been destroyed and the
	 *	   kernel has been initialized for re-use.
	 */
	private void DestroyKernel() {

		try {

		// intialize kernel for re-use...
			kp		= 0;
			kernel	= null;
			h		= null;
			P		= null;
			range	= null;

			uv = null;
			increment = null;
			offset = null;

			w = null;


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.DestroyKernel() Exception ---\n");
			e.printStackTrace();
		}

	} // end DestroyKernel



	/**
	 * <pre>
	 * Uploads input data set x into a kd-BST.
	 *
	 * Usage: CreateBST()
	 *
	 * Pre:
	 *    - x is a one dimensional array of L N-dimensional data points
	 *
	 * Post:
	 *    - x has been uploaded into a balanced kd-BSTdata structure for use by
	 *      the mean shift procedure
	 */
	private void CreateBST() {

		try {

		// Create BST using data....

		// Allocate memory for Tree
			forest = new Tree[L];

// MAY WANT TO CHANGE THIS LATER (i.e. MAKE DATA AN ARRAY OF Float SO THAT REFERENCE VARIABLE CAN BE USED)...
// IN C++ CODE, data WAS A PROTECTED DATA MEMBER AND THE KD-BST JUST POINTED
// TO THE VARIOUS MEMORY ADDRESSES OF THE FLOATS IN DATA
// FOR THIS JAVA CODE, WE ARE MODIFYING THE KD-BST FOREST TO CONTAIN A COPY OF
// THE FLOATS IN data
		// Populate 'forest' of Tree's with
		// the values stored in x
			int i;
			for(i = 0; i < L; i++) {
				forest[i].x = new float[N];

				System.arraycopy(data, (i*N), forest[i].x, 0, N);

				forest[i].right  = null;
				forest[i].left   = null;
				forest[i].parent = null;
			}

		// Build balanced Nd-Tree from the
		// forest of Trees generated above
		// retaining the root of this Tree
			root = BuildKDTree(forest, 0, L, 0, null);


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.CreateBST() Exception ---\n");
			e.printStackTrace();
		}

	} // end CreateBST



	/**
	 * <pre>
	 * Allocates memory for and initializes the input data structure.
	 *
	 * Usage: InializeInput(x)
	 *
	 * Pre:
	 *    - x is a floating point array of L, N dimensional input data points
	 *
	 * Post:
	 *    - memory has been allocated for the input data structure and x has been
	 *      stored using into the mean shift class using the resulting structure.
	 * @param x
	 */
	private void InitializeInput(float[] x) {

		try {

		// allocate memory for input data set
			data = new float [L*N];

		// copy x into data
			System.arraycopy(x, 0, data, 0, L*N);


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.InitializeInput() Exception ---\n");
			e.printStackTrace();
		}

	} // end InitializeInput



	/**
	 * <pre>
	 * De-allocates memory for and re-intializes input data structure.
	 *
	 * Usage: ResetInput()
	 *
	 * Post:
	 *   - the memory of the input data structure has been de-allocated and this
	 *     strucuture has been initialized for re-use.
	 */
	private void ResetInput() {

		try {

		// initialize input data structure for re-use
			data	= null;
			forest	= null;
			root	= null;
			L		= 0;
			N		= 0;
			width	= 0;
			height	= 0;

		// re-set class input to indicate that
		// an input is not longer stored by
		// the private data members of this class
			classState.INPUT_DEFINED = false;
			classState.LATTICE_DEFINED = false;


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.ResetInput() Exception ---\n");
			e.printStackTrace();
		}

	} // end ResetInput



	/**
	 * <pre>
	 * Builds a KD Tree given a forest of Tree's.
	 *
	 * Usage: BuildKDTree(subset, base,length, d, parent)
	 *
	 * Pre:
	 *    - subset is a subset of L un-ordered Tree nodes each containing an
	 *      N-dimensional data point
	 *
	 *    - d is the depth of the subset, used to specify the dimension used to
	 *      construct the Tree at the given depth
	 *
	 *    - parent is the parent Tree of subset
	 *
	 * Post:
	 *    - a balanced KD Tree has been constructed using the forest subset, the root
	 *      of this Tree has been returned
	 *
	 * @param subset
	 * @param base
	 * @param length
	 * @param d
	 * @param parent
	 *
	 * @return Tree
	 */
	private Tree BuildKDTree(Tree[] subset, int base, int length, int d, Tree parent) {

		// If the subset is a single Tree
		// then return this Tree otherwise
		// partition the subset and place
		// these subsets recursively into
		// the left and right sub-Trees having
		// their root specified by the median
		// of this subset in dimension d
			if (length == 1) {

				subset[base].parent = parent;

				return subset[base];

			} else if (length > 1) {

				// Sort Subset
					QuickMedian(subset, base, length-1, d);

				// Get Median of Subset and Partition
				// it into two sub-Trees - create
				// a Tree with its root being the median
				// of the subset and its left and right
				// children being the medians of the subsets
					int median = base + (length/2);
					subset[median].parent = parent;
					subset[median].left   = BuildKDTree(subset, base, (median-base), (d+1)%N, subset[median]);
					subset[median].right  = BuildKDTree(subset, median, (length-median-1), (d+1)%N, subset[median]);

				// Output Tree structure
					return subset[median];

			} else {
				return null;
			}

	} // end BuildKDTree



	/**
	 * <pre>
	 * Replaces native C "SWAP" for swapping array contents.
	 *
	 * @param a1 first array
	 * @param a2 second array
	 */
	private void SWAP(float[] a1, float[] a2) {

		try {

		int length = a1.length;
		float[] tmp = new float[length];

		// copy a1 into tmp
			System.arraycopy(a1, 0, tmp, 0, length);

		// copy a2 into a1
			System.arraycopy(a2, 0, a1, 0, length);

		// copy tmp into a2
			System.arraycopy(tmp, 0, a2, 0, length);


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.SWAP() Exception ---\n");
			e.printStackTrace();
		}

	} // end SWAP



	/**
	 * <pre>
	 * Finds the median element in an un-ordered set, restructuring the set such that
	 * points less than the median point are located to the left of the median
	 * and points greater than the median point are located to the right.
	 *
	 * Usage: QuickMedian(arr,left, right, d)
	 *
	 * Pre:
	 *   - arr is a subset of Tree nodes whose leftmost element is specified by left
	 *     and rightmost element is specified by left
	 *
	 *   - d is the dimension of the data set stored by the Tree structure that is
	 *      used to find the median
	 *
	 * Post:
	 *   - the median point is found and the subset of Trees is re-ordered such that
	 *     all Trees whose data points with d dimensional value less than that of
	 *     the median Tree node are located to the left of the median Tree node,
	 *     otherwise they are located to the right
	 *
	 * @param arr
	 * @param left
	 * @param right
	 * @param d
	 */
	private void QuickMedian(Tree[] arr, int left, int right, int d) {

		try {

		int k, n;
		float[] a = new float[N];
		n = right-left+1;
		k = n/2 + 1;
		int i, ir, j, l, mid;


		l = 1;
		ir = n;
		for (;;) {

			if (ir <= l+1) {

				if (ir == l+1 && arr[ir-1].x[d] < arr[l-1].x[d]) {
					SWAP(arr[l-1].x, arr[ir-1].x);
				}
				return;
			} else {
				mid = (l+ir) >> 1;
				SWAP(arr[mid-1].x, arr[l+1-1].x);

					if (arr[l-1].x[d] > arr[ir-1].x[d]) {
						SWAP(arr[l-1].x, arr[ir-1].x);
					}
					if (arr[l+1-1].x[d] > arr[ir-1].x[d]) {
						SWAP(arr[l+1-1].x, arr[ir-1].x);
					}
					if (arr[l-1].x[d] > arr[l+1-1].x[d]) {
						SWAP(arr[l-1].x, arr[l+1-1].x);
					}

					i = l+1;
					j = ir;
					System.arraycopy(arr[l+1-1].x, 0, a, 0, N);

					for (;;) {
						do i++; while (arr[i-1].x[d] < a[d]);
						do j--; while (arr[j-1].x[d] > a[d]);
						if (j<i) break;
						SWAP(arr[i-1].x, arr[j-1].x);
					}

					System.arraycopy(arr[j-1].x, 0, arr[l+1-1].x, 0, N);
					System.arraycopy(a, 0, arr[j-1].x, 0, N);

					if (j>=k) ir = j-1;
					if (j<=k) l = i;
			}
		}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.QuickMedian() Exception ---\n");
			e.printStackTrace();
		}

	} // end QuickMedian



	/**
	 * <pre>
	 * Searches the input data using a kd-Tree, performs the sum on the data within
	 * the Hypercube defined by the Tree using a uniform kernel.
	 *
	 * Usage: uniformSearch(gt, gd,Mh_ptr, yk_ptr)
	 *
	 * Pre:
	 *   - gt is a possibly NULL pointer to a kd Tree
	 *
	 *   - Mh_ptr is a pointer to the mean shift vector being calculated
	 *
	 *   - yk_ptr is a pointer to the current window center location
	 *
	 *   - gd is the depth of the current subTree
	 *
	 * Post:
	 *   - the mean of the points within the Hypercube of the kd Tree is computed
	 *     using a uniform kernel
	 *
	 * @param gt
	 * @param gd
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	private void uniformSearch(Tree gt, int gd, double[] Mh_ptr, double[] yk_ptr) {

		try {

		Tree c_t;
		int c_d;
		int i;
		int actionType;

		c_t = gt;
		c_d = gd;
		actionType = 0;

		double el, diff;
		int k, j, s;

		while (c_t != null) {
			switch(actionType) {
			case 0: // forward

				if ((c_t.x[c_d] > range[2*c_d]) && ((c_t.left) != null)) {
					c_t = c_t.left;
					c_d = (c_d+1)%N;
				} else {
					actionType = 1;
				}
				break;
			case 1: // backleft

				for(i = 0; i < N; i++) {
					if((c_t.x[i] < range[2*i])||(c_t.x[i] > range[2*i+1]))
						break;
				}

				if (i == N) {

					// ***     Visit Tree     ***

					// Check if xi is in the window centered about yk_ptr
					// If so - use it to compute y(k+1)
						diff = 0;
						j = 0;
						s = 0;
						while ((diff < 1.0)&&(j < kp)) { // Partial Distortion Search (PDS)
							// test each sub-dimension independently
								diff  = 0;
								for (k = 0; k < P[j]; k++) {
									el = (c_t.x[s+k] - yk_ptr[s+k])/h[j];
									diff += el*el;
								}

								s += P[j];                        // next subspace
								j++;
						}

					if (diff < 1.0) {
						wsum += 1;
						for (j = 0; j < N; j++)
							Mh_ptr[j] += c_t.x[j];
					}

				}

				if ((c_t.x[c_d] < range[2*c_d+1]) && ((c_t.right) != null)) {
					c_t = c_t.right;
					c_d = (c_d+1)%N;
					actionType = 0;
				} else {
					actionType = 2;
				}
				break;
			case 2: // backright
				c_d = (c_d+N-1)%N;

				if (c_t.parent == null) {
					c_t = null;
					break;
				}

				if (c_t.parent.left == c_t) {
					actionType = 1;
				} else {
					actionType = 2;
				}

				c_t = c_t.parent;
				break;
			}
		}


		} catch (Exception e) {
			System.out.println("\n--- MeanShiftuniformSearch() Exception ---\n");
			e.printStackTrace();
		}

	} // end uniformSearch



	/**
	 * <pre>
	 * Searches the input data using a kd Tree, performs the
	 * sum on the data within the Hypercube defined by the
	 * Tree using a general kernel.
	 *
	 * Usage: gerenalSearch(gt, gd,Mh_ptr, yk_ptr)
	 *
	 *  Pre:
	 *      - gt is a possibly null pointer to a kd Tree
	 *
	 *      - Mh_ptr is a pointer to the mean shift vector
	 *        being calculated
	 *
	 *      - yk_ptr is a pointer to the current window
	 *        center location
	 *
	 *      - gd is the depth of the current subTree
	 *  Post:
	 *      - the mean of the points within the Hypercube
	 *        of the kd Tree is computed using a general
	 *        kernel
	 *
	 * @param gt
	 * @param gd
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	private void generalSearch(Tree gt, int gd, double[] Mh_ptr, double[] yk_ptr) {

		try {

		Tree c_t;
		int c_d;
		int i;
		int actionType;

		c_t = gt;
		c_d = gd;
		actionType = 0;

		double el, diff, u, tw, y0, y1;
		int k, j, s, x0, x1;

		diff = 0;

		while (c_t != null) {
			switch(actionType) {
			case 0: // forward
				if ((c_t.x[c_d] > range[2*c_d]) && ((c_t.left) != null)) {
					c_t = c_t.left;
					c_d = (c_d+1)%N;
				} else {
					actionType = 1;
				}

				break;
			case 1: // backleft

				for (i = 0; i < N; i++) {
					if ((c_t.x[i] < range[2*i])||(c_t.x[i] > range[2*i+1])) {
						break;
					}
				}

				if(i == N) {

					// ***      Visit Tree      ***

					// Check if xi is in the window centered about yk_ptr
					// If so - use it to compute y(k+1)
					s = 0;
					for (j = 0; j < kp; j++) {

						// test each sub-dimension independently
							diff  = 0;
							for(k = 0; k < P[j]; k++) {
								el = (c_t.x[s+k] - yk_ptr[s+k])/h[j];
								diff += uv[s+k] = el*el;      // Update uv and diff
								if(diff >= offset[j]) {         // Partial Distortion Search (PDS)
									break;
								}
							}

							if (diff >= offset[j]) {            // PDS
								break;
							}

							s += P[j];                        // next subspace

					}

					// j == kp indicates that all subspaces passed the test:
					// the data point is within the search window
					if (j == kp) j--;
					if (diff < offset[j]) {

						// Initialize total weight to 1
							tw = 1;

						// Calculate weight factor using weight function
						// lookup tables and uv
							s = 0;

							for (j = 0; j < kp; j++) {
								if (kernel[j] != KernelType.UNIFORM) { // not uniform kernel
									// Compute u[i]
										u = 0;
										for (k = 0; k < P[j]; k++) {
											u += uv[s+k];
										}

									// Accumulate tw using calculated u
									// and weight function lookup table

									// Linear interpolate values given by
									// lookup table

									// Calculate x0 and x1, the points surounding
									// u
										x0 = (int)(u/increment[j]);
										x1 = x0+1;

									// Get y0 and y1 from the lookup table
										y0 = w[j][x0];
										y1 = w[j][x1];

									// Accumulate tw using linear interpolation
										tw *= (((x1)*increment[j] - u)*y0+(u - (x0)*increment[j])*y1)
											/(x1*increment[j] - x0*increment[j]);

								}

								s += P[j];                               // next subspace
							}

						// Perform weighted sum using xi
							for(j = 0; j < N; j++) {
								Mh_ptr[j] += tw*c_t.x[j];
							}

						// Increment wsum by tw
							wsum += tw;

					}
				}

				if ((c_t.x[c_d] < range[2*c_d+1]) && ((c_t.right) != null)) {
					c_t = c_t.right;
					c_d = (c_d+1)%N;
					actionType = 0;
				} else {
					actionType = 2;
				}
				break;

			case 2: // backright
				c_d = (c_d+N-1)%N;

				if (c_t.parent == null) {
					c_t = null;
					break;
				}

				if (c_t.parent.left == c_t) {
					actionType = 1;
				} else {
					actionType = 2;
				}

				c_t = c_t.parent;
				break;
		  }
	   }


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.generalSearch() Exception ---\n");
			e.printStackTrace();
		}

	} // end generalSearch



	/**
	 * <pre>
	 * Performs search on data set for all points lying within the search window
	 * defined using a uniform kernel. Their point-wise sum and count
	 * is computed and returned.
	 *
	 * Usage: uniformLSearch(Mh_ptr, yk_ptr)
	 *
	 * NOTE: This method is the only method in the MeanShift class that uses the
	 *       weight map asside from optUniformLSearch.
	 *
	 * Pre:
	 *    - Mh_ptr is a length N array of doubles
	 *
	 *    - yk_ptr is a length N array of doubles
	 *
	 *    - Mh_ptr is the sum of the data points found within search window having
	 *      center yk_ptr
	 * Post:
	 *    - a search on the data set using the lattice has been performed, and all
	 *      points found to lie within the search window defined using a uniform
	 *		kernel are summed and counted.
	 *
	 *    - their point wise sum is pointed to by Mh_ptr and their count is stored
	 *      by wsum.
	 *
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	private void uniformLSearch(double[] Mh_ptr, double[] yk_ptr) {

		try {

		// Declare variables
			int	i, j, k;
			int				s, p, dataPoint, lN;
			double			diff, el, dx, dy, tx, weight;

		// Define lattice data dimension...
			lN	= N + 2;

		// Define bounds of lattice...

		// the lattice is a 2dimensional subspace whose
		// search window bandwidth is specified by
		// h[0]:
			tx = yk_ptr[0] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundX = 0;
			else
				LowerBoundX = (int) tx;
			tx = yk_ptr[1] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundY = 0;
			else
				LowerBoundY = (int) tx;
			tx = yk_ptr[0] + h[0] - DELTA;
			if (tx >= width)
				UpperBoundX = width-1;
			else
				UpperBoundX = (int) tx;
			tx = yk_ptr[1] + h[0] - DELTA;
			if (tx >= height)
				UpperBoundY = height - 1;
			else
				UpperBoundY = (int) tx;

		// Perform search using lattice
			for (i = LowerBoundY; i <= UpperBoundY; i++) {
				for(j = LowerBoundX; j <= UpperBoundX; j++) {
					// get index into data array
						dataPoint = N*(i*width+j);

					//Determine if inside search window
						k		= 1;
						s		= 0;
						dx      = j - yk_ptr[0];
						dy      = i - yk_ptr[1];
						diff	= (dx*dx+dy*dy)/(h[0]*h[0]);
						while((diff < 1.0) && (k != kp)) { // Partial Distortion Search
							// Calculate distance squared of sub-space s
								diff = 0;
								for(p = 0; p < P[k]; p++) {
									el    = (data[dataPoint+p+s]-yk_ptr[p+s+2])/h[k];

									// in c, false==0
									// if((!p)&&(yk_ptr[2] > 80)) {
									if((p == 0) && (yk_ptr[2] > 80)) {
										diff += 4*el*el;
									} else {
										diff += el*el;
									}
								}

							// next subspace
								s += P[k];
								k++;
						}

					// if its inside search window perform sum and count
						if (diff < 1.0) {
							weight = 1 - weightMap[i*width+j];
							Mh_ptr[0] += weight*j;
							Mh_ptr[1] += weight*i;
							for (k = 2; k < lN; k++) {
								Mh_ptr[k] += weight*data[dataPoint+k-2];
							}
							wsum += weight;
						}

					//done.

				}
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.uniformLSearch() Exception ---\n");
			e.printStackTrace();
		}

	} // end uniformLSearch



	/**
	 * <pre>
	 * Performs search on data set for all points lying within the search window
	 * defined using a uniform kernel. Their point-wise sum and count is computed
	 * and returned. Also the points that lie within thewindow are stored into
	 * the basin of attraction structure used by the optimized mean shift algorithms.
	 *
	 * Usage: optUnifromLsearch(My_ptr, yk_ptr)
	 *
	 * NOTE: This method is the only method in the MeanShift class that uses the weight
	 *       map asside from uniformLSearch.
	 *
	 * Pre:
	 *    - Mh_ptr is a length N array of doubles
	 *
	 *    - yk_ptr is a length N array of doubles
	 *
	 *    - Mh_ptr is the sum of the data points found within search window having
	 *      center yk_ptr
	 *
	 * Post:
	 *    - a search on the data set using the latice has been performed, and all
	 *      points found to lie within the search window defined using a uniform
	 *      kernel are summed and counted.
	 *
	 *    - their point wise sum is pointed to by Mh_ptr and their count is stored
	 *      by wsum.
	 *
	 *    - the data points lying within h of of yk_ptr have been stored into the basin of
	 *      attraction data structure.
	 *
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	private void optUniformLSearch(double[] Mh_ptr, double[] yk_ptr) {

		try {

		// Declare variables
			int	i, j, k;
			int				s, p, dataPoint, pointIndx, lN;
			double			diff, el, dx, dy, tx, weight;

		// Define latice data dimension...
			lN	= N + 2;

		// Define bounds of latice...

		// the latice is a 2dimensional subspace whose
		// search window bandwidth is specified by
		// h[0]:
			tx = yk_ptr[0] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundX = 0;
			else
				LowerBoundX = (int) tx;
			tx = yk_ptr[1] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundY = 0;
			else
				LowerBoundY = (int) tx;
			tx = yk_ptr[0] + h[0] - DELTA;
			if (tx >= width)
				UpperBoundX = width-1;
			else
				UpperBoundX = (int) tx;
			tx = yk_ptr[1] + h[0] - DELTA;
			if (tx >= height)
				UpperBoundY = height - 1;
			else
				UpperBoundY = (int) tx;

		// Perform search using latice
			for(i = LowerBoundY; i <= UpperBoundY; i++) {
				for(j = LowerBoundX; j <= UpperBoundX; j++) {

					// get index into data array
						pointIndx	= i*width+j;
						dataPoint	= N*(pointIndx);

					// Determine if inside search window
						k		= 1;
						s		= 0;
						dx      = j - yk_ptr[0];
						dy      = i - yk_ptr[1];
						diff	= (dx*dx+dy*dy)/(h[0]*h[0]);

						while ((diff < 1.0)&&(k != kp)) {// Partial Distortion Search
							// Calculate distance squared of sub-space s
								diff = 0;
								for(p = 0; p < P[k]; p++) {
									el    = (data[dataPoint+p+s]-yk_ptr[p+s+2])/h[k];
									if (edisonPortVersion==0) {
										diff += el*el;
									} else {
										if ((p==0)&&(yk_ptr[2] > 80)) {
											diff += 4*el*el;
										} else {
											diff += el*el;
										}
									}
								}

							// next subspace
								s += P[k];
								k++;
						}

					// if its inside search window perform sum and count
					if (diff < 1.0) {
						weight = 1 - weightMap[i*width+j];
						Mh_ptr[0] += weight*j;
						Mh_ptr[1] += weight*i;

						for(k = 2; k < lN; k++) {
							Mh_ptr[k] += weight*data[dataPoint+k-2];
						}

						wsum += weight;

						// set basin of attraction mode table
							if (edisonPortVersion==0) {
								if (modeTable[pointIndx] == 0) {
									pointList[pointCount++]	= pointIndx;
									modeTable[pointIndx]	= 2;
								}
							} else {
								if (diff < 0.5) {
								   if(modeTable[pointIndx] == 0) {
										pointList[pointCount++] = pointIndx;
										modeTable[pointIndx] = 2;
								   }
								}
							}

					}
				}
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.optUniformLSearch() Exception ---\n");
			e.printStackTrace();
		}

	} // end optUniformLSearch



	/**
	 * <pre>
	 * Performs search on data set for all points lying within the search window
	 * defined using a general kernel. Their point-wise sum and count is computed
	 * and returned.
	 *
	 * Usage: gerealLSearch(Mh_ptr, yk_ptr)
	 *
	 * Pre:
	 *    - Mh_ptr is a length N array of doubles
	 *
	 *    - yk_ptr is a length N array of doubles
	 *
	 *    - Mh_ptr is the sum of the data points found within search window having
	 *      center yk_ptr
	 * Post:
	 *    - a search on the data set using the lattice has been performed, and all
	 *      points found to lie within the search window defined using a general kernel
	 *      are summed and counted
	 *
	 *    - their point wise sum is pointed to by Mh_ptr and their count is stored by
	 *      wsum
	 *
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	private void generalLSearch(double[] Mh_ptr, double[] yk_ptr) {

		try {

		// Declare variables
			int i, j, k;
			@SuppressWarnings("unused")
			int			 s, p, dataPoint, lN, x0, x1;
			double		 diff, el, dx, dy, tw, u, y0, y1, tx;

		// Define lattice data dimension...
			lN	= N + 2;

		// Define bounds of lattice...

		// the lattice is a 2dimensional subspace whose
		// search window bandwidth is specified by
		// h[0]:
			tx = yk_ptr[0] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundX = 0;
			else
				LowerBoundX = (int) tx;
			tx = yk_ptr[1] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundY = 0;
			else
				LowerBoundY = (int) tx;
			tx = yk_ptr[0] + h[0] - DELTA;
			if (tx >= width)
				UpperBoundX = width-1;
			else
				UpperBoundX = (int) tx;
			tx = yk_ptr[1] + h[0] - DELTA;
			if (tx >= height)
				UpperBoundY = height - 1;
			else
				UpperBoundY = (int) tx;

		// Perform search using lattice
			for(i = LowerBoundY; i <= UpperBoundY; i++) {
				for(j = LowerBoundX; j <= UpperBoundX; j++) {

					// get index into data array
						dataPoint = N*(i*width+j);

					// Determine if inside search window
						k		= 1;
						s		= 0;
						dx      = j - yk_ptr[0];
						dy      = i - yk_ptr[1];
						uv[0]	= (dx*dx)/(h[0]*h[0]);
						uv[1]	= (dy*dy)/(h[0]*h[0]);
						diff	= uv[0] + uv[1];

						while((diff < offset[k-1])&&(k != kp)) { // Partial Distortion Search
							// Calculate distance squared of sub-space s
								diff = 0;
								for(p = 0; p < P[k]; p++) {
									el    = (data[dataPoint+p+s]-yk_ptr[p+s+2])/h[k];
									diff += uv[p+s+2] = el*el;
								}

							// next subspace
								s += P[k];
								k++;
						}

					// if its inside search window perform weighted sum and count
						if (diff < offset[k-1]) {

							// Initialize total weight to 1
								tw = 1;

							// Calculate weight factor using weight function
							// lookup tables and uv
								s = 0;
								for (k = 0; k < kp; k++) {
									if (kernel[k] != KernelType.UNIFORM) { // not uniform kernel
										// Compute u[i]
											u = 0;
											for (p = 0; p < P[k]; p++) {
												u += uv[s+p];
											}

										// Accumulate tw using calculated u
										// and weight function lookup table

										// Linear interpolate values given by
										// lookup table

										// Calculate x0 and x1, the points surounding
										// u
											x0 = (int)(u/increment[k]);
											x1 = x0+1;

										// Get y0 and y1 from the lookup table
											y0 = w[k][x0];
											y1 = w[k][x1];

										// Accumulate tw using linear interpolation
											tw *= (((x1)*increment[k] - u)*y0+(u - (x0)*increment[k])*y1)
												/(x1*increment[k] - x0*increment[k]);

									}

									// next subspace
										s += P[k];
								}

							// Perform weighted sum using xi
								Mh_ptr[0]	+= tw*j;
								Mh_ptr[1]	+= tw*i;
								for (k = 0; k < N; k++) {
									Mh_ptr[k+2] += tw*data[dataPoint+k];
								}

							// Increment wsum by tw
								wsum += tw;

						}
				}
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.generalLSearch() Exception ---\n");
			e.printStackTrace();
		}

	} // end generalLSearch



	/**
	 * <pre>
	 * Performs search on data set for all points lying within the search window
	 * defined using a general kernel. Their point-wise sum and count is computed
	 * and returned. Also the points that lie within the window are stored into
	 * the basin of attraction structure used by the optimized mean shift algorithms.
	 *
	 * Usage: optGeneralLSearch(Mh_ptr, yk_ptr)
	 *
	 * Pre:
	 *   - Mh_ptr is a length N array of doubles
	 *
	 *   - yk_ptr is a length N array of doubles
	 *
	 *   - Mh_ptr is the sum of the data points foundwithin search window having
	 *     center yk_ptr
	 *
	 * Post:
	 *   - a search on the data set using the lattice has been performed, and all points
	 *     found to lie within the search window defined using a general kernel are
	 *     summed and counted
	 *
	 *   - their point wise sum is pointed to by Mh_ptr and their count is stored by wsum
	 *
	 *
	 *   - the data points lying within h*offset of yk_ptr have been stored into the
	 *     basin of attraction data structure.
	 *
	 * @param Mh_ptr
	 * @param yk_ptr
	 */
	private void optGeneralLSearch(double[] Mh_ptr, double[] yk_ptr) {

		try {

		// Declare variables
			int	i, j, k;
			@SuppressWarnings("unused")
			int				s, p, dataPoint, pointIndx, lN, x0, x1;
			double			diff, el, dx, dy, tw, u, y0, y1, tx;

		// Define lattice data dimension...
			lN	= N + 2;

		//Define bounds of lattice...

		// the lattice is a 2dimensional subspace whose
		// search window bandwidth is specified by
		// h[0]:
			tx = yk_ptr[0] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundX = 0;
			else
				LowerBoundX = (int) tx;
			tx = yk_ptr[1] - h[0] + DELTA + 0.99;
			if (tx < 0)
				LowerBoundY = 0;
			else
				LowerBoundY = (int) tx;
			tx = yk_ptr[0] + h[0] - DELTA;
			if (tx >= width)
				UpperBoundX = width-1;
			else
				UpperBoundX = (int) tx;
			tx = yk_ptr[1] + h[0] - DELTA;
			if (tx >= height)
				UpperBoundY = height - 1;
			else
				UpperBoundY = (int) tx;

		// Perform search using lattice
			for (i = LowerBoundY; i <= UpperBoundY; i++) {
				for (j = LowerBoundX; j <= UpperBoundX; j++) {

					// get index into data array
						pointIndx	= i*width+j;
						dataPoint = N*(i*width+j);

					// Determine if inside search window
						k		= 1;
						s		= 0;
						dx      = j - yk_ptr[0];
						dy      = i - yk_ptr[1];
						uv[0]	= (dx*dx)/(h[0]*h[0]);
						uv[1]	= (dy*dy)/(h[0]*h[0]);
						diff	= uv[0] + uv[1];
						while ((diff < offset[k-1])&&(k != kp)) {	// Partial Distortion Search
							// Calculate distance squared of sub-space s
								diff = 0;
								for (p = 0; p < P[k]; p++) {
									el    = (data[dataPoint+p+s]-yk_ptr[p+s+2])/h[k];
									diff += uv[p+s+2] = el*el;
								}

							// next subspace
								s += P[k];
								k++;
						}

					// if its inside search window perform weighted sum and count
						if (diff < offset[k-1]) {

							// Initialize total weight to 1
								tw = 1;

							// Calculate weight factor using weight function
							// lookup tables and uv
								s = 0;
								for (k = 0; k < kp; k++) {
									if (kernel[k] != KernelType.UNIFORM) { // not uniform kernel
										// Compute u[i]
											u = 0;
											for (p = 0; p < P[k]; p++) {
												u += uv[s+p];
											}

										// Accumulate tw using calculated u
										// and weight function lookup table

										// Linear interpolate values given by
										// lookup table

										// Calculate x0 and x1, the points surounding
										// u
											x0 = (int)(u/increment[k]);
											x1 = x0+1;

										// Get y0 and y1 from the lookup table
											y0 = w[k][x0];
											y1 = w[k][x1];

										// Accumulate tw using linear interpolation
											tw *= (((x1)*increment[k] - u)*y0+(u - (x0)*increment[k])*y1)
												/(x1*increment[k] - x0*increment[k]);

									}

									// next subspace
										s += P[k];
								}

							// Perform weighted sum using xi
								Mh_ptr[0]	+= tw*j;
								Mh_ptr[1]	+= tw*i;

								for (k = 0; k < N; k++) {
									Mh_ptr[k+2] += tw*data[dataPoint+k];
								}

							// Increment wsum by tw
								wsum += tw;

							// set basin of attraction mode table
								if (modeTable[pointIndx] == 0) {
									pointList[pointCount++]	= pointIndx;
									modeTable[pointIndx]	= 2;
								}

						}
				}
			}


		} catch (Exception e) {
			System.out.println("\n--- MeanShift.optGeneralLSearch() Exception ---\n");
			e.printStackTrace();
		}

	} // end optGeneralLSearch

} // end Meanshift class



/*
 * $Log$
 * Revision 1.19  2005/10/14 00:08:28  yoda2
 * Fixed inaccessible class or interface error by making ClassStateStruct a protected rather than private class.
 *
 * Revision 1.18  2004/02/25 21:59:22  yoda2
 * Updated copyright notice.
 *
 * Revision 1.17  2004/02/25 21:25:14  yoda2
 * Added options for chosing which EDISON port to use and hiding intermediate segmentation results.
 *
 * Revision 1.16  2003/11/24 16:34:41  yoda2
 * Small JavaDoc fixes to get rid of warnings.
 *
 * Revision 1.15  2003/11/24 16:20:10  yoda2
 * Updated copyright to 2002-2003.
 *
 * Revision 1.14  2003/11/24 14:56:26  yoda2
 * Updated Java port to bring code in sync with 4-14-2003 release of original EDISON C++ code (had been based on 4-25-2002 version).  The major changes are:
 * 1. LUV image data is now stored in floating point format (was integer)
 * 2. Misc bug fixes and elimination of some unnecessary code
 * 3. Added new versions of NonOptimized, Optimized1, and Optimized2 filters
 *
 * Revision 1.13  2002/12/11 23:03:12  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.12  2002/09/20 19:49:00  bpangburn
 * Fixed various JavaDoc error messages.
 *
 * Revision 1.11  2002/09/20 19:15:15  bpangburn
 * Added BSD-style license, cleaned up JavaDoc, and moved CVS log to end of each source file.
 *
 * Revision 1.10  2002/08/21 18:31:13  jayo
 * Updated error handlers to reflect class and method names.
 *
 * Revision 1.9  2002/08/20 21:49:15  bpangburn
 * Added crude error handlers to each method.
 *
 * Revision 1.8  2002/07/09 19:43:57  jayo
 * Fixed typo.
 *
 * Revision 1.7  2002/07/09 19:42:07  jayo
 * Editing comments to coincide with Javadoc
 *
 * Revision 1.5  2002/07/02 21:12:03  jayo
 * Editing comments to coincide with Javadoc
 *
 * Revision 1.4  2002/07/02 18:27:55  jayo
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