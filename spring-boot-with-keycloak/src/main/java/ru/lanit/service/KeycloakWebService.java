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

import java.net.URLEncoder;
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
        Connection.Response response = getAuthKeycloakForm();
        Element el = response.parse().getElementById("kc-form-login");
        Map<String, String> cookies = response.cookies();
        String urlLoginForm = el.attr("action");
        logDebugCookies(cookies);
        Connection.Response responseAuth = executeLogin(cookies, urlLoginForm);
        logDebugCookies(responseAuth.cookies());
        return responseAuth;
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
        log.debug("Try to connect: {}", url);
        Connection connection = Jsoup.connect(url)
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
            log.debug("Try to execute lf");
            loginForm = connection.execute();
        } catch (Exception exception) {
            log.error("Connection refused to: {}", url, exception);
            log.debug("Check keycloak call {}", loginForm.statusCode());
            log.debug("Body is {}", loginForm.body());
        }
        log.debug("Size of cookies {}", loginForm.cookies().size());
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
    @SneakyThrows
    private Connection.Response getAuthKeycloakForm() {
        Map headers = prepaireHeaderToLoginFormRequest();
        log.debug("Try to connect: {}" + keycloakProperties.getKeycloakScheme() + keycloakProperties.getKeycloakBaseUrl() +
                keycloakProperties.getPathWithReplace());
        Connection connection =
                Jsoup.connect(keycloakProperties.getKeycloakScheme() + keycloakProperties.getKeycloakBaseUrl() + keycloakProperties.getPathWithReplace())
                        .data("response_type", keycloakProperties.getKeycloakResponseType())
                        .data("client_id", keycloakProperties.getKeycloakClientId())
                        .data("redirect_uri", keycloakProperties.getRedirectUri())
                        .data("state", keycloakProperties.getKeycloakState())
                        .data("login", keycloakProperties.getIsKeycloakLogin())
                        .data("scope", keycloakProperties.getKeycloakScope())
                        .userAgent(DEFAULT_UA)
                        .method(Connection.Method.GET)
                        .headers(headers);
        Connection.Response response = null;
        try {
            response = connection.execute();
        } catch (Exception exception) {
            log.error("Connection refused to: {}", keycloakProperties.getKeycloakScheme() + keycloakProperties.getKeycloakBaseUrl() +
                    keycloakProperties.getPathWithReplace(), exception);
            log.debug("Check keycloak call {}", response.statusCode());
            log.debug("Body is {}", response.body());
        }
        return response;
    }

    private static void logDebugCookies(Map<String, String> cookies) {
        if(log.isDebugEnabled()) {
            final String[] cookiesStr = {""};
            cookies.forEach((key, value) -> {
                cookiesStr[0] += key + "=" + value + ",";
            });
            log.debug("Cookies is {}", cookiesStr[0]);
        }
    }
}
