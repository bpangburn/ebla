$Id$

==============================================================================
ReadMe file for Java port of the EDISON mean shift image segmentation code.
==============================================================================

Copyright (c) 2002, Brian E. Pangburn & Jonathan P. Ayo
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

==============================================================================

This code has been tested using the Java 2 SDK 1.4 available from
http://java.sun.com/j2se/1.4/

To compile it:
  1. extract the files (retaining the directory structure)
  2. change to the directory containing this file, SegText.java,
     and my_image.png
  3. type "javac SegTest.java"
  
  
To generate the JavaDoc documentation:
  1. change to the directory containing this file, SegText.java,
     and my_image.png
  2. type "mkdir html"
  3. type "javadoc -d html ./com/greatmindsworking/EDISON/segm/*.java"

  
To run SegTest type (on a single line):
  "java SegTest <source image> <color radius> <spatial radius> 
               <min region> <speedup: 0=none, 1=medium, 2=high>"
                    
  e.g. "java SegTest my_image.png 6.5 7 20 1"
  
Running SegTest with no parameters will display the "usage" message.

==============================================================================

For more information, on EDISON, see the following web sites:
  http://www.caip.rutgers.edu/riul/research/code/EDISON/index.html
  http://www.caip.rutgers.edu/riul/research/robust.html
  http://www.caip.rutgers.edu/riul/
  
For specific questions regarding the Java port, send e-mail to
Brian E. Pangburn (segment@greatmindsworking.com)