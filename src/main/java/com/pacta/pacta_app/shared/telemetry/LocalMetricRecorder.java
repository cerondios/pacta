package com.pacta.pacta_app.shared.telemetry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Logs metrics instead of exporting them. Active in the {@code local} and
 * {@code default} profiles, where no metrics backend / OTel agent is running.
 */
@Slf4j
@Component
@Profile({"local", "default", "dev"})
public class LocalMetricRecorder implements MetricRecorder {

    @Value("${pacta.metrics.template:pacta.default.%s}")
    private String template;

    @Override
    public void incrementCounter(String aspect, String... tags) {
        log.info("incrementCounter -> Template:{} Aspect:{} Tags:{}", template, aspect, tags);
    }

    @Override
    public void decrementCounter(String aspect, String... tags) {
        log.info("decrementCounter -> Template:{} Aspect:{} Tags:{}", template, aspect, tags);
    }

    @Override
    public void count(String aspect, double delta, String... tags) {
        log.info("count -> Template:{} Aspect:{} Delta:{} Tags:{}", template, aspect, delta, tags);
    }

    @Override
    public void histogram(String aspect, long value, String... tags) {
        log.info("histogram -> Template:{} Aspect:{} Value:{} Tags:{}", template, aspect, value, tags);
    }
}
