package com.optimus.ats.service;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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

    public InternalDataDto initProcess(Integer id){
        System.out.println("Init");
        InternalDataDto dto = new InternalDataDto();
        
        DecisionWorkflowRequest retrieved = decisionRepository.findById(Long.valueOf(id));
        EmployeeData employee = employeeRepository.findById(retrieved.getEmployeeId());
        dto.setValidRequest(Objects.isNull(retrieved)? false: true);
        dto.setWorkflowRequest(retrieved);
        dto.setEmployeeData(employee);
        return dto;
    }

    public void startProcess(DecisionWorkflowRequest data){
        System.out.println("Start");
    }

    public void postProcess(DecisionWorkflowRequest data){
        System.out.println("After process");
    }

}
