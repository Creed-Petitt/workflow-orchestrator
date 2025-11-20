package com.creedpetitt.orchestrator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "workflow_definition")
public class WorkflowDefinition {

    @Id
    private String id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<WorkflowStep> steps;
}
