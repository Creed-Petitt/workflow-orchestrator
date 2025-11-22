package com.creedpetitt.orchestrator.model;

import lombok.Data;

@Data
public class WorkflowRun {
    private String runId;
    private String workflowId;
    private int currentStep;
    private String status;
}
