REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
rmdir .\bin /s
mkdir .\bin
javac -source 1.4 -target 1.4 -Xlint:deprecation -Xlint:unchecked -d .\bin -classpath .;.\jars\rowset.jar;.\jars\pg74.1jdbc3.jar;.\jars\utils.jar; .\src\com\greatmindsworking\EDISON\segm\*.java .\src\com\greatmindsworking\EDISON\utils\*.java