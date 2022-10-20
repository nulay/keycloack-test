package ru.lanit.controller;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.lanit.configuration.KeycloakProperties;
import ru.lanit.service.KeycloakWebService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class KeycloakRegistrationController {

    @Autowired
    KeycloakProperties keycloakProperties;

    @Autowired
    KeycloakWebService keycloakWebService;

    @RequestMapping(path = "/lanit-fake-reg", method = RequestMethod.GET)
    public String lanitFakeCook(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("sso_session_key", "lanit-fake-reg");
        cookie.setMaxAge(100000);
        response.addCookie(cookie);
        return "index";
    }

    @RequestMapping(path = "/lanit-remove-fake-reg", method = RequestMethod.GET)
    public String lanitRemoveFakeCook(HttpServletRequest request, HttpServletResponse response) {
        List<Cookie> cookies = request.getCookies() != null ? List.of(request.getCookies()) : List.of();

        Optional<Cookie> cookie = cookies.stream()
                .filter(cookie1 -> cookie1.getName().equals("sso_session_key"))
                .findFirst();
        if(cookie.isPresent()){
            Cookie modifiedCookie = cookie.get();
            modifiedCookie.setMaxAge(0);
            response.addCookie(modifiedCookie);
        }
        return "index";
    }

    @RequestMapping(path = "/44fz-to-visiology2", method = RequestMethod.GET)
    public String goFrom44fzToVisiology2(HttpServletRequest request, HttpServletResponse response) {
        log.debug("We got auth from hazelcast");

        return "inner-reg";
    }

    @RequestMapping(path = "/44fz-to-visiology", method = RequestMethod.GET)
    public String goFrom44fzToVisiology(HttpServletRequest request, HttpServletResponse response) {
        List<Cookie> cookies = request.getCookies() != null ? List.of(request.getCookies()) : List.of();
        cookies.forEach(e -> log.debug("Find next cookie : {}", e.getName()));
        Optional<Cookie> cookie = cookies.stream()
                .filter(cookie1 -> cookie1.getName().equals("sso_session_key"))
                .findFirst();

        if (cookie.isEmpty() || cookie.get().getMaxAge() == 0) {
            return "redirect:" + "https://eis3.lanit.ru";
        }
        return "redirect:" + "/inner-registration";
    }

    @RequestMapping(path = "/223-to-visiology", method = RequestMethod.GET)
    public String goFrom223ToVisiology(HttpServletRequest request, HttpServletResponse response) {
        List<Cookie> cookies = request.getCookies() != null ? List.of(request.getCookies()) : List.of();
        Optional<Cookie> cookie = cookies.stream()
                .filter(cookie1 -> cookie1.getName().equals("sso_session_key"))
                .findFirst();
        if (cookie.isEmpty()) {
            return "redirect:" + "https://eis3.lanit.ru";
        }
        return "redirect:" + "/inner-registration";
    }


    /**
     * Основной метод регистрации в кейклоке
     *
     * @param request  request
     * @param response response
     * @return emptypage - просто чтобы что-то было
     */
    @RequestMapping(path = "/inner-registration", method = RequestMethod.GET)
    public String innerRegistration(HttpServletRequest request, HttpServletResponse response) {
        Connection.Response response1 = null;
        try {
            response1 = keycloakWebService.keycloakReg();
        } catch (Exception e) {
            ObjectError message = new ObjectError("globalError", e.getMessage());

        }
        if (response1 != null) {
            response1.cookies().forEach((key, val) -> {
                Cookie cookie = new Cookie(key, val);
                cookie.setDomain(keycloakProperties.getRootDomain());
                cookie.setPath("/");
                response.addCookie(cookie);
            });
            log.debug("All cookies installed to {}", keycloakProperties.getRootDomain());
            return "redirect:" + "http://195.26.187.247/";
//            return "emptypage";
        } else {
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
        log.debug("We are on sso-success");
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
        return "ssosuccess";
    }
}
