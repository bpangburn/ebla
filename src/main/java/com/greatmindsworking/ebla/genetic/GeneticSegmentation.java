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



package com.greatmindsworking.ebla.genetic;



import java.sql.*;
import java.io.*;
import java.util.*;

import com.greatmindsworking.utils.DBConnector;

import java.awt.image.*;

import javax.imageio.ImageIO;

import com.greatmindsworking.jedison.*;



public class GeneticSegmentation {

	// GENE POOL SIZE
		final static int poolSize = 100;

	// TOTAL NUMBER OF GENERATIONS
		final static int totalGenerations = 25;

	// GENETIC ALGORITHM MODE
    	final static boolean eliteMode = true;

    // MAXIMUM NUMBER OF EXCESS REGIONS PER FRAME TO REMAIN IN GENE POOL
    	final static int maxExcessRegionsPerFrame = 10;

    // VERSION OF EDISON PORT TO USE FOR SEGMENTATION 0=04-25-2002; 1=04-14-2003
    	final static int edisonPortVersion = 0;

    // SPEEDUP LEVEL
    	final static int speedUpLevel = 1;

    // NUMBER OF IMAGES TO EVALUATE FROM TOTAL IMAGE POOL
    	final static int imageSampleSize = 100;

    // PENALTY FACTOR FOR DROPPED FRAMES
    	final static int dropPenaltyFactor = 3;

    // VERBOSE MODE
    	final static boolean verboseGenerations = true;
    	final static boolean verboseSegmentation = false;

	// INITIALIZE WORST ACCEPTABLE ERROR FOR STAYING IN THE GENE POOL
		int maxAcceptableRegions = imageSampleSize * maxExcessRegionsPerFrame;

	// INITIALIZE LIST OF ALL IMAGES TO PROCESS
		ArrayList<String> fileList = new ArrayList<String>();

	// INITIALIZE TOTAL NUMBER OF IMAGES PROCESSED
		int runNumber = 0;

	// INITIALIZE SUM OF ALL EXCESS REGIONS (GA CLASSIC)
		int totExcessRegions = 0;

	// INITIALIZE GENE POOL
		SegParams pool[] = new SegParams[poolSize];

	// INITIALIZE RANDOM NUMBER GENERATOR
	//	Random rand = new Random(711);
		Random rand = new Random();


	public GeneticSegmentation(long _parameterID) {

		String sql = "";
		String expTmpPath = "";

		try {


		// CREATE A DATABASE CONNECTION
			DBConnector dbc = new DBConnector("dbSettings", true);

		// QUERY EXPERIENCES IN experience_data BASED ON MAPPINGS IN parameter_experience_data
			sql = "SELECT * FROM experience_data"
				+ " WHERE experience_id IN (SELECT experience_id FROM parameter_experience_data"
				+ "		WHERE parameter_id=" + _parameterID + ");";

		// EXECUTE QUERY
			ResultSet experienceRS = dbc.getStatement().executeQuery(sql);

		// LOAD PNG FILE LOCATIONS (APPROX 7000 FILES)
		// (REMOVE FIRST AND LAST FIVE FILES FROM EACH BATCH)
			while (experienceRS.next()) {

				// DETERMINE FULL PATH OF INTERMEDIATE IMAGES
					expTmpPath = "./images/videos-ok/" + experienceRS.getString("tmp_path");

				// ADD FILES TO MASTER FILE LIST
					File expTmpDir = new File(expTmpPath);
					File tmpList[] = expTmpDir.listFiles(new SimpleFileFilter("frame"));
					Arrays.sort(tmpList);
					for (int i=5; i<(tmpList.length-5); i++) {
						fileList.add(tmpList[i].toString());
					}

			}

		// CLOSE DATABASE CONNECTIONS
			experienceRS.close();
			dbc.closeConnection();


/*
		// PRINT OUT ALL FILES IN ARRAYLIST
			for (int i=0; i<fileList.size(); i++) {
			// EXTRACT CURRENT OBJECT
				String tmpFile = (String)fileList.get(i);
				System.out.println(tmpFile);
			}
			System.out.println("\n\nTotal # of Files = " + fileList.size());
*/

		// DETERMINE WORST ACCEPTABLE ERROR FOR STAYING IN THE GENE POOL
		//	maxAcceptableRegions = fileList.size() * maxExcessRegionsPerFrame;


		} catch (Exception e) {
			System.out.println("\n--- GeneticSegmentation Constructor Exception ---\n");
			e.printStackTrace();
		}

	}


