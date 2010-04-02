REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR EBLA
REM
echo off
java -classpath .\bin;.\jars\rowset.jar;.\jars\postgresql-8.4-701.jdbc4.jar;.\jars\jmf.jar;.\jars\swingset-bin.jar com.greatmindsworking.EBLA.Interfaces.EBLAGui
