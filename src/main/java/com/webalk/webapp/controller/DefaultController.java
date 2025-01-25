package com.webalk.webapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class DefaultController {

    @GetMapping("/default")
    public void defaultAfterLogin(Authentication authentication, HttpServletResponse response) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_USER")) {
            response.sendRedirect("/products");
        } else {
            response.sendRedirect("/login?error");
        }
    }
}