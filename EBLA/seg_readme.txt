$Id$

==============================================================================
ReadMe file for Java port of the EDISON mean shift image segmentation code.
==============================================================================

Copyright (c) 2002-2003, Brian E. Pangburn & Jonathan P. Ayo
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

This software is a partial port of the EDISON system developed by
Chris M. Christoudias and Bogdan Georgescu at the Robust Image
Understanding Laboratory at Rutgers University
(http://www.caip.rutgers.edu/riul/).

EDISON is available from:
http://www.caip.rutgers.edu/riul/research/code/EDISON/index.html

It is based on the following references:

[1] D. Comanicu, P. Meer: "Mean shift: A robust approach toward feature
    space analysis". IEEE Trans. Pattern Anal. Machine Intell., May 2002.

[2] P. Meer, B. Georgescu: "Edge detection with embedded confidence".
    IEEE Trans. Pattern Anal. Machine Intell., 28, 2001.

[3] C. Christoudias, B. Georgescu, P. Meer: "Synergism in low level vision".
    16th International Conference of Pattern Recognition, Track 1 - Computer
    Vision and Robotics, Quebec City, Canada, August 2001.

The above cited papers are available from:
http://www.caip.rutgers.edu/riul/research/robust.html

==============================================================================

This program is a Java port of the mean shift image segmentation portion
of the EDISON system developed by the Robust Image Understanding Laboratory
at Rutgers University.  It is more of a hack than an attempt at software
engineering.

The port involved the following general steps:
  1. consolidate header files (.h) and class files (.cpp) into Java
     classes (.java)
  2. consolidate existing documentation following JavaDoc conventions
  3. eliminate pointers
  4. tinker with any other data structures and constructs not compatible
     with Java until the code compiled
  5. move the code into the Java package com.greatmindsworking.EDISON.segm
  
We've added an executable class called SegTest that can be used to segment
an image from the command line.

The port was done so that the mean shift image segmentation algorithms
in EDISON could be incorporated into a separate Java software system called
Experience-Based Language Acquisition (EBLA).  EBLA allows a computer to
acquire a simple language of nouns and verbs based on a series of visually
perceived "events".  The segmentation algorithms form the backbone for EBLA's
vision system.  For more information on EBLA, visit
http://www.greatmindsworking.com

This release of jEDISON has been brought into sync with the 4-14-2003 release
of the C++ EDISON code.  The 0.5.0 release was based on teh 4-25-2002 
C++ code.

==============================================================================

This code has been tested using the Java 2 SDK 1.4.2 available from
http://java.sun.com/j2se/1.4.2/index.html

This release of jEDISON is being distributed as a single JAR file
(jEDISON_0.6.0_alpha.jar) containing the source, binaries, and JavaDoc
documentation.

To extract jEDISON, place the JAR file where you would like it installed (e.g.
"c:\temp\jEDISON\" or "/home/<username>/jEDISON/") and issue the command:

jar -xf jEDISON_0.6.0_alpha.jar

Note that except for the demo, SegTest.java, jEDISON is part of the package:
com.greatmindsworking.EDISON.segm so most of the files for jEDISON will
be located in that subdirectory.

To run the SegTest demo for jEDISON:
  1. change to the directory containing this file, SegText.java,
     and my_image.png
  2. type "javac SegTest.java" and follow the instructions provided

To recompile jEDISON:
  1. change to the directory containing this file, SegText.java,
     and my_image.png
  2. type "javac SegTest.java"
  
  
To regenerate the JavaDoc documentation:
  1. change to the installation directory containing this file, SegText.java,
     and my_image.png
  2. type "javadoc -d JavaDoc ./com/greatmindsworking/EDISON/segm/*.java"

==============================================================================

For more information, on EDISON, see the following web sites:
  http://www.caip.rutgers.edu/riul/research/code/EDISON/index.html
  http://www.caip.rutgers.edu/riul/research/robust.html
  http://www.caip.rutgers.edu/riul/
  
For specific questions regarding the Java port, send e-mail to
Brian E. Pangburn (segment@greatmindsworking.com)