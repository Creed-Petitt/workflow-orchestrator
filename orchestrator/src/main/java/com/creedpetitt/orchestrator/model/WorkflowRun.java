package com.creedpetitt.orchestrator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "workflow_run")
public class WorkflowRun {

    @Id
    private String runId;

    @OneToMany(mappedBy = "workflowRun", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StepResult> stepResults = new ArrayList<>();

    @Column
    private String workflowId;

    @Column
    private int currentStep;

    @Column
    private String status;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private String input;

    @Column
    private String finalOutput;

}
