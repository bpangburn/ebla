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



import java.awt.*;
import java.util.*;
import java.sql.*;

import com.greatmindsworking.utils.DBConnector;



/**
 * EntityExtractor.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Generates entities for objects and object-object relations based on the data in the
 * frame_analysis_data table of the ebla_data database.
 *<p>
 *<pre>
 * TO-DO:
 *  1. consider adding attributes for R, G, & B color components
 *  2. finish code for dynamic loading of attribute calculations using
 *     forName()
 *  3. restructure attInclusion[] array in extractEntities() method
 *     as an array map or other data structure so that a a statically
 *     sized array (currently 100 items) is no longer needed
 *</pre>
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class EntityExtractor {
	/**
	 * database connection info
	 */
	private DBConnector dbc = null;

	/**
	 * long containing the database record ID for the current experience
	 */
	private long expID = -1;

	/**
	 * long containing the database record ID for the current parameter-
	 * experience combination
	 */
	private long paramExpID = -1;

	/**
	 * long containing the database record ID for the current calculation run
	 */
	private long runID = -1;

	/**
	 * SessionData object containing runtime options
	 */
	private SessionData sd = null;

	/**
	 * ParameterData object containing vision system parameters
	 */
	private ParameterData pd = null;

	/**
	 * ArrayList of all entities for the current experience (each with an ArrayList of attributes)
	 */
	private ArrayList<ArrayList<Attribute>> entityAL = null;

	/**
	 * Minimum standard deviation for entity comparisions
	 */
	private double minSD = 0.40;



	/**
	 * Class constructor that does stuff
	 *
	 * @param _expID		unique id of experience for which entities are being extracted
	 * @param _paramExpID	unique id of current parameter-experience combination
	 * @param _runID		unique id of current calculation run
	 * @param _dbc			connection to EBLA database
	 * @param _sd			calculation session options
	 * @param _pd			vision system parameters
	 * @param _minSD		minimum standard deviation (<1)
	 */
    public EntityExtractor(long _expID, long _paramExpID, long _runID,
    	DBConnector _dbc, SessionData _sd, ParameterData _pd, double _minSD) {

		try {

			// SET EXPERIENCE ID
				expID = _expID;

			// SET PARAMETER-EXPERIENCE ID
				paramExpID = _paramExpID;

			// SET RUN ID
				runID = _runID;

			// SET DATABASE CONNECTION
				dbc = _dbc;

			// SET SESSION
				sd = _sd;

			// SET PARAMETERS
				pd = _pd;

			// SET MINIMUM STANDARD DEVIATION
				minSD = _minSD;

		} catch (Exception e) {
			System.out.println("\n--- EntityExtractor Constructor() Exception ---\n");
			e.printStackTrace();
		}

	} // end EntityExtractor()



	/**
	 * Extracts the object & relation entities from frame_analysis_data
	 */
	public void  extractEntities() {

		// DECLARATIONS
			// DATABASE
				ResultSet fadRS = null;					// RESULTSET FOR frame_analysis_data
				ResultSet aldRS = null;					// RESULTSET FOR attribute_list_data
				String sql = "";						// USED TO BUILD QUERIES AGAINST THE ebla_data DATABASE

			// OBJECT ATTRIBUTES...
				ArrayList<Double> areaAL = null;		// HOLDS AREA ATTRIBUTE VALUES
				//ArrayList colorAL = null;				// HOLDS COLOR ATTRIBUTE VALUES
				//ArrayList redAL = null;				// HOLDS RED COMPONENT ATTRIBUTE VALUES
				//ArrayList greenAL = null;				// HOLDS GREEN COMPONENT ATTRIBUTE VALUES
				//ArrayList blueAL = null;				// HOLDS BLUE COMPONENT ATTRIBUTE VALUES
				ArrayList<Double> grayAL = null;		// HOLDS GRAYSCALE ATTRIBUTE VALUES
				ArrayList<Double> edgesAL = null;		// HOLDS EDGE COUNT ATTRIBUTE VALUES
				ArrayList<Double> relativeCGXAL = null;	// HOLDS RELATIVE CENTROID (X) ATTRIBUTE VALUES
				ArrayList<Double> relativeCGYAL = null;	// HOLDS RELATIVE CENTROID (Y) ATTRIBUTE VALUES

			// RELATION ATTRIBUTES
				ArrayList<Double> contactAL = null;		// 0=CONTACT, 1=NO CONTACT
				ArrayList<Double> xRelationAL = null;	// -1=O1 LEFT OF O2, 0=O1 HAS SAME X-COORD AS O2, 1=O1 RIGHT OF O2
				ArrayList<Double> yRelationAL = null;	// -1=O1 OVER O2, 0=O1 HAS SAME Y-COORD AS O2, 1=O1 BELOW O2
				ArrayList<Double> xDeltaAL = null;		// -1=DELTA X IS DECREASING, 0=DELTA X IS UNCHANGED, 1=DELTA X IS INCREASING
				ArrayList<Double> yDeltaAL = null;		// -1=DELTA Y IS DECREASING, 0=DELTA Y IS UNCHANGED, 1=DELTA Y IS INCREASING
				ArrayList<Double> xTravelAL = null;		// ADD 1 FOR EACH OBJECT MOVING RIGHT AND SUBTRACT 1 FOR EACH OBJECT MOVING LEFT
				ArrayList<Double> yTravelAL = null;		// ADD 1 FOR EACH OBJECT MOVING DOWN AND SUBTRACT 1 FOR EACH OBJECT MOVING UP

			// MISC
				boolean stopLoop = false;				// INDICATES WHEN ALL OBJECTS IN frame_analysis_data HAVE BEEN PROCESSED

				ArrayList<ArrayList<FrameAnalysisData>> objectAL 
					= null;								// ARRAYLIST OF OBJECTS
				int objectIndex = 0;					// COUNTER FOR OBJECTS

				ArrayAnalysis aa = null;				// OBJECT TO CALCULATE STATISTICS FOR ArrayLists

				ArrayList<Attribute> attributeAL = null;// ARRAYLIST OF ATTRIBUTES TO HOLD ATTRIBUTE VALUES FOR EACH ENTITY


		try {
			// INITIALIZE OBJECT ARRAY LIST
				objectAL = new ArrayList<ArrayList<FrameAnalysisData>>();

			// QUERY INCLUDE CODES FROM attribute_list_data
				sql = "SELECT * FROM attribute_list_data;";
				aldRS = dbc.getStatement().executeQuery(sql);
// NEED TO 	NEED TO MOVE ATTRIBUTE INCLUSION CODE INTO SOME SORT OF MAP DATATYPE
// SO THAT ARRAY SIZE ISN'T PRE-DETERMINED
				boolean attInclusion[] = new boolean[100];
				while (aldRS.next()) {
					if (aldRS.getInt("include_code") == 1) {
						attInclusion[aldRS.getInt("attribute_list_id")] = true;
					} else {
						attInclusion[aldRS.getInt("attribute_list_id")] = false;
					}
				}
				aldRS.close();

			// QUERY EACH OBJECT OUT OF frame_analysis_data UNTIL ALL HAVE BEEN PROCESSED
				while (! stopLoop) {
				// INCREMENT OBJECT COUNTER
					objectIndex++;

				// BUILD QUERY
					sql = "SELECT * FROM frame_analysis_data WHERE parameter_experience_id = " + paramExpID
						+ " AND object_number = " + objectIndex
						+ " ORDER BY frame_number ASC;";

				// EXECUTE QUERY
					//if (fadRS!=null) fadRS.close();
					fadRS = dbc.getStatement().executeQuery(sql);

				// INITIALIZE TreeMap TO HOLD FRAME FOR CURRENT OBJECT
					ArrayList<FrameAnalysisData> frameAL = new ArrayList<FrameAnalysisData>();

				// LOOP THROUGH RESULTSET
					while (fadRS.next()) {
						// BUILD OBJECT FROM REST OF CURRENT RECORD IN fadRS
							frameAL.add(new FrameAnalysisData(fadRS));

					} // end while

				// CLOSE RESULTSET
					fadRS.close();

				// IF objMap HAS ENTRIES THEN ADD TO objectAL - OTHERWISE LAST OBJECT HAS BEEN PROCESSED
					if (! frameAL.isEmpty()) {
					// ADD TO objectAL
					// ONLY PROCESS AN OBJECT IF IT EXISTS IN MORE THAN minFrameCount FRAMES
						if (frameAL.size() > pd.getMinFrameCount()) {
							objectAL.add(frameAL);
						}

					} else {
					// STOP LOOP
						stopLoop = true;
					}

				} // end while (! stopLoop)

			// INITIALIZE ENTITY ARRAY LIST
				entityAL = new ArrayList<ArrayList<Attribute>>();

			// CALCULATE OBJECT ENTITY ATTRIBUTES IN OUTER LOOP & RELATION ENTITY ATTRIBUTES IN INNER LOOP
				for (int i=0; i<objectAL.size(); i++) {
					// EXTRACT CURRENT OBJECT MAP
						ArrayList<?> firstObj = objectAL.get(i);

					// INITIALIZE ARRAYS
					// 	- AREA=1, COLOR=2, EDGES=3, RELATIVE CG X=4, RELATIVE CG Y=5
						areaAL = new ArrayList<Double>();
						//colorAL = new ArrayList();
						//redAL = new ArrayList();
						//greenAL = new ArrayList();
						//blueAL = new ArrayList();
						grayAL = new ArrayList<Double>();
						edgesAL = new ArrayList<Double>();
						relativeCGXAL = new ArrayList<Double>();
						relativeCGYAL = new ArrayList<Double>();

					// ITERATE THROUGH FRAMES FOR FIRST OBJECT AND CALCULATE OBJECT ENTITY ATTRIBUTES
						Iterator<?> itt = firstObj.iterator();
						while (itt.hasNext()) {
							// EXTRACT frame_analysis_data RECORD DATA INTO tmpFAD OBJECT
								FrameAnalysisData tmpFAD = (FrameAnalysisData)itt.next();

							// ADD AREA, GRAYSCALE SHADE, & # POLYGON EDGES TO APPROPRIATE ARRAY LISTS
								areaAL.add(new Double(tmpFAD.area));

								grayAL.add(new Double(tmpFAD.grayScale));

								edgesAL.add(new Double(tmpFAD.numPoints));

							// CALCULATE & NORMALIZE RELATIVE CENTROID COORDINATES
							// NEED TO ADJUST BOUNDING RECTANGLE ONE POINT ON ALL SIDES
							//  SINCE IT WAS GROWN BY ONE PIXEL TO DETERMINE CONTACT
								double relativeCGX = ((double)tmpFAD.cg.x-(double)(tmpFAD.boundRect.x+1)) / (tmpFAD.boundRect.width-2);
								double relativeCGY = ((double)tmpFAD.cg.y-(double)(tmpFAD.boundRect.y+1)) / (tmpFAD.boundRect.height-2);

							// ADD NORMALIZED, RELATIVE CENTROID TO APPROPRIATE ARRAY LISTS
								relativeCGXAL.add(new Double(relativeCGX));
								relativeCGYAL.add(new Double(relativeCGY));
						}

					// INITIALIZE ATTRIBUTE ARRAY LIST
						attributeAL = new ArrayList<Attribute>();


					// PERFORM ARRAY ANALYSIS ON EACH ARRAY LIST
						if (attInclusion[1]) {
							aa = new ArrayAnalysis(areaAL);
							attributeAL.add(new Attribute(1, aa.getAvgValue(), aa.getStdDeviation()));
						}

						if (attInclusion[2]) {
							aa = new ArrayAnalysis(grayAL);
							attributeAL.add(new Attribute(2, aa.getAvgValue(), aa.getStdDeviation()));
						}

						// REDUCE COLOR DEPTH IF APPLICABLE
						//	if (pd.getReduceColor()) {
						//		colorAL.add(new Double(Math.abs(reduceColorDepth(tmpFAD.rgb))));
						//	} else {
						//		colorAL.add(new Double(Math.abs(tmpFAD.rgb)));
						//	}

						if (attInclusion[3]) {
							aa = new ArrayAnalysis(edgesAL);
							attributeAL.add(new Attribute(3, aa.getAvgValue(), aa.getStdDeviation()));
						}

						if (attInclusion[4]) {
							aa = new ArrayAnalysis(relativeCGXAL);
							attributeAL.add(new Attribute(4, aa.getAvgValue(), aa.getStdDeviation()));
						}

						if (attInclusion[5]) {
							aa = new ArrayAnalysis(relativeCGYAL);
							attributeAL.add(new Attribute(5, aa.getAvgValue(), aa.getStdDeviation()));
						}

					// ADD ATTRIBUTE ARRAY LIST FOR OBJECT TO ENTITY ARRAY LIST
						entityAL.add(attributeAL);

					// CREATE INNER LOOP TO COMPARE EACH OBJECT TO OTHER OBJECTS
						for (int j=(i+1); j<objectAL.size(); j++) {
							// EXTRACT CURRENT OBJECT MAP
								ArrayList<?> secondObj = objectAL.get(j);

							// INITILIZE CONTACT INDICATOR
								boolean contact = false;

							// INITIALIZE ARRAY LISTS FOR EACH RELATION ATTRIBUTE
							// 	- CONTACT=6, X RELATION=7, Y RELATION=8, DELTA X=9
							//	- DELTA Y=10, TRAVEL X=11, AND TRAVEL Y=12
								contactAL = new ArrayList<Double>();	// 0=CONTACT, 1=NO CONTACT
								xRelationAL = new ArrayList<Double>();	// -1=O1 LEFT OF O2, 0=O1 HAS SAME X-COORD AS O2, 1=O1 RIGHT OF O2
								yRelationAL = new ArrayList<Double>();	// -1=O1 OVER O2, 0=O1 HAS SAME Y-COORD AS O2, 1=O1 BELOW O2
								xDeltaAL = new ArrayList<Double>();		// -1=DELTA X IS DECREASING, 0=DELTA X IS UNCHANGED, 1=DELTA X IS INCREASING
								yDeltaAL = new ArrayList<Double>();		// -1=DELTA Y IS DECREASING, 0=DELTA Y IS UNCHANGED, 1=DELTA Y IS INCREASING
								xTravelAL = new ArrayList<Double>();	// ADD 1 FOR EACH OBJECT MOVING RIGHT AND SUBTRACT 1 FOR EACH OBJECT MOVING LEFT
								yTravelAL = new ArrayList<Double>();	// ADD 1 FOR EACH OBJECT MOVING DOWN AND SUBTRACT 1 FOR EACH OBJECT MOVING UP

							// DETERMINE FIRST & LAST FRAME FOR EACH OBJECT
								int o1FirstFrame = ((FrameAnalysisData)firstObj.get(0)).frameIndex;
								int o2FirstFrame = ((FrameAnalysisData)secondObj.get(0)).frameIndex;
								int o1LastFrame = ((FrameAnalysisData)firstObj.get(firstObj.size()-1)).frameIndex;
								int o2LastFrame = ((FrameAnalysisData)secondObj.get(secondObj.size()-1)).frameIndex;
								int o1Offset = o2FirstFrame - o1FirstFrame;
								int lastFrame = o2LastFrame;
								if (o1LastFrame < lastFrame) {
									lastFrame = o1LastFrame;
								}

							// INITIALIZE PRIOR & CURRENT DISTANCES BETWEEN OBJECTS IN X & Y DIRECTIONS
								double priorXDist = 0;
								double priorYDist = 0;
								double currentXDist = 0;
								double currentYDist = 0;

							// INITIALIZE PRIOR X & Y POSITIONS FOR EACH OBJECT
								double priorXO1 = 0;
								double priorYO1 = 0;
								double priorXO2 = 0;
								double priorYO2 = 0;

							// LOOP UNTIL ONE OBJECT RUNS OUT
							// 	- FIRST FRAME FOR 1ST OBJECT WILL ALWAYS BE <= FIRST FRAME FOR 2ND OBJECT
							// 	- ALL FRAMES ARE CONTIGIOUS FOR AN OBJECT
							// 	- EITHER OBJECT CAN APPEAR IN FEWER FRAMES (I.E. O1 OR O2 CAN DISAPPEAR FIRST)

								for (int k=0; k < (lastFrame-o2FirstFrame); k++) {
									// EXTRACT "CURRENT" FRAMES FOR EACH OBJECT
										FrameAnalysisData tmpFAD1 = (FrameAnalysisData)firstObj.get(k + o1Offset);
										FrameAnalysisData tmpFAD2 = (FrameAnalysisData)secondObj.get(k);

									// UPDATE PRIOR X & Y MIDPOINT POSITIONS
									//	priorXMid = currentXMid;
									//	priorYMid = currentYMid;

									// DETERMINE IF TWO OBJECT ARE IN CONTACT (SET ARRAY LIST & CONTACT INDICATOR)
										if (tmpFAD1.boundRect.intersects(tmpFAD2.boundRect)) {
											contact = true;
											contactAL.add(new Double(0));
										} else {
											contactAL.add(new Double(1));
										}

									// DETERMINE O1'S HORIZONTAL POSITION IN RELATION TO O2
										if (tmpFAD1.cg.x < tmpFAD2.cg.x) {
										// o1 LEFT OF o2
											xRelationAL.add(new Double(-1));
										} else if (tmpFAD1.cg.x > tmpFAD2.cg.x) {
										// o1 RIGHT OF o2
											xRelationAL.add(new Double(1));
										} else {
										// o1 SAME X-COORD AS o2
											xRelationAL.add(new Double(0));
										}

									// DETERMINE O1'S VERTICAL POSITION IN RELATION TO O2
										if (tmpFAD1.cg.y < tmpFAD2.cg.y) {
										// o1 ABOVE o2
											yRelationAL.add(new Double(-1));
										} else if (tmpFAD1.cg.y > tmpFAD2.cg.y) {
										// o1 BELOW o2
											yRelationAL.add(new Double(1));
										} else {
										// o1 SAME Y-COORD AS o2
											yRelationAL.add(new Double(0));
										}

									// DETERMINE ABSOLUTE VALUE OF DISTANCE IN X DIRECTION
										currentXDist = Math.abs(tmpFAD1.cg.x - tmpFAD2.cg.x);

									// DETERMINE ABSOLUTE VALUE OF DISTANCE IN Y DIRECTION
										currentYDist = Math.abs(tmpFAD1.cg.y - tmpFAD2.cg.y);

									// IF PAST FIRST MATCHING FRAME, DETERMINE IF DISTANCE BETWEEN OBJECTS IS INCREASING OR DECREASING
										if (k>0) {
										// X DIRECTION
											if (currentXDist > priorXDist) {
												xDeltaAL.add(new Double(1));
											} else if (currentXDist < priorXDist) {
												xDeltaAL.add(new Double(-1));
											} else {
												xDeltaAL.add(new Double(0));
											}
										// Y DIRECTION
											if (currentYDist > priorYDist) {
												yDeltaAL.add(new Double(1));
											} else if (currentYDist < priorYDist) {
												yDeltaAL.add(new Double(-1));
											} else {
												yDeltaAL.add(new Double(0));
											}
										}

									// UPDATE PRIOR X & Y DISTANCES
										priorXDist = currentXDist;
										priorYDist = currentYDist;


									// IF PAST FIRST MATCHING FRAME, DETERMINE WHICH DIRECTION OBJECTS ARE TRAVELLING
									// 	- EACH OBJECT MOVING LEFT (WEST) ADDS -1
									// 	- EACH OBJECT MOVING RIGHT (EAST) ADDS +1
									//	- EACH OBJECT MOVING UP (NORTH) ADDS -1
									//	- EACH OBJECT MOVING DOWN (SOUTH) ADDS +1
										int xScore = 0;
										int yScore = 0;
										if (k>0) {
										// X DIRECTION - O1
											if (priorXO1 < tmpFAD1.cg.x) {
												xScore++;
											} else if (priorXO1 > tmpFAD1.cg.x) {
												xScore--;
											}
										// X DIRECTION - O2
											if (priorXO2 < tmpFAD2.cg.x) {
												xScore++;
											} else if (priorXO2 > tmpFAD2.cg.x) {
												xScore--;
											}
										// Y DIRECTION - O1
											if (priorYO1 < tmpFAD1.cg.y) {
												yScore++;
											} else if (priorYO1 > tmpFAD1.cg.y) {
												yScore--;
											}
										// Y DIRECTION - O2
											if (priorYO2 < tmpFAD2.cg.y) {
												yScore++;
											} else if (priorYO2 > tmpFAD2.cg.y) {
												yScore--;
											}
										// ADD SCORES TO ARRAY LISTS
											xTravelAL.add(new Double(xScore));
											yTravelAL.add(new Double(yScore));
										}

									// UPDATE PRIOR X & Y POSITIONS
										priorXO1 = tmpFAD1.cg.x;
										priorYO1 = tmpFAD1.cg.y;
										priorXO2 = tmpFAD2.cg.x;
										priorYO2 = tmpFAD2.cg.y;

								} // end for (k=0...

							// IF OBJECT CONTACT THEN PERFORM ARRAY ANALYSIS FOR EACH ATTRIBUTE (IF VALUE EXIST)
								if (contact) {
									// INITIALIZE ATTRIBUTE ARRAY LIST
										attributeAL = new ArrayList<Attribute>();

									// CALCUALTE AVERAGE & STANDARD DEVIATION FOR EACH NON-EMPTY ATTRIBUTE ARRAY LIST
										if (! contactAL.isEmpty() && (attInclusion[6])) {
											aa = new ArrayAnalysis(contactAL);
											attributeAL.add(new Attribute(6, aa.getAvgValue(), aa.getStdDeviation()));
										}
										if (! xRelationAL.isEmpty() && (attInclusion[7])) {
											aa = new ArrayAnalysis(xRelationAL);
											attributeAL.add(new Attribute(7, aa.getAvgValue(), aa.getStdDeviation()));
										}
										if (! yRelationAL.isEmpty() && (attInclusion[8])) {
											aa = new ArrayAnalysis(yRelationAL);
											attributeAL.add(new Attribute(8, aa.getAvgValue(), aa.getStdDeviation()));
										}
										if (! xDeltaAL.isEmpty() && (attInclusion[9])) {
											aa = new ArrayAnalysis(xDeltaAL);
											attributeAL.add(new Attribute(9, aa.getAvgValue(), aa.getStdDeviation()));
										}
										if (! yDeltaAL.isEmpty() && (attInclusion[10])) {
											aa = new ArrayAnalysis(yDeltaAL);
											attributeAL.add(new Attribute(10, aa.getAvgValue(), aa.getStdDeviation()));
										}
										if (! xTravelAL.isEmpty() && (attInclusion[11])) {
											aa = new ArrayAnalysis(xTravelAL);
											attributeAL.add(new Attribute(11, aa.getAvgValue(), aa.getStdDeviation()));
										}
										if (! yTravelAL.isEmpty() && (attInclusion[12])) {
											aa = new ArrayAnalysis(yTravelAL);
											attributeAL.add(new Attribute(12, aa.getAvgValue(), aa.getStdDeviation()));
										}

									// ADD RELATION ENTITY IF IT HAS SOME ATTRIBUTES
										if (! attributeAL.isEmpty()) {
											entityAL.add(attributeAL);
										}
								} // end if (contact)

						} // end for (j=...)

				} // end for (i=...)

		} catch (Exception e) {
			System.out.println("\n--- EntityExtractor.extractEntities() Exception ---\n");
			e.printStackTrace();
		} finally {
			// CLOSE OPEN FILES/RESULTSETS
			try {
				if (aldRS!=null) aldRS.close();
				if (fadRS!=null) fadRS.close();

			} catch (Exception misc) {
				System.out.println("\n--- EntityExtractor.extractEntities() Exception ---\n");
				misc.printStackTrace();
			}
		}			

	} // end extractEntities()



	/**
	 * Reduces RGB color depth from 16,777,216 to 27
	 *
	 * @param _oldRGB			int containing original RGB color
	 *
	 * @return one of twenty-seven reduced RGB values
	 */
	protected int reduceColorDepth(int _oldRGB) {

		// DECLARATIONS
			int oldRed = 0;						// RED COMPONENT OF ORIGINAL COLOR
			int oldGreen = 0;					// GREEN COMPONENT OF ORIGINAL COLOR
			int oldBlue = 0;					// BLUE COMPONENT OF ORIGINAL COLOR
			int newRed = 0;						// RED COMPONENT OF REDUCED COLOR
			int newGreen = 0;					// GREEN COMPONENT OF REDUCED COLOR
			int newBlue = 0;					// BLUE COMPONENT OF REDUCED COLOR
			int newRGB = 0;						// REDUCED RGB VALUE AS SINGLE INTEGER
			Color tmpColor = null;				// COLOR OBJECT USED IN EXTRACTING RGB COMPONENTS


		try {

			// CONSTRUCT COLOR OBJECT FOR ORIGINAL RGB INTEGER VALUE
				tmpColor = new Color(_oldRGB);

			// GET COMPONENT RGB VALUE OF ORIGINAL COLOR
				oldRed = tmpColor.getRed();
				oldGreen = tmpColor.getGreen();
				oldBlue = tmpColor.getBlue();

			// CONVERT EACH COMPONENT TO 0, 128, OR 255
				newRed = Math.round(127.5f * (oldRed / 85));
				newGreen = Math.round(127.5f * (oldGreen / 85));
				newBlue = Math.round(127.5f * (oldBlue / 85));

			// CONSTRUCT COLOR OBJECT FOR REDUCED RGB COMPONENTS
				tmpColor = new Color(newRed, newGreen, newBlue);

			// EXTRACT SINGLE INTEGER CONTAINING REDUCED RGB INTEGER VALUE
				newRGB = tmpColor.getRGB();

		} catch (Exception e) {
			System.out.println("\n--- FrameProcessor.reduceColorDepth() Exception ---\n");
			e.printStackTrace();
		}

		// RETURN REDUCED RGB VALUE
			return newRGB;

	} // end reduceColorDepth()



	/**
	 * Public method to add entities and attributes to the database
	 * <pre>
	 * Iterate through entity array list and for each entity:
	 * 	1. check to see if any matching entities exist in database (+/- one SD (min 5%)
	 *     for all attribute averages)
	 * 	2. if a match is found, bump occurance_count, update averages in attribute_value_data,
	 *     and write entity_id to experience_entity_data
	 * 	3. if no match is found, generate new entity in entity_data and write attributes to
	 *     attribute_value_data
	 * 	4. write entity_id to experience_entity_data
	 *</pre>
	 *
	 * @return boolean indicating success (true) or failure (false)
	 */
	@SuppressWarnings("resource")
	public boolean writeToDB() {

		// DECLARATIONS
			ResultSet entityRS = null;
			ResultSet attRS = null;

			long entityID = 0;
			int occuranceCount = 0;

			String sql = "";

			double max = 0.0;
			double min = 0.0;

			double score = 0.0;

			double tmpSD = 0.0;


		try {

			// ITERATE THROUGH ENTITY ARRAY LIST AND FOR EACH ENTITY:
			// 	1. check to see if any matching entities exist in database (+/- one SD (min 5%)
			//     for all attribute averages)
			// 	2. if a match is found, bump occurance_count, update averages in attribute_value_data,
			//     and write entity_id to experience_entity_data
			// 	3. if no match is found, generate new entity in entity_data and write attributes to
			//     attribute_value_data
			// 	4. write entity_id to experience_entity_data
				Iterator<ArrayList<Attribute>> itt = entityAL.iterator();
				while (itt.hasNext()) {

					// DETERMINE IF A SIMILAR ENTITY HAS ALREADY BEEN DISCOVERED
					//  - INITIALLY MUST MATCH ALL ATTRIBUTES TO WITHIN A SINGLE STANDARD DEVIATION OF CURRENT AVERAGE

					// EXTRACT ATTRIBUTE ARRAY LIST
						ArrayList<?> attributeAL = itt.next();

					// INITIALIZE QUERY

					// LOOP THROUGH ATTRIBUTES TO BUILD QUERY
						for (int i=0; i<attributeAL.size(); i++) {

							// EXTRACT ATTRIBUTE VALUES
								Attribute tmpAtt = (Attribute)attributeAL.get(i);

							// SET MINIMUM STD DEV (FORCE 2.5% FOR ANY VERB ATTRIBUTES)
								if (tmpAtt.attributeListID < 6) {
									tmpSD = minSD;
								} else {
									tmpSD = 0.05;
								}

							// CALC RANGE
								if (sd.getFixedStdDev()) {
									if (tmpAtt.avgValue >= 0) {
									// NON-NEGATIVE
										max = tmpAtt.avgValue * (1.0 + tmpSD);
										min = tmpAtt.avgValue * (1.0 - tmpSD);
									} else {
									// NEGATIVE
										max = tmpAtt.avgValue * (1.0 - tmpSD);
										min = tmpAtt.avgValue * (1.0 + tmpSD);
									}

								} else {
								// CALCULATE AVERAGE OF CURRENT ATTRIBUTE +/- ONE STANDARD DEVIATION
									min = tmpAtt.avgValue - tmpAtt.stdDeviation;
									max = tmpAtt.avgValue + tmpAtt.stdDeviation;

								// MAKE SURE ADJUSTMENT WAS AT LEAST MINIMUM STD DEV
									if (tmpAtt.avgValue >= 0) {
									// NON-NEGATIVE
										if (max < (tmpAtt.avgValue * (1.0 + tmpSD))) {
											max = tmpAtt.avgValue * (1.0 + tmpSD);
										}
										if (min > (tmpAtt.avgValue * (1.0 - tmpSD))) {
											min = tmpAtt.avgValue * (1.0 - tmpSD);
										}
									} else {
									// NEGATIVE
										if (max < (tmpAtt.avgValue * (1.0 - tmpSD))) {
											max = tmpAtt.avgValue * (1.0 - tmpSD);
										}
										if (min > (tmpAtt.avgValue * (1.0 + tmpSD))) {
											min = tmpAtt.avgValue * (1.0 + tmpSD);
										}
									}
								}

							// ADD "INTERSECT" IF PROCESSING MULTIPLE ATTRIBUTES
								if (i==0) {
									sql = "SELECT * FROM entity_data WHERE entity_id IN (";
								} else {
									sql = sql + " INTERSECT ";
								}

							// ADD CURRENT ATTRIBUTE TO QUERY
							// HAD TO CAST min & max TO POSTGRES float8 TYPE USING DOUBLE COLONS
							// COULD ALSO USE "CAST(val AS float8)
								sql = sql + "SELECT entity_id FROM attribute_value_data "
									+ " WHERE run_id = " + runID
									+ " AND attribute_list_id = " + tmpAtt.attributeListID
									+ " AND avg_value BETWEEN " + min + "::float8 AND " + max + "::float8";

						} // end for

					// ADD SEMI-COLON TO QUERY
						sql = sql + ");";

					// EXECUTE QUERY
						entityRS = dbc.getStatement().executeQuery(sql);

					// CREATE NEW AttributeScore ArrayList
						ArrayList<AttributeScore> asArrayList = new ArrayList<AttributeScore>();

					// IF RECORDS EXIST, EXTRACT ATTRIBUTE VALUE FOR EACH AND COMPARE TO CURRENT ENTITY
						while (entityRS.next()) {
							// RESET SCORE
								score = 0;

							// EXTRACT ENTITY ID
								entityID = entityRS.getLong("entity_id");

							// EXTRACT OCCURANCE COUNT
								occuranceCount = entityRS.getInt("occurance_count");

							// BUILD SCORE...
							// CREATE 2ND ITERATOR AND LOOP THROUGH ATTRIBUTES, EXTRACTING EXISTING ATTRIBUTE VALUES
							// FOR CURRENT ENTITY
								Iterator<?> itt2 = attributeAL.iterator();
								while (itt2.hasNext()) {
									// EXTRACT ATTRIBUTE VALUES
										Attribute tmpAtt = (Attribute)itt2.next();

									// QUERY EXISTING ATTRIBUTE VALUES
										sql = "SELECT * FROM attribute_value_data WHERE entity_id=" + entityID
											+ " AND attribute_list_id=" + tmpAtt.attributeListID + ";";

										attRS = dbc.getStatement().executeQuery(sql);
										attRS.next();
										double oldAvg = Math.abs(attRS.getDouble("avg_value"));
										attRS.close();

										double newAvg = Math.abs(tmpAtt.avgValue);

										if (newAvg > oldAvg) {
											//score += (newAvg-oldAvg)/newAvg;
											score += (newAvg-oldAvg)/oldAvg;
										} else {
											score += (oldAvg-newAvg)/oldAvg;
										}

								}

							// ADD SCORE TO asArrayList
								// CREATE MATCH SCORE OBJECT
									AttributeScore as = new AttributeScore();

								// SET ENTITY ID
									as.entityID = entityID;

								// SET OCCURANCE COUNT
									as.occuranceCount = occuranceCount;

								// SET SCORE
									as.score = score;

								// ADD TO ARRAY LIST
									asArrayList.add(as);

						}

					// CLOSE ENTITY RESULTSET
						entityRS.close();

					// IF MATCHES WERE FOUND, SORT AND EXTRACT ENTITY - OTHERWISE ADD NEW ENTITY
						if (! asArrayList.isEmpty()) {

							// SORT asArrayList IN ASCENDING ORDER BY SCORE
								Collections.sort(asArrayList);

							// GET OBJECT WITH LOWEST SCORE
								AttributeScore as = asArrayList.get(0);

							// EXTRACT ENTITY ID
								entityID = as.entityID;

							// EXTRACT OCCURANCE COUNT AND INCREMENT
								occuranceCount = as.occuranceCount;
								occuranceCount++;

							// UPDATE OCCURANCE COUNT IN ENTITY DATA
								sql = "UPDATE entity_data SET occurance_count = " + occuranceCount
									+ " WHERE entity_id = " + entityID + ";";

							// EXECUTE QUERY
								dbc.getStatement().executeUpdate(sql);

							// CREATE 2ND ITERATOR AND LOOP THROUGH ATTRIBUTES, UPDATING AVERAGE VALUES FOR EACH
								Iterator<?> itt2 = attributeAL.iterator();
								while (itt2.hasNext()) {
									// EXTRACT ATTRIBUTE VALUES
										Attribute tmpAtt = (Attribute)itt2.next();

									// QUERY EXISTING ATTRIBUTE VALUES
										sql = "SELECT * FROM attribute_value_data WHERE entity_id=" + entityID
											+ " AND attribute_list_id=" + tmpAtt.attributeListID + ";";

										attRS = dbc.getStatement().executeQuery(sql);
										attRS.next();
										double oldAvg = attRS.getDouble("avg_value");
										double oldSD = attRS.getDouble("std_deviation");
										attRS.close();

										double newAvg = (tmpAtt.avgValue * (1.0/occuranceCount)) + (oldAvg * ((double)(occuranceCount-1)/(double)occuranceCount));
										double newSD = (tmpAtt.stdDeviation * (1.0/occuranceCount)) + (oldSD * ((double)(occuranceCount-1)/(double)occuranceCount));

									// WRITE UPDATED ATTRIBUTE VALUES TO DATABASE
										sql = "UPDATE attribute_value_data SET avg_value=" + newAvg
											+ ", std_deviation=" + newSD
											+ " WHERE entity_id=" + entityID + " AND attribute_list_id=" + tmpAtt.attributeListID + ";";

										dbc.getStatement().executeUpdate(sql);

								}

						} else {
						// ADD NEW ENTITY AND WRITE ATTRIBUTE VALUES TO DATABASE
							// GET NEXT entity_id
								sql = "SELECT nextval('entity_data_seq') AS next_index;";
								entityRS = dbc.getStatement().executeQuery(sql);
								entityRS.next();
								entityID = entityRS.getLong("next_index");
								entityRS.close();

							// ADD ENTITY DATA RECORD
								sql = "INSERT INTO entity_data (entity_id, run_id)"
									+ " VALUES (" + entityID + "," + runID + ");";
								dbc.getStatement().executeUpdate(sql);

							// CREATE 2ND ITERATOR AND LOOP THROUGH ATTRIBUTES
								Iterator<?> itt2 = attributeAL.iterator();
								while (itt2.hasNext()) {
									// EXTRACT ATTRIBUTE VALUES
										Attribute tmpAtt = (Attribute)itt2.next();

									// WRITE ATTRIBUTE VALUES TO DATABASE
										sql = "INSERT INTO attribute_value_data (attribute_list_id, run_id, entity_id, avg_value, std_deviation)"
											+ " VALUES (" + tmpAtt.attributeListID + ", " + runID + "," + entityID + ", " + tmpAtt.avgValue
											+ ", " + tmpAtt.stdDeviation + ");";
										dbc.getStatement().executeUpdate(sql);
								}
						} // end if (entityExists)...

					// ADD EXPERIENCE-ENTITY RECORD
						sql = "INSERT INTO experience_entity_data (experience_id, run_id, entity_id, resolution_code) VALUES ("
							+ expID + ", " + runID + ", " + entityID + ", 0);";
						dbc.getStatement().executeUpdate(sql);

				} // end while

		} catch (Exception e) {
			System.out.println("\n--- EntityExtractor.writeToDB() Exception ---\n");
			e.printStackTrace();
			return false;
		} finally {
			// CLOSE OPEN FILES/RESULTSETS
			try {
				if (entityRS!=null) entityRS.close();
				if (attRS!=null) attRS.close();

			} catch (Exception misc) {
				System.out.println("\n--- EntityExtractor.writeToDB() Exception ---\n");
				misc.printStackTrace();
			}			
			
		}

		// RETURN RESULT
			return true;

	} // end writeToDB()

} // end EntityExtractor class



