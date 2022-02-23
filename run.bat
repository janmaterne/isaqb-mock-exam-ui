call .version.bat
java -Dfile.encoding=UTF-8 --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED -jar build\libs\onlinetrainer-%APP_VERSION%.jar %*