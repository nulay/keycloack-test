FROM openjdk:15-jdk-alpine
ADD https://github.com/nulay/keycloack-test/blob/inner-registration/spring-boot-with-keycloak/jar/keycloak-1.0.jar?raw=true /usr/local/target/
ARG JAR_FILE=usr/local/target/*.jar
ENV http_proxy "http://host.docker.internal:8888/"
ENV https_proxy "http://host.docker.internal:8888/"
#COPY ${JAR_FILE} keycloak-1.0.jar
ENTRYPOINT ["java","-jar","/usr/local/target/keycloak-1.0.jar"]