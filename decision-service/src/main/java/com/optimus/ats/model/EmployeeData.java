package com.optimus.ats.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_EMPLOYEE")
public class EmployeeData {
    @Column(name="employee_id")
    @Id
    private Long id;
    @Column(name="employee_name")
    private String employeeName;
    private String department;
    private String designation;
    @Column(name="cs_employee_id")
    private String csEmpId;
    private String email;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }    
    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getCsEmpId() {
        return csEmpId;
    }
    public void setCsEmpId(String csEmpId) {
        this.csEmpId = csEmpId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
