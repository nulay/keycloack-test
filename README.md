# keycloack-test

https://www.baeldung.com/spring-boot-keycloak
кейклок сервер поставлен по инструкции выше - версию сервера брать такую-же как в инструкции. Более новая версия не заработала

кейклок доккер запускал на порту 8090 следующей командой:
docker run -p 8090:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:18.0.2 start-dev