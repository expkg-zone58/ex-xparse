@echo off
setLocal EnableDelayedExpansion
REM Path to this script
set PWD=%~dp0

REM run REx to  get class
REM e.g. rex EcmaScript 
set name=%1
echo Running REx at %PWD% name= %name%
java -cp %PWD% REx ebnf\%1.ebnf -java -basex -tree -ll 1 -backtrack -asi 