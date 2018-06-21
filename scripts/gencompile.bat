REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
cd ..
rmdir .\bin /s
mkdir .\bin
javac -d .\bin -classpath .;.\jars\rowset.jar;.\jars\h2-1.4.184.jar; .\src\com\greatmindsworking\utils\*.java .\src\com\greatmindsworking\EDISON\segm\*.java .\src\com\greatmindsworking\EDISON\utils\*.java