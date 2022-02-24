# Specify how Heroku should start the application
#   web      Require a web resource (internet port, loadbalancer...)
#   java...  The command to start the application
#   ...jar   Hardcoded version in the JAR, so we must update it with every release
#   ...--server.port=$PORT   Specify the port the application should use; given via env variable by Heroku
#                            This port is mapped to isaqb-onlinetrainer.herokuapp.com:80/ 
web: java --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED -jar build/libs/onlinetrainer-0.4.jar --server.port=$PORT
