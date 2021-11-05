FROM gradle:7.2.0-jdk16 as builder
RUN ln -s /home/gradle/project /project
WORKDIR /project
COPY . /project/
RUN gradle -x test --no-daemon clean build
WORKDIR /project/build/libs
RUN rm *-plain.jar \
 && mv mockexam*.jar mockexam.jar \
 && java -Djarmode=layertools -jar mockexam.jar extract


FROM openjdk:16.0.2
EXPOSE 8080
RUN mkdir /app 
WORKDIR /app
ENTRYPOINT ["java","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.base/java.io=ALL-UNNAMED","org.springframework.boot.loader.JarLauncher"]
COPY --from=builder /project/build/libs/spring-boot-loader    /app
COPY --from=builder /project/build/libs/dependencies          /app
COPY --from=builder /project/build/libs/snapshot-dependencies /app
COPY --from=builder /project/build/libs/application           /app
