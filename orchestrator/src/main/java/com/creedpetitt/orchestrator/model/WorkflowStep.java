package com.creedpetitt.orchestrator.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "workflow_step")
public class WorkflowStep {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String action;

    @Column
    private int stepIndex;

}
