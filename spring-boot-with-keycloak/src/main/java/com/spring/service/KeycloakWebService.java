package com.spring.service;

import com.spring.configuration.KeycloakProperties;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class KeycloakWebService {

    @Autowired
    KeycloakProperties keycloakProperties;

    @SneakyThrows
    public Connection.Response keycloakReg() {
        Connection connection = getAuthKeycloakForm();
        Connection.Response response = connection.execute();
        Element el = response.parse().getElementById("kc-form-login");
        Map<String, String> cookies = response.cookies();
        String urlLoginForm = el.attr("action");
        Connection.Response logF = executeLogin(cookies, urlLoginForm);
        return logF;
    }

    @SneakyThrows
    private Connection.Response executeLogin(Map<String, String> cookies, String url) {
        Connection connection = Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .cookies(cookies)
//                        .data("credentialId", "")
                .data("username", keycloakProperties.getKeycloakUserName())
                .data("password", keycloakProperties.getPassword())
                .userAgent("Mozilla")
                .method(Connection.Method.POST)
                .headers(Map.of("Content-Type", "application/x-www-form-urlencoded",
                        "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8," +
                                "application/signed-exchange;v=b3;q=0.9",
                        "Content-Length", "42",
                        "Connection", "keep-alive"));

        Connection.Response loginForm = null;
        try {
            loginForm = connection.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return loginForm;
    }

    // Open auth keycloak form
    private Connection getAuthKeycloakForm() {
        Connection connection =
                Jsoup.connect(keycloakProperties.getKeycloakScheme() + keycloakProperties.getKeycloakBaseUrl() + keycloakProperties.getPathWithReplace())
                        .data("response_type", keycloakProperties.getKeycloakResponseType())
                        .data("client_id", keycloakProperties.getKeycloakClientId())
                        .data("redirect_uri", keycloakProperties.getRedirectUri())
                        .data("state", keycloakProperties.getKeycloakState())
                        .data("login", keycloakProperties.getIsKeycloakLogin())
                        .data("scope", keycloakProperties.getKeycloakScope())
                        .userAgent("Mozilla")
                        .method(Connection.Method.GET);
        return connection;
    }
}
