package com.pacta.pacta_app.shared.infrastructure;

import com.pacta.pacta_app.shared.domain.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidIdGenerator implements IdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
