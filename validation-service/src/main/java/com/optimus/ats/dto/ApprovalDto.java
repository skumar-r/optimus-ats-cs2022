package com.optimus.ats.dto;

public class ApprovalDto {

    Long workflowId;
    boolean approved;
    String remarks;

    public Long getWorkflowId() {
        return workflowId;
    }
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }
    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }        
    
}
