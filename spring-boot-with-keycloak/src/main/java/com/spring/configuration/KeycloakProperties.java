package com.spring.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties
public class KeycloakProperties {

    @Value("${keycloak-inner-client.scheme:http://}")
    private String keycloakScheme;
    @Value("${keycloak-inner-client.base-url:localhost:8080}")
    private String keycloakBaseUrl;
    @Value("${keycloak-inner-client.path:/realms/<changerealm>/protocol/openid-connect/auth}")
    private String keycloakPath;
    @Value("${keycloak-inner-client.path-token:/realms/<changerealm>/protocol/openid-connect/token}")
    private String keycloakPathToken;
    @Value("${keycloak-inner-client.realm:master}")
    private String keycloakRealm;
    @Value("${keycloak-inner-client.response-type:code}")
    private String keycloakResponseType;
    @Value("${keycloak-inner-client.grant-type:password}")
    private String keycloakGrantType;

    @Value("${keycloak-inner-client.client-id:client}")
    private String keycloakClientId;
    @Value("${keycloak-inner-client.redirect-uri:http://localhost:8080}")
    private String redirectUri;
    @Value("${keycloak-inner-client.state:0}")
    private String keycloakState;
    @Value("${keycloak-inner-client.is-login:true}")
    private String isKeycloakLogin;
    @Value("${keycloak-inner-client.scope:openid}")
    private String keycloakScope;

    @Value("${keycloak-inner-client.root-domain}")
    private String rootDomain;


    @Value("${keycloak-inner-client.keycloak-user-name:user}")
    private String keycloakUserName;
    @Value("${keycloak-inner-client.password:password}")
    private String password;


    public String getPathWithReplace() {
        return this.keycloakPath.replace("<changerealm>", keycloakRealm);
    }

    public String getPathTokenWithReplace() {
        return this.keycloakPathToken.replace("<changerealm>", keycloakRealm);
    }

}
