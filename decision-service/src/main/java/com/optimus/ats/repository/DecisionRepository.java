package com.optimus.ats.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.optimus.ats.model.DecisionWorkflowRequest;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class DecisionRepository implements PanacheRepository<DecisionWorkflowRequest> {

   // put your custom logic here as instance methods

   public List<DecisionWorkflowRequest> findByEmployeeId(Long employeeId){
       return find("employeeId", employeeId).list();
   }
}