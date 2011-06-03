==============================================================================
ReadMe file for the Experience-Based Language Acquisition (EBLA) system.
==============================================================================

$Id$


==============================================================================
LICENSE
==============================================================================

Copyright (c) 2002-2011, Brian E. Pangburn
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

This code has been tested on both Windows and Linux platforms using
the Java SE 6 available from
 http://www.oracle.com/technetwork/java/javase/downloads/index.html

To run EBLA:
  1. download the binary release file to the desired folder/directory and type:
     jar -xf ebla-bin_CURRENT.jar
  2. type:
     linrun (Linux platforms)
     OR
     winrun (Windows platforms)
     
To download the source code for EBLA:
	(NEED THIS)
	
To recompile EBLA:
  1. download checkout the project from SourceForge
     OR
     download the source release file to the desired folder/directory and type:
     jar -xf ebla-src_CURRENT.jar   
  2. type "lincompile" (Linux platforms)
     OR
     type "wincompile" (Windows platforms)   
     
To generate the JavaDoc documentation for EBLA:
  1. download the JavaDoc release file to the desired folder/directory and type:
     jar -xf ebla-docs_CURRENT.jar
  OR
  2. download the source code (see above) and type:
     lindocs (Linux platforms)
     OR
     windocs (Windows platforms)
     
  


==============================================================================
DATABASE INSTALLATION
==============================================================================

The EBLA software framework requires a PostgreSQL database called ebla_data
for storage of its parameters, dataset, intermediate results, and 
entity-lexeme mappings.

If you are interested in evaluating EBLA, but do not wish to install 
PostgreSQL, a "demo" database with limited functionality is available at
pgserver.greatmindsworking.com.  The "demo" database allows users to 
process several sets of videos, but restricts access to several top-level
tables to read-only.  For instructions on connecting to the demo server,
see the user's manual "manual.pdf" in the EBLA installation directory.

To fully customize EBLA, its parameters, and the video dataset requires access
to a PostgreSQL database server.  Instructions on creating and populating the
ebla_data database are provided below, but this file does not discuss how to
setup a PostgreSQL database server.  For more information, see
http://www.postgresql.org/users-lounge/docs/.  Chapter 1 of the
Administrator's Guide is particularly helpful.  After installing, remember that
TCP/IP connections are disabled in PostgreSQL by default.  If connecting to the
EBLA database via TCP/IP, the "#tcpip_socket = false" should be changed to
"tcpip_socket = true" in the ./data/postgresql.conf file in the PostgreSQL
installation directory.

The database, ebla_data, can be installed in two ways:
  1. table structure only (no sample dataset)
  2. table structure along with parameters, experiences, and attributes for
     evaluating EBLA with the video dataset (see next section).
     
Method 1 (from a command prompt on the database server type):
  1. createdb ebla_data (creates database)
  2. psql ebla_data (starts command line interface for ebla_data)
  3. \i ebla_data.sql (loads SQL to create database table structure)
     *** note that the file, ebla_data.sql, from the ./database/ subdirectory of
         the EBLA installation directory must be on the machine running
         the database server ***
  4. \q (quits psql interface)
  
Method 2 (from a command prompt on the database server type):
  1. createdb ebla_data (creates database)
  2. psql ebla_data (starts command line interface for ebla_data)
  3. \i ebla_data_with_experiences.sql (loads SQL to create database table
     structure and populates parameters, experiences, & attributes)
     *** note that the file, ebla_data_with_experiences.sql, from the
         ./database/ subdirectory of the EBLA installation directory must be on
         the machine running the database server ***
  4. \q (quits psql interface)
         
Until you are comfortable with how EBLA operates and are ready to create your
own experiences, it is recommended that you setup the database WITH the sample
parameters, experiences, & attributes (method 2).

Note that as you use EBLA, a lot of data is temporarily written to and
deleted from the database.  Over time, this can severely impair performance.
To keep the database running well, it is a good idea to run the VACUUMDB
command periodically from the command prompt on the database server. The 
command "vacuumdb -a -f -z" will perform a full vacuum and analysis
on all of the databases on the PostgreSQL server.

To browse and interact with the ebla_data database, there are excellent
GUI interfaces for both Windows and Linux:
  1. PGAdmin (Cross-Platform via wxWindows):  http://www.pgadmin.org
  2. PGAccess (Linux): http://www.pgaccess.org
  
Finally, if you are interested in install PostgreSQL, but only have access
to a Windows machine, EBLA will work with the PostgreSQL 8.0.X "win32"
distribution.  A general overview of this install process follows below:
  1. Go to http://www.postgresql.org/ftp
  2. Click on the "win32" link
  3. Download postgresql-X.Y.Z.zip
  4. Unzip the file downloaded in step #3
  5. From the directory into which you Unzipped the installation files, right-
     click the ".msi" file to install PostgreSQL.
  6. Choose any custom options that you want, but note that the default
     installation options appear to work fine.  Also note that the default
     installation options DO NOT seem allow outside connections meaning that
     EBLA will have to be run locally.
  7. Either add the PostgreSQL "bin" subdirectory to your path or from a
     command prompt, change to the "bin" subdirectory (e.g. "c:\Program Files\
     PostgreSQL\8.0\bin\"
  8. Type "createdb -U postgres ebla_data"
  9. Type "psql -U postgres ebla_data"
 10. Type "\i {path to .sql file to load per Method 1 or 2 above}"
     Note that to load ebla_data.sql from the "tmp" directory on your "d" drive,
     you would enter "\i d:/tmp/ebla_data.sql" NOT "\i d:\tmp\ebla_data.sql"


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
  http://www.caip.rutgers.edu/riul/research/code/EDISON/index.html

For more information on PostgreSQL, see:
  http://www.postgresql.org
  
For more information on Java, see:
  http://java.sun.com
  
For questions or comments regarding EBLA, send e-mail to
Brian E. Pangburn (ebla@greatmindsworking.com)