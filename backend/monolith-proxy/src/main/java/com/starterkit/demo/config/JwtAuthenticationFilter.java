package com.starterkit.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.service.CustomUserDetailsService;
import com.starterkit.demo.util.JwtUtil;
import com.starterkit.demo.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (shouldSkipFilter(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);
            if (jwt != null && jwtUtil.validateToken(jwt)) {
                setAuthenticationContext(jwt, request);
            } else {
                throw new AuthenticationException("Invalid Token");
            }
        } catch (AuthenticationException ex) {
            handleAuthenticationException(response, ex);
            return;
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipFilter(String path) {
        return path.startsWith("/actuator/health") || path.startsWith("/api/users/login") || path.startsWith("/api/users/logout" ) || path.startsWith("/api/users/register");
    }

    private void setAuthenticationContext(String jwt, HttpServletRequest request) {
        String username = jwtUtil.getUserNameFromToken(jwt);
        UserDetails userDetails = userService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        return CookieUtils.getInstance().getCookie(request, "JWT_TOKEN").map(Cookie::getValue).orElse(null);
    }

    private void handleAuthenticationException(HttpServletResponse response, AuthenticationException ex) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), Collections.singletonMap("error", ex.getMessage()));
    }
}
