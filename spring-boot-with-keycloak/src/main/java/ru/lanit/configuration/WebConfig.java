package ru.lanit.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.net.ssl.HttpsURLConnection;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig {
    public static final int TIMEOUT = 3000;

    @Autowired
    public void disabledSSLHostCheck() throws Exception {
//        URL url = new URL("https://localhost:9443/soap_rpc");
//        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
//        connection.setHostnameVerifier(new NullHostnameVerifier());
        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
    }

    @Bean
    public KeycloakProperties keycloakDTO() {
        return new KeycloakProperties();
    }

    @Bean
    public WebClient webClientWithTimeout(KeycloakProperties keycloakProperties) {
        final TcpClient tcpClient = TcpClient
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
                .baseUrl(keycloakDTO().getKeycloakBaseUrl())
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
