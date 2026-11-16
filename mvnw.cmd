@echo off
if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-17" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)
if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

set "MAVEN_HOME=%~dp0.tools\apache-maven-3.9.6"
"%MAVEN_HOME%\bin\mvn.cmd" %*
