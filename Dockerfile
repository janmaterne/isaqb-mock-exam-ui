# Use a "builder image" where we can do lots of stuff, including
# creating lots of files we dont want to have in production.
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /tmp
COPY pom.xml /tmp/
COPY .git/   /tmp/.git/
COPY src     /tmp/src/
# Easier to debug. Also we don't need small layers in the builder image,
# as this is just temporarily.
RUN mvn package 
WORKDIR /tmp/target 
RUN mv onlinetrainer*.jar onlinetrainer.jar 
RUN java -Djarmode=layertools -jar onlinetrainer.jar extract
run touch x.tmp
RUN ls -al


#RUN mvn package \
# && cd /tmp/target \
# && mv onlinetrainer*.jar onlinetrainer.jar \
# && java -Djarmode=layertools -jar onlinetrainer.jar extract


# Create a production image. More stable layers first so help the caching of layers.
FROM openjdk:21-slim
EXPOSE 8080
RUN mkdir /app 
WORKDIR /app

# Using extracted layer doesn't work at the moment, so use the standard JAR launch
COPY --from=builder /tmp/target/onlinetrainer.jar  /app
ENTRYPOINT ["java","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.base/java.io=ALL-UNNAMED","-Dfile.encoding=UTF-8","-jar","onlinetrainer.jar"]
# ENTRYPOINT ["java","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.base/java.io=ALL-UNNAMED","-Dfile.encoding=UTF-8","org.springframework.boot.loader.JarLauncher"]
# COPY --from=builder /tmp/target/spring-boot-loader    /app
# COPY --from=builder /tmp/target/dependencies          /app
# COPY --from=builder /tmp/target/snapshot-dependencies /app
# COPY --from=builder /tmp/target/application           /app

