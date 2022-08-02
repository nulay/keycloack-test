FROM  adoptopenjdk/openjdk15:ubi
ARG MAVEN_VERSION=3.6.3
ARG USER_HOME_DIR="/root"
ARG USER_APP_DIR="/opt/app"
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries
RUN dnf install git -y
RUN dnf install tomcat -y
RUN mkdir -p ${USER_APP_DIR}/inner
  # Clone the conf files into the docker container
RUN git clone https://github.com/nulay/keycloack-test.git ${USER_APP_DIR}/inner
WORKDIR ${USER_APP_DIR}/inner/
RUN git checkout -b inner-registration
WORKDIR ${USER_APP_DIR}/inner/spring-boot-with-keycloak/
