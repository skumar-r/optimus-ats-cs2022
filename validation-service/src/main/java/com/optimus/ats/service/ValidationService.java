package com.optimus.ats.service;

import com.optimus.ats.model.Employee;
import com.optimus.ats.service.impl.RecognitionServiceImpl;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.optimus.ats.dto.ApprovalDto;
import com.optimus.ats.dto.ResponseDto;
import com.optimus.ats.model.DecisionWorkflowRequest;
import com.optimus.ats.service.remote.WorkflowService;

@ApplicationScoped
public class ValidationService {

    static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    @ConfigProperty(name = "upload.directory.employee")
    String uploadDir;

    @Inject
    EventBus bus;

    @Inject
    WorkflowService workflowService;

    @Inject
    RecognitionServiceImpl recognitionService;

    @ConsumeEvent("echo")
    public String echo(String name) {
        return name;
    }

    public String invokeDecisionService(Long employeeId, Long managerId, String csId) {
        String response = "success";
        DecisionWorkflowRequest workflow = workflowService.createNewWorkflowRequest(employeeId, managerId, csId);
        log.info("workflowId->" + workflow.getId());

        try {
            String processInstanceId = workflowService.invokeDecisionService(workflow.getId());
            log.info("processInstanceId->" + processInstanceId);
            workflow.setProcessInstanceId(processInstanceId);
            Integer countUpdate = workflowService.updateWorkflowRequestForProcessInstance(workflow);
            log.info("processInstanceId.countUpdate->" + countUpdate);
            String processTaskId = workflowService.invokeDecisionServiceForTask(processInstanceId);
            log.info("processTaskId->" + processTaskId);
            if (Objects.nonNull(processTaskId) && processTaskId.length() > 5) {
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

    public ResponseDto approve(ApprovalDto aDto) {
        DecisionWorkflowRequest workflowRequest = DecisionWorkflowRequest.findById(aDto.getWorkflowId());
        if (Objects.nonNull(workflowRequest) && Objects.nonNull(workflowRequest.getProcessInstanceId())
                && Objects.nonNull(workflowRequest.getProcessTaskId())) {
            try{
                return workflowService.approveWorkflow(workflowRequest, aDto);
            } catch(Exception e){
                log.error("excepion", e);
                return ResponseDto.createResponse(Status.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseDto.createResponse(Status.NOT_FOUND);
        }
    }

    public List<DecisionWorkflowRequest> getAll() {
        List<DecisionWorkflowRequest> list = DecisionWorkflowRequest.list("approved=?1 and processInstanceId != null and processTaskId != null and (workflowStatus = 0 or workflowStatus is null)",false);
       return list.stream().map(req->{
           if(!Objects.isNull(Employee.findByName(req.getCsEmpId()))) {
               Employee emp = Employee.findByName(req.getCsEmpId());
               if(emp.isHasS3Photo()){
                   req.setEmpPhoto(recognitionService.getS3Photo(Employee.findByName(req.getCsEmpId()).getPhotoFront()));
                   req.setComparePhoto(recognitionService.getS3Photo(req.getCsEmpId()));
               }else {
                   req.setEmpPhoto(recognitionService.getLocalPhoto(Employee.findByName(req.getCsEmpId()).getPhotoFront()));
                   req.setComparePhoto(recognitionService.getLocalPhoto(uploadDir + File.separator + req.getCsEmpId() + ".png"));
               }
           }
           return req;
        }).collect(Collectors.toList());
    }
}
