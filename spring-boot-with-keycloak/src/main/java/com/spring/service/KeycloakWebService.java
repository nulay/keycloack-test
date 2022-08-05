package com.spring.service;

import com.spring.configuration.KeycloakProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KeycloakWebService {

    @Autowired
    KeycloakProperties keycloakProperties;

    @SneakyThrows
    public Connection.Response keycloakReg() {
        Connection connection = getAuthKeycloakForm();
        Connection.Response response = null;
        try {
            response = connection.execute();
            Element el = response.parse().getElementById("kc-form-login");
            Map<String, String> cookies = response.cookies();
            String urlLoginForm = el.attr("action");

            response = executeLogin(cookies, urlLoginForm);
        } catch (IOException exception) {
            log.error("Connection refused to: {}", response.url().getHost() +
                            ((-1 != response.url().getPort()) ? ":" + response.url().getPort() : "") +
                            response.url().getPath(),
                    exception);
            throw exception;
        }
        return response;
    }

    @SneakyThrows
    private Connection.Response executeLogin(Map<String, String> cookies, String url) {
        Map headerMap = new HashMap();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8," +
                "application/signed-exchange;v=b3;q=0.9");
        headerMap.put("Content-Length", "42");
        headerMap.put("Connection", "keep-alive");

        Connection connection = Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .cookies(cookies)
//                        .data("credentialId", "")
                .data("username", keycloakProperties.getKeycloakUserName())
                .data("password", keycloakProperties.getPassword())
                .userAgent("Mozilla")
                .method(Connection.Method.POST)
                .headers(headerMap);

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

    @SneakyThrows
    public void keycloakCheck() {
        Connection connection =
                Jsoup.connect("https://www.google.com/search?q=sad&oq=sad&aqs=chrome..69i57j46i512l2j46i199i465i512j46i512j0i512j46i512j0i512l2j0i271.11371j0j15&sourceid=chrome&ie=UTF-8")
                        .userAgent("Mozilla")
                        .method(Connection.Method.GET);
        Connection.Response response = connection.execute();
        System.out.println("Check google call " + response.statusMessage());
        log.error("Check google call " + response.statusMessage());
    }
}
