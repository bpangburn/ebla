REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR EBLA
REM
echo off
java -classpath .;./jars/rowset.jar;./jars/pg73jdbc3.jar;./jars/jmf.jar;./jars/swingset-bin_0.6.0_beta.jar;./jars/utils.jar com.greatmindsworking.EBLA.Interfaces.EBLAGui
