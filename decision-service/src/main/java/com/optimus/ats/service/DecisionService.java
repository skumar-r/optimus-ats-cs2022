package com.optimus.ats.service;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.optimus.ats.model.DecisionWorkflowRequest;
import com.optimus.ats.model.EmployeeData;
import com.optimus.ats.model.InternalDataDto;
import com.optimus.ats.repository.DecisionRepository;
import com.optimus.ats.repository.EmployeeRepository;

@ApplicationScoped
public class DecisionService {
    
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

    public void postProcess(DecisionWorkflowRequest data){
        System.out.println("After process");
    }

    @Transactional
    public void createDummyRecord(Long employeeId){
        DecisionWorkflowRequest newRecord = new DecisionWorkflowRequest();
        newRecord.setEmployeeId(employeeId);
        newRecord.setApproved(false);
        decisionRepository.persist(newRecord);
        
    }

}
