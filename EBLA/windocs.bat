REM $Id$
REM
REM WINDOWS JAVADOC GENERATION SCRIPT FOR EBLA
REM
echo off
rmdir .\javadoc /s
mkdir .\javadoc
javadoc -d javadoc -classpath .;.\jars\h2-1.3.155.jar;.\jars\jmf.jar;.\jars\swingset-bin.jar .\src\com\greatmindsworking\utils\*.java .\src\com\greatmindsworking\EBLA\*.java .\src\com\greatmindsworking\EBLA\Interfaces\*.java .\src\com\greatmindsworking\EDISON\segm\*.java