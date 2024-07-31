/* (C)2024 */
package com.starterkit.demo.config.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@Component
public class LogIdFilter implements Filter {

    public static final String LOG_ID = "log-id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String logId = UUID.randomUUID().toString();
            MDC.put(LOG_ID, logId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(LOG_ID);
        }
    }
}
