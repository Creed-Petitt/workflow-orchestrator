package com.creedpetitt.orchestrator.dto;

public record JobMessage(
    String workflowRunId,
    String action,
    String payload
) {}
