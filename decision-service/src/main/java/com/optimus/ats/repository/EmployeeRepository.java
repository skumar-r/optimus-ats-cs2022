package com.optimus.ats.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;


import com.optimus.ats.model.EmployeeData;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<EmployeeData> {

   // put your custom logic here as instance methods


}