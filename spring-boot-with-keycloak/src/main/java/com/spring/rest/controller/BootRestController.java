package com.spring.rest.controller;

import com.spring.dto.KeycloakDTO;
import com.spring.service.KeycloakWebService;
import com.spring.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BootRestController {
    public static final String URL_NOTES = "/notes";
    public static final String URL_CLIENTS = "/clients";

    @Autowired
    StudentService studentService;


    @Autowired
    KeycloakDTO keycloakDTO;

    @Autowired
    KeycloakWebService keycloakWebService;

    @RequestMapping("/rest")
    public String rest() {
        return "rest";

    }




    @RequestMapping(path = "/inner-registration10", method = RequestMethod.GET)
    public String innerRegistration(Principal principal, Model model) {
//		Keycloak keycloak = KeycloakBuilder.builder()
//				.serverUrl("http://localhost:8090/auth")
//				.realm("myrealm")
//				.username("user1")
//				.password("pass")
//				.clientId("myclient")
////				.clientSecret("secret")
//				.build();

        model.addAttribute("keycloak", null);

        return "";
    }

    @RequestMapping(path = "/inner-registration2", method = RequestMethod.GET)
    public String innerRegistration2(Principal principal, Model model) {
//		Keycloak keycloak = KeycloakBuilder.builder()
//				.serverUrl("http://localhost:8090/auth")
//				.realm("myrealm")
//				.username("user1")
//				.password("pass")
//				.clientId("myclient")
////				.clientSecret("secret")
//				.build();

//		model.addAttribute("keycloak", keycloak);

        keycloakWebService.keycloakReg2("");
        return "keycloak";
    }


    @GetMapping(path = "/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/student/home";
    }
}
