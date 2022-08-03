FROM openjdk:8-jdk-alpine
ADD https://github.com/nulay/keycloack-test/blob/inner-registration/spring-boot-with-keycloak/war/keycloak-1.0.jar?raw=true /usr/local/target/
ARG JAR_FILE=usr/local/target/*.jar
#COPY ${JAR_FILE} keycloak-1.0.jar
ENTRYPOINT ["java","-jar","/usr/local/target/keycloak-1.0.jar"]