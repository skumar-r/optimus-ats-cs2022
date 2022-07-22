package com.optimus.ats.service;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.optimus.ats.model.DecisionWorkflowRequest;
import com.optimus.ats.service.remote.WorkflowService;

@ApplicationScoped
public class ValidationService {

    static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    @Inject
    EventBus bus;

    @Inject
    WorkflowService workflowService;

    @ConsumeEvent("echo")
    public String echo(String name) {
        return name;
    }

    public String invokeDecisionService(Long employeeId, Long managerId) {
        String response = "success";
        DecisionWorkflowRequest workflow = workflowService.createNewWorkflowRequest(employeeId, managerId);
        log.info("workflowId->" + workflow.getId());
       
        try {
            String processInstanceId = workflowService.invokeDecisionService(workflow.getId());
            log.info("processInstanceId->" + processInstanceId);
            workflow.setProcessInstanceId(processInstanceId);
            Integer countUpdate = workflowService.updateWorkflowRequestForProcessInstance(workflow);
            log.info("processInstanceId.countUpdate->" + countUpdate);
            String processTaskId = workflowService.invokeDecisionServiceForTask(processInstanceId);
            log.info("processTaskId->" + processTaskId);
            if(Objects.nonNull(processTaskId) && processTaskId.length() > 5){
                workflow.setProcessTaskId(processTaskId);
                countUpdate = workflowService.updateWorkflowRequestForTaskInstance(workflow);
                log.info("processTaskId.countUpdate->" + countUpdate);
            }            

        } catch (Exception e) {
            log.error("exception", e);
            response = "fail";
        }

        log.info("res->" + response);
        return response;
    }

}
