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
javac -classpath .;./jars/rowset.jar;./jars/pg73jdbc3.jar;./jars/jmf.jar;./jars/swingset-bin_0.6.0_beta.jar;./jars/utils.jar com/greatmindsworking/EBLA/Interfaces/EBLAGui.java