	@SuppressWarnings("unused")
	public void initializeGeneticAlgorithm() {
	// METHOD TO INITIALIZE GENE POOL FOR GENETIC ALGORITHM

		// DECLARATIONS
			float colorRadius = (float)0.0;
			int spatialRadius = 0;
			int minRegion = 0;
			int speedUp = speedUpLevel;
			float speedUpFactor = (float)0.0;


		try {

		// INITIALIZE GENE POOL LISTS:
		// 		NEXTINT(NUM) == VALUE BETWEEN 0 AND NUM (BUT NOT INCLUDING NUM)
		//  		I.E. NUM=150 -- 0 TO 149
		// 		NEXTDOUBLE() == VALUE BETWEEN 0 AND 1
			for (int i = 0; i < poolSize; i++) {
				// SET COLOR RADIUS (0 to 25 in increments of 0.5)
					colorRadius = rand.nextInt(51) / (float)2.0;

				// SET SPATIAL RADIUS (0 to 25)
					spatialRadius = rand.nextInt(26);

				// SET MIN REGION (0 TO 1000)
					minRegion = rand.nextInt(1001);

				// SET SPEEDUP FACTOR
					if (speedUpLevel == 2) {
						speedUpFactor = (rand.nextInt(91)+5)/(float)100.0;
					}

				// SETTINGS TO GENE POOL
					pool[i] = new SegParams(colorRadius, spatialRadius, minRegion, speedUp, speedUpFactor);

			} // end for loop

		} catch (Exception e) {
			System.out.println("\n--- GeneticSegmentation.initializeGeneticAlgorithm() Exception ---\n");
			e.printStackTrace();
		}

	} // end initializeGeneticAlgorithm()



