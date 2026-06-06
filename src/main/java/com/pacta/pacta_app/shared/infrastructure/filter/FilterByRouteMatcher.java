package com.pacta.pacta_app.shared.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Delegates to an inner filter only when the request matches a given RequestMatcher.
 * Keeps route ownership in SecurityConfig — filters themselves stay matcher-agnostic.
 */
public class FilterByRouteMatcher extends OncePerRequestFilter {

    private final RequestMatcher matcher;
    private final OncePerRequestFilter delegate;

    public FilterByRouteMatcher(RequestMatcher matcher, OncePerRequestFilter delegate) {
        this.matcher  = matcher;
        this.delegate = delegate;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !matcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        delegate.doFilter(request, response, chain);
    }
}
