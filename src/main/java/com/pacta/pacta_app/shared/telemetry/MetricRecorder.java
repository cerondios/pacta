package com.pacta.pacta_app.shared.telemetry;

/**
 * Records custom metrics. Inject this bean wherever metrics are needed — Spring
 * provides {@link LocalMetricRecorder} for the {@code local}/{@code default}
 * profiles and {@link MicrometerMetricRecorder} otherwise.
 */
public interface MetricRecorder {

    void incrementCounter(String aspect, String... tags);

    void decrementCounter(String aspect, String... tags);

    void count(String aspect, double delta, String... tags);

    void histogram(String aspect, long value, String... tags);
}
