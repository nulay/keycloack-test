package com.spring.controller;

import com.spring.dto.KeycloakDTO;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig {
    public static final int TIMEOUT = 3000;

    @Bean
    public KeycloakDTO keycloakDTO() {
//        return remoteKeyCloak();
        return localKeyCloak3();
    }


    public KeycloakDTO localKeyCloak() {

        return KeycloakDTO.builder()
                .scheme("http")
                .baseUrl("http://localhost:8093")
                .path("/student/home")
                .realm("myrealm")
                .responseType("code")
                .clientId("myclient")
                .redirectUri("http://localhost:8093/student/home")
                .state("d10b2894-e46c-403c-9ddd-66b565965158")
                .login("true")
                .scope("openid")
                .userName("user1")
                .pass("pass")
                .build();
    }

    public KeycloakDTO localKeyCloak2() {

        return KeycloakDTO.builder()
                .scheme("http")
                .baseUrl("http://localhost:8090")
                .path("/realms/{changerealm}/protocol/openid-connect/auth")
                .realm("myrealm")
                .responseType("code")
                .clientId("myclient")
                .redirectUri("http://localhost:8093/student/home")
                .state("d10b2894-e46c-403c-9ddd-66b565965158")
                .login("true")
                .scope("openid")
                .userName("user1")
                .pass("pass")
                .build();
    }

    public KeycloakDTO localKeyCloak3() {

        return KeycloakDTO.builder()
                .scheme("http")
                .baseUrl("http://localhost:8090")
                .path("/realms/{changerealm}/protocol/openid-connect/auth")
                .pathToken("/realms/{changerealm}/protocol/openid-connect/token")
                .realm("myrealm")
                .responseType("code")
                .grantType("password")
                .clientId("myclient2")
                .redirectUri("http://localhost:8093/empty-page")
                .state("d10b2894-e46c-403c-9ddd-66b565965158")
                .login("true")
                .scope("openid")
                .userName("user1")
                .pass("pass")
                .build();
    }

    public KeycloakDTO remoteKeyCloak() {

        return KeycloakDTO.builder()
                .scheme("https")
                .baseUrl("https://172.29.16.185:8443")
                .path("/realms/{changerealm}/protocol/openid-connect/auth")
                .realm("eisrealm")
                .responseType("code")
                .clientId("visiologyclient")
                .redirectUri("http://195.26.187.247/idsrv/signin-keycloak")
                .state("d10b2894-e46c-403c-9ddd-66b565965158")
                .login("true")
                .scope("openid")
                .userName("user1")
                .pass("pass")
                .build();
    }

    @Bean
    public WebClient webClientWithTimeout(KeycloakDTO localKeyCloak) {
        final var tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                });
        ExchangeFilterFunction errorResponseFilter = ExchangeFilterFunction
                .ofResponseProcessor(WebConfig::exchangeFilterResponseProcessor);
        return WebClient.builder()
                .filter(errorResponseFilter)
                .baseUrl(localKeyCloak.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    private static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
        HttpStatus status = response.statusCode();
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ServerErrorException(body)));
        }
        if (HttpStatus.BAD_REQUEST.equals(status)) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ServerErrorException(body)));
        }
        return Mono.just(response);
    }
}
