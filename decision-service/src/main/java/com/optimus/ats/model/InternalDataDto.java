package com.optimus.ats.model;

public class InternalDataDto {
    
    boolean validRequest;
    DecisionWorkflowRequest workflowRequest;
    EmployeeData employeeData;

    public boolean isValidRequest() {
        return validRequest;
    }
    public void setValidRequest(boolean validRequest) {
        this.validRequest = validRequest;
    }
    public EmployeeData getEmployeeData() {
        return employeeData;
    }
    public void setEmployeeData(EmployeeData employeeData) {
        this.employeeData = employeeData;
    }
    public DecisionWorkflowRequest getWorkflowRequest() {
        return workflowRequest;
    }
    public void setWorkflowRequest(DecisionWorkflowRequest workflowRequest) {
        this.workflowRequest = workflowRequest;
    }
    
    
}
