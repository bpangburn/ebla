REM DELETE ANY EXISTING .class FILES AND RE-COMPILE ALL .java FILES FOR EBLA

echo off

REM $Id$

del .\com\greatmindsworking\EBLA\*.class
del .\com\greatmindsworking\EBLA\Interfaces\*.class
del .\com\greatmindsworking\EDISON\segm\*.class
javac -classpath .;./jars/rowset.jar;./jars/pg73jdbc3.jar;./jars/jmf.jar;./jars/swingUtils.jar;./jars/utils.jar com/greatmindsworking/EBLA/Interfaces/EBLAGui.java

REM $Log$
