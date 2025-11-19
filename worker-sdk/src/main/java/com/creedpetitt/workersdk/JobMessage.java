package com.creedpetitt.workersdk;

public record JobMessage(
        String workflowRunId,
        String action,
        String payload
) {}