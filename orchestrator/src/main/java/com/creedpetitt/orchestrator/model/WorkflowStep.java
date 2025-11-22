package com.creedpetitt.orchestrator.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "workflow_step")
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String action;

    @Column
    private int stepIndex;

    @ManyToOne
    @JoinColumn(name = "workflow_definition_id")
    private WorkflowDefinition workflowDefinition;

}
