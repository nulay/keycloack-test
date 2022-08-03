package com.spring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakState {
    String state;
    String sessionCode;
    String execution;
    String tabId;

}
