package ru.lanit.controller;

import io.micrometer.core.instrument.util.StringUtils;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.lanit.configuration.KeycloakProperties;
import ru.lanit.service.KeycloakWebService;

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
     *
     * @param request  request
     * @param response response
     * @return emptypage - просто чтобы что-то было
     */
    @RequestMapping(path = "/inner-registration", method = RequestMethod.GET)
    public String innerRegistration(HttpServletRequest request, HttpServletResponse response, BindingResult result) {
        Connection.Response response1 = null;
        try {
            response1 = keycloakWebService.keycloakReg();
        } catch (Exception e) {
            ObjectError message = new ObjectError("globalError", e.getMessage());
            result.addError(message);
        }
        if(response1 !=null) {
            response1.cookies().forEach((key, val) -> {
                Cookie cookie = new Cookie(key, val);
                cookie.setDomain(keycloakProperties.getRootDomain());
                response.addCookie(cookie);
            });
            return "emptypage";
        }else{
            return "error";
        }
    }

    /**
     * Страница на которой будет ссылка на другой ресурс и iframe с URL для внутренней регистрации у нас это inner-registration
     *
     * @return страницу
     */
    @RequestMapping(path = "/keycloak", method = RequestMethod.GET)
    public String keycloak(String state, String session_state, String code) {
        return "keycloak";
    }

    /**
     * Пустая страница на которую сделаем редирект после регистрации - она ничего не должна делать
     * Здесь можно почитать разные токкены и секьюрети ключи, если нужно
     *
     * @return страницу
     */
    @RequestMapping(path = "/empty-page", method = RequestMethod.GET)
    public String emptyPage(HttpServletRequest request, HttpServletResponse response) {
        return "emptypage";
    }


    @RequestMapping(path = "/change-settings", method = RequestMethod.GET)
    public String changeSettings(@RequestAttribute String baseUrl,
                                 @RequestAttribute String realm,
                                 @RequestAttribute String clientId,
                                 @RequestAttribute String redirectUrl,
                                 @RequestAttribute String rootDomain,
                                 @RequestAttribute String userName,
                                 @RequestAttribute String password

    ) {
        if (StringUtils.isNotBlank(baseUrl)) {
            keycloakProperties.setKeycloakBaseUrl(baseUrl);
        }
        if (StringUtils.isNotBlank(realm)) {
            keycloakProperties.setKeycloakRealm(realm);
        }
        if (StringUtils.isNotBlank(clientId)) {
            keycloakProperties.setKeycloakClientId(clientId);
        }
        if (StringUtils.isNotBlank(redirectUrl)) {
            keycloakProperties.setRedirectUri(redirectUrl);
        }
        if (StringUtils.isNotBlank(rootDomain)) {
            keycloakProperties.setRootDomain(rootDomain);
        }
        if (StringUtils.isNotBlank(userName)) {
            keycloakProperties.setKeycloakUserName(userName);
        }
        if (StringUtils.isNotBlank(password)) {
            keycloakProperties.setPassword(password);
        }
        return "emptypage";
    }

}
