@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  GeoSpace startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GEO_SPACE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\opencsv-4.2.jar;%APP_HOME%\lib\postgresql-42.2.16.jre7.jar;%APP_HOME%\lib\commons-beanutils-1.9.3.jar;%APP_HOME%\lib\commons-collections4-4.1.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar
set MODULE_PATH=%APP_HOME%\lib\GeoSpace.jar;%APP_HOME%\lib\arcgis-java-200.2.0.jar;%APP_HOME%\lib\slf4j-nop-2.0.7.jar;%APP_HOME%\lib\javafx-fxml-17.0.8-mac-aarch64.jar;%APP_HOME%\lib\javafx-web-17.0.8-mac-aarch64.jar;%APP_HOME%\lib\javafx-controls-17.0.8-mac-aarch64.jar;%APP_HOME%\lib\javafx-media-17.0.8-mac-aarch64.jar;%APP_HOME%\lib\javafx-graphics-17.0.8-mac-aarch64.jar;%APP_HOME%\lib\gson-2.10.1.jar;%APP_HOME%\lib\httpclient5-cache-5.2.1.jar;%APP_HOME%\lib\httpclient5-5.2.1.jar;%APP_HOME%\lib\commons-codec-1.16.0.jar;%APP_HOME%\lib\slf4j-api-2.0.7.jar;%APP_HOME%\lib\commons-text-1.3.jar;%APP_HOME%\lib\commons-lang3-3.7.jar;%APP_HOME%\lib\javafx-base-17.0.8-mac-aarch64.jar;%APP_HOME%\lib\httpcore5-h2-5.2.jar;%APP_HOME%\lib\httpcore5-5.2.jar

@rem Execute GeoSpace
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GEO_SPACE_OPTS%  -classpath "%CLASSPATH%" --module-path "%MODULE_PATH%" --module com.example.app/com.example.app.App %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GEO_SPACE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GEO_SPACE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
