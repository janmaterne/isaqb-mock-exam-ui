@echo off
call ".version.bat"
rem DATE  : dd.MM.yyyy
rem TSTAMP: HH:MM:ss,SS
rem           YYYY   .    MM     .     DD    _  HH       .    MM
set TSTAMP=%DATE:~-4%.%DATE:~3,2%.%DATE:~0,2%_%TIME:~0,2%.%TIME:~3,2%
rem replace leading spaces by zero by time field (before 10:00): " 8:30" becomes "08:30"
set TSTAMP=%TSTAMP: =0%
echo Nutze TSTAMP=%TSTAMP%
echo APP_VERSION: %APP_VERSION%



set PUBLISH=
set CLEAN=

:arg-loop
IF NOT "%1"=="" (
    IF "%1"=="publish" (
        SET PUBLISH=true
    )
    IF "%1"=="clean" (
        SET CLEAN=true
    )
    IF "%1"=="help" (
        goto help
    )
    SHIFT
    GOTO :arg-loop
)

echo publish: %PUBLISH%
echo clean  : %CLEAN%




if "%CLEAN%"=="true" (
    echo Clean
    rd /Q/S target
)


:build
echo Docker Build 
docker build -t janmaterne/onlinetrainer:latest -t janmaterne/onlinetrainer:%TSTAMP% -t janmaterne/onlinetrainer:%APP_VERSION% .

echo Maven Distribution
call mvn package -Pdistribution


if "%PUBLISH%"=="true" goto publish
goto end


:publish


echo git-tag
git tag %TSTAMP%
git tag %APP_VERSION%

echo Publish Git
git push
git push origin %TSTAMP%
git push origin %APP_VERSION%

echo Publish Image
docker push janmaterne/onlinetrainer:latest
docker push janmaterne/onlinetrainer:%TSTAMP%
docker push janmaterne/onlinetrainer:%APP_VERSION%

echo Erstelle GitHub Release mit Assets
rem https://cli.github.com/manual/gh_release_create
rem gh release create %APP_VERSION% --title "iSAQB OnlineTrainer %APP_VERSION% - %TSTAMP%" --notes-file src\changelog-%APP_VERSION%.adoc target\onlinetrainer-linux.tar.gz target\onlinetrainer-macos.tar.gz target\onlinetrainer-win64.zip target\onlinetrainer-%APP_VERSION%.jar
gh release create %APP_VERSION% --title "iSAQB OnlineTrainer %APP_VERSION% - %TSTAMP%" --notes-file src\changelog-%APP_VERSION%.adoc target\onlinetrainer-linux.tar.gz target\onlinetrainer-macos.tar.gz target\onlinetrainer-win64.zip target\onlinetrainer-%APP_VERSION%.jar
goto end


:help
rem Mostly a note to myself
echo Creates a distribution and optionally publishes that to GitHub and DockerHub.
echo Prerequisite:
echo    Logged into DockerHub (using password or access token)
echo        docker login -u janmaterne
echo    Logged into GitHub
echo        gh auth login
echo Parameter
echo    clean    cleans the 'target' directory so the JDK-downloads don't have
echo             to be reloaded again
echo    publish  publishes git-tags and and distribution to GitHub
echo             also Docker Image to DockerHub
echo    help     print this help and exit
echo .
echo Steps
echo   * optionally (clean) 
echo     ** delete 'target' directory
echo   * build docker image (using multistage dockerfile)
echo   * create maven distribution
echo   * optionally (publish)
echo     ** create git tags
echo     ** push git and tags to GitHub
echo     ** push docker image to DockerHub
echo     ** create and upload release to GitHub
echo .
echo Notes
echo   Before a 'real' (means published) release, change version in pom.xml to final version.
echo   After the release change that version to next SNAPSHOT version and create new src/changelog-*.adoc file.
goto end


:end