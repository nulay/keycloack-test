package ru.lanit.service;

import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lanit.configuration.KeycloakProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.jsoup.helper.HttpConnection.DEFAULT_UA;

@Service
public class KeycloakWebService {
    private static final Logger log = LoggerFactory.getLogger(KeycloakWebService.class);

    @Autowired
    KeycloakProperties keycloakProperties;

    @SneakyThrows
    public Connection.Response keycloakReg() {
        Connection connection = getAuthKeycloakForm();
        Connection.Response response = null;
        try {
            response = connection.execute();
            log.debug("Check keycloak call to " + response.url().toExternalForm() + ":" + response.statusCode());
            Element el = response.parse().getElementById("kc-form-login");
            Map<String, String> cookies = response.cookies();
            String urlLoginForm = el.attr("action");
            log.debug("UrlLoginForm " + urlLoginForm);
            final String[] cookiesStr = {""};
            cookies.forEach((key, value) -> {
                cookiesStr[0] += key + "=" + value + ",";
            });
            log.debug(cookiesStr[0]);
            response = executeLogin(cookies, urlLoginForm);
            log.debug("Check keycloak call " + response.statusCode());
            log.debug("Count of cookies " + response.cookies().size());
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
        Map headers = prepaireHeaderToLoginFormRequest();
//        URI aURL = new URI(url);
//        String urlLoginForm = aURL.getScheme() + "://" + aURL.getHost() +
//                ((aURL.getPort() != 80) ? ":" + aURL.getPort() : "") +
//                aURL.getPath();
//
//        List<NameValuePair> params = URLEncodedUtils.parse(aURL, Charset.forName("UTF-8"));

        Connection connection = Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .cookies(cookies)
                .data("credentialId", "")
                .data("username", keycloakProperties.getKeycloakUserName())
                .data("password", keycloakProperties.getPassword())
                .userAgent(DEFAULT_UA)
                .method(Connection.Method.POST)
                .headers(headers);
//        for (NameValuePair param : params) {
//            connection = connection.data(param.getName(), param.getValue());
//        }
        Connection.Response loginForm = null;
        try {
            loginForm = connection.execute();
        } catch (IOException exception) {
            log.error("Connection refused to: {}", url, exception);
        }
        return loginForm;
    }

    private static Map prepaireHeaderToLoginFormRequest() {
        Map headerMap = new HashMap();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded");
        headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8," +
                "application/signed-exchange;v=b3;q=0.9");
        headerMap.put("Content-Length", "42");
        headerMap.put("Connection", "keep-alive");
        return headerMap;
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
                        .userAgent(DEFAULT_UA)
                        .method(Connection.Method.GET);
        return connection;
    }
}
