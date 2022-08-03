FROM tomcat:8.0-alpine

LABEL maintainer="deepak@softwareyoga.com"

ADD file:c92c248239f8c7b9b3c067650954815f391b7bcb09023f984972c082ace2a8d0 in /


ADD https://github.com/nulay/keycloack-test/blob/inner-registration/spring-boot-with-keycloak/war/keycloak.war?raw=true /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]