	public void executeGeneticAlgorithm() {
	// main loop to perform genetic optimization in order to determine the "best"
	// segmentation settings for a given set of videos


		try {
			
		// NEXT GENERATION OF PARAMETERS
			SegParams nextGen[] = new SegParams[poolSize];

		// MISC SCRATCH VARIABLES FOR CLASSIC METHOD
			double sum = 0.0;
			double factor = 0.0;
			int genes = 0;
			int k;

		// ARRAY TO TRACK PROBABILITY OF BEING CHOSEN FOR NEXT GENERATION (GA CLASSIC)
			double chance[] = new double[poolSize];

		// TEXT FILE FOR RESULTS
			FileWriter fw = null;

		// GET START DATE/TIME
			java.util.Date startTime = new java.util.Date();

		// INITIALIZE OUTPUT FILE
			fw = new FileWriter("GeneticSegmentationResults.txt");

		// INITIALIZE NEXT GENERATION
			for (int i=0; i<poolSize; i++) {
				nextGen[i] = new SegParams();
			}

		// MAIN LOOP
		// LOOP THROUGH SPECIFIED ITERATIONS
			for (int i=0; i<totalGenerations; i++) {
				// RESET TOTAL EXCESS REGIONS (GA CLASSIC)
					totExcessRegions = 0;

				// FIND THE ERROR FOR EACH SET OF GENES WITH LATEST SENSOR READING.
				// THEN SORT THE GENE POOL INTO ASCENDING ORDER BASED ON ERROR.
					if (eliteMode && i>0) {
					// RE-EVALUATE ALL NEW SEG PARAMETERS
						//for (int j=(int)(poolSize*0.2); j<poolSize; j++) {
// if running all images then no need to re-run existing seg parameters, but if running
// just a subset (e.g. 200 of 4000), existing parameters will be tried on additional images
// and final survivors must perform well again and again
						for (int j=0; j<poolSize; j++) {
							analyizeSegmentationSettings(pool[j]);
						}
					} else {
					// RE-EVALUATE ALL SEG PARAMETERS
						for (int j=0; j<poolSize; j++) {
							analyizeSegmentationSettings(pool[j]);
						}
					}

				// SORT GENES BY ERROR (ASCENDING)
					Arrays.sort(pool);

				// DISPLAY TOP 10 FOR CURRENT RUN
					fw.write("Generation # " + i + ":\n");
					for (int j=0; j<10; j++) {
						pool[j].printParameters(fw);
					}
					fw.write("\n\n");
					fw.flush();

				// CROSSFERTILIZE & MUTATE FOR NEXT GENERATION BASED ON ELITE OR CLASSIC SCHEME
					if (eliteMode) {
					// ELITE SCHEME...

					// SET THE GENE POOL FOR THE NEXT GENERATION:
					//  - KEEP BEST 20%
					//  - USE CROSS FERTILIZATION TO GET ANOTHER 79%
					//  - MUTATE REMAINING 1%
						for (int j=0; j<(int)(poolSize*0.2); j++) {
							nextGen[j].copyParameters(pool[j]);
						}
						for (int j=(int)(poolSize*0.2); j<(int)(poolSize*0.99); j++) {
							nextGen[j] = crossover(pool);
						}
						for (int j=(int)(poolSize*0.99); j<poolSize; j++) {
							nextGen[j] = mutation();
						}

					// COPY THE NEW GENE POOL INTO THE GENE POOL FOR THE NEXT GENERATION
						for (int j=0; j<poolSize; j++) {
							pool[j].copyParameters(nextGen[j]);
						}
					} else {
					// CLASSIC SCHEME...

					// CROSSFERTILIZE FOR NEXT GENERATION
					//  - CHANCE OF BEING CHOSEN FOR NEXT GENERATION IS PROPORTIONAL
					//    TO THE RELATIVE LACK OF ERROR
					//  - MUTATION OCCURES WITH A PROBABILITY OF 0.1%
						double tmp = (double)poolSize * totExcessRegions;

						for (int j=0; j<poolSize; j++) {
							chance[j] = (totExcessRegions - pool[j].excessRegions) / tmp;
						}

						for (int j=0; j<poolSize; j++) {
							k = -1;
							sum = 0.0;
							factor = rand.nextDouble();
							while ((sum <= factor) && (k < (poolSize - 1))
								&& (pool[k+1].excessRegions < maxAcceptableRegions)) {

								k++;
								sum += chance[k];
							}

							nextGen[j].copyParameters(pool[k]);
						}

						for (int j=0; j<poolSize; j++) {
							if ((++genes % 1000) == 0) {
								pool[j]=mutation();
							} else {
								pool[j] = crossover(nextGen);
							}
						}
					}  // end classic scheme

			} // end for loop

		// GET STOP DATE/TIME
			java.util.Date stopTime = new java.util.Date();

		// WRITE OUT START/STOP DATE/TIME
			fw.write("\n\nSegmentation Parameter Genetic Optimizer started: " + startTime + " ... finished: "
				+ stopTime);

		// CLOSE RESULTS FILE
			fw.close();

		} catch (Exception e) {
			System.out.println("\n--- GeneticSegmentation.executeGeneticAlgorithm() Exception ---\n");
			e.printStackTrace();
		}

	} // end executeGeneticAlgorithm()



