@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Maven wrapper for Windows

@setlocal

set "MAVEN_HOME=C:/Program Files/apache-maven-3.9.9"
set "JAVA_HOME=C:/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot"

if not "%JAVA_HOME%" == "" goto findJavaFromJavaHome
echo ERROR: JAVA_HOME is not set, and 'java' command could not be found in your PATH.
exit /b 1

:findJavaFromJavaHome
set "JAVA_EXE=%JAVA_HOME%/bin/java.exe"
if exist "%JAVA_EXE%" goto execute
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
exit /b 1

:execute
"%JAVA_EXE%" %JAVA_OPTS% -classpath "%MAVEN_HOME%/lib/*" org.apache.maven.cli.MavenCli %*
