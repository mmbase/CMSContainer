echo PROJECT = %1
echo JAVA_HOME = %2
set JAVA_HOME=%2
set PATH=%2\bin;%PATH%
IF NOT "%CVSROOT%"=="" goto CVSROOTNOTEMPTY
	set CVSROOT=:pserver:anonymous@cvs.mmbase.org:/var/cvs
:CVSROOTNOTEMPTY