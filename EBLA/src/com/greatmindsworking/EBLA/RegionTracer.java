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



import java.util.*;
import java.awt.*;
import com.greatmindsworking.EDISON.segm.*;



/**
 * RegionTracer.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Takes an array of com.greatmindsworking.EDISION.segm.REGION objects
 * and returns an ArrayList of Polygon objects that trace the border of
 * each region.
 *<p>
 * This code is based loosely on Douglas Lyon's bugWalk code from
 * the book "Image Processing in Java."  See http://www.docjava.com
 * for more information.
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class RegionTracer {
	/**
	 * integer used to denote NORTH
	 */
	static private final int north = 1;

	/**
	 * integer used to denote EAST
	 */
	static private final int east = 2;

	/**
	 * integer used to denote SOUTH
	 */
	static private final int south = 3;

	/**
	 * integer used to denote WEST
	 */
	static private final int west = 4;



	/**
	 * Determines the relative right, forward, left, and backward 2-D pixel neighbors based on current direction.
	 *
	 * @param _direction equal to constant north (1), south (2), east (3), or west(4) indicating current direction of travel
	 * @param _width width of row of pixels in source image
	 *
	 * @return priortized array of neighboring pixels that tracing algorithm should attempt to visit
	 */
	static private int[] getEdgeNeighbors(int _direction, int _width) {

		// DECLARATIONS
			int[] neighbors = new int[4];  // ARRAY TO HOLD POSITION OF 4 EDGE NEIGHBORS

		try {

			// DETERMINE THE RELATIVE RIGHT, FORWARD, LEFT, AND BACKWARD 2-D PIXEL NEIGHBORS BASED ON CURRENT DIRECTION.
				if (_direction == north) {
					neighbors[0] = +1;			// right
					neighbors[1] = -_width; 	// forward
					neighbors[2] = -1;			// left
					neighbors[3] = +_width;		// back
				} else if (_direction == south) {
					neighbors[0] = -1;			// right
					neighbors[1] = +_width; 	// forward
					neighbors[2] = +1;			// left
					neighbors[3] = -_width;		// back
				} else if (_direction == east) {
					neighbors[0] = +_width;		// right
					neighbors[1] = +1; 			// forward
					neighbors[2] = -_width;		// left
					neighbors[3] = -1;			// back
				} else if (_direction == west) {
					neighbors[0] = -_width;		// right
					neighbors[1] = -1; 			// forward
					neighbors[2] = +_width;		// left
					neighbors[3] = +1;			// back
				}

		} catch (Exception e) {
			System.out.println("\n--- RegionTracer.getEdgeNeighbors() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN NEIGHBORS
			return neighbors;

   	} // end getEdgeNeighbors



	/**
	 * Determines the offset for neighboring pixels based on the image width for a 1-D pixel array
	 *
	 * @param _width width of row of pixels in source image
	 *
	 * @return array of neighboring pixels
	 */
	static private int[] getAllNeighbors(int _width) {

		// DECLARATIONS
			int[] neighbors = new int[8];	// ARRAY TO HOLD POSITION OF 8 PIXEL NEIGHBORS

		try {

			// DETERMINES THE OFFSET FOR NEIGHBORING PIXELS BASED ON THE IMAGE WIDTH
				neighbors[0]= -_width-1;  neighbors[1]= -_width;
				neighbors[2]= -_width+1;  neighbors[3]= +1;
				neighbors[4]= +_width+1;  neighbors[5]= +_width;
				neighbors[6]= +_width-1;  neighbors[7]= -1;

		} catch (Exception e) {
			System.out.println("\n--- RegionTracer.getAllNeighbors() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN NEIGHBORS
			return neighbors;

   	} // end getAllNeighbors()



	/**
	 * Generates a list of polygon traces for an array of com.greatmindsworking.EDISION.segm.REGION objects
	 *
	 * @param _regionArray 	array of segmented REGIONs to trace
	 * @param _width 		width of row of pixels in source image
	 * @param _height 		height of column of pixels in source image
	 * @param _minPixels 	minimum number of pixels that constitute a "significant" object
	 * @param _maxPixels 	number of pixels that constitute "background"
	 *
	 * @return ArrayList of bounding polygons
	 */
	static public ArrayList bugWalk(REGION[] _regionArray, int _width, int _height,
		int _minPixels, int _maxPixels) {

		// DECLARATIONS
			int minTrace = 70;				// MINIMUM NUMBER OF PIXELS THAT CONSTITUES A LEGITIMATE TRACE
											// ((_minPixels ^ (1/2)) * 4) - 4)
			int curX = 0;					// "CURRENT" X POSITION
			int curY = 0;					// "CURRENT" Y POSITION
			ArrayList polygonList
				= new ArrayList();			// POLYGON TRACES OF SEGMENTED REGIONS
			int neigh4[] = new int[4];		// N, S, E, & W PIXEL NEIGHBORS
			REGION curRegion = null;		// "CURRENT" SEGMENTED REGION
			int pixelCount
				= _width * _height;			// # PIXELS IN EACH IMAGE
			int curDirection = south;		// "CURRENT" DIRECTION OF TRACE
			int startRegIndex = 0;			// REGION ARRAY INDEX FOR TRACE
			int startIndex = 0;				// STARTING PIXEL POSITION FOR TRACE
			int curIndex = 0;				// "CURRENT" PIXEL POSITION FOR TRACE
			int tmpIndex = 0;				// TMP PIXEL POSITION FOR TRACE
			boolean stopLoop = false;		// INDICATES WHEN TO STOP TRACING LOOP FOR "CURRENT" REGION
			int neigh8[]
				= getAllNeighbors(_width); 	// OFFSET OF EACH NEIGHBOR BASED ON IMAGE WIDTH
			boolean traceFailed = false;	// INDICATES THAT TRACE FAILED (E.G. BACKTRACKING, ETC.)


		try {

			// LOOP THROUGH ARRAY OF REGIONS
			//	- IF NUMBER OF PIXELS IN REGION (AREA) IS WITHIN MIN/MAX PIXEL RANGE FOR A SIGNIFICANT REGION THEN
			//	  PERFORM A BUGWALK ON THAT REGION TO DETERMINE ITS POLYGON DEFINITION
				for (int i=0; i<_regionArray.length; i++) {
					// EXTRACT CURRENT REGION
						curRegion = _regionArray[i];

					// EXTRACT NUMBER OF PIXELS IN REGION
						int boundCount = curRegion.pointCount;

					// MAKE SURE THAT NUMBER OF PIXELS IN CURRENT REGION IS IN "ACCEPTABLE" RANGE
					// (I.E. NOT TOO SMALL (NOISE) OR TOO LARGE (BACKGROUND)
						if ((boundCount > _minPixels) && (boundCount < _maxPixels)) {

						// CREATE IMAGE ARRAY TO SET CONTRASTING BORDER POINTS
							int[] curImage = new int[pixelCount];

						// SORT REGION PIXELS (TOP TO BOTTOM, LEFT TO RIGHT)
						//	- THIS MAKES THE FIRST ARRAY ELEMENT THE LEFT-MOST PIXEL IN THE TOP ROW OF THE REGION
							Arrays.sort(curRegion.region);

						// SET ALL REGION PIXEL VALUES TO 255
							for (int j=0; j<boundCount; j++) {
								curImage[curRegion.region[j]] = 255;
							}



						// STRIP ANY PIXELS WITH LESS THAN THREE NEIGHBORS...
							for (int j=0; j<boundCount; j++) {
								// INITILIZE # NEIGHBORS
									int neighbors=0;

								// DETERMINE NEIGHBOR COUNT
									for (int k=0;k<8;k++) {
										int tmpCoord = curRegion.region[j] + neigh8[k];
										if ((tmpCoord > -1) && (tmpCoord < pixelCount)) {
											if (curImage[tmpCoord] == 255) {
												// FOUND A NEIGHBOR SO BUMP COUNT
													neighbors++;

												// STOP IF TWO NEIGHBORS ARE FOUND
													if (neighbors > 2) {
														break;
													}

											}

										}
									} // end for

								// IF NUMBER OF NEIGHBORS < 3 THEN REMOVE PIXEL
									if (neighbors < 3) {
										curImage[curRegion.region[j]] = 0;
									}

							} // end for (int j=0; j<boundCount; j++)



						// DETERMINE STARTING POINT
							for (int j=0; j<boundCount; j++) {
								if (curImage[curRegion.region[j]] == 255) {
									startRegIndex = j;
									break;
								}
							}
							startIndex = curRegion.region[startRegIndex];

						// INITIALIZE STOP LOOP FLAG
							stopLoop = false;

						// CHANGE VALUE OF STARTING POINT ASSUMING INITIAL ORIENTATION IS SOUTH
						// 	(SINCE WE'RE STARTING AT THE LEFT-MOST PIXEL IN THE TOP ROW, WE CAN
						//	ONLY MOVE SOUTH OR WEST)
							curDirection = south;
							curImage[startIndex] = curDirection;

						// GENERATE ARRAY OF POINTS
							int ptList[] = new int[_maxPixels];
							int ptCnt = 0;

						// ADD FIRST POLYGON POINT
							ptList[ptCnt] = startIndex;

						// SET "CURRENT" X & Y COORDINATES
							curIndex = startIndex;

						// LOOP UNTIL IMAGE IS TRACED
							while (! stopLoop) {
								// GET NEIGHBORING PIXELS
									neigh4 = getEdgeNeighbors(curImage[curIndex], _width);

								// INITIALIZE SEARCH FLAG
									boolean searching = true;

								// DETERMINE IF TRACE HAS RETURNED TO STARTING POINT
									if (ptCnt > minTrace) {
										if ((Math.abs((curIndex / _width)-(startIndex / _width)) < 5)
											&& (Math.abs((curIndex % _width)-(startIndex % _width)) < 5)) {
											// SET FLAGS TO STOP SEARCH AND EXIT LOOP
												stopLoop = true;
												searching = false;
										}
									}

								// CHECK RIGHT
									if (searching) {
										tmpIndex = curIndex + neigh4[0];
										if ((tmpIndex >=0) && (tmpIndex < pixelCount)) {
											if (curImage[tmpIndex] == 255) {
												// UPDATE CURRENT POINT
													curIndex = tmpIndex;

												// ADD CURRENT POINT TO ARRAY
													ptCnt++;
													ptList[ptCnt] = tmpIndex;

												// SET DIRECTION
													curDirection++;
													if (curDirection > 4) {
														curDirection = 1;
													}
													curImage[tmpIndex] = curDirection;


												// INDICATE THAT SEARCH IS OVER
													searching = false;
											}
										}
									}

								// CHECK FORWARD
									if (searching) {
										tmpIndex = curIndex + neigh4[1];
										if ((tmpIndex >=0) && (tmpIndex < pixelCount)) {
											if (curImage[tmpIndex] == 255) {
												// UPDATE CURRENT POINT
													curIndex = tmpIndex;

												// ADD CURRENT POINT TO ARRAY
													ptCnt++;
													ptList[ptCnt] = tmpIndex;

												// SET DIRECTION - NO CHANGE IF MOVING FORWARD
													curImage[tmpIndex] = curDirection;

												// INDICATE THAT SEARCH IS OVER
													searching = false;
											}
										}
									}

								// CHECK LEFT
									if (searching) {
										tmpIndex = curIndex + neigh4[2];
										if ((tmpIndex >=0) && (tmpIndex < pixelCount)) {
											if (curImage[tmpIndex] == 255) {
												// UPDATE CURRENT POINT
													curIndex = tmpIndex;

												// ADD CURRENT POINT TO ARRAY
													ptCnt++;
													ptList[ptCnt] = tmpIndex;

												// SET DIRECTION
													curDirection--;
													if (curDirection < 1) {
														curDirection = 4;
													}
													curImage[tmpIndex] = curDirection;

												// INDICATE THAT SEARCH IS OVER
													searching = false;
											}
										}
									}

								// BACKTRACK IF NO NEW DIRECTION HAS BEEN FOUND
									if (searching) {
										// SET "CURRENT" POINT TO ZERO (MAKE PART OF BACKGROUND)
											curImage[curIndex] = 0;

										// DECREMENT POINT COUNT
											ptCnt--;

										// IF NOT BACK AT STARTING POINT, THEN DETERMINE OLD DIRECTION
											if (ptCnt >= 0) {
											// RETRIEVE OLD X & Y COORDINATES...
												curIndex = ptList[ptCnt];

											// EXTRACT DIRECTION WHILE AT OLD COORDINATES
												curDirection = curImage[curIndex];
											} else {
											// STARTING POINT WAS BAD, SO PICK A NEW ONE...
											//	- ASSUME THAT A NEW STARTING POINT WON'T BE FOUND
												stopLoop = true;
												for (int j=startRegIndex+1; j<boundCount; j++) {
													if (curImage[curRegion.region[j]] == 255) {
														// RESET STOP LOOP FLAG
															stopLoop = false;

														// RESET REGION INDEX
															startRegIndex = j;

														// RESET START INDEX
															startIndex = curRegion.region[j];

														// RESET CURRENT INDEX
															curIndex = startIndex;

														// CHANGE VALUE OF STARTING POINT ASSUMING INITIAL ORIENTATION IS SOUTH
														// 	(SINCE WE'RE STARTING AT THE LEFT-MOST PIXEL IN THE TOP ROW, WE CAN
														//	ONLY MOVE SOUTH OR WEST)
															curDirection = south;
															curImage[curIndex] = curDirection;

														// 	RESET POINT COUNTER
															ptCnt = 0;

														// ADD FIRST POLYGON POINT
															ptList[ptCnt] = startIndex;

														// BREAK LOOP
															break;

													} // end if

													//if (! stopLoop) break;

												} // end for

											// IF STOP LOOP IS TRUE THEN ALL POSSIBLE STARTING POINTS WERE EXHAUSTED
												if (stopLoop) {
													System.out.println("All possible starting points were exhausted for region #" + i + "!");
													traceFailed = true;
												}

											} // if (ptCnt >= 0)

									} // end if (searching)


							} // end tracing loop


						// BUILD POLYGON BASED ON RESULTS OF BUGWALK
						// 	ONLY CONTINUE IF THERE IS MORE THAT ONE POINT
							if (ptCnt > 1) {
								// BUILD POLYGON FROM COLINEAR POINTS IN ARRAY
									Polygon poly = new Polygon();
									curY = ptList[0] / _width;
									curX = ptList[0] % _width;

									poly.addPoint(curX,curY);

									boolean matchX = false;

									if (curX == (ptList[1] % _width)) {
										matchX=true;
									}

									curX = ptList[1] % _width;
									curY = ptList[1] / _width;

									for (int k=2;k<=ptCnt;k++) {
										if (matchX) {
											if ((ptList[k] % _width) != curX) {
												// update current x,y
													curX = ptList[k-1] % _width;
													curY = ptList[k-1] / _width;

												// add prior point
													poly.addPoint(curX, curY);

												// switch to matching Y
													matchX=false;
											}
										} else {
											if ((ptList[k] / _width) != curY) {
												// update current x,y
													curX = ptList[k-1] % _width;
													curY = ptList[k-1] / _width;

												// add prior point
													poly.addPoint(curX, curY);

												// switch to matching Y
													matchX=true;
											}
										} // end if (matchX) {

									} // end for (int k=2;k<=ptCnt;k++)

								// ADD FIRST POINT TO COMPLETE LOOP
									poly.addPoint((ptList[0] % _width), (ptList[0] / _width));

								// ADD CURRENT POLYGON TO LIST (VECTOR)
									polygonList.add(poly);

							} // end if ptCnt > 1

						} // end boundCount range test...

				} // end for

		} catch (Exception e) {
			System.out.println("\n--- RegionTracer.bugWalk() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN POLYGON LIST
			if (traceFailed) {
				return null;
			} else {
				return polygonList;
			}

	} // end bugWalk

} // end RegionTracer class



/*
 * $Log$
 * Revision 1.5  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.4  2002/10/02 20:51:33  bpangburn
 * Added BSD style license and cleaned up docs.
 *
 * Revision 1.3  2002/08/09 21:46:44  bpangburn
 * Added code to skip frames where RegionTracer fails to trace some object.
 *
 * Revision 1.2  2002/08/01 17:36:25  bpangburn
 * Debugged and cleaned up tracing algorithm.
 *
 * Revision 1.1  2002/07/03 22:54:09  bpangburn
 * Moved bug walk code to RegionTracer.java.
 *
 */