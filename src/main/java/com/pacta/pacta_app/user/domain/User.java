package com.pacta.pacta_app.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.Instant;
import java.util.Set;

@Getter
@Builder
public class User {

    private final String id;
    private final String email;
    @Singular private final Set<Role> roles;
    private final Instant createdAt;
}
