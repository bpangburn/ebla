REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR jEDISON GENETIC TRAINING ALGORITHM
REM
echo off
java -Xmx256m -classpath .;./jars/postgresql-8.0-310.jdbc3.jar;./jars/utils.jar  com.greatmindsworking.EDISON.utils.GeneticSegmentation
