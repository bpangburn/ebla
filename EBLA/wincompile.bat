REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR EBLA
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR EBLA
REM
echo off
rmdir .\bin /s
mkdir .\bin
javac -source 1.6 -target 1.6 -Xlint:deprecation -Xlint:unchecked -d .\bin -classpath .;.\jars\rowset.jar;.\jars\postgresql-8.4-701.jdbc4.jar;.\jars\jmf.jar;.\jars\swingset-bin.jar .\src\com\greatmindsworking\utils\*.java .\src\com\greatmindsworking\EDISON\segm\*.java .\src\com\greatmindsworking\EBLA\*.java .\src\com\greatmindsworking\EBLA\Interfaces\*.java