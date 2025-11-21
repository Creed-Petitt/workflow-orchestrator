package com.creedpetitt.orchestrator.service;

import com.creedpetitt.orchestrator.dto.CreateWorkflowRequest;
import com.creedpetitt.orchestrator.dto.TriggerWorkflowRequest;
import com.creedpetitt.orchestrator.model.WorkflowRun;
import com.creedpetitt.orchestrator.repository.WorkflowDefinitionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final WorkflowDefinitionRepository workflowRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public WorkflowService(
            WorkflowDefinitionRepository workflowRepo,
            KafkaTemplate<String, String> kafkaTemplate,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.workflowRepo = workflowRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public String createWorkflow(CreateWorkflowRequest req) {
        return "";
    }

    public String triggerWorflow(String id, TriggerWorkflowRequest req) {
        return "";
    }

    public WorkflowRun getRunStatus(String runId) {
        return null;
    }
}
