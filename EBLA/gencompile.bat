REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
rmdir .\bin /s
mkdir .\bin
javac -source 1.6 -target 1.6 -Xlint -d .\bin -classpath .;.\jars\rowset.jar;.\jars\h2-1.3.155.jar; .\src\com\greatmindsworking\utils\*.java .\src\com\greatmindsworking\EDISON\segm\*.java .\src\com\greatmindsworking\EDISON\utils\*.java