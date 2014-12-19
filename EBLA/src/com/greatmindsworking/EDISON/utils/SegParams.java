/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2004, Brian E. Pangburn
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



package com.greatmindsworking.EDISON.utils;



import java.io.FileWriter;



public class SegParams implements Comparable<SegParams> {

	/**
	 * float containing the color radius for mean-shift analysis image segmentation
	 *
	 * The color radius is the number of pixels that constitute a "significant" color.
	 */
	public float colorRadius = (float)6.5;

	/**
	 * integer containing the spatial radius for mean-shift analysis image segmentation
	 *
	 * The spatial radius is the pixel radius of the search window.  The smaller the value, the
	 * higher the segmentation resolution.
	 */
	public int spatialRadius = 7;

	/**
	 * integer containing the minimum number of pixel that constitute a region for
	 * mean-shift analysis image segmentation
	 *
	 * The minimum region is the smallest number of contiguous pixels required for a
	 * "significant" image region.
	 */
	public int minRegion = 20;

	/**
	 * integer containing the speed-up level for mean-shift analysis image segmentation
	 *
	 * 0=no speedup, 1=medium speedup, 2=high speedup
	 */
	public int speedUp = 2;

	/**
	 * float containing the speed-up level factor for high speed up segmentation
	 *
	 * 0.0 = highest quality, 1.0 = highest speedup
	 */
	public float speedUpFactor = (float)0.5;

	public int excessRegions = 0;


	public SegParams() {
		// empty constructor

	}



	public SegParams(float _colorRadius, int _spatialRadius, int _minRegion, int _speedUp, float _speedUpFactor) {

		colorRadius = _colorRadius;
		spatialRadius = _spatialRadius;
		minRegion = _minRegion;
		speedUp = _speedUp;
		speedUpFactor = _speedUpFactor;

	}




  // IMPLEMENT COMPARETO METHOD FOR SORTING SO THAT JAVA WILL KNOW HOW TO SORT
  //  AN ARRAY OF SegParams
    @Override
	public int compareTo(SegParams _sp) {

		//SegParams sp = (SegParams)_o;

		// compare excess regions (lower is better)
			if (excessRegions < _sp.excessRegions) {
				return -1;
			} else if (_sp.excessRegions < excessRegions) {
				return 1;
			}

		// compare speedup factor (higher is better)
			if (excessRegions > _sp.excessRegions) {
				return -1;
			} else if (_sp.excessRegions > excessRegions) {
				return 1;
			}

		// match condition
			return 0;

    }


    public void printParameters(FileWriter _fw) {

		try {

			if (_fw == null) {
				System.out.println("Excess Regions=" + excessRegions
					+ ", Color Radius=" + colorRadius + ", Spatial Radius=" + spatialRadius
					+ ", Min Region Size=" + minRegion + ", Speedup Factor=" + speedUpFactor);
			} else {
				_fw.write("Excess Regions=" + excessRegions
					+ ", Color Radius=" + colorRadius + ", Spatial Radius=" + spatialRadius
					+ ", Min Region Size=" + minRegion + ", Speedup Factor=" + speedUpFactor + "\n");
			}

		} catch (Exception e) {
			System.out.println("\n--- SegParams.printParameters() Exception ---\n");
			e.printStackTrace();
		}


	}



  // COPY x, y, & THETA PARAMETERS
    public void copyParameters(SegParams _sp) {

		colorRadius = _sp.colorRadius;
		spatialRadius = _sp.spatialRadius;
		minRegion = _sp.minRegion;
		speedUp = _sp.speedUp;
		speedUpFactor = _sp.speedUpFactor;
		excessRegions = _sp.excessRegions;

    }


} // end of SegParams class


/*
 * $Log$
 * Revision 1.2  2011/04/25 03:52:10  yoda2
 * Fixing compiler warnings for Generics, etc.
 *
 * Revision 1.1  2004/01/21 19:40:30  yoda2
 * Added experimental jEDISON genetic training algorithm for determining "optimal" segmentation parameters for a given set of images.
 *
 */