REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
cd ..
java -Xmx256m -classpath .\bin;.\jars\h2-1.4.184.jar com.greatmindsworking.EDISON.utils.GeneticSegmentation
