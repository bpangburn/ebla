REM $Id$
REM
REM WINDOWS COMPILATION SCRIPT FOR EBLA
REM
REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR EBLA
REM
echo off
del .\com\greatmindsworking\EBLA\*.class
del .\com\greatmindsworking\EBLA\Interfaces\*.class
del .\com\greatmindsworking\EDISON\segm\*.class
javac -source 1.4 -target 1.4 -Xlint:deprecation -Xlint:unchecked -classpath .;./jars/rowset.jar;./jars/pg74.1jdbc3.jar;./jars/jmf.jar;./jars/swingset-bin.jar;./jars/utils.jar com/greatmindsworking/EBLA/Interfaces/EBLAGui.java