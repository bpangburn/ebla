REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
java -Xmx256m -classpath .\bin;.\jars\postgresql-8.4-701.jdbc4.jar com.greatmindsworking.EDISON.utils.GeneticSegmentation
