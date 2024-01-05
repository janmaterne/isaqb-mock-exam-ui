# Use a "builder image" where we can do lots of stuff, including
# creating lots of files we dont want to have in production.
FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /tmp
COPY pom.xml /tmp/
COPY .git/   /tmp/.git/
COPY src     /tmp/src/
RUN mvn package \
 && cd /tmp/target \
 && mv onlinetrainer*.jar onlinetrainer.jar \
 && java -Djarmode=layertools -jar onlinetrainer.jar extract


# Create a production image. More stable layers first so help the caching of layers.
FROM openjdk:21-slim
EXPOSE 8080
RUN mkdir /app 
WORKDIR /app
ENTRYPOINT ["java","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.base/java.io=ALL-UNNAMED","-Dfile.encoding=UTF-8","org.springframework.boot.loader.JarLauncher"]
COPY --from=builder /tmp/target/spring-boot-loader    /app
COPY --from=builder /tmp/target/dependencies          /app
COPY --from=builder /tmp/target/snapshot-dependencies /app
COPY --from=builder /tmp/target/application           /app
