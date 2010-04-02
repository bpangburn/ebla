REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
rmdir .\bin /s
mkdir .\bin
javac -source 1.4 -target 1.4 -Xlint:deprecation -Xlint:unchecked -d .\bin -classpath .;.\jars\rowset.jar;.\jars\postgresql-8.4-701.jdbc4.jar; .\src\com\greatmindsworking\utils\*.java .\src\com\greatmindsworking\EDISON\segm\*.java .\src\com\greatmindsworking\EDISON\utils\*.java