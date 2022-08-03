package com.spring.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KeycloakDTO {

    private String scheme;
    private String baseUrl;
    private String path;
    private String pathToken;
    private String realm;
    private String responseType;
    private String grantType;

    private String clientId;
    private String redirectUri;
    private String state;
    private String login;
    private String scope;

    private String userName;
    private String pass;


    public String getPathWithReplace(){
        return this.path.replace("{changerealm}", realm);
    }

    public String getPathTokenWithReplace(){
        return this.pathToken.replace("{changerealm}", realm);
    }
}
