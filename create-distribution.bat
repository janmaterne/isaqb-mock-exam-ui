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


echo Clean
rd /Q/S target


:build
echo Docker Build 
docker build -t janmaterne/onlinetrainer:latest -t janmaterne/onlinetrainer:%TSTAMP% -t janmaterne/onlinetrainer:%APP_VERSION% .

echo Maven Distribution
call mvn package -Pdistribution


if "%1"=="publish" goto publish
goto end


:publish


echo git-tag
git tag %TSTAMP%

echo Publish Git
git push

echo Publish Image
docker push janmaterne/onlinetrainer:latest
docker push janmaterne/onlinetrainer:%TSTAMP%
docker push janmaterne/onlinetrainer:%APP_VERSION%

echo Erstelle GitHub Release mit Assets
rem https://cli.github.com/manual/gh_release_create
rem gh release create %APP_VERSION% --title "iSAQB OnlineTrainer %APP_VERSION% - %TSTAMP%" --notes-file src\changelog-%APP_VERSION%.adoc target\onlinetrainer-linux.tar.gz target\onlinetrainer-macos.tar.gz target\onlinetrainer-win32.zip target\onlinetrainer-%APP_VERSION%.jar
gh release create %APP_VERSION% --title "iSAQB OnlineTrainer %APP_VERSION% - %TSTAMP%" --notes-file src\changelog-%APP_VERSION%.adoc target\onlinetrainer-linux.tar.gz target\onlinetrainer-macos.tar.gz target\onlinetrainer-win32.zip target\onlinetrainer-%APP_VERSION%.jar


:end