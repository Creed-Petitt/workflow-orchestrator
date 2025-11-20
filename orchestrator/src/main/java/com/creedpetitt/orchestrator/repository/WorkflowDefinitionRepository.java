package com.creedpetitt.orchestrator.repository;

import com.creedpetitt.orchestrator.model.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowDefinitionRepository
        extends JpaRepository<WorkflowDefinition, String> {}