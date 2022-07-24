package com.optimus.ats.dto;

public class ApprovalDto {

    Long workflowId;
    boolean approved;
    String approvalRemarks;
    
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
    public String getApprovalRemarks() {
        return approvalRemarks;
    }
    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
    }

        
    
}