	@SuppressWarnings("unused")
	private SegParams crossover(SegParams pool[]) {
	// GENE CROSSOVER FUNCTION

		// DECLARATIONS
			int choice;
			SegParams answer = new SegParams();


		try {

		// CROSSOVER CODE
			// INITIALIZE ANSWER TO FIRST RANDOMLY CHOSEN SEG PARAMETERS
				choice = rand.nextInt(poolSize);
				answer.copyParameters(pool[choice]);

			// RANDOMLY CHOOSE ANOTHER SET OF SEG PARAMETERS
				choice = rand.nextInt(poolSize);

			// CROSSOVER
				if (rand.nextBoolean()) {
					answer.colorRadius = pool[choice].colorRadius;
				}
				if (rand.nextBoolean()) {
					answer.spatialRadius = pool[choice].spatialRadius;
				}
				if (rand.nextBoolean()) {
					answer.minRegion = pool[choice].minRegion;
				}
				if (speedUpLevel == 2) {
					if (rand.nextBoolean()) {
						answer.speedUpFactor = pool[choice].speedUpFactor;
					}
				}

		} catch (Exception e) {
			System.out.println("\n--- GeneticSegmentation.crossover() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN FUNCTION VALUE
			return(answer);

    } // end crossover()



	@SuppressWarnings("unused")
	private SegParams mutation() {
	// GENE MUTATION ROUTINE

		// DECLARATIONS
			float colorRadius = (float)0.0;
			int spatialRadius = 0;
			int minRegion = 0;
			int speedUp = speedUpLevel;
			float speedUpFactor = (float)0.0;
			SegParams answer = null;


		try {

		// INITIALIZE GENE POOL LISTS:
		// 		NEXTINT(NUM) == VALUE BETWEEN 0 AND NUM (BUT NOT INCLUDING NUM)
		//  		I.E. NUM=150 -- 0 TO 149
		// 		NEXTDOUBLE() == VALUE BETWEEN 0 AND 1

		// SET COLOR RADIUS (0 to 25 in increments of 0.5)
			colorRadius = rand.nextInt(51) / (float)2.0;

		// SET SPATIAL RADIUS (0 to 25)
			spatialRadius = rand.nextInt(26);

		// SET MIN REGION (0 TO 1000)
			minRegion = rand.nextInt(1001);

		// SET SPEEDUP FACTOR
			if (speedUpLevel == 2) {
				speedUpFactor = (rand.nextInt(91)+5)/(float)100.0;
			}

		// SETTINGS TO GENE POOL
			answer = new SegParams(colorRadius, spatialRadius, minRegion, speedUp, speedUpFactor);

		} catch (Exception e) {
			System.out.println("\n--- GeneticSegmentation.mutation() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN FUNCTION VALUE
			return(answer);

	} // end mutation()



	public void analyizeSegmentationSettings(SegParams _sp) {
	// runs a given set of segmentation settings on all of the files in the file list
	// and determines a "score" for the segmentation settings

	// consider analyizing some random subset of the full file list

		int currentRegions = 0;
		int excessRegions = 0;
		BufferedImage tmpImage;


		try {

		// LOOP THROUGH ALL FILES IN ARRAYLIST
			//for (int i=0; i<fileList.size(); i++) {\
			for (int i=0; i<imageSampleSize; i++) {
			// EXTRACT CURRENT FILE PATH
				String tmpFile = fileList.get(rand.nextInt(fileList.size()));

			// LOAD SOURCE IMAGE USING LOADER CLASS
				tmpImage = ImageIO.read(new File(tmpFile));

			// DETERMINE WIDTH AND HEIGHT
				int width = tmpImage.getWidth();
				int height = tmpImage.getHeight();

			// CROP IMAGE
				tmpImage = tmpImage.getSubimage(5, 5, width-5, height-5);

			// RECALCULATE WIDTH AND HEIGHT
				width = tmpImage.getWidth();
				height = tmpImage.getHeight();

			// DETERMINE NUMBER OF PIXELS
				int pixelCount = width * height;

			// INITIALIZE ARRAYS FOR RGB PIXEL VALUES
				int rgbPixels[] = new int[pixelCount];
				tmpImage.getRGB(0, 0, width, height, rgbPixels, 0, width);

			// CREATE MSImageProcessor OBJECT
				MSImageProcessor mySegm = new MSImageProcessor();

			// SET IMAGE
				mySegm.DefineBgImage(rgbPixels, ImageType.COLOR, height, width);

			// SET SetSpeedThreshold FOR HIGH SPEEDUP OPTION
				if (_sp.speedUp == 2) {
					mySegm.SetSpeedThreshold(_sp.speedUpFactor);
				}

			// SEGMENT IMAGE
				if (_sp.speedUp == 0) {
					mySegm.Segment(edisonPortVersion, verboseSegmentation,
						_sp.spatialRadius, _sp.colorRadius, _sp.minRegion, SpeedUpLevel.NO_SPEEDUP);
				} else if (_sp.speedUp == 1) {
					mySegm.Segment(edisonPortVersion, verboseSegmentation,
					_sp.spatialRadius, _sp.colorRadius, _sp.minRegion, SpeedUpLevel.MED_SPEEDUP);
				} else {
					mySegm.Segment(edisonPortVersion, verboseSegmentation,
					_sp.spatialRadius, _sp.colorRadius, _sp.minRegion, SpeedUpLevel.HIGH_SPEEDUP);
				}

			// DETERMINE # REGIONS
				currentRegions = mySegm.GetRegions().length - 3;

			// APPLY PENALTY FOR DROPPING OBJECTS (E.G. NEGATIVE RESULT)
				if (currentRegions < 0) {
					currentRegions = Math.abs(currentRegions * dropPenaltyFactor);
				}

			// DISCARD mySegm
				mySegm = null;

			// DETERMINE IF CURRENT SETTINGS ARE HORRIFIC
				if (currentRegions > maxExcessRegionsPerFrame) {
					excessRegions = maxAcceptableRegions;
					break;
				}

			// UPDATE SCORE FOR CURRENT SEGMENTATION PARAMETERS
				excessRegions += currentRegions;

			// RECOMMEND GARBAGE COLLECTION
				System.gc();

			}

		// UPDATE # EXCESS REGIONS
			_sp.excessRegions = excessRegions;

		// UPDATE TOTAL # EXCESS REGIONS
			totExcessRegions += excessRegions;

		// UPDATE RUN NUMBER
			runNumber++;

		// DISPLAY SCORE
			if (verboseGenerations) {
				System.out.println("\nRun # " + runNumber + " - Total Excess Regions = "
					+ excessRegions);
				_sp.printParameters(null);
			}

		} catch (Exception e) {
			System.out.println("\n--- GeneticSegmentation.analyizeSegmentationSettings() Exception ---\n");
			e.printStackTrace();
		}

	} // end analyizeSegmentationSettings()



	public static void main(String[] args) {

		try {

		// initialize GeneticSegmentation class
			GeneticSegmentation gs = new GeneticSegmentation(11);

		// initialize alogrithm
			gs.initializeGeneticAlgorithm();

		// execute alogrithm
			gs.executeGeneticAlgorithm();

		// SET TO NULL
			gs = null;

		// quit
			System.exit(0);

		} catch (Exception e) {
			System.out.println("\n--- GeneticSegmentation.main() Exception ---\n");
			e.printStackTrace();
		}

	} // end main



} // end GeneticSegmentation class


/*
 * $Log$
 * Revision 1.7  2014/12/19 23:23:32  yoda2
 * Cleanup of misc compiler warnings. Made EDISON GFunction an abstract class.
 *
 * Revision 1.6  2011/04/28 14:55:07  yoda2
 * Addressing Java 1.6 -Xlint warnings.
 *
 * Revision 1.5  2011/04/25 03:52:10  yoda2
 * Fixing compiler warnings for Generics, etc.
 *
 * Revision 1.4  2005/02/17 23:34:01  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.3  2004/03/03 19:07:17  yoda2
 * Made addtions to select version of edision port and to control segmentation messages.
 *
 * Revision 1.2  2004/02/25 22:14:57  yoda2
 * Made changes to allow different speedup options.
 *
 * Revision 1.1  2004/01/21 19:40:30  yoda2
 * Added experimental jEDISON genetic training algorithm for determining "optimal" segmentation parameters for a given set of images.
 *
 */