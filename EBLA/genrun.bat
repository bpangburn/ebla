REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
java -Xmx256m -classpath .\bin;.\jars\h2-1.3.155.jar com.greatmindsworking.EDISON.utils.GeneticSegmentation
