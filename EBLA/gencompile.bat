REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
del .\com\greatmindsworking\EDISON\utils\*.class
javac -classpath .;./jars/pg74.1jdbc3.jar;./jars/utils.jar  com/greatmindsworking/EDISON/utils/GeneticSegmentation.java