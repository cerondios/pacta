package com.pacta.pacta_app.shared.infrastructure.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> extraHeaders = new HashMap<>();

    MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    void addHeader(String name, String value) {
        extraHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        if (extraHeaders.containsKey(name)) return extraHeaders.get(name);
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (extraHeaders.containsKey(name))
            return Collections.enumeration(Collections.singleton(extraHeaders.get(name)));
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        var names = Collections.list(super.getHeaderNames());
        names.addAll(extraHeaders.keySet());
        return Collections.enumeration(names);
    }
}
