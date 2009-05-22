@ECHO OFF

IF "%~1"=="" GOTO usage
IF "%~2"=="" GOTO usage
IF "%~3"=="" GOTO usage

IF ""%3"" == ""build"" GOTO build
IF ""%3"" == ""tag"" GOTO tag
GOTO end

:build
	set VERSION=%~1
	set BRANCHVERSION=%~2
	set TAG=%VERSION:.=_%
	set BRANCH=%BRANCHVERSION:.=_%
	
	call clean ..\cmsc
	call clean ..\..\CMSContainer_Modules
	call clean ..\..\CMSContainer_Portlets
	call clean ..\..\CMSContainer_Templates
	call clean ..\..\CMSContainer_Demo
	
	call build build cmsc ..\..\CMSContainer_Modules ..\..\CMSContainer_Portlets .\..\CMSContainer_Demo\demo.cmscontainer.org

	GOTO end

:tag
	copy /Y %MAVEN_HOME_LOCAL%\repository\finalist\jars\cmsc-*-%VERSION%.* \\intranet\cmscontainer.org\public_html\files\maven\finalist\jars\
	copy /Y %MAVEN_HOME_LOCAL%\repository\finalist\mmbase-modules\cmsc-*-%VERSION%.* \\intranet\cmscontainer.org\public_html\files\maven\finalist\mmbase-modules\
	
	mkdir \\intranet\cmscontainer.org\public_html\files\%VERSION%
	copy /Y %MAVEN_HOME_LOCAL%\repository\finalist\wars\cmsc-*-%VERSION%.* \\intranet\cmscontainer.org\public_html\files\

	svn copy -m "Release %VERSION%" https://scm.mmbase.org/CMSContainer/branches/b%BRANCH% https://scm.mmbase.org/CMSContainer/tags/v%TAG%

	GOTO end

:usage
	echo Usage:  release version branch build|tag
	GOTO end

:end
