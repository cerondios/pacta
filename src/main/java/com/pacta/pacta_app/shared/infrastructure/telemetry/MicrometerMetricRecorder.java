package com.pacta.pacta_app.shared.infrastructure.telemetry;

import com.pacta.pacta_app.shared.domain.MetricRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"prod"})
@RequiredArgsConstructor
public class MicrometerMetricRecorder implements MetricRecorder {

    private final MeterRegistry registry;

    @Value("${pacta.metrics.template:pacta.default.%s}")
    private String template;

    @Override
    public void incrementCounter(String aspect, String... tags) {
        Counter.builder(name(aspect)).tags(tags).register(registry).increment();
    }

    @Override
    public void decrementCounter(String aspect, String... tags) {
        Counter.builder(name(aspect)).tags(tags).register(registry).increment(-1);
    }

    @Override
    public void count(String aspect, double delta, String... tags) {
        Counter.builder(name(aspect)).tags(tags).register(registry).increment(delta);
    }

    @Override
    public void histogram(String aspect, long value, String... tags) {
        DistributionSummary.builder(name(aspect))
                .tags(tags)
                .publishPercentiles(0.95)
                .register(registry)
                .record(value);
    }

    private String name(String aspect) {
        return String.format(template, aspect);
    }
}