/*
 * $Log$
 * Revision 1.27  2011/04/25 03:52:10  yoda2
 * Fixing compiler warnings for Generics, etc.
 *
 * Revision 1.26  2005/02/17 23:33:15  yoda2
 * JavaDoc fixes & retooling for SwingSet 1.0RC compatibility.
 *
 * Revision 1.25  2004/08/02 18:22:52  yoda2
 * Changed attribute value averaging such that existing average is always the denominator when updating score variable.
 *
 * Revision 1.24  2004/02/25 21:58:10  yoda2
 * Updated copyright notice.
 *
 * Revision 1.23  2003/12/26 20:25:52  yoda2
 * Misc fixes required for renaming of Params.java to ParameterData.java and Session.java to SessionData.java.
 *
 * Revision 1.22  2003/08/08 13:29:14  yoda2
 * Rewritten for use with new database structure (e.g. session_data & parameter_experience_data).
 * Also removed main() method for standalone testing - no longer applicable.
 *
 * Revision 1.21  2002/12/11 22:50:27  yoda2
 * Initial migration to SourceForge.
 *
 * Revision 1.20  2002/10/27 23:04:50  bpangburn
 * Finished JavaDoc.
 *
 * Revision 1.19  2002/10/02 20:50:52  bpangburn
 * Added parameters for min SD step size and locking min SD value.
 *
 * Revision 1.18  2002/09/05 18:13:05  bpangburn
 * Added code for testing EBLA using multiple variances over multiple runs, writing results to semi-colon separated files.
 *
 * Revision 1.17  2002/08/23 16:30:07  bpangburn
 * Added code to score entities based on closest attribute values when there are multiple matches.
 *
 * Revision 1.16  2002/08/21 14:33:16  bpangburn
 * Added fields for red, green, & blue pixel values as well as a grayscale value.
 *
 * Revision 1.15  2002/08/20 22:14:21  bpangburn
 * Added code to update attribute averages and std. dev. as new instances of an existing entity are encountered.
 *
 * Revision 1.14  2002/08/15 17:26:24  bpangburn
 * Added code to insure that color and area attributes are positive and to properly handle BETWEEN query for negative attribute averages.
 *
 * Revision 1.13  2002/08/07 13:42:16  bpangburn
 * Added boolean flag to determine if the color attribute should be included in entity processing and added a minimum frame count that determines how many consecutive frames an object must appear in to be considered an entity.
 *
 * Revision 1.12  2002/08/02 13:37:59  bpangburn
 * Added code to exclude any objects in less then six frames.
 *
 * Revision 1.11  2002/05/14 22:39:52  bpangburn
 * Debugging lexical resolution code.
 *
 * Revision 1.10  2002/05/08 22:50:10  bpangburn
 * Fixed typo.
 *
 * Revision 1.9  2002/05/03 22:16:26  bpangburn
 * Cleaned up code and documentation.
 *
 * Revision 1.8  2002/04/24 22:32:31  bpangburn
 * Added code to compare current entity with entities in database before creating new entity.  Added code for relation attributes to track X and Y motion.
 *
 * Revision 1.7  2002/04/23 22:49:22  bpangburn
 * Debugging - found that x coordinate of centroid was being read from database than area.
 *
 * Revision 1.6  2002/04/22 21:07:27  bpangburn
 * Grew bounding rectangle by one pixel on all sides to improve calculation of overlap/contact.
 *
 * Revision 1.5  2002/04/19 22:52:20  bpangburn
 * Debugged EntityExtractor and added writeToDB method.
 *
 * Revision 1.4  2002/04/17 23:05:07  bpangburn
 * Completed code to detect entities (and their attributes) from frame_analysis_data.  Still need to flesh out writeToDB() in EntityExtractor class.
 *
 * Revision 1.3  2002/04/14 01:38:46  bpangburn
 * Worked on code to analyze object entities.
 *
 * Revision 1.2  2002/04/13 03:11:49  bpangburn
 * Worked on entity creation code for objects.
 *
 * Revision 1.1  2002/04/08 22:33:18  bpangburn
 * Created skeleton of class to perform extraction of object & relation entity information from frame_analysis_data table.
 *
 */