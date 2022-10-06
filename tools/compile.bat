@echo off
REM compile java parser and build jar %1= name of java file
setLocal EnableDelayedExpansion
REM Path to this script
set PWD=%~dp0

REM run REx to  get class
REM e.g. rex EcmaScript 
echo Compile %1 in %cd% ...

mkdir temp
"%JAVA_JDK%\bin\javac" -Xbootclasspath:"%JAVA_JDK%\jre\lib\rt.jar" -source 1.8 -target 1.8 -cp "%BASEX_HOME%\BaseX.jar" -d temp "%1.java"
@echo Main-Class: expkg_zone58.ex_xparse.%1 >Manifest.txt
"%JAVA_JDK%\bin\jar" cmf  Manifest.txt %1.jar  -C temp .
rmdir /s /q temp