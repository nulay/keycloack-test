package com.spring.service;

import com.spring.dto.KeycloakDTO;
import lombok.SneakyThrows;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Service

public class KeycloakWebService {

    public static final String URL_KEYCLOAK = "realms/myrealm/protocol/openid-connect/auth?response_type==code&client_id=myclient" +
            "&redirect_uri=http%3A%2F%2Flocalhost%3A8095%2Finner-registration2&state=d10b2894-e46c-403c-9ddd-66b565965158&login=true&scope=openid";
    private final WebClient webClient;

    @Autowired
    public KeycloakWebService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Autowired
    KeycloakDTO keycloakDTO;

    @SneakyThrows
    public Connection.Response keycloakReg() {
        Connection connection = getAuthKeycloakForm();
        Connection.Response response = connection.execute();
        Element el = response.parse().getElementById("kc-form-login");
        Map<String, String> cookies = response.cookies();
        String urlLoginForm = el.attr("action");
        URI aURL = new URI(urlLoginForm);
        List<NameValuePair> params = URLEncodedUtils.parse(aURL, Charset.forName("UTF-8"));
        Connection.Response logF = executeLogin(cookies, urlLoginForm);

//        Connection.Response response2 = Jsoup.connect("http://localhost:8093" +  "/student/home")
//        Document doc2 = callSomeApi(cookies, "http://195.26.187.247/dashboardPreviews?sectionId=3");
        //Document someApi = callSomeApi(cookies, "http://localhost:8093/home");
        return logF;
    }

    private Document callSomeApi(Map<String, String> cookies, String urlForCall) throws IOException {
        Connection.Response response2 = Jsoup.connect(urlForCall)
                .cookies(cookies)
                .userAgent("Mozilla")
                .method(Connection.Method.GET)
                .execute();
        Document doc2 = response2.parse();
        return doc2;
    }

    @SneakyThrows
    private Connection.Response executeLogin(Map<String, String> cookies, String url) {//URI aURL, List<NameValuePair> params) {
//        String url = aURL.getScheme() + "://" + aURL.getHost() +
//                ((aURL.getPort() != 80) ? ":" + aURL.getPort() : "") +
//                aURL.getPath();
//        URLEncoder.encode(url, "UTF-8")
        Connection connection = Jsoup.connect(url)
                .ignoreHttpErrors(true)
                        .cookies(cookies)
//                        .data("credentialId", "")
                        .data("username", keycloakDTO.getUserName())
                        .data("password", keycloakDTO.getPass())
                        .userAgent("Mozilla")
                        .method(Connection.Method.POST)
                        .headers(Map.of("Content-Type", "application/x-www-form-urlencoded",
                                "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8," +
                                        "application/signed-exchange;v=b3;q=0.9",
                                "Content-Length", "42",
                                "Connection", "keep-alive"));
//        for (NameValuePair param : params) {
//            connection = connection.data(param.getName(), param.getValue());
//        }
        Connection.Response loginForm = null;
        try {
            loginForm = connection.execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return loginForm;
    }

    // Open auth keycloak form
    private Connection getAuthKeycloakForm() throws IOException {
        Connection connection = Jsoup.connect(keycloakDTO.getBaseUrl() + keycloakDTO.getPathWithReplace())
                .data("response_type", keycloakDTO.getResponseType())
                .data("client_id", keycloakDTO.getClientId())
                .data("redirect_uri", keycloakDTO.getRedirectUri())
                .data("state", keycloakDTO.getState())
                .data("login", keycloakDTO.getLogin())
                .data("scope", keycloakDTO.getScope())
                .userAgent("Mozilla")
                .method(Connection.Method.GET);
        return connection;
    }

    // Open auth keycloak form
    private Connection getAuthKeycloakFormNoParams() throws IOException {
        Connection connection = Jsoup.connect(keycloakDTO.getBaseUrl() + keycloakDTO.getPathWithReplace())
                .userAgent("Mozilla")
                .method(Connection.Method.GET);
        return connection;
    }

    private Connection connectToKeycloak3() throws IOException {
        Connection connection = Jsoup.connect(keycloakDTO.getBaseUrl() + keycloakDTO.getPathTokenWithReplace())
                .data("grant_type", keycloakDTO.getGrantType())
                .data("client_id", keycloakDTO.getClientId())
//                .data("redirect_uri", keycloakDTO.getRedirectUri())
                .data("scope", keycloakDTO.getScope())
//                .data("state", keycloakDTO.getState())
                .data("username", keycloakDTO.getUserName())
                .data("password", keycloakDTO.getPass())
                .headers(Map.of("Content-Type", "application/*+x-www-form-urlencoded",
                                "Accept","*/*"))
                .userAgent("Mozilla")
                .method(Connection.Method.POST);
        return connection;
    }

    public void keycloakRegN() {
        //http://localhost:8090/realms/myrealm/protocol/openid-connect/auth?response_type=code&client_id=myclient&redirect_uri=http%3A%2F%2Flocalhost%3A8095%2Fhome&state=d10b2894-e46c-403c-9ddd-66b565965158&login=true&scope=openid
//        HttpClient client = new DefaultHttpClient();
//        HttpGet response = new HttpGet("http://192.168.1.205/js/jssrc/model/dvcinfo/dvccounter/DvcInfo_Counter_PrnCounter.model.htm");
//        ResponseHandler<String> handler = new BasicResponseHandler();
//        String body = client.execute(response, handler);
//        System.out.println(body);

        Mono mono = webClient
                .get()
//                .uri(URL_KEYCLOAK)
                .uri(uriBuilder -> uriBuilder.scheme(keycloakDTO.getScheme())
                        .path(keycloakDTO.getPath())
                        .queryParam("response_type", keycloakDTO.getResponseType())
                        .queryParam("client_id", keycloakDTO.getClientId())
                        .queryParam("redirect_uri", keycloakDTO.getRedirectUri())
                        .queryParam("state", keycloakDTO.getState())
                        .queryParam("login", keycloakDTO.getLogin())
                        .queryParam("scope", keycloakDTO.getScope())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .onErrorResume(WebClientResponseException.class, e -> {

//                    logger.warn(format("Could not revoke token", e));

                    return Mono.empty();
                }).onErrorResume(Exception.class, e -> {

//                    logger.warn("Could not revoke token", e);

                    return Mono.empty();
                }).then();
        mono.subscribe(System.out::println);
    }

    public void keycloakReg2(String body) {
        System.out.println(body);
    }
//
//    public Mono<void> revoke() {
//
//        return webClient.post().uri("auth/token/revoke-self").headers(httpHeaders -> {
//            httpHeaders.addAll(VaultHttpHeaders.from(token));
//        }).retrieve().bodyToMono(String.class).then()
//                .onErrorResume(WebClientResponseException.class, e -> {
//
//                    logger.warn(format("Could not revoke token", e));
//
//                    return Mono.empty();
//                }).onErrorResume(Exception.class, e -> {
//
//                    logger.warn("Could not revoke token", e);
//
//                    return Mono.empty();
//                }).then();
//    }
}
