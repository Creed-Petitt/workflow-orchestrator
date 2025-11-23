package com.creedpetitt.orchestrator.repository;

import com.creedpetitt.orchestrator.model.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRunRepository
        extends JpaRepository<WorkflowRun, String> {
}
