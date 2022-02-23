FROM gradle:7.2.0-jdk17 as builder
RUN ln -s /home/gradle/project /project
WORKDIR /project
COPY . /project/
RUN gradle -x test --no-daemon clean build
WORKDIR /project/build/libs
RUN rm *-plain.jar \
 && mv onlinetrainer*.jar onlinetrainer.jar \
 && java -Djarmode=layertools -jar onlinetrainer.jar extract


FROM openjdk:17.0.1-slim
EXPOSE 8080
RUN mkdir /app 
WORKDIR /app
ENTRYPOINT ["java","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.base/java.io=ALL-UNNAMED","-Dfile.encoding=UTF-8","org.springframework.boot.loader.JarLauncher"]
COPY --from=builder /project/build/libs/spring-boot-loader    /app
COPY --from=builder /project/build/libs/dependencies          /app
COPY --from=builder /project/build/libs/snapshot-dependencies /app
COPY --from=builder /project/build/libs/application           /app
