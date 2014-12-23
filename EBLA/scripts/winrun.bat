REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR EBLA
REM
echo off
cd ..
java -classpath .\bin;.\jars\h2-1.4.184.jar;.\jars\jmf.jar;.\jars\swingset-bin.jar com.greatmindsworking.EBLA.Interfaces.EBLAGui
