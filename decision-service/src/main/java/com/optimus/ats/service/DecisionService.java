package com.optimus.ats.service;

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

@ApplicationScoped
public class DecisionService {
    
    static final Logger log = LoggerFactory.getLogger(DecisionService.class);
    
    @Inject
    DecisionRepository decisionRepository;
    @Inject
    EmployeeRepository employeeRepository;

    public InternalDataDto initProcess(Long incomingId){
        InternalDataDto dto = new InternalDataDto();       
        DecisionWorkflowRequest retrieved = decisionRepository.findById(Long.valueOf(incomingId));
        if(Objects.nonNull(retrieved)){
            EmployeeData employee = employeeRepository.findById(retrieved.getEmployeeId());
            dto.setValidRequest(Objects.isNull(retrieved)? false: true);
            dto.setWorkflowRequest(retrieved);
            dto.setEmployeeData(employee);
        } else{
            dto.setValidRequest(false);
        }
        
        return dto;
    }
    

    public void startProcess(DecisionWorkflowRequest data){
        System.out.println("Start");
    }

    @Transactional
    public void postProcess(Boolean approved, String remarks, Long incomingId){
        System.out.println("After process");
        log.info("approved:{},remarks:{},incomingId:{}",approved, remarks,incomingId);
        decisionRepository.update("approved=?1,approvalRemarks=?2, workflowStatus=?3 where id=?4", Objects.requireNonNullElse(approved, Boolean.TRUE), Objects.requireNonNullElse(remarks, "Auto Approved by Decision Rule"), Objects.requireNonNullElse(approved, Boolean.TRUE).booleanValue() ? 1 : 2, incomingId);
    }

    @Transactional
    public void createDummyRecord(Long employeeId){
        DecisionWorkflowRequest newRecord = new DecisionWorkflowRequest();
        newRecord.setEmployeeId(employeeId);
        newRecord.setApproved(false);
        decisionRepository.persist(newRecord);
        
    }

}
