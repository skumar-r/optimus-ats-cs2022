package com.optimus.ats.service.remote;


import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimus.ats.dto.ProcessDto;
import com.optimus.ats.model.DecisionWorkflowRequest;
import com.optimus.ats.service.ValidationService;

@ApplicationScoped                          
public class WorkflowService {

    static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    @Inject
    @RestClient
    WorkflowRemoteRestService workflowRemoteService;

   

    public String invokeDecisionService(Long workflowId) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String processInstanceId ="";
       if(Objects.nonNull(workflowId)){
            ProcessDto dto = new ProcessDto();
            dto.setIncomingId(workflowId);
            String responseFromRemote =  workflowRemoteService.post(dto);
            Map mapObj = mapper.readValue(responseFromRemote, Map.class);
            processInstanceId = (String) mapObj.get("id");

       }
       return processInstanceId;
    }

    public String invokeDecisionServiceForTask(String processInstanceId) throws Exception{
       ObjectMapper mapper = new ObjectMapper();
        String taskInstanceId=null;
       if(Objects.nonNull(processInstanceId)){           
            String responseFromRemote =  workflowRemoteService.getTasks(processInstanceId);
            List<Map> list = mapper.readValue(responseFromRemote, List.class);
            if(Objects.nonNull(list) && list.size() > 0){
                taskInstanceId = (String)list.get(0).get("id");
            }
       }
       return taskInstanceId;
    }

    @Transactional
    public DecisionWorkflowRequest createNewWorkflowRequest(Long employeeId, Long managerId){
        DecisionWorkflowRequest request = getWorkflowRequestModel(employeeId, managerId);
        DecisionWorkflowRequest.persist(request);
        return request;
    }

    @Transactional
    public Integer updateWorkflowRequestForProcessInstance(DecisionWorkflowRequest request){
            String updateQuery = "processInstanceId=?1 where id=?2";
            return DecisionWorkflowRequest.update(updateQuery, request.getProcessInstanceId(), request.getId());
    }

    @Transactional
    public Integer updateWorkflowRequestForTaskInstance(DecisionWorkflowRequest request){
            String updateQuery = "processTaskId=?1 where id=?2";
            return DecisionWorkflowRequest.update(updateQuery, request.getProcessTaskId(), request.getId());
    }

    private DecisionWorkflowRequest getWorkflowRequestModel(Long employeeId, Long managerId){
        DecisionWorkflowRequest request = new DecisionWorkflowRequest();
        request.setEmployeeId(employeeId);
        request.setManagerId(managerId);
        return request;
    }
}
