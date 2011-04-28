REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
java -Xmx256m -classpath .\bin;.\jars\h2-1.3.154.jar com.greatmindsworking.EDISON.utils.GeneticSegmentation
