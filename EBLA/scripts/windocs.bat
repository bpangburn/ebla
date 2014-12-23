REM $Id$
REM
REM WINDOWS JAVADOC GENERATION SCRIPT FOR EBLA
REM
echo off
cd ..
rmdir .\docs /s
mkdir .\docs
javadoc -d docs -classpath .;.\jars\h2-1.4.184.jar;.\jars\jmf.jar;.\jars\swingset-bin.jar .\src\com\greatmindsworking\utils\*.java .\src\com\greatmindsworking\EBLA\*.java .\src\com\greatmindsworking\EBLA\Interfaces\*.java .\src\com\greatmindsworking\EDISON\segm\*.java