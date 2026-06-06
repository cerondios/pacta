package com.pacta.pacta_app.reviewer.application.dto;

import java.util.List;

public record PendingQueueResponse(
        int totalUsers,
        int totalItems,
        List<UserReviewRequestDto> users
) {}
