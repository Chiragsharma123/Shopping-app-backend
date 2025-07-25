package com.example.imagedemo.config;

import com.example.imagedemo.service.MySellerService;
import com.example.imagedemo.service.MyUserService;
import com.example.imagedemo.service.jwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class jwtFilter extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(jwtFilter.class);
    @Autowired
    private jwtService jwtService;
    @Autowired
    private MyUserService userService;
    @Autowired
    private MySellerService sellerService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("jwtToken".equals(c.getName())) {
                    token = c.getValue();
                    username = jwtService.extractUserName(token);
                    break;
                }
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            String role = jwtService.extractUserRole(token);
            if ("ROLE_USER".equalsIgnoreCase(role)) {
                userDetails = userService.loadUserByUsername(username);
            } else if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
                userDetails=userService.loadUserByUsername(username);
            } else if ("ROLE_SELLER".equalsIgnoreCase(role)) {
                userDetails = sellerService.loadUserByUsername(username);
            }
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}