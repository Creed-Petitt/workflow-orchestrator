package com.creedpetitt.orchestrator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "workflow_definition")
public class WorkflowDefinition {

    @Id
    private String id;

    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowStep> steps = new ArrayList<>();
}
