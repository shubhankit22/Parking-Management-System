package com.demo.parkinglot.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableMethodSecurity
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Value("${admin.emails}")
    private String adminEmails;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        Set<String> ADMIN_EMAILS = new HashSet<>(
                Arrays.asList(adminEmails.split(",")));

        OAuth2User user = super.loadUser(userRequest);
        String email = user.getAttribute("email");

        Set<SimpleGrantedAuthority> mappedAuthorities = new HashSet<>();
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); // All get USER role

        // Assign ADMIN role to specific emails
        if (ADMIN_EMAILS.contains(email)) {
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // Return new OAuth2User with mapped roles
        return new DefaultOAuth2User(
                mappedAuthorities, user.getAttributes(), "sub");
    }
}

