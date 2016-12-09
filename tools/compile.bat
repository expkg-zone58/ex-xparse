@echo off
REM compile java parser and build jar %1= name of java file
setLocal EnableDelayedExpansion
REM Path to this script
set PWD=%~dp0

REM run REx to  get class
REM e.g. rex EcmaScript 
echo Compile %1 in %cd% ...

mkdir temp
javac -cp "C:\Program Files (x86)\BaseX\BaseX.jar" -d temp %1.java
@echo Main-Class: expkg_zone58.ex_xparse.%1 >Manifest.txt
jar cmf  Manifest.txt %1.jar  -C temp .
rmdir /s /q temp