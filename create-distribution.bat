@echo off
rem DATE  : dd.MM.yyyy
rem TSTAMP: HH:MM:ss,SS
rem           YYYY   .    MM     .     DD    _  HH       .    MM
set TSTAMP=%DATE:~-4%.%DATE:~3,2%.%DATE:~0,2%_%TIME:~0,2%.%TIME:~3,2%
echo Nutze TSTAMP=%TSTAMP%



echo Clean
rd /Q/S build

:build
echo Docker Build 
docker build -t janmaterne/mockexam:latest -t janmaterne/mockexam:%TSTAMP% .

echo Gradle Distribution
call gradlew distribution



if "%1"=="publish" goto publish
goto end


:publish


echo git-tag
git tag %TSTAMP%

echo Publish Git
git push

echo Publish Image
docker push janmaterne/mockexam:latest 
docker push janmaterne/mockexam:%TSTAMP%

echo Erstelle GitHub Release mit Assets
rem https://cli.github.com/manual/gh_release_create
gh release create %TSTAMP% --title "%TSTAMP%" --notes "Next version" build\mockexam-linux.tar.gz build\mockexam-macos.tar.gz build\mockexam-win32.zip build\libs\mockexam-0.0.1-SNAPSHOT.jar



:end