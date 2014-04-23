REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR EBLA
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR EBLA
REM
echo off
cd ..
rmdir .\bin /s
mkdir .\bin
javac -d .\bin -classpath .;.\jars\h2-1.4.177.jar;.\jars\jmf.jar;.\jars\swingset-bin.jar .\src\com\greatmindsworking\utils\*.java .\src\com\greatmindsworking\EDISON\segm\*.java .\src\com\greatmindsworking\EBLA\*.java .\src\com\greatmindsworking\EBLA\Interfaces\*.java
