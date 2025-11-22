package com.creedpetitt.orchestrator.service;

import com.creedpetitt.orchestrator.config.Topics;
import com.creedpetitt.orchestrator.dto.CreateWorkflowRequest;
import com.creedpetitt.orchestrator.dto.TriggerWorkflowRequest;
import com.creedpetitt.orchestrator.dto.JobMessage;
import com.creedpetitt.orchestrator.model.WorkflowDefinition;
import com.creedpetitt.orchestrator.model.WorkflowRun;
import com.creedpetitt.orchestrator.model.WorkflowStep;
import com.creedpetitt.orchestrator.repository.WorkflowDefinitionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

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

        WorkflowDefinition workflow = new WorkflowDefinition();
        workflow.setId(req.id());

        List<WorkflowStep> steps = req.steps().stream()
                .map(stepDto -> {
                    WorkflowStep step = new WorkflowStep();
                    step.setAction(stepDto.action());
                    step.setStepIndex(stepDto.stepIndex());
                    step.setWorkflowDefinition(workflow);
                    return step;
                }).toList();

        workflow.setSteps(steps);

        workflowRepo.save(workflow);

        return workflow.getId();
    }

    public String triggerWorkflow(String id, TriggerWorkflowRequest req) {

        WorkflowDefinition workflow = workflowRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow not found" + id));

        String runId = UUID.randomUUID().toString();

        WorkflowRun run = new WorkflowRun();
        run.setRunId(runId);
        run.setCurrentStep(0);
        run.setStatus("RUNNING");

        String key = "workflow:run:" + runId;
        try {
            String runJson = objectMapper.writeValueAsString(run);
            redisTemplate.opsForValue().set(key, runJson, Duration.ofHours(24));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save workflow run to Redis",e);
        }

        WorkflowStep firstStep = workflow.getSteps().get(0);

        JobMessage job =  new JobMessage(runId, firstStep.getAction(), req.input());

        try {
            String jobJson = objectMapper.writeValueAsString(job);
            kafkaTemplate.send(Topics.WORKFLOW_JOBS, jobJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send job to Kafka",e);
        }

        return runId;
    }

    public WorkflowRun getRunStatus(String runId) {

        String key = "workflow:run:" + runId;

        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            throw new RuntimeException("workflow run not found:" + runId);
        }

        try {
            return objectMapper.readValue(json, WorkflowRun.class);
        } catch (Exception e) {
            throw new RuntimeException("could not deserialize workflow run:" + runId, e);
        }
    }
}
