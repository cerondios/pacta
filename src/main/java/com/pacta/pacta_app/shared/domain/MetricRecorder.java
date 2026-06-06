package com.pacta.pacta_app.shared.domain;

public interface MetricRecorder {

    void incrementCounter(String aspect, String... tags);

    void decrementCounter(String aspect, String... tags);

    void count(String aspect, double delta, String... tags);

    void histogram(String aspect, long value, String... tags);
}
