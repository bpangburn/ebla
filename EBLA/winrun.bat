REM $Id$
REM
REM WINDOWS EXECUTION SCRIPT FOR EBLA
REM
echo off
cd .\bin
java -classpath .;..\jars\rowset.jar;..\jars\postgresql-8.0-310.jdbc3.jar;..\jars\jmf.jar;..\jars\swingset-bin.jar; com.greatmindsworking.EBLA.Interfaces.EBLAGui
