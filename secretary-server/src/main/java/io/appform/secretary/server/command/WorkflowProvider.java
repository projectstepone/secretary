package io.appform.secretary.server.command;

import io.appform.secretary.model.Workflow;

import java.util.List;
import java.util.Optional;

public interface WorkflowProvider {

    public Optional<Workflow> save(Workflow workflow);

    public Optional<Workflow> get(String name);

    public List<Workflow> getAll();

    public Optional<Workflow> update(Workflow workflow);
}
