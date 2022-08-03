package com.spring.controller;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

@KeycloakConfiguration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Autowired
    public void disabledSSLHostCheck() throws Exception {
//        URL url = new URL("https://localhost:9443/soap_rpc");
//        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
//        connection.setHostnameVerifier(new NullHostnameVerifier());
        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(buildSessionRegistry());
    }

    @Bean
    protected SessionRegistry buildSessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                // TODO switch off on prod
                .csrf().disable()
                .authorizeRequests()
//                .antMatchers("/customers*").hasRole("user")
//                .antMatchers("/admin*").hasRole("admin")
                .antMatchers("/student/*").hasRole("USER")
                .antMatchers("/teacher/*").hasRole("TEACHER")
                .antMatchers("/inner-registration2").hasRole("USER")
                .anyRequest().permitAll();
    }

	@Bean
	public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

}