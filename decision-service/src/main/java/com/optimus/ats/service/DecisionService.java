package com.optimus.ats.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.optimus.ats.model.DecisionWorkflowRequest;
import com.optimus.ats.model.EmployeeData;
import com.optimus.ats.model.InternalDataDto;
import com.optimus.ats.repository.DecisionRepository;
import com.optimus.ats.repository.EmployeeRepository;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;

@ApplicationScoped
public class DecisionService {
    
    static final Logger log = LoggerFactory.getLogger(DecisionService.class);
    
    @Inject
    DecisionRepository decisionRepository;
    @Inject
    EmployeeRepository employeeRepository;
    @Inject
    EmailService emailSrv;

    @Inject
    EventBus bus;

    public InternalDataDto initProcess(Long incomingId){
        InternalDataDto dto = new InternalDataDto();  
        log.info("incomingId->{}",incomingId);   
        DecisionWorkflowRequest retrieved = decisionRepository.findById(Long.valueOf(incomingId));
        log.info("Retrieved DecisionWorkflowRequest-> {}", Objects.nonNull(retrieved)?JsonObject.mapFrom(retrieved).toString():"");  
        if(Objects.nonNull(retrieved)){
            EmployeeData employee = employeeRepository.findById(retrieved.getEmployeeId());
            if(Objects.nonNull(employee))
                emailSrv.sendEmailNotification(employee.getEmail(), employee.getEmployeeName(), employee.getCsEmpId());
            log.info("Retrieved EmployeeData-> {}", Objects.nonNull(employee)?JsonObject.mapFrom(employee).toString():"");
            dto.setValidRequest(Objects.isNull(retrieved)? false: true);
            dto.setWorkflowRequest(retrieved);
            dto.setEmployeeData(employee);
        } else{
            dto.setValidRequest(false);
        }

        eventLog("Initial Processing-Kogito",dto, "" );
        
        return dto;
    }

    public void eventLog( String action, Object dto, String stringIfDtoIsNull){       
        JsonObject mapObj = new JsonObject();
        mapObj.put("serviceName", "decision-service");
        mapObj.put("action",action);
        mapObj.put("details",Objects.nonNull(dto)?JsonObject.mapFrom(dto).toString(): stringIfDtoIsNull);
        bus.publish("log",mapObj);
    }    
    

    public void startProcess(DecisionWorkflowRequest data){
        System.out.println("Start");
    }

    @Transactional
    public void postProcess(Boolean approved, String remarks, Long incomingId){
        System.out.println("After process");
        log.info("approved:{},remarks:{},incomingId:{}",approved, remarks,incomingId);
        decisionRepository.update("approved=?1,approvalRemarks=?2, workflowStatus=?3 where id=?4", Objects.requireNonNullElse(approved, Boolean.TRUE), Objects.requireNonNullElse(remarks, "Auto Approved by Decision Rule"), Objects.requireNonNullElse(approved, Boolean.TRUE).booleanValue() ? 1 : 2, incomingId);
        sendEmailNotification(incomingId, approved.booleanValue());
        eventLog("Post Processing-Kogito",null,  "approved:"+approved+",remarks:"+remarks+",incomingId:"+incomingId);
    }

    private void sendEmailNotification(Long incomingId, boolean approved){
        DecisionWorkflowRequest retrieved = decisionRepository.findById(Long.valueOf(incomingId));
        log.info("sendEmailNotification -> Retrieved DecisionWorkflowRequest-> {}", Objects.nonNull(retrieved)?JsonObject.mapFrom(retrieved).toString():"");  
        if(Objects.nonNull(retrieved)){
            EmployeeData employee = employeeRepository.findById(retrieved.getEmployeeId());
            if(Objects.nonNull(employee))
                emailSrv.sendActionUpdateEmail(employee.getEmail(), employee.getEmployeeName(), employee.getCsEmpId(),approved?"Approved":"Rejected");
        }
    }

    @Transactional
    public void createDummyRecord(Long employeeId){
        DecisionWorkflowRequest newRecord = new DecisionWorkflowRequest();
        newRecord.setEmployeeId(employeeId);
        newRecord.setApproved(false);
        decisionRepository.persist(newRecord);
        
    }

}
