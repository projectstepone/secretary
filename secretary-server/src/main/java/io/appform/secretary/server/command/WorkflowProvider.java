package io.appform.secretary.server.command;

import io.appform.secretary.model.Workflow;

import java.util.List;
import java.util.Optional;

public interface WorkflowProvider {

    Optional<Workflow> save(Workflow workflow);

    Optional<Workflow> get(String name);

    List<Workflow> getAll();

    Optional<Workflow> update(Workflow workflow);
}
