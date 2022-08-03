FROM openjdk:8-jdk-alpine
ARG JAR_FILE=../target/*.jar
COPY ${JAR_FILE} keycloak-1.0.jar
ENTRYPOINT ["java","-jar","/keycloak-1.0.jar"]