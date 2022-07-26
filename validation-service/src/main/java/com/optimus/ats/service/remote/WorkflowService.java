package com.optimus.ats.service.remote;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimus.ats.dto.ApprovalDto;
import com.optimus.ats.dto.ProcessDto;
import com.optimus.ats.dto.ResponseDto;
import com.optimus.ats.model.DecisionWorkflowRequest;
import com.optimus.ats.service.ValidationService;

@ApplicationScoped
public class WorkflowService {

    static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    @Inject
    @RestClient
    WorkflowRemoteRestService workflowRemoteService;

    public String invokeDecisionService(Long workflowId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String processInstanceId = "";
        if (Objects.nonNull(workflowId)) {
            ProcessDto dto = new ProcessDto();
            dto.setIncomingId(workflowId);
            String responseFromRemote = workflowRemoteService.post(dto);
            Map mapObj = mapper.readValue(responseFromRemote, Map.class);
            processInstanceId = (String) mapObj.get("id");

        }
        return processInstanceId;
    }

    public String invokeDecisionServiceForTask(String processInstanceId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String taskInstanceId = null;
        if (Objects.nonNull(processInstanceId)) {
            String responseFromRemote = workflowRemoteService.getTasks(processInstanceId);
            List<Map> list = mapper.readValue(responseFromRemote, List.class);
            if (Objects.nonNull(list) && list.size() > 0) {
                taskInstanceId = (String) list.get(0).get("id");
            }
        }
        return taskInstanceId;
    }

    public ResponseDto approveWorkflow(DecisionWorkflowRequest workflowRequest, ApprovalDto aDto)
            throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        log.info("Request for updateTaskStatus, ProcessInstanceId:{}, ProcessTaskId:{}", workflowRequest.getProcessInstanceId(),workflowRequest.getProcessTaskId() );
        String res="";
        try{
            res = workflowRemoteService.updateTaskStatus(workflowRequest.getProcessInstanceId(),
            workflowRequest.getProcessTaskId(), aDto);
        } catch ( WebApplicationException e){
                if(e.getResponse().getStatus()==404){
                    updateWorkflowRequestForWorkflowStatus(workflowRequest);
                }
                throw e;
        }
        
        log.info("Response for updateTaskStatus:{}", res);
        Map resMap = mapper.readValue(res, Map.class);
        if (Objects.nonNull(resMap.get("id"))) {
            return ResponseDto.createResponse(Status.NOT_FOUND);
        } else {
            return ResponseDto.createSuccessResponse();
        }

    }

    @Transactional
    public DecisionWorkflowRequest createNewWorkflowRequest(Long employeeId, Long managerId) {
        DecisionWorkflowRequest request = getWorkflowRequestModel(employeeId, managerId);
        DecisionWorkflowRequest.persist(request);
        return request;
    }

    @Transactional
    public Integer updateWorkflowRequestForProcessInstance(DecisionWorkflowRequest request) {
        String updateQuery = "processInstanceId=?1 where id=?2";
        return DecisionWorkflowRequest.update(updateQuery, request.getProcessInstanceId(), request.getId());
    }

    @Transactional
    public Integer updateWorkflowRequestForTaskInstance(DecisionWorkflowRequest request) {
        String updateQuery = "processTaskId=?1 where id=?2";
        return DecisionWorkflowRequest.update(updateQuery, request.getProcessTaskId(), request.getId());
    }

    @Transactional
    public Integer updateWorkflowRequestForWorkflowStatus(DecisionWorkflowRequest request) {
        String updateQuery = "workflowStatus=?1 where id=?2";
        return DecisionWorkflowRequest.update(updateQuery, 3, request.getId());
    }

    private DecisionWorkflowRequest getWorkflowRequestModel(Long employeeId, Long managerId) {
        DecisionWorkflowRequest request = new DecisionWorkflowRequest();
        request.setEmployeeId(employeeId);
        request.setManagerId(managerId);
        return request;
    }
}
