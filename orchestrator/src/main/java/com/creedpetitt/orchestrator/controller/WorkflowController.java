package com.creedpetitt.orchestrator.controller;

import com.creedpetitt.orchestrator.dto.CreateWorkflowRequest;
import com.creedpetitt.orchestrator.dto.TriggerWorkflowRequest;
import com.creedpetitt.orchestrator.model.WorkflowRun;
import com.creedpetitt.orchestrator.service.WorkflowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/")
    public ResponseEntity<String> createWorkflow(@RequestBody CreateWorkflowRequest req) {
        String id = workflowService.createWorkflow(req);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/trigger")
    public ResponseEntity<String> triggerWorkflow(@PathVariable String id, @RequestBody TriggerWorkflowRequest req) {
        String runId = workflowService.triggerWorflow(id, req);
        return ResponseEntity.ok(runId);
    }

    @GetMapping("/run/{runId}")
    public ResponseEntity<WorkflowRun> getWorkflow(@PathVariable String runId) {
        WorkflowRun run = workflowService.getRunStatus(runId);
        return ResponseEntity.ok(run);
    }
}
