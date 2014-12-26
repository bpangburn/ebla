==============================================================================
ReadMe file for the Experience-Based Language Acquisition (EBLA) system.
==============================================================================

$Id$


==============================================================================
LICENSE
==============================================================================

Copyright (c) 2002-2014, Brian E. Pangburn
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.  Redistributions in binary
form must reproduce the above copyright notice, this list of conditions and
the following disclaimer in the documentation and/or other materials
provided with the distribution.  The names of its contributors may not be
used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.


==============================================================================
DESCRIPTION
==============================================================================

EBLA is an open computational framework for visual perception and
grounded language acquisition.  EBLA can watch a series of short
videos and acquire a simple language of nouns and verbs corresponding
to the objects and object-object relations in those videos.  Upon
acquiring this protolanguage, EBLA can perform basic scene analysis
to generate descriptions of novel videos.

The general architecture of EBLA is comprised of three stages: vision
processing, entity extraction, and lexical resolution.  In the vision
processing stage, EBLA processes the individual frames in short videos,
using a variation of the mean shift analysis image segmentation algorithm
to identify and store information about significant objects.  In the entity
extraction stage, EBLA abstracts information about the significant objects
in each video and the relationships among those objects into internal
representations called entities.  Finally, in the lexical acquisition
stage, EBLA extracts the individual lexemes (words) from simple
descriptions of each video and attempts to generate entity-lexeme mappings
using an inference technique called cross-situational learning.  EBLA is
not primed with a base lexicon, so it faces the task of bootstrapping its
lexicon from scratch.

While there have been several systems capable of learning object or event
labels for videos, EBLA is the first known system to acquire both nouns and
verbs using a grounded computer vision system.

EBLA was developed as part of Brian E. Pangburn's dissertation research
in the Department of Computer Science at Louisiana State University.

The full dissertation along with other information on EBLA is available from
http://www.greatmindsworking.com

Much of the vision system is based on a partial Java port of the EDISON
image processing system developed by Chris M. Christoudias and Bogdan
Georgescu at the Robust Image Understanding Laboratory at Rutgers University
(http://coewww.rutgers.edu/riul/).  See the "seg_readme.txt" file for
more information.

The graphical user interface (GUI) for EBLA was developed in part by
Prasanth R. Pasala.


==============================================================================
CODE EXECUTION, DOCUMENTATION, & COMPILATION
==============================================================================

This code has been tested using the Java SE 7

This release of EBLA is being distributed as a single JAR file
(EBLA_1.0.jar) containing the source, binaries, and JavaDoc
documentation.

Note that this JAR file only contains the AVI files for the "Animation"
Experience Set. Unless you download the full video dataset (see VIDEO DATASET
below), you will only be able to run EBLA Sessions for the 1st record on the
Parameter Screen.

There is a changelog.txt file, which contains changes for EBLA.

To extract EBLA, place the JAR file where you would like it installed (e.g.
"c:\temp\" or "/home/<username>/") and issue the command:

jar -xf EBLA_1.0.jar

To run EBLA:
  1. navigate to the "scripts" folder/director (e.g. "cd ./ebla/scripts/")
  2. type "winrun.bat" (Windows) or "sh linrun" (Linux)

To recompile EBLA:
  1. navigate to the "scripts" folder/director (e.g. "cd ./ebla/scripts/")
  2. type "wincompile.bat" (Windows) or "sh lincompile" (Linux)
     
To generate the JavaDoc documentation for EBLA:
  1. navigate to the "scripts" folder/director (e.g. "cd ./ebla/scripts/")
  2. type "windocs.bat" (Windows) or "sh lindocs" (Linux)


==============================================================================
DATABASE INSTALLATION
==============================================================================

The EBLA software framework utilizes an embedded H2 database called ebla
for storage of its parameters, dataset, intermediate results, and 
entity-lexeme mappings.


==============================================================================
VIDEO DATASET
==============================================================================

EBLA has been evaluated on a small test set of animations created with 
Macromedia Flash and a much larger test set of real videos.  In both cases,
the files were delivered to EBLA as AVI files.
     
The set of animations are contained in ./experiences/ subdirectory of
the installation directory.  The full set of real videos used to
evaluate EBLA is just over 326MB (compressed JAR file).  It is available 
from the following link:
http://www.greatmindsworking.com/downloads/ebla_experiences.jar

To install the full test set, simply download the release file
ebla_experiences.jar to the EBLA installation directory and type:
  jar -xf ebla_experiences.jar
  
The full set of videos will be extracted to the  ./experiences/ subdirectory.    


==============================================================================
EBLA INTERFACE
==============================================================================

Starting with version 0.6.0-alpha, EBLA is run via a graphical user interface
(GUI) developed using Java Swing.  For more information see the user's manual,
"manual.pdf" in the EBLA installation directory.


==============================================================================
RESULTS
==============================================================================

While it is possible to analyze results from EBLA on a run-by-run basis using
the information in the ebla_data database (e.g. the entity_lexeme_data table),
it is generally easier to use the three text files of results generated by
EBLA.

The three text files are delimited using semicolons and are described below:

1. performance.ssv (one record for each pass through the current set of
    experiences):

    loopCount - counter of how many times the entire set of experiences
      has been processed
      
    stdDev - current minimum standard deviation for entity comparisons
    
    runNumber - counter of how many times the current set of experiences
      has been processed for the current minimum standard deviation
      
    experienceIndex - total number of experiences processed during current run
    
    totalSec - seconds elapsed during current run
    
    totalLex - total number of lexemes processed
    
    totalUMLex - total number of lexemes NOT mapped to an entity by EBLA
    
    totalEnt - total number of entities processed
    
    totalUMEnt - total number of entities NOT mapped to a lexeme by EBLA


2. mappings.ssv (one record for each mapped entity in each experience):

    loopCount - counter of how many times the entire set of experiences
      has been processed
      
    experienceIndex - order that experience was processed during current run
    
    resolutionCount - number of experiences elapsed before entity for current
      experience was resolved

      
3. descriptions.ssv (one row for every experience for which a description
    was generated):
    
    loopCount - counter of how many times the entire set of experiences
      has been processed
      
    stdDev - current minimum standard deviation for entity comparisons
    
    experienceIndex - order that experience was processed during current run
    
    generatedDescription - description generated by EBLA for current 
      experience (comma-separated)
      
    numCorrect - number of correctly generated lexemes for current experience
    
    numWrong - number of incorrectly generated lexemes for current experience
    
    numUnknown - number of unknown lexemes for current experience
    
    origDescription - original description for current experience (from
      experience_data table)
      
Note that descriptions.ssv is only generated when EBLA is run in description
mode (e.g. generate_desc_code = 1).


==============================================================================
MISC
==============================================================================

For more information on EBLA, see:
  http://www.greatmindsworking.com
  
For more information on EDISON, see:
  http://coewww.rutgers.edu/riul/research/code/EDISON/

For more information on H2, see:
  http://www.h2database.com
  
For more information on Java, see:
  http://www.java.com
  
For questions or comments regarding EBLA, send e-mail to
Brian E. Pangburn (ebla@greatmindsworking.com)