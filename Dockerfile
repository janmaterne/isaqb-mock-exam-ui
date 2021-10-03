FROM gradle:7.2.0-jdk16 as builder
WORKDIR /home/gradle/project
COPY . /home/gradle/project/
RUN gradle -x test build


FROM openjdk:16.0.2
RUN mkdir /app
COPY --from=builder /home/gradle/project/build/libs/mockexam-0.0.1-SNAPSHOT.jar /app/mockexam.jar
WORKDIR /app
ENTRYPOINT ["java","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.base/java.io=ALL-UNNAMED","-jar","mockexam.jar"]