@echo off
call ".version.bat"
rem DATE  : dd.MM.yyyy
rem TSTAMP: HH:MM:ss,SS
rem           YYYY   .    MM     .     DD    _  HH       .    MM
set TSTAMP=%DATE:~-4%.%DATE:~3,2%.%DATE:~0,2%_%TIME:~0,2%.%TIME:~3,2%
echo Nutze TSTAMP=%TSTAMP%
echo APP_VERSION: %APP_VERSION%



echo Clean
rd /Q/S build

:build
echo Docker Build 
docker build -t janmaterne/mockexam:latest -t janmaterne/mockexam:%TSTAMP% -t janmaterne/mockexam:%APP_VERSION% .

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
docker push janmaterne/mockexam:%APP_VERSION%

echo Erstelle GitHub Release mit Assets
rem https://cli.github.com/manual/gh_release_create
gh release create %APP_VERSION% --title "iSAQB MockUI %APP_VERSION% - %TSTAMP%" --notes "Version %APP_VERSION%" build\mockexam-linux.tar.gz build\mockexam-macos.tar.gz build\mockexam-win32.zip build\libs\mockexam-%APP_VERSION%.jar



:end