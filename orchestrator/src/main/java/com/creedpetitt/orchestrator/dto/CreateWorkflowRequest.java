package com.creedpetitt.orchestrator.dto;

import java.util.List;

public record CreateWorkflowRequest(
        String id,
        List<WorkflowStepDto> steps
) {}

