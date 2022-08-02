package com.spring.controller;

import com.spring.configuration.KeycloakProperties;
import com.spring.service.KeycloakWebService;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class KeycloakRegistrationController {

    @Autowired
    KeycloakProperties keycloakProperties;

    @Autowired
    KeycloakWebService keycloakWebService;

    /**
     * Основной метод регистрации в кейклоке
     * @param request request
     * @param response response
     * @return emptypage - просто чтобы что-то было
     */
    @RequestMapping(path = "/inner-registration", method = RequestMethod.GET)
    public String innerRegistration(HttpServletRequest request, HttpServletResponse response) {
        Connection.Response response1 = keycloakWebService.keycloakReg();
        response1.cookies().forEach((key, val) -> {
            Cookie cookie = new Cookie(key, val);
            cookie.setDomain(keycloakProperties.getRootDomain());
            response.addCookie(cookie);
        });
        return "emptypage";
    }

    /**
     * Страница на которой будет ссылка на другой ресурс и iframe с URL для внутренней регистрации у нас это inner-registration
     * @return страницу
     */
    @RequestMapping(path = "/keycloak", method = RequestMethod.GET)
    public String keycloak(String state, String session_state, String code) {
        return "keycloak";
    }

    /**
     * Пустая страница на которую сделаем редирект после регистрации - она ничего не должна делать
     * Здесь можно почитать разные токкены и секьюрети ключи, если нужно
     * @return страницу
     */
    @RequestMapping(path = "/empty-page", method = RequestMethod.GET)
    public String emptyPage(HttpServletRequest request, HttpServletResponse response) {
        return "emptypage";
    }
}
