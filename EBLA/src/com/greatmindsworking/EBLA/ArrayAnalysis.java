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



import java.util.*;



/**
 * ArrayAnalysis.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * This class performs basic statistical analysis on an array of
 * double-precision numbers.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class ArrayAnalysis {
	/**
	 * initial/first value in array
	 */
	private double initialValue = 0;

	/**
	 * minimum value in array
	 */
    private double minValue = 0;

	/**
	 * maximum value in array
	 */
    private double maxValue = 0;

	/**
	 * average value in array
	 */
    private double avgValue = 0;

	/**
	 * standard deviation for values in array
	 */
    private double stdDeviation = 0;



	/**
	 * Class constructor that takes an array list and calculates statistics
	 *
	 * @param _al array list of doubles for statistical analysis
	 */
    public ArrayAnalysis(ArrayList _al) {

		// DECLARATIONS
			double total = 0;				// RUNNING TOTAL FOR AVERAGE
			int count = 0;					// NUMBER OF ELEMENTS IN ARRAY
			double current = 0;				// "CURRENT" VALUE IN ARRAY
			double variance = 0;			// RUNNING TOTAL OF VARIANCE

		try {

			// CONVERT ARRAY LIST TO ARRAY
				Object doubleA[] = _al.toArray();

			// INITIALIZE VALUES
				initialValue = ((Double)doubleA[0]).doubleValue();
				minValue = initialValue;
				maxValue = initialValue;
				total = initialValue;

			// DETERMINE NUMBER OF ELEMENTS
				count = doubleA.length;

			// LOOP THROUGH ARRAY TO DETERMINE MIN, MAX, & TOTAL
				for (int i=1; i<count; i++) {
					// "CURRENT" VALUE
						current = ((Double)doubleA[i]).doubleValue();

					// MIN & MAX
						if (current < minValue) {
							minValue = current;
						} else if (current > maxValue) {
							maxValue = current;
						}

					// RUNNING TOTAL
						total += current;

				} // end for loop

			// CALCULATE AVERAGE
				avgValue = total / count;

			// CALCULATE TOTAL VARIANCE (REQUIRES 2ND PASS THROUGH ARRAY)
				for (int i=0; i<count; i++) {
					// "CURRENT" VALUE
						current = ((Double)doubleA[i]).doubleValue() - avgValue;

					// RUNNING TOTAL OF VARIANCE
						variance += current * current;

				} // end for loop

			// FINISH VARIANCE CALCULATION
				variance = variance / (count - 1);

			// CALCULATE STANDARD DEVIATION
				stdDeviation = Math.sqrt(variance);

		} catch (Exception e) {
			System.out.println("\n--- ArrayAnalysis Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end ArrayAnalysis()



	/**
	 * Returns the initial/first value in the array list
	 *
	 * @return first value in array list
	 */
	public double getInitialValue() {
		return initialValue;
	} // end getInitialValue()



	/**
	 * Returns the minimum value in the array list
	 *
	 * @return minimum value in array list
	 */
	public double getMinValue() {
		return minValue;
	} // end getMinValue()



	/**
	 * Returns the maximum value in the array list
	 *
	 * @return maximum value in array list
	 */
	public double getMaxValue() {
		return maxValue;
	} // end getMaxValue()



	/**
	 * Returns the average value in the array list
	 *
	 * @return average value in array list
	 */
	public double getAvgValue() {
		// ROUND TO TWO DECIMAL PLACES
			Double tmpD = new Double(avgValue * 100.0);
			Long tmpL = new Long(tmpD.longValue());
			return tmpL.doubleValue() / 100.0;
	} // end getAvgValue()



	/**
	 * Returns the standard deviation for the values in the array list
	 *
	 * @return standard deviation for the array list
	 */
	public double getStdDeviation() {
		// ROUND TO TWO DECIMAL PLACES
			Double tmpD = new Double(stdDeviation * 100.0);
			Long tmpL = new Long(tmpD.longValue());
			return tmpL.doubleValue() / 100.0;
	} // end getStdDeviation()



    /**
     * Main procedure - allows ArrayAnalysis to be run in stand-alone mode
     */
    public static void main(String[] args) {

		try {

			// INITIALIZE AN ARRAY LIST
				ArrayList al = new ArrayList();

			// ADD A FEW DATA ELEMENTS
				al.add(new Double(26));
				al.add(new Double(33));
				al.add(new Double(16));
				al.add(new Double(22));
				al.add(new Double(18));

			// GENERATE STATISTICS
				ArrayAnalysis aa = new ArrayAnalysis(al);

			// PRINT RESULTS
				System.out.println("Size of array list: " + al.size());
				System.out.println("Contents of array list: " + al);
				System.out.println("First value in array list: " + aa.getInitialValue());
				System.out.println("Minimum value in array list: " + aa.getMinValue());
				System.out.println("Maximum value in array list: " + aa.getMaxValue());
				System.out.println("Average value in array list: " + aa.getAvgValue());
				System.out.println("Standard deviation for the values in the array list: " + aa.getStdDeviation());

			// INDICATE SUCCESS TO USER
				System.out.println("Array list processed successfully!");

			// EXIT
				System.out.println("Exiting ArrayAnalysis.main() standalone testing routine.");
				System.exit(0);

		} catch (Exception e) {
			System.out.println("\n--- ArrayAnalysis.main() Exception ---\n");
			e.printStackTrace();
		}

    } // end main()

} // end ArrayAnalysis class



/*
 * $Log$
 * Revision 1.6  2002/12/11 22:47:06  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.5  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.4  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.3  2002/09/27 23:11:06  bpangburn
 * Moved information for execution mode and # passed to parameters.
 *
 * Revision 1.2  2002/04/23 22:48:25  bpangburn
 * Added rounding to two decimal places for average and standard deviation.
 *
 * Revision 1.1  2002/02/08 22:31:26  bpangburn
 * Created ArrayAnalysis class to determine first, minimum, maximum, and average values for an array list of Doubles.  It also calculates the standard deviation of the values in the list.
 *
 */